package rac.devs.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rac.devs.backend.model.Producto;
import rac.devs.backend.service.GoogleVisionService;

import java.util.List;

@RestController
@RequestMapping("/api/google-vision")
public class GoogleVisionController {

    @Autowired
    private GoogleVisionService googleVisionService;

    @PostMapping("/analyze")
    public ResponseEntity<List<Producto>> analyzeImage(@RequestBody String base64Image) {
        System.out.println("Solicitud recibida para analizar la imagen.");
        List<Producto> productos = googleVisionService.analyzeReceipt(base64Image);
        return ResponseEntity.ok(productos);
    }
}
