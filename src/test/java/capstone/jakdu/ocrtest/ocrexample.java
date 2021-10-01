package capstone.jakdu.ocrtest;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;


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
            System.out.println("text = " + text);
            if (startOfLine)
            {

                TextPosition firstPosition = textPositions.get(0);

                writeString(String.format("[%s %s %s]", firstPosition.getFontSizeInPt(),firstPosition.getXDirAdj(), firstPosition.getYDirAdj()));
                startOfLine = false;
            }
            super.writeString(text, textPositions);
        }

    };

    public ocrexample() throws IOException {
    }

    @Test
    public void getAllPageExtractPDF() throws IOException {
        String fileName = "example-페이지-4.pdf";
        String filePath = System.getProperty("user.dir") + "/"+ fileName;
        File source = new File(filePath);
        PDDocument pdfDoc = PDDocument.load(source);
        //reader.setSortByPosition(true);

        String text = reader.getText(pdfDoc);
        System.out.println(text);

    }
    
    @Test
    public void getSomePageExtractPDF() throws IOException {

        String fileName = "수학의힘알파중1-1.pdf";
        File source = new File(fileName);
        PDDocument pdfDoc = PDDocument.load(source);
        int i=4; // page no.
       // PDFTextStripper reader = new PDFTextStripper();
       // reader.set
      //  reader.setWordSeparator(" ");
       // reader.setLineSeparator("\n");

        System.out.println("separate:" + reader.getLineSeparator());

        reader.setStartPage(i);
        reader.setEndPage(i);
      //  reader.setSortByPosition(true);
        String pageText = reader.getText(pdfDoc);
        System.out.println(pageText);
    }


    @Test
    public void tesseractTest() throws TesseractException, IOException {
        Tesseract tesseract = new Tesseract();
        tesseract.setDatapath("C:/Program Files/Tesseract-OCR/tessdata");
        tesseract.setLanguage("kor");
        String fileName = "일등급수학I.pdf";
        String filePath = System.getProperty("user.dir") + "/"+ fileName;
        File source = new File(filePath);
        PDDocument pdfDoc = PDDocument.load(source);

        PDFRenderer pdfRenderer = new PDFRenderer(pdfDoc);
        for (int page = 3; page < 4; page++) {
            BufferedImage bufferedImage = pdfRenderer.renderImageWithDPI(page, 1000, ImageType.GRAY);

            String str = tesseract.doOCR(bufferedImage);
            System.out.println(str);

        }

        //String text = tesseract.doOCR(source);
        //System.out.print(text);
    }
}
