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

    public ArrayList<SociosDTO> buscarSocios(String nombre, String email, Boolean activo) {
        StringBuilder sql = new StringBuilder(
                "SELECT id, nombre, apellidos, email, fecha_nacimiento, fecha_alta, activo "
                        + "FROM socios WHERE 1 = 1");
        ArrayList<Object> parametros = new ArrayList<>();

        if (nombre != null && !nombre.isBlank()) {
            sql.append(" AND CONCAT(nombre, ' ', apellidos) LIKE ?");
            parametros.add("%" + nombre + "%");
        }
        if (email != null && !email.isBlank()) {
            sql.append(" AND email LIKE ?");
            parametros.add("%" + email + "%");
        }
        if (activo != null) {
            sql.append(" AND activo = ?");
            parametros.add(activo);
        }
        sql.append(" ORDER BY apellidos, nombre");

        ArrayList<SociosDTO> listaSocios = new ArrayList<>();
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql.toString());
            for (int i = 0; i < parametros.size(); i++) {
                ps.setObject(i + 1, parametros.get(i));
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                listaSocios.add(mapearSocio(rs));
            }
            rs.close();
            ps.close();
            connection.close();
        } catch (SQLException e) {
            logger.error("Error al buscar socios", e);
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
    public int insertarSocio(SociosDTO socio) {
        String sql = "INSERT INTO socios (nombre, apellidos, email, fecha_nacimiento, fecha_alta, activo) "
                + "VALUES (?, ?, ?, ?, CURDATE(), 1)";
        int filasAfectadas = 0;
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, socio.getNombre());
            ps.setString(2, socio.getApellidos());
            ps.setString(3, socio.getEmail());
            ps.setDate(4, Date.valueOf(socio.getFechaNacimiento()));
            filasAfectadas = ps.executeUpdate();
            ps.close();
            connection.close();
        } catch (SQLException e) {
            logger.error("Error al insertar socio", e);
            cerrarConexion(connection);
        }
        return filasAfectadas;
    }
    public int contarSociosActivos() {
        String sql = "SELECT COUNT(*) AS total FROM socios WHERE activo = 1";
        int total = 0;
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                total = rs.getInt("total");
            }
            rs.close();
            ps.close();
            connection.close();
        } catch (SQLException e) {
            logger.error("Error al contar socios activos", e);
            cerrarConexion(connection);
        }
        return total;
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
