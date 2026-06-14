package dev.aditya.userauthservice.Model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDate;
import java.util.Date;

@Getter
@Setter
@MappedSuperclass
public abstract class Base {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreatedDate //tells Spring Data JPA to automatically populate this var with the current system timestamp, the exact moment the record is inserted into the database.
    private LocalDate createdAt; //Date is a legacy system. 'LocalDate' brings many benefits over 'Date' like no "Time Zone Shift",thread safety etc.

    @LastModifiedDate //it updates every single time a record is changed in the database
    private LocalDate lastUpdatedAt; //Date is a legacy system. 'LocalDate' brings many benefits over 'Date' like no "Time Zone Shift",thread safety etc.

    private Status currentStatus;

    Base(){
        this.currentStatus =Status.ACTIVE;
    }
}
