package com.elite.app.builder.controllers;

import com.elite.app.builder.entities.Application;
import com.elite.app.builder.services.ApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/applications")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:4200/"})
public class ApplicationController {

    private final ApplicationService applicationService;


    @Operation(summary = "Retrieve all applications", description = "Fetches a list of all available applications.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Applications fetched successfully", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation =List.class)
            )),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("")
    public ResponseEntity<List<Application>> getAll() {
        return applicationService.getAll();
    }

    @Operation(summary = "Get application by ID", description = "Fetches details of a specific application by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Application fetched successfully", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = Application.class)
            )),
            @ApiResponse(responseCode = "404", description = "Application not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{appId}")
    public ResponseEntity<?> getUserById(@PathVariable("appId") Long appId) {
        return applicationService.getById(appId);
    }

    @Operation(summary = "Create a new application", description = "Uploads a file and creates a new application associated with a specific user ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Application created successfully", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = Application.class)
            )),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/")
    public ResponseEntity<?> create(@RequestParam("file") MultipartFile file ,
                                    @RequestParam("email") String email ,
                                    @RequestParam("appname") String appname

    ) {
        return applicationService.create(email,appname, file);
    }

    @Operation(summary = "Delete an application by ID", description = "Deletes a specific application by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Application deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Application not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/{application}")
    public ResponseEntity<?> delete(@PathVariable("application") Long application) {
        return applicationService.deleteById(application);
    }

    @Operation(summary = "Download application file", description = "Downloads the file associated with a specific application ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "File downloaded successfully", content = @Content(
                    mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE,
                    schema = @Schema(type = "string", format = "binary")
            )),
            @ApiResponse(responseCode = "404", description = "Application or file not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/download/{appId}")
    public ResponseEntity<?> download(@PathVariable("appId") Long id) {
        return applicationService.download(id);
    }


    @Operation(summary = "get all user's applications", description = "get all the apps associated with a specific user ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successfully"),
            @ApiResponse(responseCode = "404", description = "Application or file not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/byUser/{email}")
    public ResponseEntity<?> getAllByUser(@PathVariable("email") String email) {
        return applicationService.getUserApps(email);
    }
}