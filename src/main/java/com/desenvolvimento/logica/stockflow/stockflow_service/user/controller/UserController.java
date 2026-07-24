package com.desenvolvimento.logica.stockflow.stockflow_service.user.controller;

import com.desenvolvimento.logica.stockflow.stockflow_common.dto.ApiResponse;
import com.desenvolvimento.logica.stockflow.stockflow_common.dto.UserDataResponse;
import com.desenvolvimento.logica.stockflow.stockflow_common.dto.UserResponse;
import com.desenvolvimento.logica.stockflow.stockflow_common.enums.MessageCode;
import com.desenvolvimento.logica.stockflow.stockflow_common.service.MessageService;
import com.desenvolvimento.logica.stockflow.stockflow_service.user.dto.UserCreateRequest;
import com.desenvolvimento.logica.stockflow.stockflow_service.user.dto.UserUpdateRequest;
import com.desenvolvimento.logica.stockflow.stockflow_service.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("${app.api.base}/user")
public class UserController {

    private final UserService userService;
    private final MessageService messageService;

    public UserController(
            UserService userService,
            MessageService messageService
    ) {
        this.userService = userService;
        this.messageService = messageService;
    }

    @PostMapping()
    @PreAuthorize("hasAnyAuthority('MASTER', 'ADMIN')")
    public ApiResponse<UserDataResponse> create(@Valid @RequestBody UserCreateRequest request){
        return new ApiResponse<>(
                true,
                messageService.get(MessageCode.MESSAGE_SUCCESS_QUERY.getCode()),
                userService.create(request)
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('MASTER', 'ADMIN')")
    public ApiResponse<UserDataResponse> getUser(@PathVariable UUID id) {
        return new ApiResponse<>(
                true,
                messageService.get(MessageCode.MESSAGE_SUCCESS_QUERY.getCode()),
                userService.findUserById(id)
        );
    }

    @GetMapping()
    @PreAuthorize("hasAnyAuthority('MASTER', 'ADMIN')")
    public ApiResponse<Page<UserDataResponse>> getUsers(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "sortBy", defaultValue = "ds_name") String sortBy,
            @RequestParam(name = "companyId", required = false) UUID companyId,
            @RequestParam(name = "profileId", required = false) UUID profileId,
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "document", required = false) String document,
            @RequestParam(name = "status", required = false) String status
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        return new ApiResponse<>(
                true,
                messageService.get(MessageCode.MESSAGE_SUCCESS_QUERY.getCode()),
                userService.getUsers(companyId, profileId, name, document, status, pageable)
        );
    }

    @PutMapping()
    @PreAuthorize("hasAnyAuthority('MASTER', 'ADMIN')")
    public ApiResponse<UserDataResponse> update(@Valid @RequestBody UserUpdateRequest request) {
        return new ApiResponse<>(
                true,
                messageService.get(MessageCode.MESSAGE_SUCCESS_QUERY.getCode()),
                userService.update(request)
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('MASTER', 'ADMIN')")
    public ApiResponse<Void> delete(@PathVariable UUID id) {
        userService.delete(id);
        return new ApiResponse<>(
                true,
                messageService.get(MessageCode.MESSAGE_SUCCESS_QUERY.getCode())
        );
    }

    @PutMapping("/activate/{id}")
    @PreAuthorize("hasAnyAuthority('MASTER', 'ADMIN')")
    public ApiResponse<Void> activate(@PathVariable UUID id) {
        userService.activate(id);
        return new ApiResponse<>(
                true,
                messageService.get(MessageCode.MESSAGE_SUCCESS_QUERY.getCode())
                );
    }
}
