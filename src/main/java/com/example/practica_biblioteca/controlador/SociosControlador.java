package com.example.practica_biblioteca.controlador;

import com.example.practica_biblioteca.excepcion.ReglaNegocioException;
import com.example.practica_biblioteca.service.SociosService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/socios")
public class SociosControlador {
    private final SociosService sociosService;
    public SociosControlador(SociosService sociosService) {
        this.sociosService = sociosService;
    }

    @GetMapping
    public String listado(@RequestParam(required = false) String nombre,
                          @RequestParam(required = false) String email,
                          @RequestParam(required = false) Boolean activo,
                          Model model) {
        model.addAttribute("listaSocios", sociosService.buscarSocios(nombre, email, activo));
        model.addAttribute("nombre", nombre);
        model.addAttribute("email", email);
        model.addAttribute("activo", activo);
        return "socios/listado";
    }

    @GetMapping("/insertar")
    public String formulario() {
        return "socios/insertar";
    }

    @PostMapping("/insertar")
    public String insertar(@RequestParam String nombre,
                           @RequestParam String apellidos,
                           @RequestParam String email,
                           @RequestParam String fechaNacimiento,
                           Model model, RedirectAttributes redirectAttributes) {
        try {
            sociosService.insertarSocio(nombre, apellidos, email, fechaNacimiento);
            redirectAttributes.addFlashAttribute("mensaje", "Socio insertado correctamente.");
            return "redirect:/socios";
        } catch (ReglaNegocioException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("nombre", nombre);
            model.addAttribute("apellidos", apellidos);
            model.addAttribute("email", email);
            model.addAttribute("fechaNacimiento", fechaNacimiento);
            return "socios/insertar";
        }
    }
}
