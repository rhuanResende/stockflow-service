package com.desenvolvimento.logica.stockflow.stockflow_service.auth.controller;

import com.desenvolvimento.logica.stockflow.stockflow_common.dto.ApiResponse;
import com.desenvolvimento.logica.stockflow.stockflow_common.enums.MessageCode;
import com.desenvolvimento.logica.stockflow.stockflow_common.service.MessageService;
import com.desenvolvimento.logica.stockflow.stockflow_service.auth.dto.LoginRequest;
import com.desenvolvimento.logica.stockflow.stockflow_service.auth.dto.LoginResponse;
import com.desenvolvimento.logica.stockflow.stockflow_service.auth.dto.RefreshTokenRequest;
import com.desenvolvimento.logica.stockflow.stockflow_service.auth.dto.UserDataResponse;
import com.desenvolvimento.logica.stockflow.stockflow_service.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${app.api.base}/auth")
public class AuthController {

    private final AuthService authService;
    private final MessageService messageService;

    public AuthController(
            AuthService authService,
            MessageService messageService) {
        this.authService = authService;
        this.messageService = messageService;
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return new ApiResponse<>(
                true,
                MessageCode.MESSAGE_SUCCESS_QUERY.getCode(),
                messageService.get(MessageCode.MESSAGE_SUCCESS_QUERY.getCode()),
                authService.login(request)
        );
    }

    @PostMapping("/refresh")
    @PreAuthorize("hasAnyAuthority('MASTER','ADMIN')")
    public ApiResponse<LoginResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return new ApiResponse<>(
                true,
                MessageCode.MESSAGE_SUCCESS_QUERY.getCode(),
                "Refresh realizado com sucesso.",
                authService.refresh(request)
        );
    }

    @GetMapping("/me")
    @PreAuthorize("hasAnyAuthority('MASTER','ADMIN')")
    public ApiResponse<UserDataResponse> me() {
        return new ApiResponse<>(
                true,
                MessageCode.MESSAGE_SUCCESS_QUERY.getCode(),
                "Usuário autenticado.",
                authService.me()
        );
    }

    @PostMapping("/logout")
    @PreAuthorize("hasAnyAuthority('MASTER','ADMIN')")
    public void logout(@Valid @RequestBody RefreshTokenRequest request) {
        authService.logout(request);
    }
}
