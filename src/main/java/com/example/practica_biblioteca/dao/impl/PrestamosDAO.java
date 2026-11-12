package com.example.practica_biblioteca.dao.impl;

import com.example.practica_biblioteca.DTO.PrestamosDTO;
import com.example.practica_biblioteca.DTO.PrestamoListadoDTO;
import com.example.practica_biblioteca.dao.interfaz.IPrestamosDAO;
import com.example.practica_biblioteca.repository.PrestamosRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.ArrayList;

@Repository
public class PrestamosDAO implements IPrestamosDAO {
    private final PrestamosRepository prestamosRepository;
    public PrestamosDAO(PrestamosRepository prestamosRepository) {
        this.prestamosRepository = prestamosRepository;
    }
    @Override
    public ArrayList<PrestamoListadoDTO> buscarPrestamos(String nombre, String titulo,
                                                          LocalDate fechaPrestamoDesde) {
        return prestamosRepository.buscarPrestamos(nombre, titulo, fechaPrestamoDesde);
    }
    @Override
    public ArrayList<PrestamoListadoDTO> buscarPrestamosAbiertos(String nombre) {
        return prestamosRepository.buscarPrestamosAbiertos(nombre);
    }
    @Override
    public PrestamosDTO buscarPrestamoPorId(int idPrestamo) {
        return prestamosRepository.buscarPrestamoPorId(idPrestamo);
    }
    @Override
    public int registrarPrestamo(PrestamosDTO prestamo) {
        return prestamosRepository.registrarPrestamo(prestamo);
    }
    @Override
    public int contarPrestamosAbiertos(int idSocio) {
        return prestamosRepository.contarPrestamosAbiertos(idSocio);
    }
    @Override
    public int contarPrestamosVencidos(int idSocio) {
        return prestamosRepository.contarPrestamosVencidos(idSocio);
    }
    @Override
    public int contarPrestamosAbiertos() {
        return prestamosRepository.contarPrestamosAbiertos();
    }
    @Override
    public int contarPrestamosVencidos() {
        return prestamosRepository.contarPrestamosVencidos();
    }
    @Override
    public boolean devolverPrestamo(int idPrestamo, int idCopia, double multa, boolean copiaDanada) {
        return prestamosRepository.devolverPrestamo(idPrestamo, idCopia, multa, copiaDanada);
    }
}
