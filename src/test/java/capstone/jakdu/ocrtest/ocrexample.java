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
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ocrexample {
    List<MyTextPosition> myTextPositions = new ArrayList<>();
    static int id = 0;
    float xMin = 999;
    float xMax = 0;


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
                if(Math.abs(o1.y - o2.y) < Math.min(o1.height, o2.height)) {
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
                    tempText.contains("목차") ||
                    tempText.toLowerCase().contains("contents") ||
                    tempText.toLowerCase().contains("indd") ||
                    isDate(tempText)  ) {

                System.out.println("[차례]text = " + text);
                return;
            }



            for (int i = 0; i < text.length(); i++) {
                TextPosition t = textPositions.get(i);
                if(t.getX() < 0 || t.getY() < 0) {
                    continue;
                }

                xMax = Math.max(xMax, t.getEndX());
                xMin = Math.min(xMin, t.getX());
                System.out.println("" + text.substring(i, i + 1) + "/" + t.getX() + "/" + t.getY());
                myTextPositions.add(new MyTextPosition(t.getX(), t.getY(), t.getEndX(), t.getFontSizeInPt(), t.getHeight(), text.substring(i, i + 1), id));
            }
            id++;
//            System.out.println("text = " + text + "/" + first.getX() + "/" + first.getY() + "/" + last.getEndX() + "/" + last.getHeight());
            System.out.println("text = " + text);

            //myTextPositions.add(new MyTextPosition(first.getX(), first.getY(), last.getEndX(), last.getHeight(),text));
            if (startOfLine)
            {
                TextPosition t = textPositions.get(0);
                //myTextPositions.add(new MyTextPosition(t.getX() - 0.1f, t.getY(), t.getEndX(), t.getHeight(), "@"));

                TextPosition firstPosition = textPositions.get(0);
                writeString(String.format("[%s %s %s %s]", firstPosition.getFontSizeInPt(),firstPosition.getHeight(), firstPosition.getXDirAdj(), firstPosition.getYDirAdj()));
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

    /**
     * @throws IOException
     * 테스트하려면 이 메소드 돌려보면 됩니다.
     * sort 할 때 Comparator 생성자 인자 중 colNum 돌리는 파일에 따라서 맞춰 넣어줘야 함.
     * colNum을 로 하면 1열짜리도 가능
     */
    @Test
    public void getMultiColPageExtract() throws IOException {
        String fileName = "지학_풍산자반복수학_수학(상)_본문(학생용).pdf";
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
        myTextPositions.sort(new MyTextPositionComparatorMultiCol(xMax - xMin, 1));
        int before = myTextPositions.get(0).getId();

        int id = 0;
        myTextPositions.get(0).setId(id);
        for(int j = 1; j < myTextPositions.size(); j++) {
            MyTextPosition prev = myTextPositions.get(j - 1);
            MyTextPosition here = myTextPositions.get(j);
            if (Math.abs(prev.getY() - here.getY()) < Math.min(prev.getHeight(), here.getHeight())) {
                here.setId(id);
            }
            else {
                here.setId(++id);
            }
        }

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

        }
        
        for(int j=0; j<chuckWordList.size(); j++) {
            System.out.println("chuckWordList.get("+ j +") = ["+ chuckWordList.get(j).getFontSize() + " "+
                    chuckWordList.get(j).getX() +  "]: " +
                    chuckWordList.get(j).getText());
        }

        System.out.println("xMin = " + xMin + "/" + xMax);

        /** 목차의 계층구조 파악하기 **/
        // fontsize, 접두어 번호, 계층

        Map<Integer, Map<Integer, Integer>> hierarchyDB = new HashMap<>();
        List<Integer> hierarchyMap = new ArrayList<>(); // [index] = 계층 번호
        // 같은 열: 폰트사이즈 다른 경우 -> 구분 가능 / [같은 경우 -> 시작 위치]
        // 같은 폰트 사이즈 중에서, 접두어 유형에 해당하는 계층 번호 key -> fontSize + 접두어 유형

        float currentfontSize = chuckWordList.get(0).getFontSize();

        for(int j = 0; j < chuckWordList.size(); j++) {
            String text = chuckWordList.get(j).getText();
            int hierarchyNumber = getPrefixNum(text);
            System.out.println("[hierarchyNumber]: " + hierarchyNumber + "/ " + text);
            chuckWordList.get(j).setPrefixId(hierarchyNumber);
        }

        System.out.println();

        for(int j = 0; j < chuckWordList.size()-1; j++) {
            MyTextPosition myTextPosition = chuckWordList.get(j);
            MyTextPosition nextTextPosition = chuckWordList.get(j+1);
            // 접두어가 혼자있을때
            if(isAllTextIsPrefix(myTextPosition.getText(), myTextPosition.getPrefixId())) {
                // 텍스ㅌ
                if(nextTextPosition.getPrefixId() == 10) {
                        chuckWordList.set(j+1,
                                new MyTextPosition(chuckWordList.get(j).getFontSize(),
                                        chuckWordList.get(j).getX(),
                                        chuckWordList.get(j).getText().concat(" " + chuckWordList.get(j+1).getText())));

                        chuckWordList.get(j+1).setPrefixId(chuckWordList.get(j).getPrefixId());
                        chuckWordList.remove(j);
                }
            }


        }

        for(int j = 0; j < chuckWordList.size(); j++) {
            String text = chuckWordList.get(j).getText();
            int hierarchyNumber = getPrefixNum(text);
            System.out.println("[hierarchyNumber]: " + hierarchyNumber + "/ " + text);
            chuckWordList.get(j).setPrefixId(hierarchyNumber);
        }


    }
    public static boolean isAllTextIsPrefix(String text, int prefixId) {
        text = text.replaceAll("(^\\p{Z}+|\\p{Z}+$)", "");

        switch (prefixId) {
            case 0:
                return text.matches("^[0-9]{2}$");
            case 1:
                return text.matches("^(I|II|III|IV|V|VI|VII|VIII|IX|X|XI|XII)$");
            case 2:
                return text.matches("^[1-9]$");
            case 3:
                return text.matches("^[A-Z]$");
            case 4:
                return text.matches("^(가|나|다|라|마|바|사|아|자|차|카|타|파|하)$");
            case 5:
                return text.matches("^(유형)\\d{2}$");
            case 6:
                return text.matches("^(•|●)$");
            case 7:
                return text.matches("^(첫|둘|셋|넷|다섯|여섯|일곱|여덟|아홉|열)째$");
        }
        return false;
    }

    public static int getPrefixNum(String text) {
        text = text.replaceAll("(^\\p{Z}+|\\p{Z}+$)", "");

        if(text.matches("^[0-9]{2}.*$"))
            return 0;
        else if(text.matches("^(I|II|III|IV|V|VI|VII|VIII|IX|X|XI|XII).*$"))
            return 1;
        else if(text.matches("^[1-9].*$"))
            return 2;
        else if(text.matches("^[A-Z].*$"))
            return 3;
        else if(text.matches("^(가|나|다|라|마|바|사|아|자|차|카|타|파|하).*$"))
            return 4;
        else if(text.matches("^(유형)\\d{2}.*$"))
            return 5;
        else if(text.matches("^(•|●).*$"))
            return 6;
        else if(text.matches("^(첫|둘|셋|넷|다섯|여섯|일곱|여덟|아홉|열)째.*$"))
            return 7;
        else  // 접두어가 없을 때
            return 10;


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

    /**                        ^M{0,4}(CM|CD|D?C{0,3})(XC|XL|L?X{0,3})(IX|IV|V?I{0,3})$
     * I, II, III ... => 정규식 ^[I, II, III, IV, V, VI, VII, VIII, IX, X, XI, XII].*$
     * 1, 2, 3, 4 ....... => 정규식   ^[1-9].*$
     * 01, 02, 03 ... => 정규식        ^\d{2}.*$
     * A, B, C, D ...                ^[A-Z].*$
     * 가, 나, 다,                     ^(가|나|다|라|마|바|사|아|자|차|카|타|파|하).*$
     * 유형 01, 유형 02, 유형 03, 유형 04 ...   ^(유형)\d{2}.*$
     * •, ●                              ^(•|●).*$
     *  첫째, 둘째, 셋째 ... ^(첫|둘|셋|넷|다섯|여섯|일곱|여덟|아홉|열)째.*$
     * chapter 1, chapter 2, chapter 3 ...I, II, III ... ^(?i)chapter.*$
     *  1. ~~~~
     *   1[-, ., _] ...                         ^\d((-|.|_)\d)+.*$
     *   1-2.~~
     *  자신 = i
     * part 1, part 2, part 3 ...I, II, III ...     ^(?i)part\d.*$
     * 1장, 2장, 3장 ...                                   ^\d장.*$
     * appendix 1, appendix 2, appendix 3 ...I, II, III ... ^(?i)appendix.*$
     * section 1 section 2, section 3 ...I, II, II ... ^(?i)section.*$
     * case 1, case 2, case 3 ...I, II, III ...         ^(?i)case.*
     *
     * 1, 2, 3 순차적 / charper, 장 고정적
     * 순차적 | 순차적 + 고정적 | 고정적 + 순차적
     * 
     * h = 계층의 history 저장하는 Map
     * if fontSize[i-1] > fontSize[i] then 상위의 child, h map에 추가
     * elif fontSize[i-1] < fontSize[i] then h map 내부에서 찾기
     * else
     *      if x[i-1] < x[i] then 상위의 child
     *      elif x[i-1] > x[i] then 자신과 폰트 + 위치가 동일한 곳 찾기
     *      else then 자신과 같은 위치
     **/

   /**
     *
     * 1-13
     * 1. (교사용) 2022 수능특강 / 가능
     * 2.  [2018년개정] 개념원리~ / 가능
     * 3. [올림포스고난도]수학 스캔본 / 가능은 해보임
     * 4.  [유형필수]2019pdf해결의 법칙 /가능    *
     * 5. [unlocked]2020 자이스토리 / 애매함 *
     * 6. 02-1개념원리RPM / 가능
     * 7. 수력충전 / 가능
     * 8. 18짱중요한_수학 / 가능
     * 9. 2015개정중등수학3-1수력충전 / 불가해보임
     * 10. 2015개정중등수학3-2 ㅎ념플러~ / 불가해보임
     * 11. 2020자이스토리고2수학I / 애매함
     * 12. 2020짱중요한유형확률과통계 / 될듯한데 살짝 애매해보임
     * 13. 2021알피엠3_1 / 가능해보이는데 애매함
     *
     * 아샘HiMath기하 / 가능
     * 일등급수학I / 가능
     * 지학_풍산자반복수학_수학(상)_본문(학생용) / 가능
     * 짱쉬운유형 / 가능
     * 텐투_고등수학(하)_본문 / 가능
     * 풍산자반복수학중2-1본문 / 가능
     * Informationselfbook1(정보책) / 가능
     * RPM기하 / 가능
     *
     * 풍산자라이가확률과통계본문 / 불가
     * EBS2020학년도수능완성수학가형 / 불가
     * EBS2020학년도수능완성수학나형 / 불가
     * 중 1 중학 연산 1권 / 불가
     *
     *1. 4-7 불가
     * 2. 9종시크릿교과서 가능
     * 3. 개념원리수학하 가능
     * 4. 개념플러스유형수학2유형편 가능
     * 5. 고_미적분(홍성복)_지도서 불가능
     * 6. 고등_수학)개념플러스유형_확률과통계-개념편 애매
     * 7. 고등_수학)개념플러스유형_확률과통계-유형편
     * 8. 지학사 미적분 교사용 교과서 가능
     * 9. 수학의힘감마중1-1 가능
     * 10. 수학의힘베타중1-1 가능
     * 11. 수학의힘알파중1-1 가능
     * 12. 숨마쿰라우데고등스타트업(상) 가능 / 스캔본
     * 13. 수학의 바이블 가능 / 한글깨짐
     *
     * 백에 공백 지워야함!!!!
     *     */
}
