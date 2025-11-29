package com.unilibre.vinos_licores.app.Ecommerce.service;

import com.unilibre.vinos_licores.app.Ecommerce.exception.ProductoNotFoundException;
import com.unilibre.vinos_licores.app.Ecommerce.model.Producto;
import com.unilibre.vinos_licores.app.Ecommerce.repository.ProductoRepository;
import com.unilibre.vinos_licores.app.Ecommerce.functor.ProductoFunctor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductoService implements IProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    // Guardado asincrónico con functor
    public Producto guardarProductoAsync(Producto producto, ProductoFunctor functor) {
        ProductoTask tarea = new ProductoTask(producto, functor);
        tarea.start();
        return productoRepository.save(producto);
    }

    // Guardado normal con functor
    public Producto guardarProducto(Producto producto, ProductoFunctor functor) {
        Producto modificado = functor.aplicar(producto);
        return productoRepository.save(modificado);
    }

    @Override
    public List<Producto> listarProductos() {
        return productoRepository.findAll();
    }

    @Override
    public Producto buscarProductoPorId(Integer idProducto) {
        return productoRepository.findById(idProducto)
                .orElseThrow(() -> new ProductoNotFoundException("Producto con ID " + idProducto + " no encontrado."));
    }

    @Override
    @Transactional
    public void guardarProducto(Producto producto) {
        try {
            productoRepository.save(producto);
        } catch (OptimisticLockingFailureException e) {
            System.err.println("⚠ Conflicto de concurrencia: otro usuario modificó este producto antes que tú.");
            throw new RuntimeException("Conflicto de concurrencia detectado al guardar el producto.");
        } catch (Exception e) {
            System.err.println("Error al guardar el producto: " + e.getMessage());
            throw new RuntimeException("Error al guardar el producto.");
        }
    }

    @Override
    @Transactional
    public void eliminarProducto(Producto producto) {
        try {
            productoRepository.delete(producto);
        } catch (OptimisticLockingFailureException e) {
            System.err.println("⚠ Conflicto de concurrencia: otro usuario eliminó o modificó este producto.");
            throw new RuntimeException("Conflicto de concurrencia al eliminar el producto.");
        } catch (Exception e) {
            System.err.println("Error al eliminar el producto: " + e.getMessage());
            throw new RuntimeException("Error al eliminar el producto.");
        }
    }

    @Transactional
    public Producto actualizarProducto(Integer id, Producto datos) {
        Producto productoExistente = productoRepository.findById(id)
                .orElseThrow(() -> new ProductoNotFoundException("Producto con ID " + id + " no encontrado."));

        productoExistente.setNombre(datos.getNombre());
        productoExistente.setDescripcion(datos.getDescripcion());
        productoExistente.setPrecio(datos.getPrecio());
        productoExistente.setImagen(datos.getImagen());

        try {
            // Hibernate verifica automáticamente la versión (@Version)
            return productoRepository.save(productoExistente);
        } catch (OptimisticLockingFailureException e) {
            System.err.println("⚠ Conflicto de concurrencia: otro usuario ya actualizó este producto.");
            throw new RuntimeException("Otro usuario modificó este producto antes que tú. Refresca la página e inténtalo de nuevo.");
        } catch (Exception e) {
            System.err.println("Error al actualizar el producto: " + e.getMessage());
            throw new RuntimeException("Error al actualizar el producto.");
        }
    }
}
