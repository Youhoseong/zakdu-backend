package capstone.jakdu.ocrtest;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;


public class ocrexample {

    @Test
    public void getAllPageExtractPDF() throws IOException {
        String fileName = "수력충전본문목차.pdf";
        File source = new File(fileName);
        PDDocument pdfDoc = PDDocument.load(source);
        PDFTextStripper reader = new PDFTextStripper();
        reader.setSortByPosition(true);
        String text = reader.getText(pdfDoc);
        System.out.println(text);
    }
    
    @Test
    public void getSomePageExtractPDF() throws IOException {

        String fileName = "개념원리수학하.pdf";
        File source = new File(fileName);
        PDDocument pdfDoc = PDDocument.load(source);
        int i=6; // page no.

        PDFTextStripper reader = new PDFTextStripper();
        reader.setStartPage(i);
        reader.setEndPage(i+2);
        reader.setSortByPosition(true);
        String pageText = reader.getText(pdfDoc);


        System.out.println(pageText);
    }


    @Test
    public void getScannedPDF() {

    }
}
