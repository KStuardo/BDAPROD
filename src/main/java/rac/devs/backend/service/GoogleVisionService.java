package rac.devs.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import rac.devs.backend.model.Producto;

import java.util.ArrayList;
import java.util.List;

@Service
public class GoogleVisionService {

    private final String VISION_API_URL = "https://vision.googleapis.com/v1/images:annotate";
    private final String API_KEY = "AIzaSyBnTDyzH8GQEPcDckYway8MfIM7JMgqV1k";

    // Método principal que procesa la imagen y extrae productos
    public List<Producto> analyzeReceipt(String base64Image) {
        String url = VISION_API_URL + "?key=" + API_KEY;

        try {
            // Crear el cuerpo de la solicitud JSON
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode request = mapper.createObjectNode();
            ArrayNode requestsArray = request.putArray("requests");

            ObjectNode imageRequest = mapper.createObjectNode();
            imageRequest.putObject("image").put("content", base64Image);
            imageRequest.putArray("features").addObject().put("type", "TEXT_DETECTION");

            requestsArray.add(imageRequest);
            String requestBody = request.toString();

            // Configurar encabezados HTTP
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");

            // Enviar la solicitud a Google Vision API
            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

            // Procesar la respuesta
            ObjectNode root = (ObjectNode) mapper.readTree(response.getBody());
            JsonNode annotations = root.path("responses").get(0).path("textAnnotations");

            if (annotations != null && annotations.size() > 0) {
                String fullText = annotations.get(0).path("description").asText();
                System.out.println("Texto detectado: \n" + fullText);
                return extractProductsFromText(fullText);
            } else {
                System.err.println("No se detectó texto en la imagen.");
                return new ArrayList<>();
            }
        } catch (Exception e) {
            System.err.println("Error al conectar con Google Vision API:");
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

// Método para extraer productos válidos del texto detectado
private List<Producto> extractProductsFromText(String fullText) {
    List<Producto> productos = new ArrayList<>();
    String[] lines = fullText.split("\n"); // Dividir texto en líneas
    String nombreProducto = null;

    for (String line : lines) {
        line = line.trim();
        System.out.println("Línea procesada: " + line);

        // Filtrar líneas irrelevantes
        if (line.matches("(?i).*RUT.*|.*TOTAL.*|.*IVA.*|.*SUBTOTAL.*|.*DESCUENTO.*|.*REDONDEO.*|.*VUELTO.*|.*EFECTIVO.*|.*MI CLUB.*")) {
            System.out.println("Línea descartada: " + line);
            continue;
        }

        // Filtrar líneas con solo números (ej. códigos de barras)
        if (line.matches("^\\d+$")) {
            System.out.println("Línea descartada por ser solo números: " + line);
            continue;
        }

        // Detectar si la línea contiene un precio al final
        if (line.matches(".*\\d+(\\.\\d{2})?$")) {
            try {
                // Extraer el precio al final de la línea
                Double precio = Double.parseDouble(line.replaceAll(".*?(\\d+(\\.\\d{2})?)$", "$1"));

                // Extraer el nombre del producto (parte antes del precio)
                String nombre = line.replaceAll("\\d+(\\.\\d{2})?$", "").trim();

                // Validar precios dentro de un rango realista
                if (precio <= 0 || precio > 100000) {
                    System.out.println("Precio descartado por ser irreal: " + precio);
                    continue;
                }

                // Si se encuentra un nombre directo
                if (nombre.length() > 2) {
                    Producto producto = new Producto();
                    producto.setNombre(nombre);
                    producto.setPrecio(precio);
                    producto.setMarca("Desconocida");
                    producto.setStock(1);

                    productos.add(producto);
                    System.out.println("Producto agregado: " + nombre + " - $" + precio);
                    nombreProducto = null; // Reiniciar el acumulador
                }

            } catch (NumberFormatException e) {
                System.err.println("Error al convertir precio: " + line);
            }
        } else {
            // Acumular nombres largos o combinar líneas
            if (nombreProducto == null) {
                nombreProducto = line;
            } else {
                nombreProducto += " " + line;
            }
        }
    }
    return productos;
}
}
