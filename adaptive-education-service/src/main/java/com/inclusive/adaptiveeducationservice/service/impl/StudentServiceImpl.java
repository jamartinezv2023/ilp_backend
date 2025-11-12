package com.inclusive.adaptiveeducationservice.service.impl;

import com.inclusive.adaptiveeducationservice.service.StudentService;
import com.inclusive.adaptiveeducationservice.entity.Student;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class StudentServiceImpl implements StudentService {

    private final Map<Long, Student> store = new ConcurrentHashMap<>();
    private final AtomicLong idGen = new AtomicLong(1);

    @Override
    public List<Student> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public Student findById(Long id) {
        return store.get(id);
    }

    @Override
    public Student create(Student e) {
        long id = idGen.getAndIncrement();
        try {
            // Si la entidad tiene setId(Long), ÃƒÂºsalo; si no, ignora
            e.getClass().getMethod("setId", Long.class).invoke(e, id);
        } catch (Exception ignored) {}
        store.put(id, e);
        return e;
    }

    @Override
    public Student update(Long id, Student e) {
        if (!store.containsKey(id)) return null;
        try {
            e.getClass().getMethod("setId", Long.class).invoke(e, id);
        } catch (Exception ignored) {}
        store.put(id, e);
        return e;
    }

    @Override
    public boolean delete(Long id) {
        return store.remove(id) != null;
    }
}



