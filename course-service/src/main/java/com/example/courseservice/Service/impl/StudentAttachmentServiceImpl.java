package com.example.courseservice.Service.impl;

import com.example.courseservice.Dto.StudenHomeworkAttachment.StudentHomeworkAttachmentDto;
import com.example.courseservice.Mappers.HomeworkMapper;
import com.example.courseservice.Model.Homework;
import com.example.courseservice.Model.StudentHomeworkAttachment;
import com.example.courseservice.Repository.HomeworkRepository;
import com.example.courseservice.Repository.StudentAttachmentRepository;
import com.example.courseservice.Service.CourseService;
import com.example.courseservice.Service.HomeworkService;
import com.example.courseservice.Service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.courseservice.Service.StudentAttachmentService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class StudentAttachmentServiceImpl implements StudentAttachmentService{
    private CourseService courseService; private UserService userService; private HomeworkService homeworkService; private HomeworkRepository  homeworkRepository; private StudentAttachmentRepository studentHomeworkAttachmentRepository; private HomeworkMapper mapper;

    @Autowired
    public StudentAttachmentServiceImpl(CourseService courseService, UserService userService, HomeworkService homeworkService, HomeworkRepository homeworkRepository, StudentAttachmentRepository studentHomeworkAttachmentRepository,HomeworkMapper mapper) {
        this.courseService = courseService;
        this.userService = userService;
        this.homeworkService = homeworkService;
        this.homeworkRepository = homeworkRepository;
        this.studentHomeworkAttachmentRepository = studentHomeworkAttachmentRepository;
        this.mapper= mapper;
    }
    @Override
    @Transactional
    public StudentHomeworkAttachmentDto findStudentAttachmentsByHomeworkIdAndStudentId(Long homeworkId, Long studentId) {

        Homework homework = homeworkRepository.findById(homeworkId).get();
        if (homework == null) {
            log.error("Homework is null");
            return null;
        }
        StudentHomeworkAttachment studentHomeworkAttachment = studentHomeworkAttachmentRepository.findByStudentIdAndHomework(studentId, homework);
        if (studentHomeworkAttachment == null) {
            log.error("StudentHomeworkAttachment is null");
            return null;
        }

        return mapper.studentHomeworkAttachmentToDto(studentHomeworkAttachment);
    }
    @Override
    public List<StudentHomeworkAttachmentDto> findHomeworkAttachmentsByIds(List<Long> studentAttachmentsIds) {
        List<StudentHomeworkAttachmentDto> studentHomeworkAttachmentsDtos = studentAttachmentsIds.stream()
                .map(id -> HomeworkMapper.studentHomeworkAttachmentToDto(studentHomeworkAttachmentRepository.findById(id).get()))
                .collect(Collectors.toList());

        return studentHomeworkAttachmentsDtos;
    }
    @Override
    @Transactional
    public List<StudentHomeworkAttachmentDto> findHomeworkAttachmentsByHomeworkId(Long homeworkId) {
        if (homeworkId == null) {
            return null;
        }

        Homework homework = homeworkRepository.findById(homeworkId).get();

        if (homework == null) {
            return null;
        }

        List<StudentHomeworkAttachment> studentHomeworkAttachments = studentHomeworkAttachmentRepository.findAllByHomework(homework);

        if (studentHomeworkAttachments == null || studentHomeworkAttachments.isEmpty())
            return null;

        return studentHomeworkAttachments.stream().map(HomeworkMapper::studentHomeworkAttachmentToDto).collect(Collectors.toList());
    }
    @Override
    @Transactional
    public StudentHomeworkAttachmentDto findStudentAttachmentById(Long studentAttachmentId) {
        StudentHomeworkAttachment studentHomeworkAttachment = studentHomeworkAttachmentRepository.findById(studentAttachmentId).get();
        if(studentHomeworkAttachment == null)
        {
            log.error("Student attachment with id {} not found", studentAttachmentId);
            return null;
        }
        return HomeworkMapper.studentHomeworkAttachmentToDto(studentHomeworkAttachment);
    }
}
