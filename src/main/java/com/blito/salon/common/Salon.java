package com.blito.salon.common;
/*
    @author Farzam Vatanzadeh
*/

import com.blito.salon.common.base.AbstractBaseSalonEntity;
import com.blito.salon.common.base.SalonComponent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

public class Salon extends AbstractBaseSalonEntity implements SalonComponent<Salon> {
    private Integer numberOfSections;
    private List<Section> sections;

    public List<Section> getSections() {
        return sections;
    }

    public void setSections(List<Section> sections) {
        this.sections = sections;
    }

    public Salon(String uid, String name) {
        super(uid, name);
        sections = new ArrayList<>();
    }

    public Salon() {
    }

    public Integer getNumberOfSections() {
        return numberOfSections;
    }

    public void setNumberOfSections(Integer numberOfSections) {
        this.numberOfSections = numberOfSections;
    }

    @Override
    public String toJson(ObjectMapper objectMapper) throws JsonProcessingException {
        return objectMapper.writeValueAsString(this);
    }
}
