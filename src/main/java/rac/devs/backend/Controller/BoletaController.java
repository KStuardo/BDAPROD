package rac.devs.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import rac.devs.backend.model.Boleta;
import rac.devs.backend.model.Producto;
import rac.devs.backend.Repository.BoletaRepository;
import rac.devs.backend.Repository.ProductoRepository;
import rac.devs.backend.service.GoogleVisionService;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/boletas")
@CrossOrigin(origins = "*")
public class BoletaController {

    private static final Logger logger = Logger.getLogger(BoletaController.class.getName());

    @Autowired
    private BoletaRepository boletaRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private GoogleVisionService googleVisionService;

    /**
     * Analizar una imagen y guardar la boleta.
     */
    @PostMapping("/analyze")
    public ResponseEntity<?> analyzeAndSaveBoleta(@RequestParam("file") MultipartFile file) {
        try {
            // Validar el archivo
            if (file == null || file.isEmpty()) {
                return ResponseEntity.badRequest().body("La imagen es requerida.");
            }

            if (!isImage(file)) {
                return ResponseEntity.badRequest().body("El archivo debe ser una imagen válida.");
            }

            // Convertir la imagen a Base64
            String base64Image = convertToBase64(file);
            logger.info("Imagen convertida a Base64 con éxito.");

            // Analizar la imagen con Google Vision API
            List<Producto> productos = googleVisionService.analyzeReceipt(base64Image);

            if (productos.isEmpty()) {
                return ResponseEntity.badRequest().body("No se detectaron productos en la boleta.");
            }

            // Crear y guardar la boleta
            Boleta boleta = createAndSaveBoleta(productos);

            return ResponseEntity.ok(boleta);

        } catch (IOException e) {
            logger.severe("Error al convertir la imagen a Base64: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al procesar la imagen: " + e.getMessage());
        } catch (Exception e) {
            logger.severe("Error inesperado: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al procesar la boleta: " + e.getMessage());
        }
    }

    /**
     * Verifica si el archivo es una imagen válida.
     */
    private boolean isImage(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && (contentType.startsWith("image/jpeg") || contentType.startsWith("image/png"));
    }

    /**
     * Convierte un archivo MultipartFile a una cadena Base64.
     */
    private String convertToBase64(MultipartFile file) throws IOException {
        return java.util.Base64.getEncoder().encodeToString(file.getBytes());
    }

    /**
     * Crea y guarda una boleta con los productos procesados.
     */
    private Boleta createAndSaveBoleta(List<Producto> productos) {
        Boleta boleta = new Boleta();
        boleta.setFechaEmision(new Date());
        boleta.setTotal(productos.stream().mapToDouble(Producto::getPrecio).sum());
        boleta.setProductos(productos);

        productoRepository.saveAll(productos);
        return boletaRepository.save(boleta);
    }
}
