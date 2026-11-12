package com.example.practica_biblioteca.repository;

import com.example.practica_biblioteca.DTO.DesarrolladorDTO;
import com.example.practica_biblioteca.DTO.SociosDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

@Repository
public class SociosRepository {
    private static final Logger logger = LoggerFactory.getLogger(SociosRepository.class);
    private final DataSource dataSource;

    public SociosRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

   public ArrayList<SociosDTO> buscarSocios(String nombre, String email, Boolean activo) {
        String sql = "SELECT id, nombre, apellidos, email, fecha_nacimiento, fecha_alta, activo "
                + "FROM socios WHERE 1 = 1";

        if (nombre != null && !nombre.trim().isEmpty()) {
            sql = sql + " AND CONCAT(nombre, ' ', apellidos) LIKE ?";
        }
        if (email != null && !email.trim().isEmpty()) {
            sql = sql + " AND email LIKE ?";
        }
        if (activo != null) {
            sql = sql + " AND activo = ?";
        }
        sql = sql + " ORDER BY apellidos, nombre";

        ArrayList<SociosDTO> listaSocios = new ArrayList<>();
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            int numeroParametro = 1;
            if (nombre != null && !nombre.trim().isEmpty()) {
                ps.setString(numeroParametro, "%" + nombre + "%");
                numeroParametro++;
            }
            if (email != null && !email.trim().isEmpty()) {
                ps.setString(numeroParametro, "%" + email + "%");
                numeroParametro++;
            }
            if (activo != null) {
                ps.setBoolean(numeroParametro, activo);
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

    private SociosDTO mapearSocio(ResultSet rs) throws SQLException {
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
    public SociosDTO buscarSocioPorId(int idSocio) {
        String sql = "SELECT id, nombre, apellidos, email, fecha_nacimiento, fecha_alta, activo "
                + "FROM socios WHERE id = ?";
        SociosDTO socio = null;
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, idSocio);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                socio = mapearSocio(rs);
            }
            rs.close();
            ps.close();
            connection.close();
        } catch (SQLException e) {
            logger.error("Error al buscar socio por ID", e);
            cerrarConexion(connection);
        }
        return socio;
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
    public boolean existeEmail(String email) {
        String sql = "SELECT id FROM socios WHERE email = ?";
        boolean existe = false;
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            existe = rs.next();
            rs.close();
            ps.close();
            connection.close();
        } catch (SQLException e) {
            logger.error("Error al comprobar email", e);
            cerrarConexion(connection);
        }
        return existe;
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
