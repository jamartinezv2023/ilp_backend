package com.inclusive.learning.commons.service.impl;

import com.inclusive.learning.commons.dto.CommonResourceDto;
import com.inclusive.learning.commons.service.CommonResourceService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class CommonResourceServiceImpl implements CommonResourceService {

    private final Map<Long, CommonResourceDto> data = new ConcurrentHashMap<>();
    private final AtomicLong seq = new AtomicLong(1);

    @Override
    public List<CommonResourceDto> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public CommonResourceDto findById(Long id) {
        return data.get(id);
    }

    @Override
    public CommonResourceDto create(CommonResourceDto dto) {
        long id = seq.getAndIncrement();
        dto.setId(id);
        data.put(id, dto);
        return dto;
    }

    @Override
    public CommonResourceDto update(Long id, CommonResourceDto dto) {
        if (!data.containsKey(id)) return null;
        dto.setId(id);
        data.put(id, dto);
        return dto;
    }

    @Override
    public boolean delete(Long id) {
        return data.remove(id) != null;
    }
}







