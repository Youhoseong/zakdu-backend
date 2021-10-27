package capstone.jakdu.refactoring;

import capstone.jakdu.ocrtest.MyTextPosition;
import capstone.jakdu.refactoring.Regex;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public class MyPDFTextStripper extends PDFTextStripper  {
    List<MyTextPosition> myTextPositions = new ArrayList<>();

    public MyPDFTextStripper() throws IOException {
    }

    int id = 0;
    float xMin = 999;
    private float xMax = 0;
    float contentsY = 0;
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
        System.out.println("text.length() + textPositions.size() = " + text.length() + "/ " +textPositions.size());
        byte[] b = text.getBytes(StandardCharsets.UTF_8);
        if(b[b.length-1] == 8)
            b = Arrays.copyOfRange(b, 0, b.length - 1);
        else if(b[0] == 8 && b[1] == 8) {
            b = Arrays.copyOfRange(b, 2, b.length);
        }
        text = new String(b, StandardCharsets.UTF_8);
        String tempText = text.replaceAll(" ", "");
        System.out.println("text.length() + textPositions.size() = " + text.length() + "/ " +textPositions.size());
        //text = text.replaceAll("(\\s| )+", " ");

        // 쓸데없는 값 제거
        // 차례, 목차, contents
        // indd, 날짜 형식
        if(tempText.matches("(\\s| )+")) {
            return;
        }
        else if(tempText.contains("차례") ||
                tempText.contains("목차") ||
                tempText.toLowerCase().contains("contents"))
        {
            contentsY = textPositions.get(0).getY();
            System.out.println("[차례]text = " + text);
            return;
        }
        else if(tempText.toLowerCase().contains("indd") || Regex.isDate(tempText)) {
            return;
        }

        // 차례 글자 보다 Y 좌표 크면 제거
        if(contentsY != 0 && textPositions.get(0).getY() <= contentsY) {
            return;
        }

        for (int i = 0; i < text.length(); i++) {
            TextPosition t;
            if(i >= textPositions.size()) break;
            t = textPositions.get(i);

            if(t.getX() < 0 || t.getY() < 0)
                continue;

            xMax = Math.max(xMax, t.getEndX());
            xMin = Math.min(xMin, t.getX());
            System.out.println("" + text.substring(i, i + 1) + "/" + t.getX() + "/" + t.getY() + "/ " + t.getFontSizeInPt());
            myTextPositions.add(new MyTextPosition(t.getX(), t.getY(), t.getEndX(), t.getFontSizeInPt(), t.getHeight(), text.substring(i, i + 1), id));
        }
        // 마지막에 공백 삽입

        TextPosition t = textPositions.get(Math.min(textPositions.size() - 1, text.length() - 1));
        myTextPositions.add(new MyTextPosition(t.getX(), t.getY(), t.getEndX(), t.getFontSizeInPt(), t.getHeight(), " ", id));
        id++;
        //System.out.println("text = " + text);

        if (startOfLine) {

            TextPosition firstPosition = textPositions.get(0);
            writeString(String.format("[%s %s %s %s]", firstPosition.getFontSizeInPt(),firstPosition.getHeight(), firstPosition.getXDirAdj(), firstPosition.getYDirAdj()));
            startOfLine = false;
        }
        super.writeString(text, textPositions);
    }
}

