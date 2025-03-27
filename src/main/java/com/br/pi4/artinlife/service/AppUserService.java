package com.br.pi4.artinlife.service;

import com.br.pi4.artinlife.dto.AppUserDTO;
import com.br.pi4.artinlife.model.AppUser;
import com.br.pi4.artinlife.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service // Marca essa classe como um serviço (camada de regra de negócio)
@RequiredArgsConstructor // Lombok: cria um construtor com os campos 'final' automaticamente
public class AppUserService {

    // Injeção do repositório para salvar e consultar usuários no banco
    private final AppUserRepository appUserRepository;

    /**
     * Registra um novo usuário no sistema.
     * Antes de salvar, verifica se o CPF ou o email já estão cadastrados.
     * A senha é criptografada com BCrypt antes de ser salva no banco.
     *
     * @param dto Objeto que carrega os dados do usuário (vindo da requisição)
     * @return O AppUser salvo no banco
     * @throws IllegalArgumentException se o CPF ou email já existirem no sistema
     */
    public AppUser register(AppUserDTO dto) {
        // Verifica se já existe um usuário com o CPF informado
        if (appUserRepository.existsByCpf(dto.getCpf())) {
            throw new IllegalArgumentException("CPF already registered.");
        }

        // Verifica se já existe um usuário com o email informado
        if (appUserRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email already registered.");
        }

        // Cria um novo objeto AppUser com os dados do DTO e criptografa a senha
        AppUser user = AppUser.builder()
                .name(dto.getName())
                .cpf(dto.getCpf())
                .email(dto.getEmail())
                .password(new BCryptPasswordEncoder().encode(dto.getPassword())) // Criptografia segura
                .status(true) // Usuário começa como ativo
                .type(dto.getType()) // Tipo do usuário (CLIENT, ADMIN, STOCKER)
                .build();

        // Salva o usuário no banco de dados e retorna o resultado
        return appUserRepository.save(user);
    }
}
