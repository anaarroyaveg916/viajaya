package com.dayana.viajaya.services.impl;

import com.dayana.viajaya.entities.Usuario;
import com.dayana.viajaya.repositories.IUsuarioRepository;
import com.dayana.viajaya.services.IUsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements IUsuarioService {
    private final IUsuarioRepository usuarioRepository;

    //public UsuarioServiceImpl(IUsuarioRepository usuarioRepository) {
      //  this.usuarioRepository = usuarioRepository;
    //}

    @Override
    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    @Override
    public Usuario findById(Integer id) {
        return usuarioRepository.findById(id).orElse(null);
    }

    @Override
    public Usuario save(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    @Override
    public Usuario update(Integer id, Usuario usuario) {
        Usuario usuarioDb = findById(id);
        if (usuarioDb == null) return null;

        usuarioDb.setNombre(usuario.getNombre());
        usuarioDb.setApellido(usuario.getApellido());
        usuarioDb.setCorreo(usuario.getCorreo());
        usuarioDb.setTelefono(usuario.getTelefono());
        return usuarioRepository.save(usuarioDb);
    }

    @Override
    public void deleteById(Integer id) {
        usuarioRepository.deleteById(id);
    }
}