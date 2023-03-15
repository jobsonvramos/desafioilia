package com.ilia.folhadeponto.service;

import static com.ilia.folhadeponto.service.MomentoBatidaService.RESPONSE_TIME_FORMAT;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.ilia.folhadeponto.dtos.RelatorioDTO;
import com.ilia.folhadeponto.entity.MomentoBatida;
import com.ilia.folhadeponto.messages.RequestErrorMessages;
import com.ilia.folhadeponto.model.RegistroJSON;
import com.ilia.folhadeponto.model.RelatorioJSON;

/**
 * Serviço responsável por gerar o relatório de ponto
 */
@Service
public class RelatorioService {

    public static final String DEFAULT_ANO_E_MES_FORMAT = "yyyy-MM";

    @Value("#{'${ilia.feriados}'.split(',')}")
    private Set<String> feriados;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private MomentoBatidaService momentoBatidaService;

    @Transactional
    public RelatorioJSON gerarRelatorio(RelatorioDTO relatorioDTO) {
        LocalDateTime momento;

        try {
            DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern(RelatorioService.DEFAULT_ANO_E_MES_FORMAT);
            momento = YearMonth.parse(relatorioDTO.getMesEAno(), dateFormat).atEndOfMonth().atStartOfDay();
        } catch (DateTimeParseException dateTimeParseException) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, RequestErrorMessages.DATA_E_HORA_EM_FORMATO_INVALIDO);
        }

        RelatorioJSON relatorioJSON = new RelatorioJSON();
        relatorioJSON.setMes(relatorioDTO.getMesEAno());

        List<MomentoBatida> momentosBatida = this.momentoBatidaService.findBatidasByMesAndAno(
                momento.getMonthValue(),
                momento.getYear()
        );

        if (momentosBatida.size() == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, RequestErrorMessages.RELATORIO_NAO_ENCONTRADO);
        }

        Map<Integer, LinkedList<MomentoBatida>> momentosBatidaPorDia =
                this.agruparMomentosBatidaPorDia(momentosBatida);

        Duration qntDeTrabalhoEsperado = this.calcularHorasEsperadas(momentosBatida);
        Duration qntDeTrabalhoTrabalhado = this.calcularHorasTrabalhadasEmSegundos(momentosBatidaPorDia);
        Duration qntTrabalhoExcedente = Duration.ofSeconds(0L);
        Duration qntTrabalhoDevido = Duration.ofSeconds(0L);
        if (qntDeTrabalhoTrabalhado.toSeconds() > qntDeTrabalhoEsperado.toSeconds()) {
             qntTrabalhoExcedente =
                    Duration.ofSeconds(qntDeTrabalhoTrabalhado.toSeconds() - qntDeTrabalhoEsperado.toSeconds());
        } else {
            qntTrabalhoDevido =
                    Duration.ofSeconds(qntDeTrabalhoEsperado.toSeconds() - qntDeTrabalhoTrabalhado.toSeconds());
        }

        relatorioJSON.setHorasTrabalhadas(qntDeTrabalhoTrabalhado.toString());
        relatorioJSON.setHorasExcedentes(qntTrabalhoExcedente.toString());
        relatorioJSON.setHorasDevidas(qntTrabalhoDevido.toString());

        relatorioJSON.setRegistros(this.montarRelatorioRegistros(momentosBatidaPorDia));

        return relatorioJSON;
    }

    private List<RegistroJSON> montarRelatorioRegistros(Map<Integer, LinkedList<MomentoBatida>> momentosBatidaPorDia) {
        LinkedList<RegistroJSON> registros = new LinkedList<>();
        for (Map.Entry<Integer, LinkedList<MomentoBatida>> entry : momentosBatidaPorDia.entrySet()) {
            RegistroJSON registro = new RegistroJSON();
            for (MomentoBatida momentoBatida : entry.getValue()) {
                String horario = momentoBatida.getTimestamp().format(DateTimeFormatter.ofPattern(RESPONSE_TIME_FORMAT));
                registro.addHorariosItem(horario);
            }
            Optional<MomentoBatida> anyMomentoBatida = entry.getValue().stream().findAny();
            registro.setDia(anyMomentoBatida.get().getTimestamp().toLocalDate());
            registros.add(registro);
        }
        return new ArrayList<>(registros);
    }

    private Map<Integer, LinkedList<MomentoBatida>> agruparMomentosBatidaPorDia (List<MomentoBatida> momentosBatida) {
        Map<Integer, LinkedList<MomentoBatida>> momentosBatidaPorDia = new HashMap<Integer, LinkedList<MomentoBatida>>();
        for (MomentoBatida momentoBatida : momentosBatida) {
            int diaDoMes = momentoBatida.getTimestamp().getDayOfMonth();
            if (momentosBatidaPorDia.containsKey(diaDoMes)) {
                momentosBatidaPorDia.get(diaDoMes).add(momentoBatida);
            } else {
                LinkedList<MomentoBatida> newMomentoBatidaSet = new LinkedList<>();
                newMomentoBatidaSet.add(momentoBatida);
                momentosBatidaPorDia.put(diaDoMes, newMomentoBatidaSet);
            }
        }
        return momentosBatidaPorDia;
    }

    private Duration calcularHorasEsperadas(List<MomentoBatida> momentosBatida) {
        Set<LocalDate> diasTrabalhados = new HashSet<>();

        for (MomentoBatida momentoBatida : momentosBatida) {
            LocalDate data = momentoBatida.getTimestamp().toLocalDate();
            if (this.ehDiaUtil(data)) {
                diasTrabalhados.add(data);
            }
        }

        // Multiplicado por 8 pois deve-se contabilizar 8 horas por dia de trabalho
        return Duration.ofHours(diasTrabalhados.size() * 8);
    }

    private Duration calcularHorasTrabalhadasEmSegundos(Map<Integer, LinkedList<MomentoBatida>> momentosBatida) {

        Long segundosTrabalhados = 0L;

        for (Map.Entry<Integer, LinkedList<MomentoBatida>> entry : momentosBatida.entrySet()) {

            LocalDateTime primeiroPonto = entry.getValue().get(0).getTimestamp();
            LocalDateTime segundoPonto = entry.getValue().get(1).getTimestamp();
            LocalDateTime terceiroPonto = entry.getValue().get(2).getTimestamp();
            LocalDateTime quartoPonto = entry.getValue().get(3).getTimestamp();

            Duration antesDoAlmoco = Duration.between(primeiroPonto, segundoPonto);
            Duration depoisDoAlmoco = Duration.between(terceiroPonto, quartoPonto);

            Long horasTrablhadasNoDia = antesDoAlmoco.toSeconds() + depoisDoAlmoco.toSeconds();

            segundosTrabalhados += horasTrablhadasNoDia;

        }

        return Duration.ofSeconds(segundosTrabalhados);

    }

    private boolean ehDiaUtil(LocalDate data) {
        DayOfWeek dayOfWeek = data.getDayOfWeek();
        if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
            return false;
        }
        if (this.feriados != null && this.feriados.contains(data)) {
            return false;
        }
        return true;
    }

    public RelatorioDTO prepararDTO(String mes) {
        RelatorioDTO relatorioDTO = new RelatorioDTO();
        relatorioDTO.setMesEAno(mes);
        return relatorioDTO;
    }
}
