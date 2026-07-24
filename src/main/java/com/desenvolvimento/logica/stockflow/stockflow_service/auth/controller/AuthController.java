package com.desenvolvimento.logica.stockflow.stockflow_service.auth.controller;

import com.desenvolvimento.logica.stockflow.stockflow_common.dto.ApiResponse;
import com.desenvolvimento.logica.stockflow.stockflow_common.dto.UserDataResponse;
import com.desenvolvimento.logica.stockflow.stockflow_common.enums.MessageCode;
import com.desenvolvimento.logica.stockflow.stockflow_common.service.MessageService;
import com.desenvolvimento.logica.stockflow.stockflow_service.auth.dto.*;
import com.desenvolvimento.logica.stockflow.stockflow_service.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
                messageService.get(MessageCode.MESSAGE_SUCCESS_QUERY.getCode()),
                authService.login(request)
        );
    }

    @PostMapping("/refresh")
    public ApiResponse<LoginResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return new ApiResponse<>(
                true,
                "Refresh realizado com sucesso.",
                authService.refresh(request)
        );
    }

    @GetMapping("/me")
    @PreAuthorize("hasAnyAuthority('MASTER','ADMIN', 'MANAGER', 'USER')")
    public ApiResponse<UserDataResponse> me() {
        return new ApiResponse<>(
                true,
                "Usuário autenticado.",
                authService.me()
        );
    }

    @PostMapping("/logout")
    @PreAuthorize("hasAnyAuthority('MASTER','ADMIN', 'MANAGER', 'USER')")
    public ApiResponse<Void> logout(@Valid @RequestBody RefreshTokenRequest request) {
        authService.logout(request);
        return new ApiResponse<>(
                true,
                messageService.get(MessageCode.MESSAGE_SUCCESS_QUERY.getCode())
        );
    }

    @PostMapping("/change-initial-password")
    @PreAuthorize("hasAnyAuthority('MASTER','ADMIN', 'MANAGER', 'USER')")
    public ApiResponse<Void> changeInitialPassword(@Valid @RequestBody NewPasswordRequest request){
        authService.changePassword(request);
        return new ApiResponse<>(
                true,
                messageService.get(MessageCode.MESSAGE_SUCCESS_QUERY.getCode())
        );
    }

    @PostMapping("/change-password")
    @PreAuthorize("hasAnyAuthority('MASTER','ADMIN', 'MANAGER', 'USER')")
    public ApiResponse<Void> changePassword(@Valid @RequestBody NewPasswordRequest request){
        authService.changePassword(request);
        return new ApiResponse<>(
                true,
                messageService.get(MessageCode.MESSAGE_SUCCESS_QUERY.getCode())
        );
    }

    @PostMapping("/reset-password")
    @PreAuthorize("hasAnyAuthority('MASTER','ADMIN', 'MANAGER', 'USER')")
    public ApiResponse<Void> resetPassword(@Valid @RequestBody NewPasswordRequest request){
        authService.changePassword(request);
        return new ApiResponse<>(
                true,
                messageService.get(MessageCode.MESSAGE_SUCCESS_QUERY.getCode())
        );
    }

    @PutMapping("/forgot-password")
    @PreAuthorize("hasAnyAuthority('MASTER','ADMIN', 'MANAGER', 'USER')")
    public ApiResponse<Void> forgotPassword(){
        authService.forgotPassword();
        return new ApiResponse<>(
                true,
                messageService.get(MessageCode.MESSAGE_SUCCESS_QUERY.getCode())
        );
    }

    @GetMapping("/findRoles")
    @PreAuthorize("hasAnyAuthority('MASTER','ADMIN')")
    public ApiResponse<List<RoleResponse>> findRoles(){
        return new ApiResponse<>(
                true,
                messageService.get(MessageCode.MESSAGE_SUCCESS_QUERY.getCode()),
                authService.findRoles()
        );
    }
}
