package com.module3.ccafe.service;

import com.module3.ccafe.entity.Size;
import com.module3.ccafe.repository.SizeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SizeService {

    private final SizeRepository sizeRepository;

    @Transactional(readOnly = true)
    public List<Size> getAllSizes() {
        return sizeRepository.findAll();
    }
}