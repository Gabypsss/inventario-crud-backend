package com.unilibre.vinos_licores.app.Ecommerce.functor;

import com.unilibre.vinos_licores.app.Ecommerce.model.Producto;

public class CalcularIVA implements ProductoFunctor {

    private static final double IVA = 0.19; // 19%

    @Override
    public Producto aplicar(Producto producto) {
        if (producto.getPrecio() != null) {
            double precioConIVA = producto.getPrecio() * (1 + IVA);
            producto.setPrecio(precioConIVA);
        }
        return producto;
    }
}
