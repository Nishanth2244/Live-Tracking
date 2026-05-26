package com.Project.Mechanic.Repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.Project.Mechanic.Entity.SupportTicket;
import com.Project.Mechanic.Entity.TicketStatus;
import java.util.List;


@Repository
public interface SupportTicketRepository extends JpaRepository<SupportTicket, Long> {
    
    // User tickets and related booking information
    @Query("SELECT t FROM SupportTicket t JOIN FETCH t.booking WHERE t.user.id = :userId ORDER BY t.createdAt DESC")
    List<SupportTicket> findByUserIdWithDetails(@Param("userId") Long userId);
    
    // Admin pending tickets, along with User and Booking objects
    @Query("SELECT t FROM SupportTicket t JOIN FETCH t.user JOIN FETCH t.booking WHERE t.status = :status ORDER BY t.createdAt DESC")
    List<SupportTicket> findByStatusWithDetails(@Param("status") TicketStatus status);
}