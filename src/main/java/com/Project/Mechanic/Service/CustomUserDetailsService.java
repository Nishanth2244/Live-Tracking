package com.Project.Mechanic.Service;

import java.util.Collections;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.Project.Mechanic.Entity.Users;
import com.Project.Mechanic.Repo.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found in UserSnapshot DB"));

        // Converting DB Role to Spring Security SimpleGrantedAuthority
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + user.getRoles().name());

        return new User(
                user.getEmail(),
                user.getPassword(),
                Collections.singleton(authority)
        );
    }
}