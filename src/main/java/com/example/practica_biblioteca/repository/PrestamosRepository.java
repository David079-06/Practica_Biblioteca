package com.example.practica_biblioteca.repository;

import com.example.practica_biblioteca.DTO.PrestamosDTO;
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
public class PrestamosRepository {
    private static final Logger logger = LoggerFactory.getLogger(PrestamosRepository.class);
    private final DataSource dataSource;

    public PrestamosRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public ArrayList<PrestamosDTO> buscarPrestamos(int id_socio) {
        String sql = String sql = "SELECT pr.*, "
                + "CONCAT(s.nombre, ' ', s.apellidos) AS socio, "
                + "v.titulo AS videojuego, p.nombre AS plataforma, "
                + "CASE WHEN pr.fecha_devolucion IS NOT NULL THEN 'DEVUELTO' "
                + "WHEN pr.fecha_limite < CURDATE() THEN 'VENCIDO' "
                + "ELSE 'EN PLAZO' END AS estado, "
                + "CASE WHEN pr.fecha_devolucion IS NULL AND pr.fecha_limite < CURDATE() "
                + "THEN DATEDIFF(CURDATE(), pr.fecha_limite) ELSE 0 END AS dias_retraso "
                + "FROM prestamos pr "
                + "JOIN socios s ON pr.id_socio = s.id "
                + "JOIN copias c ON pr.id_copia = c.id "
                + "JOIN videojuegos v ON c.id_videojuego = v.id "
                + "JOIN plataformas p ON c.id_plataforma = p.id "
                + "WHERE CONCAT(s.nombre, ' ', s.apellidos) LIKE ? "
                + "AND v.titulo LIKE ?";

        ArrayList<PrestamosDTO> listaPrestamos = new ArrayList<>();
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, id_socio);

            logger.info("Query a ejecutar: {}", ps);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                listaPrestamos.add(mapearPrestamos(rs));
            }
            connection.close();
        } catch (SQLException e) {
            logger.error("Error al buscar prestamos", e);
            cerrarConexion(connection);
        }
        return listaPrestamos;
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
