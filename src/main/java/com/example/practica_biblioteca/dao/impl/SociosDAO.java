package com.example.practica_biblioteca.dao.impl;

import com.example.practica_biblioteca.DTO.SociosDTO;
import com.example.practica_biblioteca.dao.interfaz.ISociosDAO;
import com.example.practica_biblioteca.repository.SociosRepository;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;

@Repository
public class SociosDAO implements ISociosDAO {
    private final SociosRepository sociosRepository;
    public SociosDAO(SociosRepository sociosRepository) {
        this.sociosRepository = sociosRepository;
    }
    @Override
    public ArrayList<SociosDTO> buscarSocios(String nombre, String email, Boolean activo) {
        return sociosRepository.buscarSocios(nombre, email, activo);
    }
    @Override
    public SociosDTO buscarSocioPorId(int idSocio) {
        return sociosRepository.buscarSocioPorId(idSocio);
    }
    @Override
    public boolean existeEmail(String email) {
        return sociosRepository.existeEmail(email);
    }
    @Override
    public int insertarSocio(SociosDTO socio) {
        return sociosRepository.insertarSocio(socio);
    }
    @Override
    public int contarSociosActivos() {
        return sociosRepository.contarSociosActivos();
    }
}
