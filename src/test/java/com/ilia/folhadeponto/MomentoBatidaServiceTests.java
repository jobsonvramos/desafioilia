package com.ilia.folhadeponto;

import com.ilia.folhadeponto.dtos.MomentoBatidaDTO;
import com.ilia.folhadeponto.entity.MomentoBatida;
import com.ilia.folhadeponto.model.RegistroJSON;
import com.ilia.folhadeponto.service.MomentoBatidaService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static com.ilia.folhadeponto.service.MomentoBatidaService.RESPONSE_TIME_FORMAT;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(MockitoExtension.class)
public class MomentoBatidaServiceTests {

    private MomentoBatidaService momentoBatidaService;

    @Before
    public void setup() {
        this.momentoBatidaService = spy(MomentoBatidaService.class);
    }

    @Test
    public void momentoBatidaServiceTest$InserirBatidaSemExistirAnteriorNoMesmoDia() {

        LocalDateTime localDateTime = this.formatarData("2022-01-01T08:00:00");
        MomentoBatidaDTO momentoBatidaDTO = this.montarMomentoBatidaDTO(localDateTime);

        String dia = String.valueOf(localDateTime.getDayOfMonth());
        String mes = String.valueOf(localDateTime.getMonthValue());
        String ano = String.valueOf(localDateTime.getYear());

        List<MomentoBatida> emptyList = new ArrayList<>();
        doReturn(emptyList).when(momentoBatidaService).findBatidasByData(dia, mes, ano);
        MomentoBatida momentoBatidaParaRetornar = this.montarMomentoBatidaFromMomentoBatidaDTO(momentoBatidaDTO);
        doReturn(momentoBatidaParaRetornar).when(momentoBatidaService).persistMomentoBatida(momentoBatidaDTO);

        RegistroJSON registroJSON = momentoBatidaService.insereBatida(momentoBatidaDTO);

        Assert.assertEquals(registroJSON.getDia(), localDateTime.toLocalDate());
        Assert.assertEquals(registroJSON.getHorarios().size(), 1);
        Assert.assertEquals(registroJSON.getHorarios().get(0), localDateTime.format(DateTimeFormatter.ofPattern(RESPONSE_TIME_FORMAT)));

    }

    @Test
    public void momentoBatidaServiceTest$InserirBatidaExistindoUmaAnterior() {
        LocalDateTime localDateTime = this.formatarData("2022-01-01T08:00:00");
        MomentoBatidaDTO momentoBatidaDTO = this.montarMomentoBatidaDTO(localDateTime);

        LocalDateTime localDateTime2 = this.formatarData("2022-01-01T12:00:00");
        MomentoBatidaDTO momentoBatidaDTO2 = this.montarMomentoBatidaDTO(localDateTime2);

        List<MomentoBatida> returnList = new ArrayList<>();
        returnList.add(this.montarMomentoBatidaFromMomentoBatidaDTO(momentoBatidaDTO));
        doReturn(returnList).when(momentoBatidaService).findBatidasByData(
                String.valueOf(localDateTime.getDayOfMonth()),
                String.valueOf(localDateTime.getMonthValue()),
                String.valueOf(localDateTime.getYear())
        );
        MomentoBatida momentoBatidaParaRetornar = this.montarMomentoBatidaFromMomentoBatidaDTO(momentoBatidaDTO2);
        doReturn(momentoBatidaParaRetornar).when(momentoBatidaService).persistMomentoBatida(momentoBatidaDTO2);


        RegistroJSON registroJSON = momentoBatidaService.insereBatida(momentoBatidaDTO2);

        Assert.assertEquals(registroJSON.getDia(), localDateTime.toLocalDate());
        Assert.assertEquals(registroJSON.getDia(), localDateTime2.toLocalDate());
        Assert.assertEquals(registroJSON.getHorarios().size(), 2);
        Assert.assertEquals(registroJSON.getHorarios().get(0), localDateTime.format(DateTimeFormatter.ofPattern(RESPONSE_TIME_FORMAT)));
        Assert.assertEquals(registroJSON.getHorarios().get(1), localDateTime2.format(DateTimeFormatter.ofPattern(RESPONSE_TIME_FORMAT)));
    }

