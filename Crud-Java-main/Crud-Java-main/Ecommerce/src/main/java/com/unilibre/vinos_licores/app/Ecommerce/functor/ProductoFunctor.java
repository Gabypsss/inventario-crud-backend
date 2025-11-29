package com.unilibre.vinos_licores.app.Ecommerce.functor;

import com.unilibre.vinos_licores.app.Ecommerce.model.Producto;

@FunctionalInterface
public interface ProductoFunctor {
    Producto aplicar(Producto producto);
}
