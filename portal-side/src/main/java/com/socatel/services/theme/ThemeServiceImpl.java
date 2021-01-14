package com.socatel.services.theme;

import com.socatel.dtos.knowledge_base.ThemeIdDTO;
import com.socatel.models.Theme;
import com.socatel.repositories.ThemeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ThemeServiceImpl implements ThemeService{

    private ThemeRepository themeRepository;

    @Autowired
    public ThemeServiceImpl(ThemeRepository themeRepository) {
        this.themeRepository = themeRepository;
    }

    @Override
    public Set<Theme> findAllThemes() {
        return new HashSet<>(themeRepository.findAll());
    }

    @Override
    public Theme findThemeById(Integer id) {
        return themeRepository.findById(id).orElse(null);
    }

    @Override
    public List<ThemeIdDTO> findThemesByUserId(Integer id) {
        return themeRepository.findThemesByUserId(id);
    }
}
