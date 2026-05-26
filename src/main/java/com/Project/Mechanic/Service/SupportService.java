package com.Project.Mechanic.Service;

import org.springframework.stereotype.Service;
import com.Project.Mechanic.DTO.*;
import com.Project.Mechanic.Entity.*;
import com.Project.Mechanic.Repo.SupportTicketRepository;
import com.Project.Mechanic.Repo.BookingRepository;
import com.Project.Mechanic.Repo.UserRepository;
import com.Project.Mechanic.ExceptionHandler.ResourceNotFoundException;
import com.Project.Mechanic.ExceptionHandler.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SupportService {

    private final SupportTicketRepository ticketRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    public String raiseTicket(Long userId, TicketRequestDTO dto) {
        
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
                
        Booking booking = bookingRepository.findById(dto.getBookingId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with ID: " + dto.getBookingId()));
        
        // Ensure user can't raise a ticket for someone else's booking
        if (!booking.getUserId().equals(userId)) {
            throw new UnauthorizedException("You can only raise support tickets for your own bookings.");
        }

        SupportTicket ticket = new SupportTicket();
        ticket.setUser(user);        
        ticket.setBooking(booking);  
        ticket.setSubject(dto.getSubject());
        ticket.setDescription(dto.getDescription());
        
        ticketRepository.save(ticket);
        return "Your query has been submitted successfully.";
    }
    
    

    // 2. Fetch User's Own Tickets
    public List<UserTicketViewDTO> getUserTickets(Long userId) {
    	
        List<SupportTicket> tickets = ticketRepository.findByUserIdWithDetails(userId);
        
        return tickets.stream().map(ticket -> UserTicketViewDTO.builder()
                .ticketId(ticket.getId())
                .bookingId(ticket.getBooking().getId())
                .subject(ticket.getSubject())
                .description(ticket.getDescription())
                .adminReply(ticket.getAdminReply())
                .status(ticket.getStatus().name())
                .createdAt(ticket.getCreatedAt())
                .build()).collect(Collectors.toList());
    }
    
    

    // 3. Fetch Pending Tickets for Admin
    public List<AdminTicketViewDTO> getTicketsForAdmin(TicketStatus status) {
    	
        List<SupportTicket> tickets = ticketRepository.findByStatusWithDetails(status);
        
        return tickets.stream().map(ticket -> AdminTicketViewDTO.builder()
                .ticketId(ticket.getId())
                .subject(ticket.getSubject())
                .description(ticket.getDescription())
                .status(ticket.getStatus().name())
                .createdAt(ticket.getCreatedAt())
                .userName(ticket.getUser().getName()) // Directly fetched via Foreign Key
                .userPhone(ticket.getUser().getPhone())
                .bookingId(ticket.getBooking().getId())
                .bookingProblem(ticket.getBooking().getProblem())
                .totalAmount(ticket.getBooking().getTotalAmount())
                .build()).collect(Collectors.toList());
    }
    
    

    // 4. Resolve Ticket (Admin)
    public String resolveTicket(Long ticketId, AdminReplyDTO dto) {
    	
        SupportTicket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));

        ticket.setAdminReply(dto.getReply());
        ticket.setStatus(TicketStatus.RESOLVED);
        ticket.setResolvedAt(LocalDateTime.now());
        
        ticketRepository.save(ticket);
        return "Ticket resolved successfully.";
    }
}