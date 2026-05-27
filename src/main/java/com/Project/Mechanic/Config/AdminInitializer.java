package com.Project.Mechanic.Config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.Project.Mechanic.Entity.Roles;
import com.Project.Mechanic.Entity.Users;
import com.Project.Mechanic.Repo.UserRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Component
public class AdminInitializer implements CommandLineRunner {
	
	private UserRepository userRepository;
	private PasswordEncoder passwordEncoder;

	@Override
	public void run(String... args) throws Exception {
		
		if(!userRepository.existsByEmail("admin@mechanic.com")) {
			
			Users users = new Users();
			users.setEmail("admin@mechanic.com");
			users.setPassword(passwordEncoder.encode("admin@123"));
			users.setApprovalStatus(true);
			users.setRoles(Roles.ADMIN);
			users.setIsAvailable(true);
			
			userRepository.save(users);
			
			log.info("Default admin created");
		}
		
		log.info("admin data loaded");
	}

}
