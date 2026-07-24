package com.desenvolvimento.logica.stockflow.stockflow_service.user.service;

import com.desenvolvimento.logica.stockflow.stockflow_common.dto.CompanyResponse;
import com.desenvolvimento.logica.stockflow.stockflow_common.dto.UserDataResponse;
import com.desenvolvimento.logica.stockflow.stockflow_common.dto.UserResponse;
import com.desenvolvimento.logica.stockflow.stockflow_common.enums.MessageCode;
import com.desenvolvimento.logica.stockflow.stockflow_common.exception.BusinessException;
import com.desenvolvimento.logica.stockflow.stockflow_common.exception.UnauthorizedException;
import com.desenvolvimento.logica.stockflow.stockflow_common.service.MessageService;
import com.desenvolvimento.logica.stockflow.stockflow_security.holder.TenantContextHolder;
import com.desenvolvimento.logica.stockflow.stockflow_service.auth.dto.NewPasswordRequest;
import com.desenvolvimento.logica.stockflow.stockflow_service.auth.dto.RoleResponse;
import com.desenvolvimento.logica.stockflow.stockflow_service.auth.service.RoleService;
import com.desenvolvimento.logica.stockflow.stockflow_service.auth.service.UserRoleService;
import com.desenvolvimento.logica.stockflow.stockflow_service.company.service.CompanyService;
import com.desenvolvimento.logica.stockflow.stockflow_service.user.dto.UserCreateRequest;
import com.desenvolvimento.logica.stockflow.stockflow_service.user.dto.UserUpdateRequest;
import com.desenvolvimento.logica.stockflow.stockflow_service.user.entity.User;
import com.desenvolvimento.logica.stockflow.stockflow_service.user.mapper.UserMapper;
import com.desenvolvimento.logica.stockflow.stockflow_service.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class UserService {

    private static final String PASSWORD_DEFAULT = "Abc@123";

    private final UserRepository userRepository;
    private final CompanyService companyService;
    private final RoleService roleService;
    private final UserRoleService userRoleService;
    private final MessageService messageService;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            CompanyService companyService,
            RoleService roleService,
            UserRoleService userRoleService,
            MessageService messageService,
            UserMapper userMapper,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.companyService = companyService;
        this.roleService = roleService;
        this.userRoleService = userRoleService;
        this.messageService = messageService;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public UserDataResponse create(UserCreateRequest request) {

        var existingUser = userRepository.findUserByDocumentAndActiveTrue(request.document());

        if (existingUser != null) {
            throw new BusinessException("Usuário já cadastrado.");
        }

        CompanyResponse companyResponse = companyService.findCompanyById(request.companyId());

        RoleResponse roleResponse = roleService.findRoleById(request.profileId());

        User user = userMapper.toEntity(
                request,
                companyResponse,
                passwordEncoder.encode(PASSWORD_DEFAULT));

        user = userRepository.save(user);

        userRoleService.save(user.getId(), UUID.fromString(roleResponse.id()));

        return userMapper.toUserDataDTO(user, companyResponse, roleResponse.name());
    }

    public UserDataResponse findUserById(final UUID id) {
        User user = getUserByIdAndActiveTrue(id);
        CompanyResponse companyResponse = companyService.findCompanyById(user.getCompany());
        return userMapper.toUserDataDTO(user, companyResponse, userRoleService.findRoleByUser(user.getId()));
    }

    public UserResponse findUserByIdResponse(final UUID id) {
        User user = getUserByIdAndActiveTrue(id);
        CompanyResponse companyResponse = companyService.findCompanyById(user.getCompany());
        return userMapper.toDTO(user, companyResponse, userRoleService.findRoleByUser(user.getId()));
    }

    public Page<UserDataResponse> getUsers(
            final UUID companyId,
            final UUID profileId,
            final String name,
            final String document,
            final String status,
            final Pageable pageable) {

        String role = TenantContextHolder.getUserRole();

        UUID tenantId = null;

        if (!role.equalsIgnoreCase("MASTER")) {
            tenantId = UUID.fromString(getCompany(companyId).id());
        }

        Page<User> page = userRepository.filter(
                tenantId,
                profileId != null ? UUID.fromString(getProfile(profileId).id()) : null,
                name,
                document,
                status,
                pageable
        );
        return page.map(user -> {
            CompanyResponse companyResponse = companyService.findCompanyById(user.getCompany());
            return userMapper.toUserDataDTO(
                    user,
                    companyResponse,
                    userRoleService.findRoleByUser(user.getId())
            );
        });
    }

    @Transactional
    public UserDataResponse update(UserUpdateRequest request) {
        var existingUser = getUserByIdAndActiveTrue(request.id());
        CompanyResponse companyResponse = companyService.findCompanyById(request.companyId());
        RoleResponse roleResponse = roleService.findRoleById(request.profileId());
        User user = userMapper.toEntity(existingUser, companyResponse, request);
        user = userRepository.save(user);
        userRoleService.save(user.getId(), UUID.fromString(roleResponse.id()));
        return userMapper.toUserDataDTO(user, companyResponse, roleResponse.name());
    }

    public void delete(UUID id) {
        var existingUser = getUserByIdAndActiveTrue(id);
        existingUser.setActive(false);
        userRepository.save(existingUser);
    }

    public void activate(UUID id) {
        var existingUser = getUserByIdAndActiveFalse(id);
        existingUser.setActive(true);
        userRepository.save(existingUser);
    }

    public UserResponse findUserByDocument(final String document) {

        User user = userRepository.findUserByDocumentAndActiveTrue(document);

        if (user == null) {
            throw new UnauthorizedException(
                    messageService.get(MessageCode.ERROR_USUARIO_NOT_FOUND.getCode())
            );
        }

        CompanyResponse companyResponse = companyService.findCompanyById(user.getCompany());

        return userMapper.toDTO(user, companyResponse, userRoleService.findRoleByUser(user.getId()));
    }

    public UserResponse updatePassword(UUID userId, NewPasswordRequest request) {

        User user = userRepository.findUserByIdAndActiveTrue(userId);

        if (user == null) {
            throw new UnauthorizedException(
                    messageService.get(MessageCode.ERROR_USUARIO_NOT_FOUND.getCode())
            );
        }

        CompanyResponse companyResponse = companyService.findCompanyById(user.getCompany());

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        user.setFirstAccess(false);
        user.setPasswordChangedAt(LocalDateTime.now());
        user.setPasswordExpiresAt(LocalDateTime.now().plusMonths(2));
        return userMapper.toDTO(
                userRepository.save(user),
                companyResponse,
                userRoleService.findRoleByUser(user.getId())
        );
    }

    private CompanyResponse getCompany(UUID companyId) {
        UUID id = companyId != null ? companyId : TenantContextHolder.getTenantId();
        return companyService.findCompanyById(id);
    }

    private RoleResponse getProfile(UUID profileId) {
        return roleService.findRoleById(profileId);
    }

    private User getUserByIdAndActiveTrue(UUID id){
        User user = userRepository.findUserByIdAndActiveTrue(id);

        if (user == null) {
            throw new UnauthorizedException(
                    messageService.get(MessageCode.ERROR_USUARIO_NOT_FOUND.getCode())
            );
        }

        return user;
    }

    private User getUserByIdAndActiveFalse(UUID id){
        User user = userRepository.findUserByIdAndActiveFalse(id);

        if (user == null) {
            throw new UnauthorizedException(
                    messageService.get(MessageCode.ERROR_USUARIO_NOT_FOUND.getCode())
            );
        }

        return user;
    }
}
