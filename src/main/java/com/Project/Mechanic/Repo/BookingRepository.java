package com.Project.Mechanic.Repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.Project.Mechanic.Entity.Booking;
import com.Project.Mechanic.Entity.BookingStatus;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

	List<Booking> findByStatus(BookingStatus status);

    Page<Booking> findByStatus(BookingStatus status, Pageable pageable);
//   Booking history for ADMIN
	@Query("SELECT b, u.name, m.name FROM Booking b " +
	           "LEFT JOIN Users u ON b.userId = u.id " +
	           "LEFT JOIN Users m ON b.mechanicId = m.id " +
	           "WHERE b.status = :status")
	    List<Object[]> findBookingHistoryWithNames(@Param("status") BookingStatus status);
	    
	
//	    Booking history of User
	    @Query("SELECT b, m.name, m.phone FROM Booking b " +
	    	       "LEFT JOIN Users m ON b.mechanicId = m.id " +
	    	       "WHERE b.userId = :userId AND b.status = :status " +
	    	       "ORDER BY b.createdAt DESC")
	    	List<Object[]> findUserBookingHistory(@Param("userId") Long userId, @Param("status") BookingStatus status);
	    	
	    	
	    	@Query("SELECT COALESCE(SUM(b.totalAmount), 0.0) FROM Booking b WHERE b.status = :status")
	    	Double getTotalRevenue(@Param("status") BookingStatus status);
	    	
	 
//	Booking History for Mechanic
	    	@Query("SELECT b, u.name, u.phone FROM Booking b " +
	    		       "LEFT JOIN Users u ON b.userId = u.id " +
	    		       "WHERE b.mechanicId = :mechanicId AND b.status = :status " +
	    		       "ORDER BY b.createdAt DESC")
	    		List<Object[]> findMechanicBookingHistory(@Param("mechanicId") Long mechanicId, @Param("status") BookingStatus status, Pageable pageable);
    @Query("""
            SELECT COALESCE(SUM(b.partsCost),0)
            FROM Booking b
            """)
    Double getTotalExpenses();

    @Query("""
            SELECT COALESCE(SUM(b.totalAmount - b.partsCost),0)
            FROM Booking b
            """)
    Double getTotalProfit();
    @Query(value = """
    SELECT
        TO_CHAR(created_at, 'Day') AS day,
        COALESCE(SUM(total_amount),0) AS sales
    FROM booking
    WHERE created_at >= CURRENT_DATE - INTERVAL '6 days'
    GROUP BY DATE(created_at), TO_CHAR(created_at, 'Day')
    ORDER BY DATE(created_at)
    """, nativeQuery = true)
    List<Object[]> getWeeklySales();
    @Query(value = """
    SELECT 
        TO_CHAR(created_at, 'Day') AS day,
        COALESCE(SUM(total_amount - parts_cost),0) AS profit,
        COALESCE(SUM(parts_cost),0) AS expense
    FROM booking
    WHERE created_at >= CURRENT_DATE - INTERVAL '6 days'
    GROUP BY DATE(created_at), TO_CHAR(created_at, 'Day')
    ORDER BY DATE(created_at)
    """, nativeQuery = true)
    List<Object[]> getWeeklyFinance();
    Long countByMechanicId(Long mechanicId);

    Long countByMechanicIdAndStatus(
            Long mechanicId,
            BookingStatus status
    );
    @Query(value = """
    SELECT 
        TO_CHAR(created_at, 'Day') AS day,
        COUNT(id) AS totalJobs
    FROM booking
    WHERE mechanic_id = :mechanicId
    AND created_at >= CURRENT_DATE - INTERVAL '6 days'
    GROUP BY DATE(created_at), TO_CHAR(created_at, 'Day')
    ORDER BY DATE(created_at)
    """, nativeQuery = true)
    List<Object[]> getWeeklyJobs(Long mechanicId);
}
