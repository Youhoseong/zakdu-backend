package capstone.jakdu.Book.controller;

import capstone.jakdu.Book.object.HierarchyObject;
import capstone.jakdu.Book.object.MyTextPosition;
import capstone.jakdu.Book.service.BookRegisterService;
import capstone.jakdu.Common.response.ResponseDto;
import capstone.jakdu.Common.response.StatusEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value="/book")
public class BookRegisterController {
    private final BookRegisterService bookRegisterService;

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/bookmark")
    public ResponseDto fileUploadWithBookMarkAnalysis(@RequestParam("files") MultipartFile file) throws IOException {

        System.out.println("file = " + file.getOriginalFilename());
        List<HierarchyObject> hierarchyObjects = bookRegisterService.bookmarkAnalysisFromPdf(file);


        if(hierarchyObjects.size() == 0) {
            return new ResponseDto(StatusEnum.BOOKMARK_NO_EXIST, "success", null);
        } else {
            return new ResponseDto(StatusEnum.OK, "success", hierarchyObjects);
        }

    }

    @PostMapping("/text3")
    public void test3(@RequestParam("files") MultipartFile file) {
        System.out.println("file = " + file.getOriginalFilename());

    }

    @PostMapping("/test")
    public String test2() {
        System.out.println("welcome");
        return "ok";
    }




}
