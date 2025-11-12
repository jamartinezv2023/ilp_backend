package com.inclusive.authservice.service;

import java.util.List;
import com.inclusive.authservice.model.StudentAIOutput;

public interface StudentAIOutputService {
    List<StudentAIOutput> listAll();
    StudentAIOutput getById(Long id);
    StudentAIOutput create(StudentAIOutput entity);
    StudentAIOutput update(Long id, StudentAIOutput entity);
    void delete(Long id);
}