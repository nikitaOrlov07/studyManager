package com.example.mainservice.Service;

import com.example.mainservice.Dto.Homeworks.HomeworkDto;
import com.example.mainservice.Dto.Homeworks.HomeworkRequest;

import java.io.IOException;
import java.util.List;

public interface HomeworkService {
    Boolean createHomework(HomeworkRequest homeworkRequest) throws IOException;

    List<HomeworkDto>  findHomeworksByAuthorAndStatusAndCourseId(Long authorId, String homeworkStatus, Long courseId);
}
