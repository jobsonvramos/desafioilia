package com.ilia.folhadeponto.entity;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "MOMENTO_BATIDA")
@NamedQueries({
    @NamedQuery(
        name = MomentoBatida.FIND_MOMENTO_BATIDA_BY_DATE,
        query = MomentoBatida.QUERY_FIND_MOMENTO_BATIDA_BY_DATE
    ),
    @NamedQuery(
        name = MomentoBatida.FIND_ALL_MOMENTO_BY_MES_AND_ANO,
        query = MomentoBatida.QUERY_FIND_ALL_MOMENTO_BY_MES_AND_ANO
    )
})
public class MomentoBatida {

    public static final String FIND_MOMENTO_BATIDA_BY_DATE
                = "find_momento_batida_by_date";
    public static final String QUERY_FIND_MOMENTO_BATIDA_BY_DATE
                = "SELECT mb FROM MomentoBatida mb " +
                "WHERE mb.dia = :dia AND mb.mes = :mes AND mb.ano = :ano " +
                "ORDER BY mb.momento ASC";
    public static final String FIND_ALL_MOMENTO_BY_MES_AND_ANO
                = "find_all_momento_by_mes_and_ano";
    public static final String QUERY_FIND_ALL_MOMENTO_BY_MES_AND_ANO
                = "FROM MomentoBatida mb WHERE mb.mes=:mes AND mb.ano=:ano " +
                "ORDER BY mb.momento ASC";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "momento")
    private LocalDateTime momento;
    @Column(name = "dia")
    private String dia;
    @Column(name = "mes")
    private String mes;
    @Column(name = "ano")
    private String ano;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public LocalDateTime getTimestamp() {
        return momento;
    }

    public void setTimestamp(LocalDateTime momento) {
        this.momento = momento;
    }

    public String getDia() {
        return dia;
    }

    public void setDia(String dia) {
        this.dia = dia;
    }

    public String getMes() {
        return mes;
    }

    public void setMes(String mes) {
        this.mes = mes;
    }

    public String getAno() {
        return ano;
    }

    public void setAno(String ano) {
        this.ano = ano;
    }
}
