package com.example.courseservice.Dto.Homework.Enums;

public enum StudentAttachmentStatus {
    Completed , // Status when teacher checked homework , but without rate   - > 0  (in database)
    Submitted , // Status when student upload homework Attachments           - > 1
    Late,       //                                                           - > 2
    Rejected,   //                                                           - > 3
    Graded       //                                                           - > 4
}
