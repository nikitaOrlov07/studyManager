package com.example.courseservice.Controller;

import com.example.courseservice.Dto.UserEntity.UserEntityDto;
import com.example.courseservice.Dto.UserEntity.UserEntityResponse;
import com.example.courseservice.Model.Attachment;
import com.example.courseservice.Service.AttachmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

@Controller
@RequestMapping("/files")
@RestController
@RequiredArgsConstructor
public class AttachmentController {

    private final AttachmentService attachmentService;
    private final WebClient.Builder webClientBuilder; // for http requests

    // download file
    @GetMapping("/download")
    public ResponseEntity<?> downloadFile(@RequestParam(value = "fileId") String fileIdString, @RequestParam(value = "username" , required = false) String username ) throws Exception // method will return file content and file metadata
    {
        if(username == null || username.isEmpty())
        {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Not allowed to make operations");
        }
        Long fileId = Long.parseLong(fileIdString);
        Attachment attachment = attachmentService.getAttachment(fileId);
        // HTTP request to userService to find new user
        UserEntityResponse currentUser = webClientBuilder.build()
                .get()
                .uri("")
                .retrieve()
                .bodyToMono(UserEntityResponse.class)
                .block();

        if(attachment.getAccessType().equals("open") || attachmentService.canMakeOperationsWithAttachment(currentUser,attachment)) // user can`t download file if he is not involved into this course
        {
            return ResponseEntity.ok() // request was successful
                    .contentType(MediaType.parseMediaType(attachment.getFileType())) // This sets the Content-Type of the file using the fileType stored in the attachment object. MediaType.parseMediaType() converts the string representation of the file type into a MediaType object.
                    .header(HttpHeaders.CONTENT_DISPOSITION ,
                            "attachment;filename=\"" + attachment.getFileName()+"\"") // Sets the Content-Disposition header. The value "attachment;filename=\"" indicates to the browser that the file should be displayed as an attachment and not opened in the browser. attachment.getFileName() is used to set the file name.
                    .body( new ByteArrayResource(attachment.getData())); // we return the contents of the file as a resource of type ByteArrayResource. This allows Spring to treat the byte array as a data stream for the HTTP response.
        }
        // Return a 403 Forbidden response with a message
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("Not allowed to make operations");

    }
    // Display file contents
    @GetMapping("/view")
    public ResponseEntity<?> viewFile(@RequestParam(value = "fileId") String fileIdString, @RequestParam(value = "username" , required = false) String username ) throws Exception {
        Long fileId = Long.parseLong(fileIdString);
        if(username == null || username.isEmpty())
        {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Not allowed to make operations");
        }
        // HTTP request to userService to find new user
        UserEntityResponse currentUser = webClientBuilder.build()
                .get()
                .uri("https://user-service/users/{username}",username)
                .retrieve()
                .bodyToMono(UserEntityResponse.class)
                .block();
        Attachment attachment = attachmentService.getAttachment(fileId);
        if(attachment.getAccessType().equals("open") || attachmentService.canMakeOperationsWithAttachment(currentUser,attachment))
        {
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(attachment.getFileType())) // ->  converts a string representation of a file type to a MediaType object
                    .body(new ByteArrayResource(attachment.getData()));
        }
        // Return a 403 Forbidden response with a message
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("Not allowed to make operations");

    }



}
