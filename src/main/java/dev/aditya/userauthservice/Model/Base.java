package dev.aditya.userauthservice.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)// tells entity to listen for these save and update triggers
public abstract class Base {

    @Id
    //@GeneratedValue -> only 1 allowed per class
    @GeneratedValue(strategy = GenerationType.IDENTITY) //JPA creates this ID at insert operation i.e. save() -> uses DB to create an Id for the object
    private Long id;

    @CreatedDate //tells Spring Data JPA to automatically populate this var with the current system timestamp, the exact moment the record is inserted into the database.
    private Date createdAt; //Date contains current time as well. Better for these scenarios like Creation Date or Modification Date. D.O.B would benefit from Local Date

    @LastModifiedDate //it updates every single time a record is changed in the database
    private Date lastUpdatedAt;


    private Status currentStatus = Status.ACTIVE;

}
