package com.ilia.folhadeponto.service;

import com.ilia.folhadeponto.dtos.MomentoBatidaDTO;
import com.ilia.folhadeponto.entity.MomentoBatida;
import com.ilia.folhadeponto.messages.RequestErrorMessages;
import com.ilia.folhadeponto.model.MomentoJSON;
import com.ilia.folhadeponto.model.RegistroJSON;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@Service
public class MomentoBatidaService {

    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String RESPONSE_DATE_FORMAT = "yyyy-MM-dd";
    public static final String RESPONSE_TIME_FORMAT = "HH:mm:ss";

    @Value("${ilia.limiteDeBatidasPorDia}")
    private Long limiteBatidasPorDia;

    @PersistenceContext
    private EntityManager entityManager;


    @Transactional
    public RegistroJSON insereBatida (MomentoBatidaDTO momentoBatidaDTO) {

        List<MomentoBatida> momentosBatida = this.findBatidasByData(
            momentoBatidaDTO.getDia(),
            momentoBatidaDTO.getMes(),
            momentoBatidaDTO.getAno()
        );

        if (momentosBatida.size() == 2) {
            MomentoBatida momentoBatidaAntesDoAlmoco = momentosBatida.get(1);
            Duration duracaoAlmoco
                        = Duration.between(momentoBatidaAntesDoAlmoco.getTimestamp(), momentoBatidaDTO.getMomento());
            if (duracaoAlmoco.toHoursPart() < 1){
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, RequestErrorMessages.MINIMO_UMA_HORA_ALMOCO);
            }
        }

        if (momentosBatida.size() >= 4) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, RequestErrorMessages.APENAS_QUATRO_HORARIOS_POR_DIA);
        }

        MomentoBatida novoMomentoBatida = this.persistMomentoBatida(momentoBatidaDTO);

        momentosBatida.add(novoMomentoBatida);

        RegistroJSON registroJSON = new RegistroJSON();

        registroJSON.setDia(novoMomentoBatida.getTimestamp().toLocalDate());
        for (MomentoBatida momentoBatida : momentosBatida) {
            String horario = momentoBatida.getTimestamp().format(DateTimeFormatter.ofPattern(RESPONSE_TIME_FORMAT));
            registroJSON.addHorariosItem(horario);
        }

        return registroJSON;
    }

    @Transactional
    public MomentoBatida persistMomentoBatida(MomentoBatidaDTO momentoBatidaDTO) {

        MomentoBatida momentoBatida = new MomentoBatida();
        momentoBatida.setTimestamp(momentoBatidaDTO.getMomento());
        momentoBatida.setDia(momentoBatidaDTO.getDia());
        momentoBatida.setMes(momentoBatidaDTO.getMes());
        momentoBatida.setAno(momentoBatidaDTO.getAno());

        this.entityManager.persist(momentoBatida);

        return momentoBatida;
    }

    @Transactional
    public List<MomentoBatida> findBatidasByData(String dia, String mes, String ano) {
        Query query = this.entityManager.createNamedQuery(
                MomentoBatida.FIND_MOMENTO_BATIDA_BY_DATE,
                MomentoBatida.class
        );
        query.setParameter("dia", dia);
        query.setParameter("mes", mes);
        query.setParameter("ano", ano);
        return query.getResultList();
    }

    @Transactional
    public Long countBatidasNaData(String dia, String mes, String ano) {
        Query query = this.entityManager.createNamedQuery(
                MomentoBatida.COUNT_ENTRADAS_EXISTENTES_BY_DATE,
                MomentoBatida.class
        );
        query.setParameter("dia", dia);
        query.setParameter("mes", mes);
        query.setParameter("ano", ano);
        return (Long)query.getSingleResult();
    }

    @Transactional
    public List<MomentoBatida> findBatidasByMesAndAno(int mes, int ano) {
        Query query = this.entityManager.createNamedQuery(
                MomentoBatida.FIND_ALL_MOMENTO_BY_MES_AND_ANO,
                MomentoBatida.class
        );
        query.setParameter("mes", String.valueOf(mes));
        query.setParameter("ano", String.valueOf(ano));
        return query.getResultList();
    }

    public MomentoBatidaDTO prepararDTO(MomentoJSON momentoJSON) {

        LocalDateTime momento;

        try {
            DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern(MomentoBatidaService.DEFAULT_DATE_FORMAT);
            momento = LocalDateTime.parse(momentoJSON.getDataHora(), dateFormat);
        } catch (DateTimeParseException dateTimeParseException) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, RequestErrorMessages.DATA_E_HORA_EM_FORMATO_INVALIDO);
        }

        MomentoBatidaDTO momentoBatidaDTO = new MomentoBatidaDTO();
        momentoBatidaDTO.setMomento(momento);
        momentoBatidaDTO.setDia(String.valueOf(momento.getDayOfMonth()));
        momentoBatidaDTO.setMes(String.valueOf(momento.getMonthValue()));
        momentoBatidaDTO.setAno(String.valueOf(momento.getYear()));

        return momentoBatidaDTO;
    }

}
