package com.example.practica_biblioteca.dao.impl;

import com.example.practica_biblioteca.DTO.PlataformasDTO;
import com.example.practica_biblioteca.dao.interfaz.IPlataformasDAO;
import com.example.practica_biblioteca.repository.PlataformasRepository;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;

@Repository
public class PlataformasDAO implements IPlataformasDAO {
    private final PlataformasRepository plataformasRepository;
    public PlataformasDAO(PlataformasRepository plataformasRepository) {
        this.plataformasRepository = plataformasRepository;
    }
    @Override
    public ArrayList<PlataformasDTO> buscarPlataformas() {
        return plataformasRepository.buscarPlataformas();
    }
}
