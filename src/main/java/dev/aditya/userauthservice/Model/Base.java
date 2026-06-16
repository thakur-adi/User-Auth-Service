package dev.aditya.userauthservice.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.util.Date;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)// tells entity to listen for these save and update triggers
public abstract class Base {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreatedDate //tells Spring Data JPA to automatically populate this var with the current system timestamp, the exact moment the record is inserted into the database.
    private LocalDate createdAt; //Date is a legacy system. 'LocalDate' brings many benefits over 'Date' like no "Time Zone Shift",thread safety etc.

    @LastModifiedDate //it updates every single time a record is changed in the database
    private LocalDate lastUpdatedAt;

    @Enumerated(EnumType.STRING)
    private Status currentStatus;

    Base(){
        this.currentStatus = Status.ACTIVE;
    }
}
