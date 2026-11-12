package com.example.practica_biblioteca.dao.interfaz;

import com.example.practica_biblioteca.DTO.SociosDTO;
import java.util.ArrayList;

public interface ISociosDAO {
    ArrayList<SociosDTO> buscarSocios(String nombre, String email, Boolean activo);
    SociosDTO buscarSocioPorId(int idSocio);
    boolean existeEmail(String email);
    int insertarSocio(SociosDTO socio);
    int contarSociosActivos();
}
