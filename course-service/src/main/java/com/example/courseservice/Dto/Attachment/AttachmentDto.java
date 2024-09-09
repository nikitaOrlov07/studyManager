package com.example.courseservice.Dto.Attachment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttachmentDto {
    private Long id;
    private String fileName;
    private String fileType;
    private String downloadUrl;
    private String viewUrl;
    private String accessType; // can be open or private

}
