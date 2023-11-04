/*
 * ApiGatewaySecurityFilter.java -- Defines ApiGatewaySecurityFilter class 
 * This code is implemented as part of assignment given  
 * course Service Oriented Computing of MTECH Program Software Engineering
 * Student Name : Deepish Sharma
 * Student Id   : 2022MT93012
 * Course       : Service Oriented Computing
 * Program      : MTECH Software Engineering
 * Student Email: 2022MT93012@wilp.bits-pilani.ac.in
 */

package serviceorientedcomputing.assignment.apigateway;

import io.jsonwebtoken.Claims;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 *
 * @author deepish
 */
@Slf4j
public class ApiGatewaySecurityFilter implements GlobalFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("In ApiGatewaySecurityFilter.filter()...");
        
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();
        String method = request.getMethod().name();
        log.info(method+" "+path);
        
        if (path!=null && path.startsWith("/auth") && 
            method != null && method.startsWith("POST")) {
            log.info("Auth request, allowed without token.");
            return chain.filter(exchange);
        }
        
        String token = exchange.getRequest()
            .getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        log.info("token = "+token);
        if (token == null || token.trim().isEmpty()) {
            log.info("Authorization header is missing!");
            return error(exchange, HttpStatus.BAD_REQUEST,
                "Authorization header is missing!");
        }
        Optional<Claims> opClaims = jwtUtil.getClaims(token);
        if (opClaims.isEmpty()) {
            log.info("Invalid Token! Fail to parse token.");
            return error(exchange, HttpStatus.BAD_REQUEST,
                "Invalid Token!");
        }
        Claims claims = opClaims.get();
        if (jwtUtil.isExpired(claims)) {
            log.info("Token expired!");
            return error(exchange, HttpStatus.UNAUTHORIZED,
                "Token expired! Authenticate again.");
        }
        setHeaders(exchange, claims);
        return chain.filter(exchange);
    }

    private void setHeaders(ServerWebExchange exchange, Claims claims) {
        String userId = String.valueOf(claims.get("userId"));
        String firstName = String.valueOf(claims.get("firstName"));
        String lastName = String.valueOf(claims.get("lastName"));
        String role = String.valueOf(claims.get("role"));
        
        log.info("userId = "+userId 
            + "," + "firstName = "+firstName
            + "," + "lastName = "+lastName
            + "," + "role = "+role);
        
        exchange.getRequest().mutate()
            .header("userId", userId)
            .header("firstName", firstName)
            .header("lastName", lastName)
            .header("role", role)
            .build();        
    }

    /**
     * Return response with http error code and message
     *
     * @param response : Server response object
     * @param status : Represents HttpStatus code
     * @param message : Represents error message
     * @return : Mono<Void>
     */
    private Mono<Void> error(ServerWebExchange exchange, 
        HttpStatus status, String message) {
        
        ServerHttpResponse response = exchange.getResponse();
        //DataBuffer body = response.bufferFactory().wrap(message.getBytes());

        response.setStatusCode(status);
        response.getHeaders().setContentLength(message.length());
        response.writeWith(Mono.just(
            response.bufferFactory().wrap(message.getBytes()))).subscribe();
        
        exchange.mutate().response(response).build();
        return response.setComplete();
    }
}
