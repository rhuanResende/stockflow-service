package com.desenvolvimento.logica.stockflow.stockflow_service.auth.service;

import com.desenvolvimento.logica.stockflow.stockflow_common.dto.CompanyResponse;
import com.desenvolvimento.logica.stockflow.stockflow_common.dto.UserDataResponse;
import com.desenvolvimento.logica.stockflow.stockflow_common.dto.UserResponse;
import com.desenvolvimento.logica.stockflow.stockflow_common.entity.BaseEntity;
import com.desenvolvimento.logica.stockflow.stockflow_common.enums.MessageCode;
import com.desenvolvimento.logica.stockflow.stockflow_common.exception.BusinessException;
import com.desenvolvimento.logica.stockflow.stockflow_common.exception.InvalidTokenException;
import com.desenvolvimento.logica.stockflow.stockflow_common.exception.UnauthorizedException;
import com.desenvolvimento.logica.stockflow.stockflow_common.service.MessageService;
import com.desenvolvimento.logica.stockflow.stockflow_security.holder.TenantContextHolder;
import com.desenvolvimento.logica.stockflow.stockflow_security.model.AuthenticatedUser;
import com.desenvolvimento.logica.stockflow.stockflow_security.service.JwtService;
import com.desenvolvimento.logica.stockflow.stockflow_service.auth.dto.*;
import com.desenvolvimento.logica.stockflow.stockflow_service.auth.entity.RefreshToken;
import com.desenvolvimento.logica.stockflow.stockflow_service.auth.entity.UserRole;
import com.desenvolvimento.logica.stockflow.stockflow_service.auth.mapper.RefreshTokenMapper;
import com.desenvolvimento.logica.stockflow.stockflow_service.auth.mapper.RoleMapper;
import com.desenvolvimento.logica.stockflow.stockflow_service.auth.repository.RefreshTokenRepository;
import com.desenvolvimento.logica.stockflow.stockflow_service.auth.repository.RoleRepository;
import com.desenvolvimento.logica.stockflow.stockflow_service.auth.repository.UserRoleRepository;
import com.desenvolvimento.logica.stockflow.stockflow_service.company.service.CompanyService;
import com.desenvolvimento.logica.stockflow.stockflow_service.user.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRoleRepository userRoleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RoleRepository roleRepository;
    private final UserService userService;
    private final CompanyService companyService;
    private final MessageService messageService;
    private final RefreshTokenMapper refreshTokenMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RoleMapper roleMapper;

    public AuthService(
            UserRoleRepository userRoleRepository,
            RefreshTokenRepository refreshTokenRepository,
            RoleRepository roleRepository,
            UserService userService,
            CompanyService companyService,
            MessageService messageService,
            RefreshTokenMapper refreshTokenMapper,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            RoleMapper roleMapper) {
        this.userRoleRepository = userRoleRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.roleRepository = roleRepository;
        this.userService = userService;
        this.companyService = companyService;
        this.messageService = messageService;
        this.refreshTokenMapper = refreshTokenMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.roleMapper = roleMapper;
    }

    public LoginResponse login(LoginRequest request) {

        UserResponse user = userService.findUserByDocument(request.document());

        if (!passwordEncoder.matches(request.password(), user.password())) {
            throw new UnauthorizedException(
                    messageService.get(MessageCode.ERROR_PASSWORD_INCORRECT.getCode())
            );
        }

        String token = jwtService.generateToken(new AuthenticatedUser(
                UUID.fromString(user.id()),
                UUID.fromString(user.company().id()),
                user.document(),
                getRoleUser(user.id())
        ));

        String refreshTokenValue = jwtService.generateRefreshToken();
        refreshTokenRepository.save(refreshTokenMapper.toEntity(UUID.fromString(user.id()), refreshTokenValue));

        return new LoginResponse(
                token,
                refreshTokenValue,
                jwtService.getExpiration(),
                user.firstAccess()
        );
    }

    public LoginResponse refresh(RefreshTokenRequest request) {

        RefreshToken refreshToken = refreshTokenRepository
                .findByToken(request.refreshToken())
                .orElseThrow(() -> new InvalidTokenException("Token Inválido"));

        if (refreshToken.isRevoked()) {
            throw new InvalidTokenException("Token Revogado");
        }

        if (refreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new InvalidTokenException("Token Expirado");
        }

        UserResponse user = userService.findUserByIdResponse(refreshToken.getUser());

        String token = jwtService.generateToken(new AuthenticatedUser(
                UUID.fromString(user.id()),
                UUID.fromString(user.company().id()),
                user.document(),
                getRoleUser(user.id())
        ));

        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);

        String refreshTokenValue = jwtService.generateRefreshToken();
        refreshTokenRepository.save(refreshTokenMapper.toEntity(UUID.fromString(user.id()), refreshTokenValue));

        return new LoginResponse(
                token,
                refreshTokenValue,
                jwtService.getExpiration(),
                user.firstAccess()
        );
    }

    public UserDataResponse me() {
        UserResponse user = userService.findUserByDocument(TenantContextHolder.getUserDocument());

        if (user == null) {
            throw new BusinessException("Usuário não encontrado.");
        }

        return new UserDataResponse(
                user.id(),
                user.name().toUpperCase(),
                user.document(),
                user.email(),
                user.phone(),
                user.company(),
                getRoleUser(user.id()),
                user.status()
        );
    }

    public void logout(RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenRepository
                .findByToken(request.refreshToken())
                .orElseThrow(() -> new InvalidTokenException("Token Inválido"));

        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);
    }

    public void changePassword(NewPasswordRequest request) {

        UUID userId = TenantContextHolder.getUserId();

        UserResponse userResponse = userService.findUserByIdResponse(userId);

        if (!passwordEncoder.matches(request.currentPassword(), userResponse.password())) {
            throw new UnauthorizedException(
                    messageService.get(MessageCode.ERROR_PASSWORD_INCORRECT.getCode())
            );
        }

        validatePassword(request.newPassword(), request.confirmPassword());

        userService.updatePassword(userId, request);
    }

    public void forgotPassword() {

        UserResponse userResponse = userService.findUserByIdResponse(TenantContextHolder.getTenantId());

        //TODO enviar email

    }

    public List<RoleResponse> findRoles() {
        String role = TenantContextHolder.getUserRole();
        if (role.equalsIgnoreCase("MASTER")) {
            return roleRepository
                    .findAll()
                    .stream()
                    .filter(BaseEntity::getActive)
                    .sorted((r1, r2) -> r1.getName().compareTo(r2.getName()))
                    .map(roleMapper::toDTO)
                    .toList();
        } else {
            return roleRepository
                    .findAll()
                    .stream()
                    .filter(BaseEntity::getActive)
                    .filter(r -> !r.getName().equalsIgnoreCase("MASTER"))
                    .sorted((r1, r2) -> r1.getName().compareTo(r2.getName()))
                    .map(roleMapper::toDTO)
                    .toList();
        }
    }

    private void validatePassword(String password, String confirmPassword) {

        if (password == null || password.isBlank()) {
            throw new BusinessException("Senha é obrigatória");
        }

        if (confirmPassword == null || confirmPassword.isBlank()) {
            throw new BusinessException("Confirmação da senha é obrigatória");
        }

        if (!password.equals(confirmPassword)) {
            throw new BusinessException("Senhas divergentes");
        }

        if (password.length() < 8) {
            throw new BusinessException("Senha deve ter no minimo 8 caracteres");
        }

        if (!password.matches(".*[A-Z].*")) {
            throw new BusinessException("A senha deve possuir ao menos uma letra maiúscula");
        }

        if (!password.matches(".*[a-z].*")) {
            throw new BusinessException("A senha deve possuir ao menos uma letra minúscula");
        }

        if (!password.matches(".*\\d.*")) {
            throw new BusinessException("A senha deve possuir ao menos um número");
        }

        if (!password.matches(".*[@$!%*?&].*")) {
            throw new BusinessException("A senha deve possuir ao menos um caractere especial");
        }

    }

    private String getRoleUser(String userId) {
        UserRole userRole = userRoleRepository.findUserRoleByUserAndActiveTrue(UUID.fromString(userId));
        return roleRepository.findRoleById(userRole.getRole()).getDescription();
    }
}
