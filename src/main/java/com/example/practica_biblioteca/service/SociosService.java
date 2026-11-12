package com.example.practica_biblioteca.service;

import com.example.practica_biblioteca.DTO.SociosDTO;
import com.example.practica_biblioteca.DTO.SocioListadoDTO;
import com.example.practica_biblioteca.dao.interfaz.ISociosDAO;
import com.example.practica_biblioteca.dao.interfaz.IPrestamosDAO;
import com.example.practica_biblioteca.excepcion.ReglaNegocioException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

@Service
public class SociosService {
    private final ISociosDAO sociosDAO;
    private final IPrestamosDAO prestamosDAO;

    public SociosService(ISociosDAO sociosDAO, IPrestamosDAO prestamosDAO) {
        this.sociosDAO = sociosDAO;
        this.prestamosDAO = prestamosDAO;
    }

    public ArrayList<SocioListadoDTO> buscarSocios(String nombre, String email, Boolean activo) {
        ArrayList<SocioListadoDTO> listaSocios = new ArrayList<>();
        for (SociosDTO socio : sociosDAO.buscarSocios(nombre, email, activo)) {
            int edad = Period.between(socio.getFechaNacimiento(), LocalDate.now()).getYears();
            int prestamosAbiertos = prestamosDAO.contarPrestamosAbiertos(socio.getId());
            listaSocios.add(new SocioListadoDTO(socio, edad, prestamosAbiertos));
        }
        return listaSocios;
    }

    public void insertarSocio(String nombre, String apellidos, String email, String fechaNacimiento) {
        if (nombre == null || nombre.trim().isEmpty() || apellidos == null || apellidos.trim().isEmpty()
                || email == null || email.trim().isEmpty()
                || fechaNacimiento == null || fechaNacimiento.trim().isEmpty()) {
            throw new ReglaNegocioException("Nombre, apellidos, email y fecha de nacimiento son obligatorios.");
        }

        LocalDate nacimiento;
        try {
            nacimiento = LocalDate.parse(fechaNacimiento);
        } catch (DateTimeParseException e) {
            throw new ReglaNegocioException("La fecha de nacimiento no es válida.");
        }

        if (nacimiento.isAfter(LocalDate.now())) {
            throw new ReglaNegocioException("La fecha de nacimiento no puede ser futura.");
        }
        if (!email.contains("@")) {
            throw new ReglaNegocioException("Introduce un email válido.");
        }
        if (sociosDAO.existeEmail(email.trim())) {
            throw new ReglaNegocioException("Ya existe un socio con ese email.");
        }

        SociosDTO socio = new SociosDTO(0, nombre.trim(), apellidos.trim(), email.trim(),
                nacimiento, LocalDate.now(), true);
        if (sociosDAO.insertarSocio(socio) != 1) {
            throw new ReglaNegocioException("No se ha podido insertar el socio.");
        }
    }

    public ArrayList<SociosDTO> buscarSociosActivos() {
        return sociosDAO.buscarSocios(null, null, true);
    }

    public int contarSociosActivos() {
        return sociosDAO.contarSociosActivos();
    }
}
