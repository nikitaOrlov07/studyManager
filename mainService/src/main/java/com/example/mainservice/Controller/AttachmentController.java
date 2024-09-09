package com.example.mainservice.Controller;

import com.example.mainservice.Security.SecurityUtil;
import com.example.mainservice.Service.ViewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;

@Controller
@RequestMapping("/files")
@RequiredArgsConstructor
@Slf4j
public class AttachmentController {
    private final ViewService viewService;

    // For getting Download link
    @GetMapping("/download/{fileId}")
    public ResponseEntity<Resource> getDownloadLink(@PathVariable Long fileId)
    {
        ResponseEntity<Resource> resourceResponseEntity =  viewService.getDownloadLink(fileId);
        log.info("Download link"+ resourceResponseEntity.getBody());
        return resourceResponseEntity;
    }
    // For getting View Link
    @GetMapping("/view/{fileId}")
    public ResponseEntity<Resource> getViewLink(@PathVariable Long fileId)
    {
        String username = SecurityUtil.getSessionUser();
        ResponseEntity<Resource> fileView =  viewService.getFileView(fileId,username);
        return fileView;
    }
}
