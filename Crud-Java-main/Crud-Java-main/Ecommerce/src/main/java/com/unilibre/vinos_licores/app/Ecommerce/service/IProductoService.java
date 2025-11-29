package com.unilibre.vinos_licores.app.Ecommerce.service;

import com.unilibre.vinos_licores.app.Ecommerce.model.Producto;

import java.util.List;

public interface IProductoService {
    public List<Producto> listarProductos();
    public Producto buscarProductoPorId(Integer idProducto);
    public void guardarProducto(Producto producto);
    public void eliminarProducto(Producto producto);

}
