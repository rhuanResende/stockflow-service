package com.desenvolvimento.logica.stockflow.stockflow_service.config;

import com.desenvolvimento.logica.stockflow.stockflow_service.auth.entity.Role;
import com.desenvolvimento.logica.stockflow.stockflow_service.user.entity.User;
import com.desenvolvimento.logica.stockflow.stockflow_service.auth.entity.UserRole;
import com.desenvolvimento.logica.stockflow.stockflow_service.auth.repository.RoleRepository;
import com.desenvolvimento.logica.stockflow.stockflow_service.user.repository.UserRepository;
import com.desenvolvimento.logica.stockflow.stockflow_service.auth.repository.UserRoleRepository;
import com.desenvolvimento.logica.stockflow.stockflow_service.company.entity.Company;
import com.desenvolvimento.logica.stockflow.stockflow_service.company.repository.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class InitialDataLoader implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRoleRepository userRoleRepository;
    @Autowired
    private CompanyRepository companyRepository;

    @Override
    public void run(String... args) throws Exception {
        Company company = loadCompany();
        Role role = loadRoles();
        User user = loadUser(company);
        loadUserRole(user, role);
    }

    private Company loadCompany() {
        if (companyRepository.count() > 0) {
            return companyRepository.findAll().getFirst();
        }
        Company company = new Company();
        company.setName("LOGICA DESENVOLVIMENTO");
        company.setDocument("43448681000168");
        company.setDeleted(false);
        return companyRepository.save(company);
    }

    private Role loadRoles() {
        if (roleRepository.count() > 0) {
            return roleRepository.findAll().getFirst();
        }
        saveRole("MASTER");
        saveRole("ADMIN");
        saveRole("MANAGER");
        saveRole("OPERATOR");
        return roleRepository.findRoleByName("MASTER");
    }

    private void saveRole(String name) {
        Role role = new Role();
        role.setName(name.toUpperCase());
        roleRepository.save(role);
    }

    private User loadUser(Company company) {
        if (userRepository.count() > 0) {
            return userRepository.findAll().getFirst();
        }
        User user = new User();
        user.setTenant(company.getId());
        user.setName("RHUAN SILVA RESENDE");
        user.setDocument("03412808105");
        user.setEmail("rhuan.resende@hotmail.com.br");
        user.setPhone("62996487512");
        user.setPassword(passwordEncoder.encode("Abc@123"));
        user.setFirstAccess(true);
        user.setDeleted(false);
        return userRepository.save(user);
    }

    private void loadUserRole(User user, Role role) {
        if (userRoleRepository.count() > 0) {
            return;
        }
        UserRole userRole = new UserRole();
        userRole.setUser(user.getId());
        userRole.setRole(role);
        userRoleRepository.save(userRole);
    }
}
