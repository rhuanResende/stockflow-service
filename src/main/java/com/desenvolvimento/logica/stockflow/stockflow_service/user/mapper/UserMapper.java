package com.desenvolvimento.logica.stockflow.stockflow_service.user.mapper;

import com.desenvolvimento.logica.stockflow.stockflow_common.dto.CompanyResponse;
import com.desenvolvimento.logica.stockflow.stockflow_common.dto.UserResponse;
import com.desenvolvimento.logica.stockflow.stockflow_service.user.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponse toDTO(User user, CompanyResponse company) {
        return new UserResponse(
                user.getId().toString(),
                user.getName(),
                user.getDocument(),
                user.getEmail(),
                user.getPhone(),
                user.getPassword(),
                user.getFirstAccess(),
                company
        );
    }
}
