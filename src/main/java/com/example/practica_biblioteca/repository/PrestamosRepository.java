package com.example.practica_biblioteca.repository;

import com.example.practica_biblioteca.DTO.PrestamosDTO;
import com.example.practica_biblioteca.DTO.PrestamoListadoDTO;
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

    public ArrayList<PrestamoListadoDTO> buscarPrestamos(String nombre, String titulo,
                                                          LocalDate fechaPrestamoDesde) {
        String sql = "SELECT pr.*, CONCAT(s.nombre, ' ', s.apellidos) AS socio, "
                + "v.titulo AS videojuego, p.nombre AS plataforma, c.codigo_inventario "
                + "FROM prestamos pr "
                + "JOIN socios s ON pr.id_socio = s.id "
                + "JOIN copias c ON pr.id_copia = c.id "
                + "JOIN videojuegos v ON c.id_videojuego = v.id "
                + "JOIN plataformas p ON c.id_plataforma = p.id "
                + "WHERE 1 = 1";

        if (nombre != null && !nombre.trim().isEmpty()) {
            sql = sql + " AND CONCAT(s.nombre, ' ', s.apellidos) LIKE ?";
        }
        if (titulo != null && !titulo.trim().isEmpty()) {
            sql = sql + " AND v.titulo LIKE ?";
        }
        if (fechaPrestamoDesde != null) {
            sql = sql + " AND pr.fecha_prestamo >= ?";
        }
        sql = sql + " ORDER BY pr.fecha_prestamo DESC, pr.id DESC";

        ArrayList<PrestamoListadoDTO> listaPrestamos = new ArrayList<>();
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);

            // Cada valor ocupa el siguiente ? en la consulta.
            int numeroParametro = 1;
            if (nombre != null && !nombre.trim().isEmpty()) {
                ps.setString(numeroParametro, "%" + nombre + "%");
                numeroParametro++;
            }
            if (titulo != null && !titulo.trim().isEmpty()) {
                ps.setString(numeroParametro, "%" + titulo + "%");
                numeroParametro++;
            }
            if (fechaPrestamoDesde != null) {
                ps.setDate(numeroParametro, Date.valueOf(fechaPrestamoDesde));
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                PrestamoListadoDTO prestamoListado = new PrestamoListadoDTO(
                        mapearPrestamo(rs),
                        rs.getString("socio"),
                        rs.getString("videojuego"),
                        rs.getString("plataforma"),
                        rs.getString("codigo_inventario")
                );
                listaPrestamos.add(prestamoListado);
            }
            rs.close();
            ps.close();
            connection.close();
        } catch (SQLException e) {
            logger.error("Error al buscar préstamos", e);
            cerrarConexion(connection);
        }
        return listaPrestamos;
    }

    public ArrayList<PrestamoListadoDTO> buscarPrestamosAbiertos(String nombre) {
        String sql = "SELECT pr.*, CONCAT(s.nombre, ' ', s.apellidos) AS socio, "
                + "v.titulo AS videojuego, p.nombre AS plataforma, c.codigo_inventario "
                + "FROM prestamos pr "
                + "JOIN socios s ON pr.id_socio = s.id "
                + "JOIN copias c ON pr.id_copia = c.id "
                + "JOIN videojuegos v ON c.id_videojuego = v.id "
                + "JOIN plataformas p ON c.id_plataforma = p.id "
                + "WHERE pr.fecha_devolucion IS NULL";

        if (nombre != null && !nombre.trim().isEmpty()) {
            sql = sql + " AND CONCAT(s.nombre, ' ', s.apellidos) LIKE ?";
        }
        sql = sql + " ORDER BY pr.fecha_prestamo DESC";

        ArrayList<PrestamoListadoDTO> listaPrestamos = new ArrayList<>();
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            if (nombre != null && !nombre.trim().isEmpty()) {
                ps.setString(1, "%" + nombre + "%");
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                PrestamoListadoDTO prestamoListado = new PrestamoListadoDTO(
                        mapearPrestamo(rs),
                        rs.getString("socio"),
                        rs.getString("videojuego"),
                        rs.getString("plataforma"),
                        rs.getString("codigo_inventario")
                );
                listaPrestamos.add(prestamoListado);
            }
            rs.close();
            ps.close();
            connection.close();
        } catch (SQLException e) {
            logger.error("Error al buscar préstamos abiertos", e);
            cerrarConexion(connection);
        }
        return listaPrestamos;
    }

    public PrestamosDTO buscarPrestamoPorId(int idPrestamo) {
        String sql = "SELECT id, id_socio, id_copia, fecha_prestamo, fecha_limite, fecha_devolucion, multa "
                + "FROM prestamos WHERE id = ?";
        PrestamosDTO prestamo = null;
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, idPrestamo);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                prestamo = mapearPrestamo(rs);
            }
            rs.close();
            ps.close();
            connection.close();
        } catch (SQLException e) {
            logger.error("Error al buscar préstamo por ID", e);
            cerrarConexion(connection);
        }
        return prestamo;
    }

    public int registrarPrestamo(PrestamosDTO prestamo) {
        String sql = "INSERT INTO prestamos (id_socio, id_copia, fecha_prestamo, fecha_limite, fecha_devolucion, multa) "
                + "VALUES (?, ?, CURDATE(), ?, NULL, 0)";
        int filasAfectadas = 0;
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, prestamo.getIdSocio());
            ps.setInt(2, prestamo.getIdCopia());
            ps.setDate(3, Date.valueOf(prestamo.getFechaLimite()));
            filasAfectadas = ps.executeUpdate();
            ps.close();
            connection.close();
        } catch (SQLException e) {
            logger.error("Error al registrar préstamo", e);
            cerrarConexion(connection);
        }
        return filasAfectadas;
    }

    public int contarPrestamosAbiertos(int idSocio) {
        String sql = "SELECT COUNT(*) AS total FROM prestamos "
                + "WHERE id_socio = ? AND fecha_devolucion IS NULL";
        return contarPrestamosDeSocio(sql, idSocio);
    }

    public int contarPrestamosVencidos(int idSocio) {
        String sql = "SELECT COUNT(*) AS total FROM prestamos "
                + "WHERE id_socio = ? AND fecha_devolucion IS NULL AND fecha_limite < CURDATE()";
        return contarPrestamosDeSocio(sql, idSocio);
    }

    public int contarPrestamosAbiertos() {
        String sql = "SELECT COUNT(*) AS total FROM prestamos WHERE fecha_devolucion IS NULL";
        return contarPrestamos(sql);
    }

    public int contarPrestamosVencidos() {
        String sql = "SELECT COUNT(*) AS total FROM prestamos "
                + "WHERE fecha_devolucion IS NULL AND fecha_limite < CURDATE()";
        return contarPrestamos(sql);
    }
     private int contarPrestamos(String sql) {
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
            logger.error("Error al contar préstamos", e);
            cerrarConexion(connection);
        }
        return total;
    }

    private int contarPrestamosDeSocio(String sql, int idSocio) {
        int total = 0;
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, idSocio);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                total = rs.getInt("total");
            }
            rs.close();
            ps.close();
            connection.close();
        } catch (SQLException e) {
            logger.error("Error al contar préstamos del socio", e);
            cerrarConexion(connection);
        }
        return total;
    }

    public boolean devolverPrestamo(int idPrestamo, int idCopia, double multa, boolean copiaDanada) {
        String sqlPrestamo = "UPDATE prestamos SET fecha_devolucion = CURDATE(), multa = ? "
                + "WHERE id = ? AND fecha_devolucion IS NULL";
        String sqlCopia = "UPDATE copias SET estado = 'DANADO', activa = 0 WHERE id = ?";
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);

            PreparedStatement ps = connection.prepareStatement(sqlPrestamo);
            ps.setDouble(1, multa);
            ps.setInt(2, idPrestamo);
            int filasPrestamo = ps.executeUpdate();
            ps.close();

            if (filasPrestamo != 1) {
                connection.rollback();
                return false;
            }

            if (copiaDanada) {
                PreparedStatement psCopia = connection.prepareStatement(sqlCopia);
                psCopia.setInt(1, idCopia);
                int filasCopia = psCopia.executeUpdate();
                psCopia.close();
                if (filasCopia != 1) {
                    connection.rollback();
                    return false;
                }
            }

            connection.commit();
            return true;
        } catch (SQLException e) {
            logger.error("Error al devolver préstamo", e);
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException errorRollback) {
                    logger.error("Error al deshacer los cambios", errorRollback);
                }
            }
            return false;
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                } catch (SQLException e) {
                    logger.error("Error al restaurar la conexión", e);
                }
                cerrarConexion(connection);
            }
        }
    }

    private PrestamosDTO mapearPrestamo(ResultSet rs) throws SQLException {
        Date fechaDevolucion = rs.getDate("fecha_devolucion");
        LocalDate fecha = null;
        if (fechaDevolucion != null) {
            fecha = fechaDevolucion.toLocalDate();
        }
        return new PrestamosDTO(
                rs.getInt("id"),
                rs.getInt("id_socio"),
                rs.getInt("id_copia"),
                rs.getDate("fecha_prestamo").toLocalDate(),
                rs.getDate("fecha_limite").toLocalDate(),
                fecha,
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
