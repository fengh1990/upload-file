package com.fengh.learnings.uploadingfiles;

import com.fengh.learnings.uploadingfiles.service.UploadService;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class UploadingFilesApplicationTests {

    @Autowired
    MockMvc mvc;
    /**
     * 注意必须使用@MockBean 注解
     */
    @MockBean
    UploadService uploadService;

    @Test
    void shouldSaveUploadedFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", MediaType.TEXT_PLAIN_VALUE, "hello".getBytes());
        this.mvc.perform(multipart("/upload").file(file))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "/"));

        BDDMockito.then(uploadService).should().store(file);
    }

}
