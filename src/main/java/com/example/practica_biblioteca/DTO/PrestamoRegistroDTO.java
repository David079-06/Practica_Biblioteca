package com.example.practica_biblioteca.DTO;

import java.time.LocalDate;

public class PrestamoRegistroDTO {
    private LocalDate fechaLimite;
    private int plazoDias;

    public PrestamoRegistroDTO(LocalDate fechaLimite, int plazoDias) {
        this.fechaLimite = fechaLimite;
        this.plazoDias = plazoDias;
    }

    public LocalDate getFechaLimite() {
        return fechaLimite;
    }

    public int getPlazoDias() {
        return plazoDias;
    }
}
