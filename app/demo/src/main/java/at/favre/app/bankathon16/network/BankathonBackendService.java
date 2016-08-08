package at.favre.app.bankathon16.network;

import java.util.List;

import at.favre.app.bankathon16.model.User;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Created by mario on 23.04.16.
 */
public interface BankathonBackendService {

    @GET("/user/childs")
    Call<List<User>> getAllChilds();

    @GET("/user/{id}")
    Call<User> getUserById(@Path("id") long userId);

    @POST("/user/childs")
    Call<User> createChild(@Body User user);

    @POST("/user/{userId}/pay")
    Call<User> pay(@Path("userId") long userId,
                    @Body Integer amountInCent);

    @POST("/user/{userId}/saveMoney")
    Call<User> saveMoney(@Path("userId") long userId,
                   @Body Integer amountInCent);

    @POST("/user/{userId}/sendMoney")
    Call<User> sendMoney(@Path("userId") long userId,
                          @Body final Integer amountInCent);

    @PUT("/user/{userId}/gcm")
    public Call<User> setGcmId(@Path("userId") long userId,
                         @Body final String gcmId);
}
