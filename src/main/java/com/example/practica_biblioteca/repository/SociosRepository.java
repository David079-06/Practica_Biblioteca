package com.example.practica_biblioteca.repository;

import com.example.practica_biblioteca.DTO.DesarrolladorDTO;
import com.example.practica_biblioteca.DTO.SociosDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class SociosRepository {
    private static final Logger logger = LoggerFactory.getLogger(SociosRepository.class);
    private final DataSource dataSource;

    public SociosRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public ArrayList<SociosDTO> buscarSocios(String nombreApellidos, String email, Boolean activo) {
        String sql = "SELECT * FROM socios "
                + "WHERE CONCAT(nombre, ' ', apellidos) LIKE ? AND email LIKE ?";
        if (activo != null) {
            sql += " AND activo = ?";
        }
        sql += " ORDER BY nombre, apellidos";

        ArrayList<SociosDTO> listaSocios = new ArrayList<>();
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            if (nombreApellidos == null) nombreApellidos = "";
            if (email == null) email = "";
            ps.setString(1, "%" + nombreApellidos + "%");
            ps.setString(2, "%" + email + "%");
            if (activo != null) {
                ps.setBoolean(3, activo);
            }
            logger.info("Query a ejecutar: {}", ps);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                listaSocios.add(mapearSocios(rs));
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            logger.error("Error al buscar socios", e);
        } finally {
            cerrarConexion(connection);
        }
        return listaSocios;
    }
    private SociosDTO mapearSocios(ResultSet rs) throws SQLException {
        return new SociosDTO(
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getString("apellidos"),
                rs.getString("email"),
                rs.getDate("fecha_nacimiento").toLocalDate(),
                rs.getDate("fecha_alta").toLocalDate(),
                rs.getBoolean("activo")
        );
    }

    private void cerrarConexion(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                logger.error("Error al cerrar la conexión", e);
            }
        }
    }
}
