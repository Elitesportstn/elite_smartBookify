package com.elite.app.builder.services;

import com.elite.app.builder.entities.Application;
import com.elite.app.builder.entities.User;
import com.elite.app.builder.repositories.ApplicationRepository;
import com.elite.app.builder.repositories.UserRepository;
import com.elite.app.builder.utils.EliteResponse;
import com.elite.app.builder.utils.MyFileNotFoundException;

import io.jsonwebtoken.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
 import org.springframework.web.multipart.MultipartFile;

 import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional; 

@Service
@RequiredArgsConstructor
@Slf4j
public class ApplicationService {
    
    final ApplicationRepository applicationRepository;
    final UserRepository userRepository;
    final FlutterService flutterService;
    final EmailService emailService;

    public ResponseEntity<?> create(String email , String appname , MultipartFile file){
        if (file.isEmpty()){
            return ResponseEntity.status(400).body(new EliteResponse("no file provided"));
        }
        Optional<User> optionalUser = userRepository.findByEmail(email);
        var application = new Application();
        LocalDateTime now = LocalDateTime.now();
        // Format the date and time (e.g., "01.01.2025.10.37")
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd_MM_yyyy_HH_mm");
        String formattedDate = now.format(formatter);
        appname = appname+formattedDate;
        appname.toLowerCase();
        if (optionalUser.isPresent()){
            var app = applicationRepository.findAllByOwner(optionalUser.get());
            if (app.isPresent()){
                return ResponseEntity.status(400).body(new EliteResponse("you have excited your limits please subscribe"));
            }

           var appCreated =  flutterService.create(file , appname);
            if (!appCreated){
                return ResponseEntity.badRequest().body(new EliteResponse("application creation failed"));
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
                return ResponseEntity.badRequest().body(new EliteResponse("application creation failed"));
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
        var error = new EliteResponse();
        error.setMessage("No application found");
        return ResponseEntity.status(200).body(error);
    }
    public ResponseEntity<?> deleteById(Long id){
        Optional<Application> optionalApplication = applicationRepository.findById(id);
        if (optionalApplication.isPresent()){
            applicationRepository.deleteById(id);
            var error = new EliteResponse();
            error.setMessage("Application deleted successfully");
            return ResponseEntity.status(200).body(error);
        }
        var error = new EliteResponse();
        error.setMessage("No Application found");
        return ResponseEntity.status(200).body(error);
    }

    public ResponseEntity<?> download(Long id) throws java.io.IOException {
            Optional<Application> application = applicationRepository.findById(id);
            if (application.isPresent()){
                var fileName = application.get().getName();

                 try {
        // Load file as Resource
        Resource resource =  loadFileAsResource(fileName);

        // Check if resource exists
        if (resource == null || !resource.exists()) {
            return ResponseEntity.notFound().build();
        }

        // Try to determine file's content type
        String contentType = "application/vnd.android.package-archive";

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);

    } catch (IOException ex) {
        log.error("Error occurred while loading the file: " + fileName, ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
        }else {
                var error = new EliteResponse();
                error.setMessage("no application found");
                return ResponseEntity.status(400).body(error);
    }}


    public ResponseEntity<?> getUserApps(String email ){
        Optional<User> user = this.userRepository.findByEmail(email);
        if (user.isPresent()) {
            return ResponseEntity.ok().body(user.get().getApplications());
        }else {
            return ResponseEntity.status(400).body(new EliteResponse("no Applications found for this user"));
        }
    }

   public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = Paths.get("apps/").resolve(fileName+".apk").normalize(); 
            Resource resource = new UrlResource(filePath.toUri());
            if(resource.exists()) {
                return resource;
            } else {
                throw new MyFileNotFoundException("File not found " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new MyFileNotFoundException("File not found " + fileName, ex);
        }
    }

}

