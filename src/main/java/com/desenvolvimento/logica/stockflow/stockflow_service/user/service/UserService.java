package com.desenvolvimento.logica.stockflow.stockflow_service.user.service;

import com.desenvolvimento.logica.stockflow.stockflow_common.dto.CompanyResponse;
import com.desenvolvimento.logica.stockflow.stockflow_common.dto.UserResponse;
import com.desenvolvimento.logica.stockflow.stockflow_common.enums.MessageCode;
import com.desenvolvimento.logica.stockflow.stockflow_common.exception.UnauthorizedException;
import com.desenvolvimento.logica.stockflow.stockflow_common.service.MessageService;
import com.desenvolvimento.logica.stockflow.stockflow_service.company.service.CompanyService;
import com.desenvolvimento.logica.stockflow.stockflow_service.user.entity.User;
import com.desenvolvimento.logica.stockflow.stockflow_service.user.mapper.UserMapper;
import com.desenvolvimento.logica.stockflow.stockflow_service.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final CompanyService companyService;
    private final MessageService messageService;
    private final UserMapper userMapper;

    public UserService(
            UserRepository userRepository,
            CompanyService companyService,
            MessageService messageService,
            UserMapper userMapper
    ) {
        this.userRepository = userRepository;
        this.companyService = companyService;
        this.messageService = messageService;
        this.userMapper = userMapper;
    }

    public UserResponse findUserById(final UUID id) {

        User user = userRepository.findUserById(id);

        if (user == null) {
            throw new UnauthorizedException(
                    messageService.get(MessageCode.ERROR_USUARIO_NOT_FOUND.getCode())
            );
        }

        CompanyResponse companyResponse = companyService.findById(user.getTenant().toString());

        return userMapper.toDTO(user, companyResponse);
    }

    public UserResponse findUserByDocument(final String document) {

        User user = userRepository.findUserByDocument(document);

        if (user == null) {
            throw new UnauthorizedException(
                    messageService.get(MessageCode.ERROR_USUARIO_NOT_FOUND.getCode())
            );
        }

        CompanyResponse companyResponse = companyService.findById(user.getTenant().toString());

        return userMapper.toDTO(user, companyResponse);
    }
}
