package com.socatel.dtos.knowledge_base;

public class SkillIdDTO {
    private int skill_id;
    private String skill_name;

    public SkillIdDTO() {}

    public SkillIdDTO(Integer id, String name) {
        this.skill_id = id;
        this.skill_name = name;
    }

    public int getSkill_id() {
        return skill_id;
    }

    public void setSkill_id(int skill_id) {
        this.skill_id = skill_id;
    }

    public String getSkill_name() {
        return skill_name;
    }

    public void setSkill_name(String skill_name) {
        this.skill_name = skill_name;
    }
}
