package capstone.jakdu.Book.object.dto;

import lombok.Data;

@Data
public class PDFBookTocAnalyzeDto {


    private int bookPDFTocStartPage;
    private int bookPDFTocEndPage;
    private int bookPDFRowCount;
}
