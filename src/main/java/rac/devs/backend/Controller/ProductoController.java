package rac.devs.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rac.devs.backend.model.Producto;
import rac.devs.backend.Repository.ProductoRepository;


import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins="*")
public class ProductoController {

    @Autowired
    private ProductoRepository productoRepository;

    // Obtener todos los productos
    @GetMapping
    public ResponseEntity<List<Producto>> getAllProductos() {
        List<Producto> productos = productoRepository.findAll();
        return productos.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(productos);
    }

    // Obtener un producto por ID
    @GetMapping("/{id}")
    public ResponseEntity<Producto> getProductoById(@PathVariable Long id) {
        Optional<Producto> producto = productoRepository.findById(id);
        return producto.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Crear un nuevo producto
    @PostMapping
    public ResponseEntity<Producto> createProducto(@RequestBody Producto producto) {
        Producto nuevoProducto = productoRepository.save(producto);
        return ResponseEntity.status(201).body(nuevoProducto);
    }

    // Actualizar un producto existente
    @PutMapping("/{id}")
    public ResponseEntity<Producto> updateProducto(@PathVariable Long id, @RequestBody Producto productoDetails) {
        Optional<Producto> productoOptional = productoRepository.findById(id);
        if (productoOptional.isPresent()) {
            Producto producto = productoOptional.get();
            producto.setNombre(productoDetails.getNombre());
            producto.setPrecio(productoDetails.getPrecio());
            producto.setMarca(productoDetails.getMarca());
            producto.setStock(productoDetails.getStock());
            Producto updatedProducto = productoRepository.save(producto);
            return ResponseEntity.ok(updatedProducto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Eliminar un producto existente 
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProducto(@PathVariable Long id) {
        if (productoRepository.existsById(id)) {
            productoRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
