package com.example.practica_biblioteca.repository;

import com.example.practica_biblioteca.DTO.DesarrolladorDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

@Repository
public class DesarrolladoresRepository {
    private static final Logger logger = LoggerFactory.getLogger(DesarrolladoresRepository.class);
    private final DataSource dataSource;

    public DesarrolladoresRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public ArrayList<DesarrolladorDTO> buscarDesarroladores(String nombre, String pais) {
        String sql = "SELECT id, nombre, pais FROM desarrolador" +
                "WHERE nombre LIKE ? ORDER BY nombre";

        ArrayList<DesarrolladorDTO> listaDesarrolladoras = new ArrayList<>();
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            if (nombre == null) nombre = "";
            ps.setString(1, "%" + nombre + "%");
            logger.info("Query a ejecutar: {}", ps);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                listaDesarrolladoras.add(mapearDesarrolladora(rs));
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            logger.error("Error al buscar desarrolladoras", e);
        } finally {
            cerrarConexion(connection);
        }
        return listaDesarrolladoras;
    }

    private DesarrolladorDTO mapearDesarrolladora(ResultSet rs) throws SQLException {
        return new DesarrolladorDTO(
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getString("pais")
        );
    }
    public DesarrolladorDTO buscarDesarrolladoraPorId(int idDesarrolladora) {
        String sql = "SELECT id, nombre, pais FROM desarrolladoras WHERE id = ?";
        DesarrolladorDTO desarrolladora = null;
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, idDesarrolladora);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                desarrolladora = mapearDesarrolladora(rs);
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            logger.error("Error al buscar desarrolladora por ID",idDesarrolladora, e);
        } finally {
            cerrarConexion(connection);
        }
        return desarrolladora;
    }

    private void cerrarConexion(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}