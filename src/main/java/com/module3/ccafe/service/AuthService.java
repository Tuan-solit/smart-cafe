package com.module3.ccafe.service;


import com.module3.ccafe.dto.request.LoginRequest;
import com.module3.ccafe.dto.request.RegisterRequest;
import com.module3.ccafe.dto.response.LoginResponse;
import com.module3.ccafe.dto.response.RegisterResponse;
import com.module3.ccafe.entity.User;
import com.module3.ccafe.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository ;

    public LoginResponse login(LoginRequest loginRequest){
        User user =   userRepository.findByPhone(loginRequest.getPhone());
        if(user.getPassword().equals(loginRequest.getPassword())){
            LoginResponse loginResponse = new LoginResponse();
            loginResponse.setPhone(user.getPhone());
            loginResponse.setPassword(user.getPassword());
        }
        return null;
    }

    public RegisterResponse register(RegisterRequest registerRequest){
        User user = new User();
        user.setFullName(registerRequest.getFullName());
        user.setPhone(registerRequest.getPhone());
        user.setPassword(registerRequest.getPassword());
        userRepository.save(user);

        RegisterResponse registerResponse = new RegisterResponse();
        registerResponse.setFullName(user.getFullName());
        registerResponse.setPassword(user.getPassword());
        registerResponse.setPhone(user.getPhone());
        return registerResponse;
    }
}
