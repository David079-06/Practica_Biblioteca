package com.example.practica_biblioteca.controlador;

import com.example.practica_biblioteca.service.InicioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class InicioControlador {
    private final InicioService inicioService;
    public InicioControlador(InicioService inicioService) {
        this.inicioService = inicioService;
    }

    @GetMapping("/")
    public String inicio(Model model) {
        model.addAttribute("videojuegos", inicioService.contarVideojuegos());
        model.addAttribute("sociosActivos", inicioService.contarSociosActivos());
        model.addAttribute("prestamosAbiertos", inicioService.contarPrestamosAbiertos());
        model.addAttribute("prestamosVencidos", inicioService.contarPrestamosVencidos());
        return "index";
    }
}
