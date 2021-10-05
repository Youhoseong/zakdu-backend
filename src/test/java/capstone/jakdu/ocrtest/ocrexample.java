package capstone.jakdu.ocrtest;


import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ocrexample {
    List<MyTextPosition> myTextPositions = new ArrayList<>();
    static int id = 0;
    float xMin = 999;
    float xMax = 0;
    // 1열
    class MyTextPositionComparator implements Comparator<MyTextPosition> {
        @Override
        public int compare(MyTextPosition o1, MyTextPosition o2) {
            if(Math.abs(o1.y - o2.y) < Math.min(o1.fontSize, o2.fontSize)) {
                if(o1.x < o2.x) return -1;
                else return 1;
            }
            else {
                if(o1.y > o2.y) {
                    return 1;
                }
                else if(o1.y < o2.y) {
                    return -1;
                }
                else {
                    if(o1.x <= o2.x) {
                        return -1;
                    }
                    else {
                        return 1;
                    }
                }
            }
        }
    }

    class MyTextPositionComparatorMultiCol implements Comparator<MyTextPosition> {
        private float rowSize;
        public MyTextPositionComparatorMultiCol(float pageWidth, int colNum) {
            rowSize = pageWidth / colNum;
        }

        @Override
        public int compare(MyTextPosition o1, MyTextPosition o2) {
            // o1과 o2가 서로 다른 열에 있는 경우
            int r1 = (int) ((o1.x - xMin) / rowSize);
            int r2 = (int) ((o2.x - xMin)/ rowSize);

            if(r1 < r2) {
                return -1;
            }
            else if(r1 > r2) {
                return 1;
            }
             // o1과 o2가 같은 열에 있는 경우
            else{
                // o1과 o2가 같은 행에 있는 경우
                if(Math.abs(o1.y - o2.y) < Math.min(o1.fontSize, o2.fontSize)) {
                    if(o1.x < o2.x) return -1;
                    else return 1;
                }
                else {
                    if(o1.y > o2.y) {
                        return 1;
                    }
                    else if(o1.y < o2.y) {
                        return -1;
                    }
                    else {
                        if(o1.x <= o2.x) {
                            return -1;
                        }
                        else {
                            return 1;
                        }
                    }
                }
            }
        }
    }



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
            byte[] b = text.getBytes(StandardCharsets.UTF_8);
            if(b[b.length-1] == 8)
                b = Arrays.copyOfRange(b, 0, b.length - 1);
            else if(b[0] == 8 && b[1] == 8) {
                b = Arrays.copyOfRange(b, 2, b.length);
            }
            text = new String(b, StandardCharsets.UTF_8);

            String tempText = text.replaceAll(" ", "");

            text = text.replaceAll("\\s+", " ");
            //text = text.replaceAll("\t")
            if(tempText.contains("차례") ||
                    tempText.toLowerCase().contains("contents") ||
                    tempText.toLowerCase().contains("indd") ||
                    isDate(tempText)  ) {

                System.out.println("[차례]text = " + text);
                return;
            }
            TextPosition first = textPositions.get(0);
            TextPosition last = textPositions.get(text.length() - 1);


            for (int i = 0; i < text.length(); i++) {
                TextPosition t = textPositions.get(i);
                xMax = Math.max(xMax, textPositions.get(i).getEndX());
                xMin = Math.min(xMin, textPositions.get(i).getX());
                System.out.println("" + text.substring(i, i + 1) + "/" + t.getX() + "/" + t.getEndX());
                myTextPositions.add(new MyTextPosition(t.getX(), t.getY(), t.getEndX(), t.getFontSizeInPt(), text.substring(i, i + 1), id));
            }
            id++;
//            System.out.println("text = " + text + "/" + first.getX() + "/" + first.getY() + "/" + last.getEndX() + "/" + last.getHeight());
//            System.out.println("text = " + text);

            //myTextPositions.add(new MyTextPosition(first.getX(), first.getY(), last.getEndX(), last.getHeight(),text));
            if (startOfLine)
            {
                TextPosition t = textPositions.get(0);
                //myTextPositions.add(new MyTextPosition(t.getX() - 0.1f, t.getY(), t.getEndX(), t.getHeight(), "@"));

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

        String fileName = "9종교과서시크릿수학1-본문(학생용).pdf";
        File source = new File(fileName);
        PDDocument pdfDoc = PDDocument.load(source);
        int i=2; // page no.
       // PDFTextStripper reader = new PDFTextStripper();
       // reader.set
       // reader.setLineSeparator("\n");

        System.out.println("separate:" + reader.getLineSeparator());

        reader.setStartPage(i);
        reader.setEndPage(i);
        String pageText = reader.getText(pdfDoc);
        System.out.println(pageText);
    }

    @Test
    public void get1ColPageExtract() throws IOException {
        String fileName = "아샘HiMath기하.pdf";
        File source = new File(fileName);
        PDDocument pdfDoc = PDDocument.load(source);
        int i=4; // page no.

        System.out.println("separate:" + reader.getLineSeparator());
        reader.setStartPage(i);
        reader.setEndPage(i);

        String pageText = reader.getText(pdfDoc);
        //System.out.println(pageText);
        System.out.println("===========");
        myTextPositions.sort(new MyTextPositionComparator());
        myTextPositions.forEach(myTextPosition -> {
            System.out.println("myTextPosition.text = " + myTextPosition.text);
        });
    }

    /**
     * @throws IOException
     * 테스트하려면 이 메소드 돌려보면 됩니다.
     * sort 할 때 Comparator 생성자 인자 중 colNum 돌리는 파일에 따라서 맞춰 넣어줘야 함.
     * colNum을 로 하면 1열짜리도 가능
     */
    @Test
    public void getMultiColPageExtract() throws IOException {
        String fileName = "2020자이스토리고2수학Ⅰ.pdf";
        File source = new File(fileName);
        PDDocument pdfDoc = PDDocument.load(source);
        int i=4; // page no.
        System.out.println("separate:" + reader.getLineSeparator());
        reader.setStartPage(i);
        reader.setEndPage(i);

        String pageText = reader.getText(pdfDoc);
        System.out.println(pageText);
        System.out.println("===========");
        System.out.println("xMax, xMin :" + xMax+ " " + xMin);
        myTextPositions.sort(new MyTextPositionComparatorMultiCol(xMax - xMin, 3));
        int before = myTextPositions.get(0).getId();

        String sameLineTextString = "";
        List<MyTextPosition> chuckWordList = new ArrayList<>();
        float fontSize = myTextPositions.get(0).getFontSize();
        float X = myTextPositions.get(0).getX();
        for (int j = 0; j < myTextPositions.size(); j++) {
            MyTextPosition myTextPosition = myTextPositions.get(j);

            if(myTextPosition.getId() != before) {
                System.out.println("");
                before = myTextPosition.getId();
                chuckWordList.add(new MyTextPosition(fontSize, X, sameLineTextString));
                sameLineTextString = "";
                fontSize = myTextPosition.getFontSize();
                X = myTextPosition.getX();
            }
            
            System.out.print(myTextPosition.text);
            sameLineTextString += myTextPosition.text;
        }
        chuckWordList.add(new MyTextPosition(fontSize,X, sameLineTextString));
        System.out.println("====================");

        for(int j=0; j<chuckWordList.size(); j++) {
            // 공백만 있는 덩어리 제거
            if(chuckWordList.get(j).getText().isBlank()) {
                chuckWordList.remove(j);
                j--;
            }
            // 숫자만 있는 덩어리 앞에 붙이고 제거
            if(j>= 1 && isNumeric(chuckWordList.get(j).getText())) {

                chuckWordList.set(j-1,
                        new MyTextPosition(chuckWordList.get(j-1).getFontSize(),
                                chuckWordList.get(j-1).getX(),
                                chuckWordList.get(j-1).getText().concat(" " + chuckWordList.get(j).getText())));
                chuckWordList.remove(j);
                j--;
            }
        }
        
        for(int j=0; j<chuckWordList.size(); j++) {
            System.out.println("chuckWordList.get("+ j +") = ["+ chuckWordList.get(j).getFontSize() + " "+
                    chuckWordList.get(j).getX() +  "]: " +
                    chuckWordList.get(j).getText());
        }

        System.out.println("xMin = " + xMin + "/" + xMax);
    }
    public static boolean isNumeric(String text) {
        text = text.replaceAll("(^\\p{Z}+|\\p{Z}+$)", "");

        try {
            int extractInt = Integer.parseInt(text);
        }
        catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    public static boolean isDate(String input) {

        String regex = ".*[0-9]{2,4}.[0-9]{1,2}.[0-9]{1,2}.*";
        if(input.matches(regex)) {
            return true;
        }else {
            return false;
        }

    }

    @Test
    public void strTest() throws UnsupportedEncodingException {
        byte[] b = {-30, -128, -94, -20, -99, -68, -21, -109, -79, -22, -72, -119, 32, -21, -113, -124, -20, -96, -124, 32, -21, -84, -72, -20, -96, -100, 8};
        b = Arrays.copyOfRange(b, 0, b.length - 1);
        String s = new String(b, "UTF-8");
        System.out.println("s = " + s);
    }
//    @Test
//    public void tesseractTest() throws TesseractException, IOException {
//        Tesseract tesseract = new Tesseract();
//        tesseract.setDatapath("C:/Program Files/Tesseract-OCR/tessdata");
//        tesseract.setLanguage("kor");
//        String fileName = "일등급수학I.pdf";
//        String filePath = System.getProperty("user.dir") + "/"+ fileName;
//        File source = new File(filePath);
//        PDDocument pdfDoc = PDDocument.load(source);
//
//        PDFRenderer pdfRenderer = new PDFRenderer(pdfDoc);
//        for (int page = 3; page < 4; page++) {
//            BufferedImage bufferedImage = pdfRenderer.renderImageWithDPI(page, 1000, ImageType.GRAY);
//
//            String str = tesseract.doOCR(bufferedImage);
//            System.out.println(str);
//
//        }

        //String text = tesseract.doOCR(source);
        //System.out.print(text);
//    }
}
