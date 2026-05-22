package com.Project.Mechanic.Repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.Project.Mechanic.Entity.Booking;
import com.Project.Mechanic.Entity.BookingStatus;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

	List<Booking> findByStatus(BookingStatus status);

	@Query("SELECT b, u.name, m.name FROM Booking b " +
	           "LEFT JOIN Users u ON b.userId = u.id " +
	           "LEFT JOIN Users m ON b.mechanicId = m.id " +
	           "WHERE b.status = :status")
	    List<Object[]> findBookingHistoryWithNames(@Param("status") BookingStatus status);
}
