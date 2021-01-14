package com.socatel.services.theme;

import com.socatel.dtos.knowledge_base.ThemeIdDTO;
import com.socatel.models.Theme;

import java.util.List;
import java.util.Set;

public interface ThemeService {
    Set<Theme> findAllThemes();
    Theme findThemeById(Integer id);
    List<ThemeIdDTO> findThemesByUserId(Integer id);
}
