package com.unilibre.vinos_licores.app.Ecommerce.controller;

import com.unilibre.vinos_licores.app.Ecommerce.model.Producto;
import com.unilibre.vinos_licores.app.Ecommerce.repository.ProductoRepository;
import com.unilibre.vinos_licores.app.Ecommerce.service.ProductoService;
import com.unilibre.vinos_licores.app.Ecommerce.functor.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/productos")
@CrossOrigin(origins = "*")
public class ProductoController {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private ProductoService productoService;

    // Crear producto normal (JSON)
    @PostMapping
    public Producto createProducto(@RequestBody Producto producto) {
        return productoRepository.save(producto);
    }

    // Crear producto desde formulario (multipart/form-data)
    @PostMapping("/form")
    public ResponseEntity<?> crearDesdeFormulario(
            @RequestParam("nombre") String nombre,
            @RequestParam("descripcion") String descripcion,
            @RequestParam("precio") Double precio,
            @RequestParam(value = "imagen", required = false) MultipartFile imagen) {

        try {
            Producto nuevo = new Producto();
            nuevo.setNombre(nombre);
            nuevo.setDescripcion(descripcion);
            nuevo.setPrecio(precio);

            if (imagen != null && !imagen.isEmpty()) {
                // Guardar solo el nombre del archivo (opcionalmente podrías almacenarlo físicamente)
                nuevo.setImagen(imagen.getOriginalFilename());
            }

            Producto guardado = productoRepository.save(nuevo);
            return ResponseEntity.ok(guardado);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al guardar el producto: " + e.getMessage());
        }
    }

    // Crear producto con IVA (procesado en hilo)
    @PostMapping("/iva")
    public Producto crearConIVA(@RequestBody Producto producto) {
        ProductoFunctor functorIVA = new CalcularIVA();
        return productoService.guardarProductoAsync(producto, functorIVA);
    }

    // Crear producto con Descuento (procesado en hilo)
    @PostMapping("/descuento")
    public Producto crearConDescuento(@RequestBody Producto producto) {
        ProductoFunctor functorDescuento = new AplicarDescuento();
        return productoService.guardarProductoAsync(producto, functorDescuento);
    }

    // Obtener todos los productos
    @GetMapping
    @Transactional(readOnly = true)
    public List<Producto> getAllProductos() {
        return productoRepository.findAll();
    }

    // Obtener producto por ID
    @GetMapping("/{id}")
    public ResponseEntity<Producto> getProductoById(@PathVariable Integer id) {
        return productoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Actualizar producto
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProducto(
            @PathVariable Integer id,
            @RequestBody Producto productoActualizado) {

        Optional<Producto> productoExistenteOpt = productoRepository.findById(id);
        if (productoExistenteOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Producto productoExistente = productoExistenteOpt.get();

        // Actualizar campos
        productoExistente.setNombre(productoActualizado.getNombre());
        productoExistente.setImagen(productoActualizado.getImagen());
        productoExistente.setDescripcion(productoActualizado.getDescripcion());
        productoExistente.setPrecio(productoActualizado.getPrecio());

        try {
            Producto actualizado = productoRepository.save(productoExistente);
            return ResponseEntity.ok(actualizado);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al actualizar el producto: " + e.getMessage());
        }
    }

    // Eliminar producto
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProducto(@PathVariable Integer id) {
        if (!productoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        productoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
