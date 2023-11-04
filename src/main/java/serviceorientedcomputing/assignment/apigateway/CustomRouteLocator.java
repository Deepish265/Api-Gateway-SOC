/*
 * CustomRouteLocator.java -- Defines CustomRouteLocator class 
 * This code is implemented as part of assignment given  
 * course Service Oriented Computing of MTECH Program Software Engineering
 * Student Name : Deepish Sharma
 * Student Id   : 2022MT93012
 * Course       : Service Oriented Computing
 * Program      : MTECH Software Engineering
 * Student Email: 2022MT93012@wilp.bits-pilani.ac.in
 */

package serviceorientedcomputing.assignment.apigateway;

import java.util.List;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.BooleanSpec;
import org.springframework.cloud.gateway.route.builder.Buildable;
import org.springframework.cloud.gateway.route.builder.PredicateSpec;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import reactor.core.publisher.Flux;

/**
 * The <code>CustomRouteLocator</code> dynamically loads  
 * It overrides getRoutes() method to provide custom implementation
 * @author Deepish Sharma
 */
public class CustomRouteLocator implements RouteLocator {

    private final RouteLocatorBuilder routeLocatorBuilder;
    private final RouteRepository routeRepository;

    /**
     * Parameterized constructor
     * @param routeLocatorBuilder - default RouteLocatorBuilder
     * @param routeRepository     - instance of RouteReposiory
     */
    public CustomRouteLocator(RouteLocatorBuilder routeLocatorBuilder,
        RouteRepository routeRepository) {
        this.routeLocatorBuilder = routeLocatorBuilder;
        this.routeRepository = routeRepository;
    }
    
    /**
     * Get routes
     * @return               - instance Flux<Route>  
     */
    @Override
    public Flux<Route> getRoutes() {
        RouteLocatorBuilder.Builder routesBuilder = routeLocatorBuilder.routes();
        List<RouteEntity> routeList = routeRepository.findAll();
        for (RouteEntity entity : routeList) {
            routesBuilder.route(entity.getName(), 
                predicateSpec -> getPredicateSpec(entity, predicateSpec));
        }
        return routesBuilder.build().getRoutes();
    }

    /**
     * Updates default PredicateSpec 
     * @param entity         - instance of RouteEntity using which Route is created
     * @param predicateSpec  - instance of PredicateSpec
     * @return               - instance of Buildable<Route>
     */
    private Buildable<Route> getPredicateSpec(RouteEntity entity, 
        PredicateSpec predicateSpec) {
        BooleanSpec booleanSpec = predicateSpec.path(entity.getPath());
        return booleanSpec.uri(entity.getUri());
    }
}
