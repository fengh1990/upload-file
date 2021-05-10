package com.fengh.learnings.uploadingfiles;

import com.fengh.learnings.uploadingfiles.config.UploadProperties;
import com.fengh.learnings.uploadingfiles.service.UploadService;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableConfigurationProperties(value = {UploadProperties.class})
public class UploadingFilesApplication {

    public static void main(String[] args) {
        SpringApplication.run(UploadingFilesApplication.class, args);
    }

    /**
     * 自动运行 ApplicationRunner
     * @param uploadService
     * @return
     */
    @Bean
    ApplicationRunner init(UploadService uploadService){
        return args -> {
            uploadService.deleteAll();
            uploadService.init();
        };
    }
}
