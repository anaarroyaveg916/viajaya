package com.dayana.viajaya.services.impl;

import com.dayana.viajaya.dto.RegistroUsuarioRequest;
import com.dayana.viajaya.dto.RegistroUsuarioResponse;
import com.dayana.viajaya.entities.Usuario;
import com.dayana.viajaya.exceptions.RegistroValidacionException;
import com.dayana.viajaya.repositories.IUsuarioRepository;
import com.dayana.viajaya.services.IUsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements IUsuarioService {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)\\S{8,}$");

    private final IUsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

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
        if (usuario.getPasswordHash() != null && !usuario.getPasswordHash().isBlank()) {
            usuario.setPasswordHash(passwordEncoder.encode(usuario.getPasswordHash()));
        }
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

        if (usuario.getPasswordHash() != null && !usuario.getPasswordHash().isBlank()) {
            usuarioDb.setPasswordHash(passwordEncoder.encode(usuario.getPasswordHash()));
        }

        return usuarioRepository.save(usuarioDb);
    }

    @Override
    public void deleteById(Integer id) {
        usuarioRepository.deleteById(id);
    }

    @Override
    public RegistroUsuarioResponse registrar(RegistroUsuarioRequest request) {
        Map<String, String> errores = new LinkedHashMap<>();

        if (request == null) {
            errores.put("nombre", "El nombre es obligatorio");
            errores.put("correo", "El correo electrónico es obligatorio");
            errores.put("password", "La contraseña es obligatoria");
            throw new RegistroValidacionException(errores);
        }

        String nombre = request.getNombre() != null ? request.getNombre().trim() : "";
        String apellido = request.getApellido() != null ? request.getApellido().trim() : "";
        String correo = request.getCorreo() != null ? request.getCorreo().trim().toLowerCase() : "";
        String telefono = request.getTelefono() != null ? request.getTelefono().trim() : null;
        String password = request.getPassword();

        if (nombre.isEmpty()) {
            errores.put("nombre", "El nombre es obligatorio");
        }

        if (correo.isEmpty()) {
            errores.put("correo", "El correo electrónico es obligatorio");
        } else if (!EMAIL_PATTERN.matcher(correo).matches()) {
            errores.put("correo", "El formato del correo electrónico no es válido");
        } else if (usuarioRepository.existsByCorreoIgnoreCase(correo)) {
            errores.put("correo", "El correo electrónico ya está registrado");
        }

        if (password == null || password.isBlank()) {
            errores.put("password", "La contraseña es obligatoria");
        } else if (!PASSWORD_PATTERN.matcher(password).matches()) {
            errores.put("password", "La contraseña debe tener al menos 8 caracteres, una letra y un número");
        }

        if (!errores.isEmpty()) {
            throw new RegistroValidacionException(errores);
        }

        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombre(nombre);
        nuevoUsuario.setApellido(apellido);
        nuevoUsuario.setCorreo(correo);
        nuevoUsuario.setTelefono(telefono);
        nuevoUsuario.setPasswordHash(passwordEncoder.encode(password));

        Usuario guardado = usuarioRepository.save(nuevoUsuario);

        return new RegistroUsuarioResponse(
                guardado.getId(),
                guardado.getNombre(),
                guardado.getApellido(),
                guardado.getCorreo(),
                guardado.getTelefono(),
                "Cuenta creada exitosamente"
        );
    }
}