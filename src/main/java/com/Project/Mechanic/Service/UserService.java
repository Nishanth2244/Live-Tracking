package com.Project.Mechanic.Service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.Project.Mechanic.DTO.UserRegistrationDto;
import com.Project.Mechanic.Entity.Roles;
import com.Project.Mechanic.Entity.Users;
import com.Project.Mechanic.Repo.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public String registerUser(UserRegistrationDto dto) {
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists.");
        }

        Users user = new Users();
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRoles(Roles.USER); 
        user.setApprovalStatus(true);

        userRepository.save(user);
        return "User registered successfully";
    }
}