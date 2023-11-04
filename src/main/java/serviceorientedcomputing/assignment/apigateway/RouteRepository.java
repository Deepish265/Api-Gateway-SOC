/*
 * RouteRepository.java -- Defines RouteRepository class 
 * This code is implemented as part of assignment given  
 * course Service Oriented Computing of MTECH Program Software Engineering
 * Student Name : Deepish Sharma
 * Student Id   : 2022MT93012
 * Course       : Service Oriented Computing
 * Program      : MTECH Software Engineering
 * Student Email: 2022MT93012@wilp.bits-pilani.ac.in
 */


package serviceorientedcomputing.assignment.apigateway;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * The <code>RouteRepository</code> is data access object that manages
 * persistence of RouteEntity 
 * It provides functionality to create and retrieve, update and delete
 * RouteEntity from database.
 * @author deepish sharma
 */
@Repository
public interface RouteRepository extends JpaRepository<RouteEntity, Long> {
    
    /**
     * Retrieve RouteEntiy by name 
     * @param name           : name of RouteEntity to be retrieved
     * @return               : Optional<RouteEntity>
     */
    public Optional<RouteEntity> findByName(String name);
}
