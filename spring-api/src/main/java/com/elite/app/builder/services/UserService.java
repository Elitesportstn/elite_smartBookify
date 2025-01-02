package com.elite.app.builder.services;

import com.elite.app.builder.entities.User;
import com.elite.app.builder.repositories.UserRepository;
import com.elite.app.builder.utils.EliteResponse;
 import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    final UserRepository userRepository;

    public   ResponseEntity<?> createUser(User user){
        Optional<User> userOptional = userRepository.findByEmail(user.getEmail());
        if (userOptional.isPresent()){
            var error = new EliteResponse();
            error.setMessage("email Already Taken");
            return ResponseEntity.badRequest().body(error);
        }
        User savedUser = userRepository.save(user);
        return ResponseEntity.status(201).body(savedUser);
    }

    public ResponseEntity<?> getAllUsers(){
        return ResponseEntity.ok(userRepository.findAll());
    }

    public ResponseEntity<?> getUserById(Long id){
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()){
            return ResponseEntity.ok(optionalUser.get());
        }
        var error = new EliteResponse();
        error.setMessage("No user found");
        return ResponseEntity.status(200).body(error);
    }
    public ResponseEntity<?> deleteById(Long id){
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()){
            userRepository.deleteById(id);
            var error = new EliteResponse();
            error.setMessage("user deleted successfully");
            return ResponseEntity.status(200).body(error);
        }
        var error = new EliteResponse();
        error.setMessage("No user found");
        return ResponseEntity.status(200).body(error);
    }

    public ResponseEntity<?> initPay(String  email, String paymentRef) {
        var user = userRepository.findByEmail(email);
        if (user.isEmpty()){
            return ResponseEntity.status(400).body(new EliteResponse("no user found"));
        }
        var activeUser = user.get();
        activeUser.setPaymentRef(paymentRef);
        userRepository.save(activeUser);
        return  ResponseEntity.ok(new EliteResponse("success"));
    }
    public ResponseEntity<?> pay(String email){
        var user = userRepository.findByEmail(email);
        if (user.isEmpty()){
            return ResponseEntity.status(400).body(new EliteResponse("no user found"));
        }
        var activeUser = user.get();
        activeUser.setSubscribed(true);
        userRepository.save(activeUser);
        return  ResponseEntity.ok(new EliteResponse("success"));
    }
}