    @Test(expected = ResponseStatusException.class)
    public void momentoBatidaServiceTest$IntervaloDeAlmocoInvalido() {
        LocalDateTime localDateTime = this.formatarData("2022-01-01T08:00:00");
        MomentoBatidaDTO momentoBatidaDTO = this.montarMomentoBatidaDTO(localDateTime);

        LocalDateTime localDateTime2 = this.formatarData("2022-01-01T12:00:00");
        MomentoBatidaDTO momentoBatidaDTO2 = this.montarMomentoBatidaDTO(localDateTime2);

        LocalDateTime localDateTime3 = this.formatarData("2022-01-01T12:30:00");
        MomentoBatidaDTO momentoBatidaDTO3 = this.montarMomentoBatidaDTO(localDateTime3);

        List<MomentoBatida> returnList = new ArrayList<>();
        returnList.add(this.montarMomentoBatidaFromMomentoBatidaDTO(momentoBatidaDTO));
        returnList.add(this.montarMomentoBatidaFromMomentoBatidaDTO(momentoBatidaDTO2));
        doReturn(returnList).when(momentoBatidaService).findBatidasByData(
                String.valueOf(localDateTime.getDayOfMonth()),
                String.valueOf(localDateTime.getMonthValue()),
                String.valueOf(localDateTime.getYear())
        );
        MomentoBatida momentoBatidaParaRetornar = this.montarMomentoBatidaFromMomentoBatidaDTO(momentoBatidaDTO3);

        RegistroJSON registroJSON = momentoBatidaService.insereBatida(momentoBatidaDTO3);
    }

    @Test(expected = ResponseStatusException.class)
    public void momentoBatidaServiceTest$ApenasQuatroHorariosViolado() {
        LocalDateTime localDateTime = this.formatarData("2022-01-01T08:00:00");
        MomentoBatidaDTO momentoBatidaDTO = this.montarMomentoBatidaDTO(localDateTime);

        LocalDateTime localDateTime2 = this.formatarData("2022-01-01T12:00:00");
        MomentoBatidaDTO momentoBatidaDTO2 = this.montarMomentoBatidaDTO(localDateTime2);

        LocalDateTime localDateTime3 = this.formatarData("2022-01-01T13:00:00");
        MomentoBatidaDTO momentoBatidaDTO3 = this.montarMomentoBatidaDTO(localDateTime3);

        LocalDateTime localDateTime4 = this.formatarData("2022-01-01T19:00:00");
        MomentoBatidaDTO momentoBatidaDTO4 = this.montarMomentoBatidaDTO(localDateTime4);

        LocalDateTime localDateTime5 = this.formatarData("2022-01-01T20:00:00");
        MomentoBatidaDTO momentoBatidaDTO5 = this.montarMomentoBatidaDTO(localDateTime5);

        List<MomentoBatida> returnList = new ArrayList<>();
        returnList.add(this.montarMomentoBatidaFromMomentoBatidaDTO(momentoBatidaDTO));
        returnList.add(this.montarMomentoBatidaFromMomentoBatidaDTO(momentoBatidaDTO2));
        returnList.add(this.montarMomentoBatidaFromMomentoBatidaDTO(momentoBatidaDTO3));
        returnList.add(this.montarMomentoBatidaFromMomentoBatidaDTO(momentoBatidaDTO4));
        doReturn(returnList).when(momentoBatidaService).findBatidasByData(
                String.valueOf(localDateTime.getDayOfMonth()),
                String.valueOf(localDateTime.getMonthValue()),
                String.valueOf(localDateTime.getYear())
        );
        MomentoBatida momentoBatidaParaRetornar = this.montarMomentoBatidaFromMomentoBatidaDTO(momentoBatidaDTO5);

        RegistroJSON registroJSON = momentoBatidaService.insereBatida(momentoBatidaDTO5);
    }

    private MomentoBatidaDTO montarMomentoBatidaDTO(LocalDateTime momento) {
        MomentoBatidaDTO momentoBatidaDTO = new MomentoBatidaDTO();
        momentoBatidaDTO.setMomento(momento);
        momentoBatidaDTO.setDia(String.valueOf(momento.getDayOfMonth()));
        momentoBatidaDTO.setMes(String.valueOf(momento.getMonthValue()));
        momentoBatidaDTO.setAno(String.valueOf(momento.getYear()));
        return momentoBatidaDTO;
    }

    private MomentoBatida montarMomentoBatidaFromMomentoBatidaDTO(MomentoBatidaDTO momentoBatidaDTO) {
        MomentoBatida momentoBatida = new MomentoBatida();
        momentoBatida.setDia(momentoBatidaDTO.getDia());
        momentoBatida.setMes(momentoBatidaDTO.getMes());
        momentoBatida.setAno(momentoBatidaDTO.getAno());
        momentoBatida.setTimestamp(momentoBatidaDTO.getMomento());
        momentoBatida.setId(0L);
        return momentoBatida;
    }

    private LocalDateTime formatarData(String data) {
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern(MomentoBatidaService.DEFAULT_DATE_FORMAT);
        return LocalDateTime.parse(data, dateFormat);
    }

}
