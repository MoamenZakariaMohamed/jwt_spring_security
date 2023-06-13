package com.santechture.api.service;

import com.santechture.api.configuration.JwtService;
import com.santechture.api.dto.GeneralResponse;
import com.santechture.api.dto.admin.AdminDto;
import com.santechture.api.entity.Admin;
import com.santechture.api.exception.BusinessExceptions;
import com.santechture.api.repository.AdminRepository;
import com.santechture.api.validation.LoginRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class AdminService {


    private final AdminRepository adminRepository;
    private final JwtService jwtService;


    public AdminService(AdminRepository adminRepository, JwtService jwtService) {
        this.adminRepository = adminRepository;
        this.jwtService = jwtService;
    }

    public ResponseEntity<GeneralResponse> login(LoginRequest request) throws BusinessExceptions {

        Admin admin = adminRepository.findByUsernameIgnoreCase(request.getUsername());

        if(Objects.isNull(admin) || !admin.getPassword().equals(request.getPassword())){
            throw new BusinessExceptions("login.credentials.not.match");
        }
        String token = "Bearer " + jwtService.generateToken(admin);
        return new GeneralResponse().response(new AdminDto(admin , token));
    }
}
