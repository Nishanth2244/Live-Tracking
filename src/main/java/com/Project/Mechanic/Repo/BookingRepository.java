package com.Project.Mechanic.Repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Project.Mechanic.Entity.Booking;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

}
