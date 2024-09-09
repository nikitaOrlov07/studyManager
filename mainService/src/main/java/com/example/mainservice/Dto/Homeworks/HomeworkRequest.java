package com.example.mainservice.Dto.Homeworks;

import com.example.mainservice.config.Validation.ValidDateFormat;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ValidDateFormat
public class HomeworkRequest {
    // basic information
    @NotEmpty(message = "Title is required")
    @NotNull
    private String title;

    @NotEmpty(message = "Description is required")
    @NotNull
    private String description;

    @NotEmpty(message = "Start date is required")
    @NotNull
    private String startDate;

    @NotEmpty(message = "End date is required")
    @NotNull
    private String endDate;

    // who should do
    private List<Long> userEntitiesId = new ArrayList<Long>();

    // homework Attachment
    private List<MultipartFile> files = new ArrayList<>();    // what to do (teacher make thsi attachments)


}
