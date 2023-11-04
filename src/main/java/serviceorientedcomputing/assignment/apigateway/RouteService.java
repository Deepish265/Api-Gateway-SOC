/*
 * RouteService.java -- Defines RouteService class 
 * This code is implemented as part of assignment given  
 * course Service Oriented Computing of MTECH Program Software Engineering
 * Student Name : Deepish Sharma
 * Student Id   : 2022MT93012
 * Course       : Service Oriented Computing
 * Program      : MTECH Software Engineering
 * Student Email: 2022MT93012@wilp.bits-pilani.ac.in
 */


package serviceorientedcomputing.assignment.apigateway;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * The <code>RouteService</code> is microservice that provides REST/JSON 
 * interface to manage routes used by ApiGateway. 
 * It provides functionality to create, retrieve, update and delete routes
 * @author Deepish Sharma
 */
@Slf4j
@RestController
public class RouteService {

    @Autowired
    private ApplicationEventPublisher eventPublisher;
    
    @Autowired
    private RouteRepository routeRepository;
    
    /**
     * Retrieves list of all Routes
     * @return              : list of type List<RouteEntity>
     */
    @GetMapping("/routes")
    public ResponseEntity<List<RouteEntity>> listRoutes() {
        log.info("In RouteService.listRoutes()...");
        List<RouteEntity> routeList = routeRepository.findAll();
        return ResponseEntity.ok(routeList);
    }
    
    /**
     * Creates new or update existing Route
     * @param entity         : entity of type RouteEntity
     * @return               : customer of type RouteEntity  
     */
    @Transactional
    @PostMapping("/routes")
    public ResponseEntity<?> saveRoute(@RequestBody RouteEntity entity) {
        log.info("In RouteService.saveRoute()...");
        log.info("Route name: " + entity.getName());
        List<String> errors = validate(entity);
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(errors);
        }
        RouteEntity dbEntity;
        Optional<RouteEntity> opEntity = routeRepository.findByName(entity.getName());
        if (opEntity.isPresent()) { //Update existing route with same
            dbEntity = opEntity.get();
            dbEntity.setPath(entity.getPath());
            dbEntity.setUri(entity.getUri());
        } else { //insert new route
            dbEntity = entity;
        }
        routeRepository.save(dbEntity);
        eventPublisher.publishEvent(new RefreshRoutesEvent(this));
        return ResponseEntity.ok(dbEntity);
    }

    /**
     * Deletes existing route with specified id
     * @param id             : route id of type Long
     * @return               : success or error message wrap in ResponseEntity<String>
     */
    @Transactional
    @DeleteMapping("/routes/{id}")
    public ResponseEntity<String> deleteRoute(@PathVariable Long id) {
        Optional<RouteEntity> optionalEntity = routeRepository.findById(id);

        if (optionalEntity.isPresent()) {
            routeRepository.delete(optionalEntity.get());
            eventPublisher.publishEvent(new RefreshRoutesEvent(this));
            return ResponseEntity.status(HttpStatus.OK)
                .body("Route with ID '" + id
                    + "' and name  '" + optionalEntity.get().getName()
                    + "' deleted!");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Customer with ID " + id + " not found");
        }
    }
    
    /**
     * Validates input for correctness. 
     * @param entity         : input parameter of type RouteEntity
     * @return               : return value of List<String>
     */
    private List<String> validate(RouteEntity entity) {
        List<String> errors = new ArrayList<>();
        if (entity.getName() == null || entity.getName().trim().equals("")) {
            errors.add("Route name is mandatory");
        }
        if (entity.getUri() == null || entity.getUri().trim().equals("")) {
            errors.add("Uri is mandatory");
        }
        if (entity.getPath() == null || entity.getPath().trim().equals("")) {
            errors.add("Path is mandatory");
        }
        return errors;
    }
}
