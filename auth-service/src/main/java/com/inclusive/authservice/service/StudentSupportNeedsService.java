package com.inclusive.authservice.service;

import java.util.List;
import com.inclusive.authservice.model.StudentSupportNeeds;

public interface StudentSupportNeedsService {
    List<StudentSupportNeeds> listAll();
    StudentSupportNeeds getById(Long id);
    StudentSupportNeeds create(StudentSupportNeeds entity);
    StudentSupportNeeds update(Long id, StudentSupportNeeds entity);
    void delete(Long id);
}