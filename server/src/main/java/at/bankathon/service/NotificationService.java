package at.bankathon.service;

import com.google.android.gcm.server.InvalidRequestException;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;

/**
 * Created by vivex on 12/6/15.
 */
@Service
public class NotificationService {

    /**
     * gcmRegId is the id which android app will give to server (one time)
     **/
    public boolean pushNotificationToGCM(String gcmRegId, String message) {
        final String GCM_API_KEY = "fill_me";
        final int retries = 3;
        Sender sender = new Sender(GCM_API_KEY);
        Message msg = new Message.Builder().addData("message", message).build();

        try {
            if (gcmRegId != null) {
                Result result = sender.send(msg, gcmRegId, retries);
                /**
                 * if you want to send to multiple then use below method
                 * send(Message message, List<String> regIds, int retries)
                 **/


                if (StringUtils.isEmpty(result.getErrorCodeName())) {
                    System.out.println("GCM Notification is sent successfully" + result.toString());
                    return true;
                }

                System.out.println("Error occurred while sending push notification :" + result.getErrorCodeName());

            }
        } catch (InvalidRequestException e) {
            System.out.println("Invalid Request");
        } catch (IOException e) {
            System.out.println("IO Exception");
        }
        return false;

    }
}