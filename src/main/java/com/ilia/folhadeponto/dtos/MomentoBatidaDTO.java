package com.ilia.folhadeponto.dtos;

import java.time.LocalDateTime;
import java.util.Objects;

public class MomentoBatidaDTO {


    private LocalDateTime momento;

    private String dia;

    private String mes;

    private String ano;

    public LocalDateTime getMomento() {
        return momento;
    }

    public void setMomento(LocalDateTime momento) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MomentoBatidaDTO that = (MomentoBatidaDTO) o;
        return Objects.equals(getMomento(), that.getMomento()) && Objects.equals(getDia(), that.getDia()) && Objects.equals(getMes(), that.getMes()) && Objects.equals(getAno(), that.getAno());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMomento(), getDia(), getMes(), getAno());
    }
}
