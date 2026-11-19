package com.dayana.viajaya.services;

import com.dayana.viajaya.entities.Usuario;
import java.util.List;

public interface IUsuarioService {
    List<Usuario> findAll();
    Usuario findById(Integer id);
    Usuario save(Usuario usuario);
    Usuario update(Integer id, Usuario usuario);
    void deleteById(Integer id);
}
