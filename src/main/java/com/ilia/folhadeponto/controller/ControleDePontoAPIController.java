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

    /**
     * GET /v1/folhas-de-ponto/{mes} : Relatório mensal
     * Geração de relatório mensal de usuário.
     *
     * @param mes  (required)
     * @return Relatório mensal (status code 200)
     *         or Relatório não encontrado (status code 404)
     */
    @Override
    public ResponseEntity<RelatorioJSON> geraRelatorioMensal(String mes) {
        return ResponseEntity.ok(this.controleDePontoAPIService.gerarRelatorio(mes));
    }

    /**
     * POST /v1/batidas : Bater ponto
     * Registrar um horário da jornada diária de trabalho
     *
     * @param momentoJSON  (optional)
     * @return Created  (status code 201)
     *         or Bad Request  (status code 400)
     *         or Forbidden  (status code 403)
     *         or Conflict  (status code 409)
     */
    @Override
    public ResponseEntity<RegistroJSON> insereBatida(MomentoJSON momentoJSON) {
        this.momentoValidator.validate(momentoJSON);
        return ResponseEntity.ok(this.controleDePontoAPIService.insereBatida(momentoJSON));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<MensagemJSON> handleRequestException(ResponseStatusException responseStatusException) {
        MensagemJSON mensagemJSON = new MensagemJSON();
        mensagemJSON.setMensagem(responseStatusException.getReason());
        return ResponseEntity.status(responseStatusException.getStatus()).body(mensagemJSON);
    }

}
