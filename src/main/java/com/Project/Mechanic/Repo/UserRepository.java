package com.Project.Mechanic.Repo;

import com.Project.Mechanic.DTO.MechanicDistanceProjection;
import com.Project.Mechanic.Entity.Roles;
import com.Project.Mechanic.Entity.Users;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, Long> {

	Optional<Users> findByEmail(String email);

	boolean existsByEmail(String email);

//	@Query(value = "SELECT * FROM \"userSnapshot\" WHERE roles = 'MECHANIC' AND is_available = true AND ST_DWithin(location::geography, ST_SetSRID(ST_MakePoint(:lon, :lat), 4326)::geography, :radiusInMeters)", nativeQuery = true)
//	List<Users> findNearbyAvailableMechanics(@Param("lon") double lon, @Param("lat") double lat, @Param("radiusInMeters") double radiusInMeters);

//	@Query(value = "SELECT * FROM \"user_snapshot\" " + "WHERE roles = 'MECHANIC' AND is_available = true "
//			+ "ORDER BY location::geography <-> ST_SetSRID(ST_MakePoint(:lon, :lat), 4326)::geography ASC "
//			+ "LIMIT 10", nativeQuery = true)
//	List<Users> findNearestMechanics(@Param("lon") double lon, @Param("lat") double lat);
	
	
	@Query(value = "SELECT " +
            "id AS id, " +
            "email AS email, " +
            "name AS name," +
            "experience AS experience,"+
            "phone AS phone, " +
            "ST_Y(location::geometry) AS latitude, " +
            "ST_X(location::geometry) AS longitude, " +
            "ST_Distance(location::geography, ST_SetSRID(ST_MakePoint(:lon, :lat), 4326)::geography) AS distance " +
            "FROM \"user_snapshot\" " +
            "WHERE roles = 'MECHANIC' AND is_available = true AND approval_status = true " +
            "ORDER BY location::geography <-> ST_SetSRID(ST_MakePoint(:lon, :lat), 4326)::geography ASC " +
            "LIMIT 10", nativeQuery = true)
List<MechanicDistanceProjection> findNearestMechanicsWithDistance(@Param("lon") double lon, @Param("lat") double lat);

	List<Users> findByApprovalStatus(boolean status);
	
	long countByRoles(Roles roles);
	long countByRolesAndApprovalStatus(Roles roles, Boolean approvalStatus);
	
	List<Users> findByRoles(Roles role);

}