package com.se1933g01.steam_clone_backend.config;

import java.beans.BeanProperty;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class ModelMapperCfg {
    @Bean
    public ModelMapper modelMapper() {

        return new ModelMapper();
    }



}
