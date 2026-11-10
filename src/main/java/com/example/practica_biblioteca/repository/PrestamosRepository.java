package com.example.practica_biblioteca.repository;

import com.example.practica_biblioteca.DTO.PrestamosDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

@Repository
public class PrestamosRepository {
    private static final Logger logger = LoggerFactory.getLogger(PrestamosRepository.class);
    private final DataSource dataSource;

    public PrestamosRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public ArrayList<PrestamosDTO> buscarPrestamos(String nombre, String titulo,LocalDate fechaDesde) {
        String sql = "SELECT pr.* FROM prestamos pr "
                + "JOIN socios s ON pr.id_socio = s.id "
                + "JOIN copias c ON pr.id_copia = c.id "
                + "JOIN videojuegos v ON c.id_videojuego = v.id "
                + "WHERE CONCAT(s.nombre, ' ', s.apellidos) LIKE ? "
                + "AND v.titulo LIKE ?";
        if (fechaDesde != null) {
            sql += " AND pr.fecha_prestamo >= ?";
        }
        sql += " ORDER BY pr.fecha_prestamo DESC";

        ArrayList<PrestamosDTO> listaPrestamos = new ArrayList<>();
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            if (nombre == null) nombre = "";
            if (titulo == null) titulo = "";
            ps.setString(1, "%" + nombre + "%");
            ps.setString(2, "%" + titulo + "%");
            if (fechaDesde != null) {
                ps.setDate(3, Date.valueOf(fechaDesde));
            }
            logger.info("Query a ejecutar: {}", ps);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                listaPrestamos.add(mapearPrestamos(rs));
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            logger.error("Error al buscar préstamos", e);
        } finally {
            cerrarConexion(connection);
        }
        return listaPrestamos;
    }

    public ArrayList<PrestamosDTO> buscarPrestamosAbiertos(String nombre) {
        String sql = "SELECT pr.* FROM prestamos pr "
                + "JOIN socios s ON pr.id_socio = s.id "
                + "WHERE pr.fecha_devolucion IS NULL "
                + "AND CONCAT(s.nombre, ' ', s.apellidos) LIKE ? "
                + "ORDER BY pr.fecha_prestamo DESC";
        ArrayList<PrestamosDTO> listaPrestamos = new ArrayList<>();
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            if (nombre == null) nombre = "";
            ps.setString(1, "%" + nombre + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                listaPrestamos.add(mapearPrestamos(rs));
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            logger.error("Error al buscar préstamos abiertos", e);
        } finally {
            cerrarConexion(connection);
        }
        return listaPrestamos;
    }

    public PrestamosDTO buscarPrestamoPorId(int idPrestamo) {
        String sql = "SELECT * FROM prestamos WHERE id = ?";
        PrestamosDTO prestamo = null;
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, idPrestamo);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                prestamo = mapearPrestamos(rs);
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            logger.error("Error al buscar préstamo por ID", e);
        } finally {
            cerrarConexion(connection);
        }
        return prestamo;
    }

    private PrestamosDTO mapearPrestamos(ResultSet rs) throws SQLException {
        return new PrestamosDTO(
                rs.getInt("id"),
                rs.getInt("id_socio"),
                rs.getInt("id_copia"),
                rs.getDate("fecha_prestamo").toLocalDate(),
                rs.getDate("fecha_limite").toLocalDate(),
                rs.getDate("fecha_devolucion").toLocalDate(),
                rs.getDouble("multa")
        );
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
