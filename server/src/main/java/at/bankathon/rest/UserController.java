package at.bankathon.rest;

import at.bankathon.dao.UserRepository;
import at.bankathon.entities.User;
import at.bankathon.entities.UserType;
import at.bankathon.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mario on 22.04.16.
 */
@RestController
@CrossOrigin
@RequestMapping(value = "user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationService notificationService;

    @RequestMapping(path = "/childs", method = RequestMethod.GET)
    public List<User> getAllChilds() {
        List<User> allUsers = userRepository.findAll();
        List<User> children = new ArrayList<>();
        for (User user : allUsers) {
            if (user.getUserType() == UserType.CHILD) {
                children.add(user);
            }
        }
        return children;
    }

    @RequestMapping(path = "/{userId}", method = RequestMethod.GET)
    public User getUser(@PathVariable(value = "userId") long userId) {
        return userRepository.findOne(userId);
    }

    @RequestMapping(path = "/childs", method = RequestMethod.POST)
    public User createChild(@RequestBody final User user) {
        user.setAmountInCent(0);
        user.setSavedAmount(0);
        user.setUserType(UserType.CHILD);
        return userRepository.saveAndFlush(user);
    }

    @RequestMapping(path = "/{userId}/gcm", method = RequestMethod.PUT)
    public User setGcmId(@PathVariable(value = "userId") long userId,
                         @RequestBody final String gcmId) {
        String tempGcmId = gcmId != null ? gcmId.replace("\"", "") : null;
        User user = userRepository.findOne(userId);
        user.setGcmId(tempGcmId);
        userRepository.saveAndFlush(user);
        return user;
    }


    @RequestMapping(path = "/{userId}/pay", method = RequestMethod.POST)
    public ResponseEntity<User> pay(@PathVariable(value = "userId") long userId,
                    @RequestBody final Integer amountInCent) {
        User user = userRepository.findOne(userId);
        if (amountInCent <= user.getAmountInCent()) {
            user.setAmountInCent(user.getAmountInCent() - amountInCent);
            userRepository.saveAndFlush(user);

            User parent = userRepository.findOne(1L);

            if (parent.getGcmId() != null) {
                notificationService.pushNotificationToGCM(parent.getGcmId(), "CHILD_PAYMENT;" + amountInCent + ";" + user.getName());
            }
        } else {
            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).body(null);
        }

        return ResponseEntity.ok(user);
    }

    @RequestMapping(path = "/{userId}/saveMoney", method = RequestMethod.POST)
    public ResponseEntity<User> saveMoney(@PathVariable(value = "userId") long userId,
                          @RequestBody final Integer amountInCent) {
        User user = userRepository.findOne(userId);
        if (amountInCent <= user.getAmountInCent()) {
            user.setAmountInCent(user.getAmountInCent() - amountInCent);
            user.setSavedAmount(user.getSavedAmount() + amountInCent);
            userRepository.saveAndFlush(user);
        } else {
            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).body(null);
        }

        return ResponseEntity.ok(user);
    }

    @RequestMapping(path = "/{userId}/sendMoney", method = RequestMethod.POST)
    public User sendMoney(@PathVariable(value = "userId") long userId,
                          @RequestBody final Integer amountInCent) {

        User sender = userRepository.findOne(1L);
        sender.setAmountInCent(sender.getAmountInCent() - amountInCent);
        User createdSender = userRepository.saveAndFlush(sender);

        User receiver = userRepository.findOne(userId);
        receiver.setAmountInCent(receiver.getAmountInCent() + amountInCent);
        userRepository.saveAndFlush(receiver);

        if (receiver.getGcmId() != null) {
            notificationService.pushNotificationToGCM(receiver.getGcmId(), "SEND_MONEY;" + amountInCent);
        }

        return createdSender;
    }
}
