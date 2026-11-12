package com.example.practica_biblioteca.dao.interfaz;

import com.example.practica_biblioteca.DTO.PrestamosDTO;
import com.example.practica_biblioteca.DTO.PrestamoListadoDTO;
import java.time.LocalDate;
import java.util.ArrayList;

public interface IPrestamosDAO {
    ArrayList<PrestamoListadoDTO> buscarPrestamos(String nombre, String titulo,
                                                   LocalDate fechaPrestamoDesde);
    ArrayList<PrestamoListadoDTO> buscarPrestamosAbiertos(String nombre);
    PrestamosDTO buscarPrestamoPorId(int idPrestamo);
    int registrarPrestamo(PrestamosDTO prestamo);
    int contarPrestamosAbiertos(int idSocio);
    int contarPrestamosVencidos(int idSocio);
    int contarPrestamosAbiertos();
    int contarPrestamosVencidos();
    boolean devolverPrestamo(int idPrestamo, int idCopia, double multa, boolean copiaDanada);
}
