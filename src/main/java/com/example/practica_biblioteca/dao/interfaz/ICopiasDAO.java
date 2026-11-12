package com.example.practica_biblioteca.dao.interfaz;

import com.example.practica_biblioteca.DTO.CopiasDTO;
import com.example.practica_biblioteca.DTO.CopiaDisponibleDTO;
import java.util.ArrayList;

public interface ICopiasDAO {
    ArrayList<CopiaDisponibleDTO> buscarCopiasDisponibles();
    CopiasDTO buscarCopiaPorId(int idCopia);
    boolean estaDisponible(int idCopia);
}
