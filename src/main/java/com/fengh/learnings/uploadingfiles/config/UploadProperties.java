package com.fengh.learnings.uploadingfiles.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "upload")
public class UploadProperties {

    /**
     * 文件上传存放的文件夹路径
     */
    private String location="upload";

}
