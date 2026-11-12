package com.example.practica_biblioteca.controlador;

import com.example.practica_biblioteca.excepcion.ReglaNegocioException;
import com.example.practica_biblioteca.service.VideojuegosService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/videojuegos")
public class VideojuegosControlador {
    private final VideojuegosService videojuegosService;
    public VideojuegosControlador(VideojuegosService videojuegosService) {
        this.videojuegosService = videojuegosService;
    }

    @GetMapping
    public String listado(@RequestParam(required = false) String titulo,
                          @RequestParam(required = false) Integer idDesarrolladora,
                          @RequestParam(required = false) Integer anioDesde,
                          @RequestParam(required = false) Integer pegiMaximo,
                          @RequestParam(defaultValue = "false") boolean soloDisponibles,
                          Model model) {
        model.addAttribute("listaVideojuegos", videojuegosService.buscarVideojuegos(
                titulo, idDesarrolladora, anioDesde, pegiMaximo, soloDisponibles));
        model.addAttribute("listaDesarrolladoras", videojuegosService.buscarDesarrolladoras());
        model.addAttribute("titulo", titulo);
        model.addAttribute("idDesarrolladora", idDesarrolladora);
        model.addAttribute("anioDesde", anioDesde);
        model.addAttribute("pegiMaximo", pegiMaximo);
        model.addAttribute("soloDisponibles", soloDisponibles);
        return "videojuegos/listado";
    }

    @GetMapping("/insertar")
    public String formulario(Model model) {
        model.addAttribute("listaDesarrolladoras", videojuegosService.buscarDesarrolladoras());
        return "videojuegos/insertar";
    }

    @PostMapping("/insertar")
    public String insertar(@RequestParam String titulo,
                           @RequestParam String anioLanzamiento,
                           @RequestParam String pegi,
                           @RequestParam String idDesarrolladora,
                           Model model, RedirectAttributes redirectAttributes) {
        try {
            videojuegosService.insertarVideojuego(titulo, anioLanzamiento, pegi, idDesarrolladora);
            redirectAttributes.addFlashAttribute("mensaje", "Videojuego insertado correctamente.");
            return "redirect:/videojuegos";
        } catch (ReglaNegocioException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("listaDesarrolladoras", videojuegosService.buscarDesarrolladoras());
            model.addAttribute("titulo", titulo);
            model.addAttribute("anioLanzamiento", anioLanzamiento);
            model.addAttribute("pegi", pegi);
            model.addAttribute("idDesarrolladora", idDesarrolladora);
            return "videojuegos/insertar";
        }
    }
}
