package com.inclusive.authservice.service.impl;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.inclusive.authservice.model.StudentAIOutput;
    import com.inclusive.authservice.repository.StudentAIOutputRepository;
import com.inclusive.authservice.service.StudentAIOutputService;

@Service
public class StudentAIOutputServiceImpl implements StudentAIOutputService {

    @Autowired
    private StudentAIOutputRepository repository;

    @Override
    public List<StudentAIOutput> listAll() { return repository.findAll(); }

    @Override
    public StudentAIOutput getById(Long id) { return repository.findById(id).orElse(null); }

    @Override
    public StudentAIOutput create(StudentAIOutput entity) { return repository.save(entity); }

    @Override
    public StudentAIOutput update(Long id, StudentAIOutput entity) {
        Optional<StudentAIOutput> opt = repository.findById(id);
        if (!opt.isPresent()) return null;
        entity.setId(id);
        return repository.save(entity);
    }

    @Override
    public void delete(Long id) { repository.deleteById(id); }
}