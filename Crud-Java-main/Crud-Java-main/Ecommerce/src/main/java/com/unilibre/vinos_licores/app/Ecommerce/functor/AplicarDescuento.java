package com.unilibre.vinos_licores.app.Ecommerce.functor;

import com.unilibre.vinos_licores.app.Ecommerce.model.Producto;

public class AplicarDescuento implements ProductoFunctor {
    private static final double DESCUENTO = 0.10;

    @Override
    public Producto aplicar(Producto producto) {
        if (producto.getPrecio() != null) {
            double precioFinal = producto.getPrecio() * (1 - DESCUENTO);
            producto.setPrecio(precioFinal);
        }
        return producto;
    }
}