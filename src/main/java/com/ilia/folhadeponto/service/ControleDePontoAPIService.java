package com.ilia.folhadeponto.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ilia.folhadeponto.dtos.MomentoBatidaDTO;
import com.ilia.folhadeponto.dtos.RelatorioDTO;
import com.ilia.folhadeponto.model.MomentoJSON;
import com.ilia.folhadeponto.model.RegistroJSON;
import com.ilia.folhadeponto.model.RelatorioJSON;

/**
 * Serviço responsável por
 */
@Service
public class ControleDePontoAPIService {

    @Autowired
    private MomentoBatidaService momentoBatidaService;

    @Autowired
    private RelatorioService relatorioService;


    public RelatorioJSON gerarRelatorio(String mes) {
        RelatorioDTO relatorioDTO = this.relatorioService.prepararDTO(mes);
        return relatorioService.gerarRelatorio(relatorioDTO);
    }

    public RegistroJSON insereBatida(MomentoJSON momento) {
        MomentoBatidaDTO momentoBatidaPersistenceInfoDTO = this.momentoBatidaService.prepararDTO(momento);
        return this.momentoBatidaService.insereBatida(momentoBatidaPersistenceInfoDTO);
    }
}
