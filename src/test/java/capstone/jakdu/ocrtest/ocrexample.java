package capstone.jakdu.ocrtest;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;


public class ocrexample {

    PDFTextStripper reader = new PDFTextStripper() {
        boolean startOfLine = true;

        @Override
        protected void startPage(PDPage page) throws IOException {
            startOfLine = true;
            super.startPage(page);
        }

        @Override
        protected void writeLineSeparator() throws IOException {
            startOfLine = true;
            super.writeLineSeparator();
        }

        @Override
        protected void writeString(String text, List<TextPosition> textPositions) throws IOException {

            if (startOfLine)
            {
                System.out.println("text = " + text);
                TextPosition firstPosition = textPositions.get(0);
                writeString(String.format("[%s %s]",firstPosition.getXDirAdj(), firstPosition.getYDirAdj()));
                startOfLine = false;
            }
            super.writeString(text, textPositions);
        }

    };

    public ocrexample() throws IOException {
    }

    @Test
    public void getAllPageExtractPDF() throws IOException {
        String fileName = "수력충전본문목차.pdf";
        String filePath = System.getProperty("user.dir") + "/"+ fileName;
        File source = new File(filePath);
        PDDocument pdfDoc = PDDocument.load(source);
        //reader.setSortByPosition(true);

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
