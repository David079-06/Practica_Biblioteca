package com.example.practica_biblioteca.controlador;

import com.example.practica_biblioteca.DTO.PrestamoRegistroDTO;
import com.example.practica_biblioteca.excepcion.ReglaNegocioException;
import com.example.practica_biblioteca.service.PrestamosService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequestMapping("/prestamos")
public class PrestamosControlador {
    private final PrestamosService prestamosService;
    public PrestamosControlador(PrestamosService prestamosService) {
        this.prestamosService = prestamosService;
    }

    @GetMapping
    public String listado(@RequestParam(required = false) String nombre,
                          @RequestParam(required = false) String titulo,
                          @RequestParam(required = false) String estado,
                          @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                  LocalDate fechaDesde,
                          Model model) {
        model.addAttribute("listaPrestamos", prestamosService.buscarPrestamos(
                nombre, titulo, estado, fechaDesde));
        model.addAttribute("nombre", nombre);
        model.addAttribute("titulo", titulo);
        model.addAttribute("estado", estado);
        model.addAttribute("fechaDesde", fechaDesde);
        return "prestamos/listado";
    }

    @GetMapping("/registrar")
    public String formularioRegistro(Model model) {
        model.addAttribute("listaSocios", prestamosService.buscarSociosActivos());
        model.addAttribute("listaCopias", prestamosService.buscarCopiasDisponibles());
        return "prestamos/registrar";
    }

    @PostMapping("/registrar")
    public String registrar(@RequestParam int idSocio, @RequestParam int idCopia,
                            Model model, RedirectAttributes redirectAttributes) {
        try {
            PrestamoRegistroDTO resultado = prestamosService.registrarPrestamo(idSocio, idCopia);
            String tipoPlazo = resultado.getPlazoDias() == 7 ? "de novedad" : "general";
            redirectAttributes.addFlashAttribute("mensaje",
                    "Préstamo registrado correctamente. Plazo " + tipoPlazo + ": "
                            + resultado.getPlazoDias() + " días. Fecha límite: "
                            + resultado.getFechaLimite());
            return "redirect:/prestamos";
        } catch (ReglaNegocioException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("listaSocios", prestamosService.buscarSociosActivos());
            model.addAttribute("listaCopias", prestamosService.buscarCopiasDisponibles());
            return "prestamos/registrar";
        }
    }

    @GetMapping("/devolver")
    public String formularioDevolucion(@RequestParam(required = false) String nombre, Model model) {
        model.addAttribute("listaPrestamos", prestamosService.buscarPrestamosAbiertos(nombre));
        model.addAttribute("nombre", nombre);
        return "prestamos/devolver";
    }

    @PostMapping("/devolver/{id}")
    public String devolver(@PathVariable int id,
                           @RequestParam(defaultValue = "false") boolean copiaDanada,
                           @RequestParam(required = false) String nombre,
                           Model model) {
        try {
            double multa = prestamosService.devolverPrestamo(id, copiaDanada);
            model.addAttribute("mensaje", "Préstamo devuelto. Multa: " + String.format("%.2f", multa) + " €");
        } catch (ReglaNegocioException e) {
            model.addAttribute("error", e.getMessage());
        }
        model.addAttribute("listaPrestamos", prestamosService.buscarPrestamosAbiertos(nombre));
        model.addAttribute("nombre", nombre);
        return "prestamos/devolver";
    }
}
