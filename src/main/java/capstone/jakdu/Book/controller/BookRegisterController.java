package capstone.jakdu.Book.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping(value="/book")
public class BookRegisterController {

    @PostMapping
    public String test(@RequestParam("files") MultipartFile file) throws IOException {
        System.out.println("file = " + file.getOriginalFilename());
        String filePath = System.getProperty("user.dir")+ "/pdfBook/";
        System.out.println(System.getProperty("user.dir"));
        File dir = new File(filePath);
        if(!dir.exists())
            dir.mkdirs();

        filePath=  filePath + file.getOriginalFilename();

        File f = new File(filePath);
        file.transferTo(f);
        return "test complete";
    }

    @PostMapping("/text3")
    public void test3(@RequestParam("file") String file) {
        System.out.println("file = " + file);

    }

    @PostMapping("/test")
    public String test2() {
        System.out.println("welcome");
        return "ok";
    }




}
