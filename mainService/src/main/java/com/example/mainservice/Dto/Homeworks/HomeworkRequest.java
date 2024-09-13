package com.example.mainservice.Dto.Homeworks;

import com.example.mainservice.config.Validation.DateFormat.ValidDateFormat;
import com.example.mainservice.config.Validation.DateRange.ValidDateRange;
import jakarta.validation.constraints.Future;
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
@ValidDateRange
public class HomeworkRequest {
    // basic information
    @NotEmpty(message = "Title is required")
    @NotNull
    private String title;

    @NotEmpty(message = "Description is required")
    @NotNull
    private String description;

    @ValidDateFormat
    @NotEmpty(message = "Start date is required")
    @NotNull
    private String startDate;

    @ValidDateFormat
    @NotEmpty(message = "End date is required")
    @NotNull
    private String endDate;

    // who should do
    private List<Long> userEntitiesId = new ArrayList<Long>();

    // homework Attachment
    private List<MultipartFile> files = new ArrayList<>();    // what to do (teacher make thsi attachments)
    private Long courseId;
    private Long authorId; // will be needed if in course will be many teachers

}
