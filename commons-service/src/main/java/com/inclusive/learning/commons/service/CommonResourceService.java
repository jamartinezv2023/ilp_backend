package com.inclusive.learning.commons.service;

import com.inclusive.learning.commons.dto.CommonResourceDto;

import java.util.List;

public interface CommonResourceService {
    List<CommonResourceDto> findAll();
    CommonResourceDto findById(Long id);
    CommonResourceDto create(CommonResourceDto dto);
    CommonResourceDto update(Long id, CommonResourceDto dto);
    boolean delete(Long id);
}







