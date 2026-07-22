package com.desenvolvimento.logica.stockflow.stockflow_service.user.service;

import com.desenvolvimento.logica.stockflow.stockflow_common.dto.CompanyResponse;
import com.desenvolvimento.logica.stockflow.stockflow_common.dto.UserResponse;
import com.desenvolvimento.logica.stockflow.stockflow_common.enums.MessageCode;
import com.desenvolvimento.logica.stockflow.stockflow_common.exception.UnauthorizedException;
import com.desenvolvimento.logica.stockflow.stockflow_common.service.MessageService;
import com.desenvolvimento.logica.stockflow.stockflow_service.auth.dto.NewPasswordRequest;
import com.desenvolvimento.logica.stockflow.stockflow_service.company.service.CompanyService;
import com.desenvolvimento.logica.stockflow.stockflow_service.user.entity.User;
import com.desenvolvimento.logica.stockflow.stockflow_service.user.mapper.UserMapper;
import com.desenvolvimento.logica.stockflow.stockflow_service.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final CompanyService companyService;
    private final MessageService messageService;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            CompanyService companyService,
            MessageService messageService,
            UserMapper userMapper,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.companyService = companyService;
        this.messageService = messageService;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponse findUserById(final UUID id) {

        User user = userRepository.findUserById(id);

        if (user == null) {
            throw new UnauthorizedException(
                    messageService.get(MessageCode.ERROR_USUARIO_NOT_FOUND.getCode())
            );
        }

        CompanyResponse companyResponse = companyService.findById(user.getCompany().toString());

        return userMapper.toDTO(user, companyResponse);
    }

    public UserResponse findUserByDocument(final String document) {

        User user = userRepository.findUserByDocument(document);

        if (user == null) {
            throw new UnauthorizedException(
                    messageService.get(MessageCode.ERROR_USUARIO_NOT_FOUND.getCode())
            );
        }

        CompanyResponse companyResponse = companyService.findById(user.getCompany().toString());

        return userMapper.toDTO(user, companyResponse);
    }

    public UserResponse updatePassword(UUID userId, NewPasswordRequest request) {

        User user = userRepository.findUserById(userId);

        if (user == null) {
            throw new UnauthorizedException(
                    messageService.get(MessageCode.ERROR_USUARIO_NOT_FOUND.getCode())
            );
        }

        CompanyResponse companyResponse = companyService.findById(user.getCompany().toString());

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        user.setFirstAccess(false);
        user.setPasswordChangedAt(LocalDateTime.now());
        user.setPasswordExpiresAt(LocalDateTime.now().plusMonths(2));
        return userMapper.toDTO(userRepository.save(user), companyResponse);
    }
}
