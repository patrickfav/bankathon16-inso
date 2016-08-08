package at.bankathon.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.math.BigDecimal;

/**
 * Created by mario on 23.04.16.
 */
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private Integer amountInCent;
    private String name;
    private UserType userType;
    private String gcmId;
    private Integer savedAmount;

    //Optional
    private String restriction;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Integer getAmountInCent() {
        return amountInCent;
    }

    public void setAmountInCent(Integer amountInCent) {
        this.amountInCent = amountInCent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public String getGcmId() {
        return gcmId;
    }

    public void setGcmId(String gcmId) {
        this.gcmId = gcmId;
    }

    public String getRestriction() {
        return restriction;
    }

    public void setRestriction(String restriction) {
        this.restriction = restriction;
    }

    public Integer getSavedAmount() {
        return savedAmount;
    }

    public void setSavedAmount(Integer savedAmount) {
        this.savedAmount = savedAmount;
    }
}
