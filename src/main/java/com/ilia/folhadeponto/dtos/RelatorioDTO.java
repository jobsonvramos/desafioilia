package com.ilia.folhadeponto.dtos;

import java.time.LocalDate;
import java.util.Objects;

public class RelatorioDTO {

    private String mesEAno;

    public String getMesEAno() {
        return mesEAno;
    }

    public void setMesEAno(String mesEAno) {
        this.mesEAno = mesEAno;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RelatorioDTO that = (RelatorioDTO) o;
        return Objects.equals(getMesEAno(), that.getMesEAno());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMesEAno());
    }
}
