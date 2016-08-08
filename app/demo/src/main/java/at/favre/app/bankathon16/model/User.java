package at.favre.app.bankathon16.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by mario on 23.04.16.
 */
public class User implements Parcelable {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (id != user.id) return false;
        if (amountInCent != null ? !amountInCent.equals(user.amountInCent) : user.amountInCent != null)
            return false;
        if (name != null ? !name.equals(user.name) : user.name != null) return false;
        if (userType != user.userType) return false;
        if (gcmId != null ? !gcmId.equals(user.gcmId) : user.gcmId != null) return false;
        if (savedAmount != null ? !savedAmount.equals(user.savedAmount) : user.savedAmount != null)
            return false;
        return restriction != null ? restriction.equals(user.restriction) : user.restriction == null;

    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (amountInCent != null ? amountInCent.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (userType != null ? userType.hashCode() : 0);
        result = 31 * result + (gcmId != null ? gcmId.hashCode() : 0);
        result = 31 * result + (savedAmount != null ? savedAmount.hashCode() : 0);
        result = 31 * result + (restriction != null ? restriction.hashCode() : 0);
        return result;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeValue(this.amountInCent);
        dest.writeString(this.name);
        dest.writeInt(this.userType == null ? -1 : this.userType.ordinal());
        dest.writeString(this.gcmId);
        dest.writeValue(this.savedAmount);
        dest.writeString(this.restriction);
    }

    public User() {
    }

    protected User(Parcel in) {
        this.id = in.readLong();
        this.amountInCent = (Integer) in.readValue(Integer.class.getClassLoader());
        this.name = in.readString();
        int tmpUserType = in.readInt();
        this.userType = tmpUserType == -1 ? null : UserType.values()[tmpUserType];
        this.gcmId = in.readString();
        this.savedAmount = (Integer) in.readValue(Integer.class.getClassLoader());
        this.restriction = in.readString();
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
