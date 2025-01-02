package com.elite.app.builder.controllers;


import com.elite.app.builder.entities.User;
import com.elite.app.builder.services.AuthService;
import com.elite.app.builder.services.LoginRequest;
import com.elite.app.builder.services.UserService;
import com.elite.app.builder.utils.ConfirmMailRequest;
import com.elite.app.builder.utils.ForgetPwdRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CrossOrigin;


@RestController
@RequestMapping("api/auth/")
@RequiredArgsConstructor
@CrossOrigin(origins = "https://smartbookify.savooria.com")
public class AuthController {

    final UserService userService;

    private  final AuthService authService;
    @GetMapping("users")
    ResponseEntity<?> getAllUsers(){
        return userService.getAllUsers();
    }


    @GetMapping("users/{uid}")
    ResponseEntity<?> getUserByIdd(@PathVariable("uid")Long uid){
        return userService.getUserById(uid);
    }
    @PostMapping("users")
    ResponseEntity<?> create(@RequestBody User user){
        return userService.createUser(user);
    }
    @DeleteMapping("users/{uid}")
    ResponseEntity<?> deleteUser(@PathVariable("uid")Long uid){
        return userService.deleteById(uid);
    }

    @PostMapping("initPay/{email}/{ref}")
    ResponseEntity<?> initPay(@PathVariable("email")String email , @PathVariable("ref")String paymentRef){
        return userService.initPay(email , paymentRef);
    }
    @PostMapping("pay/{email}")
    ResponseEntity<?> pay(@PathVariable("email")String email){
        return userService.pay(email );
    }

        @PostMapping("register")
        public ResponseEntity<?> register(@RequestBody User user) {
            return   authService.register(user);
        }

        @PostMapping("login")
        public  ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
            return  authService.login(loginRequest);
        }

        @PostMapping("confirm-email")
        public ResponseEntity<?> confirmEmail(@RequestBody() ConfirmMailRequest confirmMailRequest){
            return authService.confirmMail(confirmMailRequest);
        }

    @PostMapping("forget-password")
    public ResponseEntity<?> forgetPassword(@RequestBody() ForgetPwdRequest ForgetPwdRequest){
        return authService.forgetPassword(ForgetPwdRequest);
    }

    }

