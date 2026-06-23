package dev.aditya.userauthservice.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Session extends Base{

    //Modern WebTokens can get very large, easily can cross 1000 characters as they contain roles,expiry etc.
    // In MySQL better change the column from standard VARCHAR(255) to TEXT (MAX size: upto 64kb allowed)
    // doesn't mean it takes all 64kb, only consumes the exact number of bytes your token actually uses.
    @Lob//(Large Object) tells Hibernate that this field should map to a long text type (like TEXT or LONGTEXT) instead of a standard VARCHAR
    private String authToken;
    private String refreshToken;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
 }
