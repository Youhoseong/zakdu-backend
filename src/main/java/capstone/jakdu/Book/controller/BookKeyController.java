package capstone.jakdu.Book.controller;

import capstone.jakdu.Common.response.ResponseDto;
import capstone.jakdu.Common.response.StatusEnum;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@RestController
@RequestMapping("/key")
public class BookKeyController {
//    @GetMapping("/pdf")
//    public ResponseDto getBoughtKeys()
    @GetMapping("/downtest")
    public ResponseDto downloadTest() throws IOException {
        String fileName = "./피디에프/문제집/9종교과서시크릿수학1-본문(학생용)_enc.pdf";
        FileInputStream inputStream = new FileInputStream(fileName);
        byte[] bytes = IOUtils.toByteArray(inputStream);

        return new ResponseDto(StatusEnum.OK, "success", bytes);
    }
}
