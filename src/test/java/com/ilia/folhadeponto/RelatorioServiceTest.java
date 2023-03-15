package com.ilia.folhadeponto;

import com.ilia.folhadeponto.dtos.RelatorioDTO;
import com.ilia.folhadeponto.entity.MomentoBatida;
import com.ilia.folhadeponto.model.RelatorioJSON;
import com.ilia.folhadeponto.service.MomentoBatidaService;
import com.ilia.folhadeponto.service.RelatorioService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(MockitoExtension.class)
public class RelatorioServiceTest {

    @Value("#{'${ilia.feriados}'.split(',')}")
    private Set<String> feriados;

    @MockBean
    private MomentoBatidaService momentoBatidaService;
    @InjectMocks
    private RelatorioService relatorioService;

    @Before
    public void setup() {
        this.relatorioService = spy(RelatorioService.class);
        this.momentoBatidaService = mock(MomentoBatidaService.class);
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void relatorioServiceTest$gerarRelatorioValido() {
        RelatorioDTO relatorioDTO = new RelatorioDTO();
        relatorioDTO.setMesEAno("2023-01");

        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern(RelatorioService.DEFAULT_ANO_E_MES_FORMAT);
        LocalDateTime momento = YearMonth.parse(relatorioDTO.getMesEAno(), dateFormat).atEndOfMonth().atStartOfDay();

        List<MomentoBatida> momentosBatida = this.gerarMomentosBatida(momento);

        when(this.momentoBatidaService.findBatidasByMesAndAno(momento.getMonthValue(), momento.getYear())).thenReturn(momentosBatida);

        RelatorioJSON relatorioJSON = this.relatorioService.gerarRelatorio(relatorioDTO);


        Assert.assertEquals(relatorioJSON.getMes(), "2023-01");
        Assert.assertEquals(relatorioJSON.getRegistros().size(), 22);

    }

    private List<MomentoBatida> gerarMomentosBatida(LocalDateTime localDateTime) {
        Calendar calendar = new GregorianCalendar(
                localDateTime.getYear(),
                localDateTime.getMonthValue(),
                localDateTime.getDayOfMonth()
        );

        int mes = localDateTime.getMonthValue();
        int ano = localDateTime.getYear();
        List<MomentoBatida> momentosBatida = new ArrayList<>();

        int diasNoMes = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        for (int i = 0; i < diasNoMes; i++) {
            if (this.ehDiaUtil(i+1, localDateTime.getMonthValue(), localDateTime.getYear())) {
                for (int j = 0; j < 4; j++) {
                    MomentoBatida momentoBatida = new MomentoBatida();
                    momentoBatida.setId((long) i);
                    momentoBatida.setDia(String.valueOf(i+1));
                    momentoBatida.setMes(String.valueOf(localDateTime.getMonthValue()));
                    momentoBatida.setAno(String.valueOf(localDateTime.getYear()));
                    momentoBatida.setTimestamp(this.montarTimesptamp(mes, ano, (i+1), j));
                    momentosBatida.add(momentoBatida);
                }
            }
        }
        return momentosBatida;
    }

    private LocalDateTime montarTimesptamp(int mes, int ano, int dia, int index) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(ano);
        stringBuilder.append("-");
        if (mes < 10) {
            stringBuilder.append("0");
        }
        stringBuilder.append(mes);
        stringBuilder.append("-");
        if (dia < 10) {
            stringBuilder.append("0");
        }
        stringBuilder.append(dia);
        stringBuilder.append("T");
        String toAppend = "";
        switch (index) {
            case 0:
                toAppend = "08:00:00";
                break;
            case 1:
                toAppend = "12:00:00";
                break;
            case 2:
                toAppend = "13:00:00";
                break;
            case 3:
                toAppend = "17:00:00";
                break;
        }
        stringBuilder.append(toAppend);
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern(MomentoBatidaService.DEFAULT_DATE_FORMAT);
        return LocalDateTime.parse(stringBuilder.toString(), dateFormat);
    }

    private boolean ehDiaUtil(int dia, int mes, int ano) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(ano);
        stringBuilder.append("-");
        if (mes < 10) {
            stringBuilder.append("0");
        }
        stringBuilder.append(mes);
        stringBuilder.append("-");
        if (dia < 10) {
            stringBuilder.append("0");
        }
        stringBuilder.append(dia);
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern(MomentoBatidaService.RESPONSE_DATE_FORMAT);
        LocalDate data = LocalDate.parse(stringBuilder.toString(), dateFormat);
        DayOfWeek dayOfWeek = data.getDayOfWeek();
        if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
            return false;
        }
        return true;
    }
}
