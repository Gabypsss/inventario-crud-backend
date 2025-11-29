package com.unilibre.vinos_licores.app.Ecommerce.service;

import com.unilibre.vinos_licores.app.Ecommerce.model.Producto;
import com.unilibre.vinos_licores.app.Ecommerce.functor.ProductoFunctor;

public class ProductoTask extends Thread {

    private Producto producto;
    private ProductoFunctor functor;

    public ProductoTask(Producto producto, ProductoFunctor functor) {
        this.producto = producto;
        this.functor = functor;
    }

    @Override
    public void run() {
        System.out.println("🧵 [HILO] Procesando producto: " + producto.getNombre());
        Producto resultado = functor.aplicar(producto);
        System.out.println("✅ [HILO] Resultado: " + resultado.getNombre() + " → $" + resultado.getPrecio());
    }
}