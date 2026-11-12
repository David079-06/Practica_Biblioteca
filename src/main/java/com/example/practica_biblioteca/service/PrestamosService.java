package com.example.practica_biblioteca.service;

import com.example.practica_biblioteca.DTO.CopiasDTO;
import com.example.practica_biblioteca.DTO.CopiaDisponibleDTO;
import com.example.practica_biblioteca.DTO.PrestamosDTO;
import com.example.practica_biblioteca.DTO.PrestamoListadoDTO;
import com.example.practica_biblioteca.DTO.PrestamoRegistroDTO;
import com.example.practica_biblioteca.DTO.SociosDTO;
import com.example.practica_biblioteca.DTO.VideojuegosDTO;
import com.example.practica_biblioteca.dao.interfaz.ICopiasDAO;
import com.example.practica_biblioteca.dao.interfaz.IPrestamosDAO;
import com.example.practica_biblioteca.dao.interfaz.ISociosDAO;
import com.example.practica_biblioteca.dao.interfaz.IVideojuegosDAO;
import com.example.practica_biblioteca.excepcion.ReglaNegocioException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

@Service
public class PrestamosService {
    private final IPrestamosDAO prestamosDAO;
    private final ISociosDAO sociosDAO;
    private final ICopiasDAO copiasDAO;
    private final IVideojuegosDAO videojuegosDAO;

    public PrestamosService(IPrestamosDAO prestamosDAO, ISociosDAO sociosDAO,
                            ICopiasDAO copiasDAO, IVideojuegosDAO videojuegosDAO) {
        this.prestamosDAO = prestamosDAO;
        this.sociosDAO = sociosDAO;
        this.copiasDAO = copiasDAO;
        this.videojuegosDAO = videojuegosDAO;
    }

    public ArrayList<PrestamoListadoDTO> buscarPrestamos(String nombre, String titulo,
                                                          String estado, LocalDate fechaDesde) {
        ArrayList<PrestamoListadoDTO> listaPrestamos = prestamosDAO.buscarPrestamos(
                nombre, titulo, fechaDesde);
        ArrayList<PrestamoListadoDTO> listaFiltrada = new ArrayList<>();

        for (PrestamoListadoDTO fila : listaPrestamos) {
            calcularEstado(fila);
            if (estado == null || estado.trim().isEmpty() || estado.equals("TODOS")
                    || estado.equals(fila.getEstado())) {
                listaFiltrada.add(fila);
            }
        }
        return listaFiltrada;
    }

    public ArrayList<PrestamoListadoDTO> buscarPrestamosAbiertos(String nombre) {
        ArrayList<PrestamoListadoDTO> listaPrestamos = prestamosDAO.buscarPrestamosAbiertos(nombre);
        for (PrestamoListadoDTO fila : listaPrestamos) {
            calcularEstado(fila);
        }
        return listaPrestamos;
    }

    private void calcularEstado(PrestamoListadoDTO fila) {
        PrestamosDTO prestamo = fila.getPrestamo();
        if (prestamo.getFechaDevolucion() != null) {
            fila.setEstado("DEVUELTO");
            fila.setDiasRetraso(0);
        } else if (prestamo.getFechaLimite().isBefore(LocalDate.now())) {
            fila.setEstado("VENCIDO");
            int diasRetraso = (int) ChronoUnit.DAYS.between(
                    prestamo.getFechaLimite(), LocalDate.now());
            fila.setDiasRetraso(diasRetraso);
        } else {
            fila.setEstado("EN PLAZO");
            fila.setDiasRetraso(0);
        }
    }

    public ArrayList<SociosDTO> buscarSociosActivos() {
        return sociosDAO.buscarSocios(null, null, true);
    }

    public ArrayList<CopiaDisponibleDTO> buscarCopiasDisponibles() {
        return copiasDAO.buscarCopiasDisponibles();
    }

    public PrestamoRegistroDTO registrarPrestamo(int idSocio, int idCopia) {
        // RN-1: el socio debe existir y estar activo.
        SociosDTO socio = sociosDAO.buscarSocioPorId(idSocio);
        if (socio == null || !socio.isActiva()) {
            throw new ReglaNegocioException("RN-1: El socio debe estar activo.");
        }

        // RN-2: la copia debe estar activa y no tener préstamo abierto.
        CopiasDTO copia = copiasDAO.buscarCopiaPorId(idCopia);
        if (copia == null || !copiasDAO.estaDisponible(idCopia)) {
            throw new ReglaNegocioException("RN-2: La copia no está disponible.");
        }

        // RN-3: no se pueden tener más de tres préstamos abiertos.
        if (prestamosDAO.contarPrestamosAbiertos(idSocio) >= 3) {
            throw new ReglaNegocioException("RN-3: El socio ya tiene tres préstamos abiertos.");
        }

        VideojuegosDTO videojuego = videojuegosDAO.buscarVideojuegoPorId(copia.getIdVideojuego());
        if (videojuego == null) {
            throw new ReglaNegocioException("No se ha encontrado el videojuego de la copia.");
        }

        // RN-4: la edad debe ser igual o superior al PEGI.
        int edad = Period.between(socio.getFechaNacimiento(), LocalDate.now()).getYears();
        if (edad < videojuego.getPegi()) {
            throw new ReglaNegocioException("RN-4: El socio no cumple la edad PEGI del videojuego.");
        }

        // RN-5: no puede tener préstamos vencidos sin devolver.
        if (prestamosDAO.contarPrestamosVencidos(idSocio) > 0) {
            throw new ReglaNegocioException("RN-5: El socio tiene un préstamo vencido sin devolver.");
        }

        int plazo = 15;
        int anioActual = LocalDate.now().getYear();
        if (videojuego.getAnioLanzamiento() == anioActual
                || videojuego.getAnioLanzamiento() == anioActual - 1) {
            plazo = 7;
        }
        LocalDate fechaLimite = LocalDate.now().plusDays(plazo);

        PrestamosDTO prestamo = new PrestamosDTO(0, idSocio, idCopia,
                LocalDate.now(), fechaLimite, null, 0);
        if (prestamosDAO.registrarPrestamo(prestamo) != 1) {
            throw new ReglaNegocioException("No se ha podido registrar el préstamo.");
        }
        return new PrestamoRegistroDTO(fechaLimite, plazo);
    }

    public double devolverPrestamo(int idPrestamo, boolean copiaDanada) {
        PrestamosDTO prestamo = prestamosDAO.buscarPrestamoPorId(idPrestamo);
        if (prestamo == null || prestamo.getFechaDevolucion() != null) {
            throw new ReglaNegocioException("El préstamo no existe o ya está devuelto.");
        }

        double multa = 0;
        if (LocalDate.now().isAfter(prestamo.getFechaLimite())) {
            int diasRetraso = (int) ChronoUnit.DAYS.between(
                    prestamo.getFechaLimite(), LocalDate.now());
            multa = diasRetraso * 0.50;
        }

        if (!prestamosDAO.devolverPrestamo(idPrestamo, prestamo.getIdCopia(), multa, copiaDanada)) {
            throw new ReglaNegocioException("No se ha podido devolver el préstamo.");
        }
        return multa;
    }

    public int contarPrestamosAbiertos() {
        return prestamosDAO.contarPrestamosAbiertos();
    }

    public int contarPrestamosVencidos() {
        return prestamosDAO.contarPrestamosVencidos();
    }
}
