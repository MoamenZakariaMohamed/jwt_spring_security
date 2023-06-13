package com.santechture.api.controller;


import com.santechture.api.configuration.JwtService;
import com.santechture.api.dto.GeneralResponse;
import com.santechture.api.exception.BusinessExceptions;
import com.santechture.api.service.UserService;
import com.santechture.api.validation.AddUserRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(path = "user")
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;
    public UserController(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @GetMapping
    public ResponseEntity<GeneralResponse> list(Pageable pageable){

        return userService.list(pageable);
    }
    @PostMapping
    public ResponseEntity<GeneralResponse> addNewUser(@RequestBody AddUserRequest request , HttpServletRequest requestheader) throws BusinessExceptions {
        String token = requestheader.getHeader("token");
        jwtService.logout(token.substring(7));
        return userService.addNewUser(request);
    }
}
