package com.fengh.learnings.uploadingfiles.controller;

import com.fengh.learnings.uploadingfiles.service.UploadService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Controller
public class FileUploadController {

    private final UploadService uploadService;

    public FileUploadController(UploadService uploadService) {
        this.uploadService = uploadService;
    }

    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file, RedirectAttributes attributes) {
        this.uploadService.store(file);
        attributes.addFlashAttribute("message", "成功上传文件：" + file.getOriginalFilename());
        return "redirect:/";
    }

    @GetMapping("/files/{fileName:.+}")
    @ResponseBody
    public ResponseEntity<Resource> getFile(@PathVariable("fileName") String fileName){
        Resource resource = this.uploadService.getFile(fileName);
        String string = null;
        try {
            //URLEncoder.encode 解决文件名包含中文乱码问题
            string = "attachment; filename="+ URLEncoder.encode(Objects.requireNonNull(resource.getFilename()), StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException ignored) {

        }
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,string).body(resource);
    }

    @GetMapping("/")
    public String home(Model model){
        getUploadFileUris(model);
        return "uploadForm";
    }

    private void getUploadFileUris(Model model) {
        List<String> files = this.uploadService.loadAll().stream().map(s -> MvcUriComponentsBuilder.fromMethodCall(MvcUriComponentsBuilder.on(FileUploadController.class).getFile(s.getFileName().toString())).build().encode().toUri().toString()).collect(Collectors.toList());
        model.addAttribute("files", files);
    }
}
