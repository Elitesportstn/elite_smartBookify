package com.elite.app.builder.services;

import com.elite.app.builder.entities.Application;
import com.elite.app.builder.entities.User;
import com.elite.app.builder.repositories.ApplicationRepository;
import com.elite.app.builder.repositories.UserRepository;
import com.elite.app.builder.utils.EliteError;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@RequiredArgsConstructor
public class ApplicationService {
    
    final ApplicationRepository applicationRepository;
    final UserRepository userRepository;
    final FlutterService flutterService;
    final EmailService emailService;

    public ResponseEntity<?> create(String email , String appname , MultipartFile file){
        if (file.isEmpty()){
            return ResponseEntity.status(400).body(new EliteError("no file provided"));
        }
        Optional<User> optionalUser = userRepository.findByEmail(email);
        var application = new Application();
        if (optionalUser.isPresent()){
            var app = applicationRepository.findAllByOwner(optionalUser.get());
            if (app.isPresent()){
                return ResponseEntity.status(400).body(new EliteError("you have excited your limits please subscribe"));
            }

           var appCreated =  flutterService.create(file , appname);
            if (!appCreated){
                return ResponseEntity.badRequest().body(new EliteError("application creation failed"));
            }
            application.setName(appname);
            application.setVersion("1.0");
            application.setOwner(optionalUser.get());
            var saved = applicationRepository.save(application);
            emailService.sendEmail(email , "application generated successfully" , "hello your app is successfully generated" , application);
            return ResponseEntity.status(201).body(saved);
        }else {
            var generatedPwd = "generatedPwd";
            var user = new User();
            user.setEmail(email);
            user.setUsername(email);
            user.setPassword(generatedPwd);
           var savedUser =  userRepository.save(user);
             var appCreated =  flutterService.create(file , appname);
            if (!appCreated){
                return ResponseEntity.badRequest().body(new EliteError("application creation failed"));
            }
            application.setName(appname);
            application.setVersion("1.0");
            application.setOwner(savedUser);
            var saved = applicationRepository.save(application);
            emailService.sendEmail(email , "application generated successfully" , "hello your app is successfully generated", application);

            return ResponseEntity.status(201).body(saved);
        }

    }

    public ResponseEntity<List<Application>> getAll(){
        return ResponseEntity.ok(applicationRepository.findAll());
    }

    public ResponseEntity<?> getById(Long id){
        Optional<Application> optionalApplication= applicationRepository.findById(id);
        if (optionalApplication.isPresent()){
            return ResponseEntity.ok(optionalApplication.get());
        }
        var error = new EliteError();
        error.setMessage("No application found");
        return ResponseEntity.status(200).body(error);
    }
    public ResponseEntity<?> deleteById(Long id){
        Optional<Application> optionalApplication = applicationRepository.findById(id);
        if (optionalApplication.isPresent()){
            applicationRepository.deleteById(id);
            var error = new EliteError();
            error.setMessage("Application deleted successfully");
            return ResponseEntity.status(200).body(error);
        }
        var error = new EliteError();
        error.setMessage("No Application found");
        return ResponseEntity.status(200).body(error);
    }

    public ResponseEntity<?> download(Long id) {
            Optional<Application> application = applicationRepository.findById(id);
            if (application.isPresent()){
                var fileName = application.get().getName()+".apk";
                try {
                    // Build file path
                    Path filePath = Paths.get("apps/").resolve(fileName).normalize();

                    // Load the file as a Resource
                    Resource resource = new UrlResource(filePath.toUri());

                    if (!resource.exists()) {
                        return ResponseEntity.notFound().build();
                    }

                    // Set response headers
                    return ResponseEntity.ok()
                            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                            .body(resource);

                } catch (Exception ex) {
                    ex.printStackTrace();
                    return ResponseEntity.internalServerError().build();
                }

        }else {
                var error = new EliteError();
                error.setMessage("no application found");
                return ResponseEntity.status(400).body(error);
    }}


    public ResponseEntity<?> getUserApps(String email ){
        Optional<User> user = this.userRepository.findByEmail(email);
        if (user.isPresent()) {
            return ResponseEntity.ok().body(user.get().getApplications());
        }else {
            return ResponseEntity.status(400).body(new EliteError("no Applications found for this user"));
        }
    }

}

