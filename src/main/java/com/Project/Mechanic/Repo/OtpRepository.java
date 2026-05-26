package com.Project.Mechanic.Repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Project.Mechanic.Entity.Otps;

@Repository
public interface OtpRepository extends JpaRepository<Otps, Long> {
	
	Optional<Otps> findByEmailAndIsActiveTrue(String email);

}
