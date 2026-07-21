package com.desenvolvimento.logica.stockflow.stockflow_service.auth.service;

import com.desenvolvimento.logica.stockflow.stockflow_common.dto.CompanyResponse;
import com.desenvolvimento.logica.stockflow.stockflow_common.dto.UserResponse;
import com.desenvolvimento.logica.stockflow.stockflow_common.enums.MessageCode;
import com.desenvolvimento.logica.stockflow.stockflow_common.exception.BusinessException;
import com.desenvolvimento.logica.stockflow.stockflow_common.exception.InvalidTokenException;
import com.desenvolvimento.logica.stockflow.stockflow_common.exception.UnauthorizedException;
import com.desenvolvimento.logica.stockflow.stockflow_common.service.MessageService;
import com.desenvolvimento.logica.stockflow.stockflow_security.holder.TenantContextHolder;
import com.desenvolvimento.logica.stockflow.stockflow_security.model.AuthenticatedUser;
import com.desenvolvimento.logica.stockflow.stockflow_security.service.JwtService;
import com.desenvolvimento.logica.stockflow.stockflow_service.auth.dto.LoginRequest;
import com.desenvolvimento.logica.stockflow.stockflow_service.auth.dto.LoginResponse;
import com.desenvolvimento.logica.stockflow.stockflow_service.auth.dto.RefreshTokenRequest;
import com.desenvolvimento.logica.stockflow.stockflow_service.auth.dto.UserDataResponse;
import com.desenvolvimento.logica.stockflow.stockflow_service.auth.entity.RefreshToken;
import com.desenvolvimento.logica.stockflow.stockflow_service.auth.entity.UserRole;
import com.desenvolvimento.logica.stockflow.stockflow_service.auth.mapper.RefreshTokenMapper;
import com.desenvolvimento.logica.stockflow.stockflow_service.auth.repository.RefreshTokenRepository;
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
    private final UserService userService;
    private final CompanyService companyService;
    private final MessageService messageService;
    private final RefreshTokenMapper refreshTokenMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UserRoleRepository userRoleRepository,
            RefreshTokenRepository refreshTokenRepository,
            UserService userService,
            CompanyService companyService,
            MessageService messageService,
            RefreshTokenMapper refreshTokenMapper,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.userRoleRepository = userRoleRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.userService = userService;
        this.companyService = companyService;
        this.messageService = messageService;
        this.refreshTokenMapper = refreshTokenMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
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
                getRolesUser(user.id())
        ));

        String refreshTokenValue = jwtService.generateRefreshToken();
        refreshTokenRepository.save(refreshTokenMapper.toEntity(UUID.fromString(user.id()), refreshTokenValue));

        return new LoginResponse(
                token,
                refreshTokenValue,
                jwtService.getExpiration()
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

        UserResponse user = userService.findUserById(refreshToken.getUser());

        String token = jwtService.generateToken(new AuthenticatedUser(
                UUID.fromString(user.id()),
                UUID.fromString(user.company().id()),
                user.document(),
                getRolesUser(user.id())
        ));

        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);

        String refreshTokenValue = jwtService.generateRefreshToken();
        refreshTokenRepository.save(refreshTokenMapper.toEntity(UUID.fromString(user.id()), refreshTokenValue));

        return new LoginResponse(
                token,
                refreshTokenValue,
                jwtService.getExpiration()
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
                getRolesUser(user.id())
        );
    }

    public void logout(RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenRepository
                .findByToken(request.refreshToken())
                .orElseThrow(() -> new InvalidTokenException("Token Inválido"));

        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);
    }

    private List<String> getRolesUser(String userId) {
        List<UserRole> userRoles = userRoleRepository.findUserRoleByUser(UUID.fromString(userId));
        return userRoles.stream()
                .filter(userRole -> Boolean.FALSE.equals(userRole.getDeleted()))
                .map(userRole -> userRole.getRole().getName())
                .toList();
    }

    private CompanyResponse getCompany(String tenant) {
        return companyService.findById(tenant);
    }
}
