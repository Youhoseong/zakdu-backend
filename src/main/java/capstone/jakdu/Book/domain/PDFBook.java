package capstone.jakdu.Book.domain;

import capstone.jakdu.Book.object.dto.BookRegisterDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import java.io.File;
import java.util.Date;

@Entity
@NoArgsConstructor
@Getter
public class PDFBook {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    
    private String category;
    private String name;
    private String author;
    private String publisher;
    private Date pubDate;

    private String intro;
    private Long price;
    private int realStartPage;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private FileStream bookFile;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private FileStream bookCover;


    public PDFBook(String category,
                   String name,
                   String author,
                   String publisher,
                   Date pubDate,
                   String intro,
                   Long price,
                   int realStartPage,
                    FileStream bookFile,
                    FileStream bookCover) {

        this.category = category;
        this.name = name;
        this.author = author;
        this.publisher = publisher;
        this.pubDate = pubDate;
        this.intro = intro;
        this.price = price;
        this.realStartPage = realStartPage;
        this.bookFile = bookFile;
        this.bookCover = bookCover;
    }


    public static PDFBook of(String category,
                             String name,
                             String author,
                             String publisher,
                             Date pubDate,
                             String intro,
                             Long price,
                             int realStartPage,
                             FileStream bookFile,
                             FileStream bookCover) {


        return new PDFBook(category, name, author, publisher, pubDate, intro, price, realStartPage, bookFile, bookCover);
    }


    // 책 파일, 표지, 암호화된 파일


    // 페이지가 쓰이는 경우 정리

    // 우리 분석 => start -  end page 저장 (실제 페이지)

    // 북마크 분석 => pdf 페이지 => 실제 페이지로 바꿔서 저장 필요
    // 구입 페이지 정보는 실제 페이지. => pdf 암호화 / 키 작업할때 실제 페이지를 pdf 페이지로 변환해야됨

    // 뷰어에서 페이지 이동 => 뭘 기준으로?????? => 책 페이지 기준 이동
    // 키 관리 . pdf 페이지 기준.

     // 구매 -> 실제
    // List<key> keys; 
    //toc


    // 1. 책 등록 api 짜서 파일 저장 디비 저장 <화>
    // 2. 등록 한 책 바탕 =>  구입 뷰를 어느 정도 완성  =>  구입 api

    // 3. 프론트 복호화 정보 받고 저장 -> 뷰어 열 때 복호화 연동
    // 4  파일 다운로드 => 북스토어에 책 누르면 부분 구매랑 전체 구매 나눠져있는데 전체 구매에 연결해 두면될듯?
    // 5. async storage 생명 주기에 대한 대비.
    
    


}
