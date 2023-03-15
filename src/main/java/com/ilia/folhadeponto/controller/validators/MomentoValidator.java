package com.ilia.folhadeponto.controller.validators;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.ilia.folhadeponto.messages.RequestErrorMessages;
import com.ilia.folhadeponto.model.MomentoJSON;
import com.ilia.folhadeponto.service.MomentoBatidaService;

@Service
public class MomentoValidator implements GenericValidator<MomentoJSON>{

    @Autowired
    private MomentoBatidaService momentoBatidaService;

    @Override
    public void validate(MomentoJSON momentoJSON) {

        if (momentoJSON.getDataHora() == null || momentoJSON.getDataHora().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, RequestErrorMessages.CAMPO_OBRIGATORIO_NAO_INFORMADO);
        }

        LocalDateTime dataMomento;

        try {
            DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern(MomentoBatidaService.DEFAULT_DATE_FORMAT);
            dataMomento = LocalDateTime.parse(momentoJSON.getDataHora(), dateFormat);
        } catch (DateTimeParseException dateTimeParseException) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, RequestErrorMessages.DATA_E_HORA_EM_FORMATO_INVALIDO);
        }

        if (dataMomento.getDayOfWeek() == DayOfWeek.SATURDAY || dataMomento.getDayOfWeek() == DayOfWeek.SUNDAY) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, RequestErrorMessages.SABADO_E_DOMINGO_NAO_PERMITIDOS);
        }

        // mensagem: Apenas 4 horários podem ser registrados por dia

        // mensagem: Deve haver no mínimo 1 hora de almoço

    }
}
