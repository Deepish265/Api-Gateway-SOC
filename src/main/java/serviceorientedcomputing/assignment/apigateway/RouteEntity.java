/*
 * RouteEntity.java -- Defines RouteEntity class 
 * This code is implemented as part of assignment given  
 * course Service Oriented Computing of MTECH Program Software Engineering
 * Student Name : Deepish Sharma
 * Student Id   : 2022MT93012
 * Course       : Service Oriented Computing
 * Program      : MTECH Software Engineering
 * Student Email: 2022MT93012@wilp.bits-pilani.ac.in
 */


package serviceorientedcomputing.assignment.apigateway;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

import java.util.Date;

/**
 * Define <code>RouteEntity</code> It is persistent entity class managed by
 * spring data and JPA.
 * @author Deepish Sharma
 */
@Entity(name = "Route")
public class RouteEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private String uri;
    private String path;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date", updatable = false)
    private Date createdDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_date")
    private Date updatedDate;
    
    /**
     * Default constructor
     */
    public RouteEntity() {
    }
    
    /**
     * Parameterized constructor
     * @param name           : route name example customer-service
     * @param uri            : route uri example http://localhost:8080/
     * @param path           : route path example /customers/**
     */
    public RouteEntity(String name, String uri, String path) {
        this.name = name;
        this.uri = uri;
        this.path = path;
    }
    
    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the uri
     */
    public String getUri() {
        return uri;
    }

    /**
     * @param uri the uri to set
     */
    public void setUri(String uri) {
        this.uri = uri;
    }

    /**
     * @return the path
     */
    public String getPath() {
        return path;
    }

    /**
     * @param path the path to set
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * @return the createdDate
     */
    public Date getCreatedDate() {
        return createdDate;
    }

    /**
     * @param createdDate the createdDate to set
     */
    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    /**
     * @return the updatedDate
     */
    public Date getUpdatedDate() {
        return updatedDate;
    }

    /**
     * @param updatedDate the updatedDate to set
     */
    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }
    
    @PrePersist
    protected void onCreate() {
        setCreatedDate(new Date());
        setUpdatedDate(getCreatedDate());
    }

    @PreUpdate
    protected void onUpdate() {
        setUpdatedDate(new Date());
    }
}
