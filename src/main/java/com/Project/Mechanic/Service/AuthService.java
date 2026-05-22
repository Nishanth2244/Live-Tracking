package com.Project.Mechanic.Service;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.Project.Mechanic.DTO.MechanicRegistrationDto;
import com.Project.Mechanic.DTO.UserRegistrationDto;
import com.Project.Mechanic.Entity.Roles;
import com.Project.Mechanic.Entity.Users;
import com.Project.Mechanic.ExceptionHandler.ConflictException;
import com.Project.Mechanic.Repo.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public String registerUser(UserRegistrationDto dto) {
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new ConflictException("Email already exists.");
        }

        Users user = new Users();
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRoles(Roles.USER); 
        user.setApprovalStatus(true);

        userRepository.save(user);
        return "User registered successfully";
    }
    
    
    public String registerMechanic(MechanicRegistrationDto dto) {
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new ConflictException("Email already exists.");
        }

        Users mechanic = new Users();
        mechanic.setName(dto.getName());
        mechanic.setEmail(dto.getEmail());
        mechanic.setPassword(passwordEncoder.encode(dto.getPassword()));
        mechanic.setRoles(Roles.MECHANIC); 
        mechanic.setApprovalStatus(false);
        mechanic.setPhone(dto.getPhone());
        mechanic.setIsAvailable(false);

        // Convert Lat/Lon to PostGIS Point
        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
        // Note: Coordinate order is (longitude, latitude) not (latitude, longitude)
        Point pt = geometryFactory.createPoint(new Coordinate(dto.getLongitude(), dto.getLatitude()));
        mechanic.setLocation(pt);

        userRepository.save(mechanic);
        return "Mechanic registered successfully. Pending Admin Approval.";
    }
}