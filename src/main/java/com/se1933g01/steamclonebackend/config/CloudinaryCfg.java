package com.se1933g01.steamclonebackend.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cloudinary.Cloudinary;

@Configuration
public class CloudinaryCfg {
    @Bean
    public Cloudinary cloudinary() {
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", "davzqwcoq");
        config.put("api_key", "516683433997481");
        config.put("api_secret", "VbM7Vm8QB6InowWXUpxu8-FyiBI");
        config.put("secure", "true");
        return new Cloudinary(config);
    }
}
