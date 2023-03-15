package com.ilia.folhadeponto.controller;

import com.ilia.folhadeponto.controller.validators.MomentoValidator;
import com.ilia.folhadeponto.model.MensagemJSON;
import com.ilia.folhadeponto.model.MomentoJSON;
import com.ilia.folhadeponto.model.RegistroJSON;
import com.ilia.folhadeponto.model.RelatorioJSON;
import com.ilia.folhadeponto.service.ControleDePontoAPIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.logging.Logger;

@RestController
@Controller
@Service
public class ControleDePontoAPIController  implements V1Api{

    @Autowired
    private ControleDePontoAPIService controleDePontoAPIService;
    @Autowired
    private MomentoValidator momentoValidator;
//    @Autowired
//    private RelatorioValidator relatorioValidator;

    @Override
    public ResponseEntity<RelatorioJSON> geraRelatorioMensal(String mes) {
        return ResponseEntity.ok(this.controleDePontoAPIService.gerarRelatorio(mes));
    }

    @Override
    public ResponseEntity<RegistroJSON> insereBatida(MomentoJSON momento) {
        this.momentoValidator.validate(momento);
        return ResponseEntity.ok(this.controleDePontoAPIService.insereBatida(momento));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<MensagemJSON> handleRequestException(ResponseStatusException responseStatusException) {
        MensagemJSON mensagemJSON = new MensagemJSON();
        mensagemJSON.setMensagem(responseStatusException.getReason());
        return ResponseEntity.status(responseStatusException.getStatus()).body(mensagemJSON);
    }

}
