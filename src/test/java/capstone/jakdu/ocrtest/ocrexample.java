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


public class ocrexample {
    List<MyTextPosition> myTextPositions = new ArrayList<>();
    static int id = 0;
    float xMin = 999;
    float xMax = 0;
    float contentsY = 0;

    class MyTextPositionComparatorMultiCol implements Comparator<MyTextPosition> {
        private final float rowSize;
        public MyTextPositionComparatorMultiCol(float pageWidth, int colNum) {
            rowSize = pageWidth / colNum;
        }

        @Override
        public int compare(MyTextPosition o1, MyTextPosition o2) {
            // o1과 o2가 서로 다른 열에 있는 경우
            int r1 = (int) ((o1.x - xMin) / rowSize);
            int r2 = (int) ((o2.x - xMin) / rowSize);

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
                    else if(o1.x == o2.x) return 0;
                    else return 1;
                }
                else {
                    if(o1.y > o2.y) {
                        return 1;
                    }
                    else if(o1.y < o2.y) {
                        return -1;
                    }
                    else { //o1.y = o2.y
                        if(o1.x < o2.x) {
                            return -1;
                        }
                        else if(o1.x == o2.x) {
                            return 0;
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

            text = text.replaceAll("(\\s| )+", " ");
            //text = text.replaceAll("\t")

            // 쓸데없는 값 제거
            // 차례, 목차, contents
            // indd, 날짜 형식
            if(tempText.matches("(\\s| )+")) {
                return;
            }
            else if(tempText.contains("차례") ||
                    tempText.contains("목차") ||
                    tempText.toLowerCase().contains("contents")
                     ) {

                contentsY = textPositions.get(0).getY();
                System.out.println("[차례]text = " + text);
                return;
            } else if(tempText.toLowerCase().contains("indd") || isDate(tempText)) {
                return;
            }

            // Y 좌표 기준으로 제거
            if(contentsY != 0 && textPositions.get(0).getY() <= contentsY) {
                return;
            }


            for (int i = 0; i < text.length(); i++) {
                TextPosition t;
                t = textPositions.get(i);

                if(t.getX() < 0 || t.getY() < 0) {
                    continue;
                }

                xMax = Math.max(xMax, t.getEndX());
                xMin = Math.min(xMin, t.getX());
                System.out.println("" + text.substring(i, i + 1) + "/" + t.getX() + "/" + t.getY());
                myTextPositions.add(new MyTextPosition(t.getX(), t.getY(), t.getEndX(), t.getFontSizeInPt(), t.getHeight(), text.substring(i, i + 1), id));
            }
            TextPosition t = textPositions.get(text.length() - 1);
            myTextPositions.add(new MyTextPosition(t.getX(), t.getY(), t.getEndX(), t.getFontSizeInPt(), t.getHeight(), " ", id));
            id++;
//            System.out.println("text = " + text + "/" + first.getX() + "/" + first.getY() + "/" + last.getEndX() + "/" + last.getHeight());
            System.out.println("text = " + text);

            //myTextPositions.add(new MyTextPosition(first.getX(), first.getY(), last.getEndX(), last.getHeight(),text));
            if (startOfLine)
            {
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
        String text = reader.getText(pdfDoc);
        System.out.println(text);

    }
    
    @Test
    public void getSomePageExtractPDF() throws IOException {

        String fileName = "9종교과서시크릿수학1-본문(학생용).pdf";
        File source = new File(fileName);
        PDDocument pdfDoc = PDDocument.load(source);
        int i=2;

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
     * 차례 위쪽 자르기
     * a < b < c
     * a < b > c
     */
    @Test
    public void getMultiColPageExtract() throws IOException {
        String fileName = "gg.pdf";
        int i = 14; // page no.
        int colNum = 1;

        File source = new File(fileName);
        PDDocument pdfDoc = PDDocument.load(source);

        System.out.println("separate:" + reader.getLineSeparator());
        reader.setStartPage(i);
        reader.setEndPage(i);

        String pageText = reader.getText(pdfDoc);
        System.out.println(pageText);
        System.out.println("===========");
        System.out.println("xMax, xMin :" + xMax+ " " + xMin);
        myTextPositions.sort(new MyTextPositionComparatorMultiCol(xMax - xMin, colNum));
        int before = myTextPositions.get(0).getId();

        int id = 0;
        myTextPositions.get(0).setId(id);
        MyTextPosition startChar = myTextPositions.get(0);
        MyTextPosition tempChar = null;
        int tempStart = -1;
        for(int j = 1; j < myTextPositions.size(); j++) {
            MyTextPosition here = myTextPositions.get(j);
            // here가 startChar와 같은 행이라고 판단되는 경우
            if (Math.abs(startChar.getY() - here.getY()) < Math.min(startChar.getHeight(), here.getHeight())) {
                here.setId(id);
                if(tempStart >= 0 && tempChar != null) {
                    for(int k = tempStart; k < j; k++) {
                        myTextPositions.get(k).setId(id);
                    }
                    tempStart = -1;
                    tempChar = null;
                }
                else {
                    here.setId(id);
                }
            }
            // ex)분모 a
            else {
                if(tempChar != null) {
                    if (!(Math.abs(tempChar.getY() - here.getY()) < Math.min(tempChar.getHeight(), here.getHeight()))) {
                        id++;
                        for(int k = tempStart; k < j; k++) {
                            myTextPositions.get(k).setId(id);
                        }
                        startChar = tempChar;
                        tempStart = j;
                        tempChar = here;
                    }
                }
                else {
                    tempStart = j;
                    tempChar = here;
                }
            }
        }
        id++;
        for(int k = tempStart; k < myTextPositions.size(); k++) {
            myTextPositions.get(k).setId(id);
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
                sameLineTextString = sameLineTextString.replaceAll("((\\.\\s){3,}|(\\.{3,}))","... ");
                chuckWordList.add(new MyTextPosition(fontSize, X, sameLineTextString.replaceAll("(\\s| )+", " ")));

                sameLineTextString = "";
                fontSize = myTextPosition.getFontSize();
                X = myTextPosition.getX();
            }
            
            System.out.print(myTextPosition.text);
            sameLineTextString += myTextPosition.text;
        }
        sameLineTextString = sameLineTextString.replaceAll("((\\.\\s){3,}|(\\.{3,}))","... ");
        chuckWordList.add(new MyTextPosition(fontSize,X, sameLineTextString.replaceAll("(\\s| )+", " ")));
        System.out.println("====================");

        for(int j=0; j<chuckWordList.size(); j++) {
            System.out.println("chuckWordList.get("+ j +") = ["+ chuckWordList.get(j).getFontSize() + " "+
                    chuckWordList.get(j).getX() +  "]: " +
                    chuckWordList.get(j).getText());
        }

        for(int j=0; j<chuckWordList.size(); j++) {
            // 공백만 있는 덩어리 제거
            if(chuckWordList.get(j).getText().isBlank()) {
                chuckWordList.remove(j);
                j--;
            } else {
                chuckWordList.get(j).setText(
                        chuckWordList.get(j).getText().trim()
                );
            }

        }



        for(int j = 0; j < chuckWordList.size(); j++) {
            String text = chuckWordList.get(j).getText();
            int hierarchyNumber = getPrefixNum(text);
            System.out.println("[hierarchyNumber]: " + hierarchyNumber + "/ " + text);
            chuckWordList.get(j).setPrefixId(hierarchyNumber);
        }

        System.out.println();

        int oneDigitNumberPrefixCount = 0;
        int twoDigitNumberPrefixCount = 0;

        for(int j = 0; j < chuckWordList.size()-1; j++) {
            MyTextPosition myTextPosition = chuckWordList.get(j);
            MyTextPosition nextTextPosition = chuckWordList.get(j+1);

            int currentPrefixId = myTextPosition.getPrefixId();

            if(currentPrefixId == 2)
                oneDigitNumberPrefixCount++;
            else if(currentPrefixId == 0)
                twoDigitNumberPrefixCount++;

            // 접두어가 혼자있을때
            if(isAllTextIsPrefix(myTextPosition.getText(), myTextPosition.getPrefixId())) {
                boolean flag = false;

                if(currentPrefixId == 2) {
                    // string을 숫자로 바꿔
                    int num = Integer.parseInt(myTextPosition.getText());
                    if(oneDigitNumberPrefixCount != Integer.parseInt(myTextPosition.getText())) {
                        oneDigitNumberPrefixCount--;
                        flag = true;
                    }else if(num == 1 && oneDigitNumberPrefixCount != 1){
                        oneDigitNumberPrefixCount = 1;
                    }

                }
                else if(currentPrefixId == 0) {
                    int num = Integer.parseInt(myTextPosition.getText());
                    if(twoDigitNumberPrefixCount != num) {
                        twoDigitNumberPrefixCount--;
                        flag = true;
                    }else if(num == 1 && twoDigitNumberPrefixCount != 1){
                        twoDigitNumberPrefixCount = 1;
                    }

                }

                if(!flag) {
                    if (nextTextPosition.getPrefixId() == 10) {
                        chuckWordList.set(j + 1,
                                new MyTextPosition(chuckWordList.get(j).getFontSize(),
                                        chuckWordList.get(j).getX(),
                                        chuckWordList.get(j).getText().concat(" " + chuckWordList.get(j + 1).getText())));

                        chuckWordList.get(j + 1).setPrefixId(chuckWordList.get(j).getPrefixId());
                        chuckWordList.remove(j);
                    }
                }
                else {
                    chuckWordList.get(j).setPrefixId(10);
                }
            }

        }


        /** 목차의 계층구조 파악하기 **/

        // fontsize, 접두어 번호, 계층

        // 같은 열: 폰트사이즈 다른 경우 -> 구분 가능 / [같은 경우 -> 시작 위치]
        // 같은 폰트 사이즈 중에서, 접두어 유형에 해당하는 계층 번호 key -> fontSize + 접두어 유형


        Map<HierarchyData, Integer> hierarchyDB = new HashMap<>();
        Map<HierarchyData, Boolean> pageExistDB = new HashMap<>();
        int hierarchyNum = 0;



        for(int j = 0; j < chuckWordList.size(); j++) {
            MyTextPosition chuckWord = chuckWordList.get(j);

            HierarchyData hierarchyData = new HierarchyData(chuckWord.getFontSize(), chuckWord.getPrefixId());
            Integer hNum = hierarchyDB.get(hierarchyData);
            boolean hasPage = hasPageNum(chuckWord.getText());
            if(hNum == null) { // 1이 최상위 계층

                hierarchyDB.put(hierarchyData, ++hierarchyNum);
                pageExistDB.put(hierarchyData, hasPage);
                hNum = hierarchyNum;
            }
            else {
                if(hasPage && !pageExistDB.get(hierarchyData)) {
                    pageExistDB.put(hierarchyData, true) ;
                }
            }

            chuckWord.setHierarchyNum(hNum);
        }
        hierarchyDB.clear();
        hierarchyNum = 0;
        for(int j = 0; j < chuckWordList.size(); j++) {
            MyTextPosition chuckWord = chuckWordList.get(j);

            HierarchyData hierarchyData = new HierarchyData(chuckWord.getFontSize(), chuckWord.getPrefixId());
            Integer hNum = hierarchyDB.get(hierarchyData);
            if(hNum == null) { // 1이 최상위 계층

                hierarchyDB.put(hierarchyData, ++hierarchyNum);
                hNum = hierarchyNum;
            }

            chuckWord.setHierarchyNum(hNum);
        }

        for(int j = 0; j < chuckWordList.size()-1; j++) {
            MyTextPosition chuckWord = chuckWordList.get(j);
            MyTextPosition nextChuckWord = chuckWordList.get(j+1);
            HierarchyData hierarchyData = new HierarchyData(chuckWord.getFontSize(), chuckWord.getPrefixId());
            if(pageExistDB.get(hierarchyData) && !hasPageNum(chuckWord.getText())) {
                if(nextChuckWord.getPrefixId() == 10 && hasPageNum(nextChuckWord.getText())) {
                    chuckWord.setText(chuckWord.getText() + " " + nextChuckWord.getText());
                    chuckWordList.remove(j+1);
                }
            }
        }

        /**
         *
         *
         * 2개 이상 공백을 한개로 변경 --> 텍스트 정렬 --> 모든 내용이 공백이면 제거 --> 접두어 파악
         * --> 떨어진 접두어 붙이기 --> [계층파악 --> 페이지 t/f파악]
         * 페이지 붙이는 근거
         *
         * **/




        for(int j = 0; j < chuckWordList.size(); j++) {
            MyTextPosition chuckWord = chuckWordList.get(j);
            String text = chuckWord.getText();
            int hierarchyNumber = chuckWord.getHierarchyNum();
            int prefixNumber = chuckWord.getPrefixId();
            HierarchyData hierarchyData = new HierarchyData(chuckWord.getFontSize(), chuckWord.getPrefixId());
            Boolean pageExist = pageExistDB.get(hierarchyData);
            System.out.println("[isPageExist, hierarchyNum, prefixNum]:" + pageExist + "/ " + hierarchyNumber + "/ "+prefixNumber + "/ " + text);

        }



    }

    public static boolean isAllTextIsPrefix(String text, int prefixId) {
        text = text.replaceAll("\\s+", "");

        switch (prefixId) {
            case 0:
                return text.matches("^[0-9]{2}$");
            case 1:
                return text.matches("^(I|II|III|IV|V|VI|VII|VIII|IX|X|XI|XII|Ⅳ|Ⅲ)$");
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
        /** 모든 공백 1칸, 숫자 뒤에 점 또는 공백이 오는 거로 수정필요 **/
        text = text.replaceAll("\\s+", " ");

        if(text.matches("^[0-9]{2}((\\s|\\.).*)?$"))
            return 0;
        else if(text.matches("^(I|II|III|IV|V|VI|VII|VIII|IX|X|XI|XII|Ⅰ|Ⅱ|Ⅲ|Ⅳ|Ⅴ|Ⅵ|Ⅶ|Ⅷ|Ⅸ|Ⅹ|Ⅺ|Ⅻ)((\\s|\\.).*)?$"))
            return 1;
        else if(text.matches("^(Ⅰ|Ⅱ|Ⅲ|Ⅳ|Ⅴ|Ⅵ|Ⅶ|Ⅷ|Ⅸ|Ⅹ|Ⅺ|Ⅻ).*$")) // 뒤에 공백 없어도됨
            return 1;
        else if(text.matches("^[1-9]((\\s|\\.).*)?$"))
            return 2;
        else if(text.matches("^[A-Z]((\\s|\\.).*)?$"))
            return 3;
        else if(text.matches("^(가|나|다|라|마|바|사|아|자|차|카|타|파|하)((\\s|\\.).*)?$"))
            return 4;
        else if(text.matches("^(유형)\\s?([0-9]{1,2})((\\s|\\.).*)?$"))
            return 5;
        else if(text.matches("^(•|●|■)((\\s|\\.).*)?$"))
            return 6;
        else if(text.matches("^(첫|둘|셋|넷|다섯|여섯|일곱|여덟|아홉|열)\\s?째((\\s|\\.).*)?$"))
            return 7;
        else if(text.matches("^.*(?i)chapter.*$"))
            return 8;
        else if(text.matches("^(?i)(section).*$"))
            return 9;
        else  // 접두어가 없을 때
            return 10;

    }

    public static boolean isDate(String input) {

        String regex = ".*[0-9]{2,4}.[0-9]{1,2}.[0-9]{1,2}.*";
        if(input.matches(regex)) {
            return true;
        }else {
            return false;
        }

    }

    public static boolean hasPageNum(String s) {
        String s1 = s.replaceAll("(\\s| )+", " ");
        System.out.println("s1 = " + s1);
        return s1.matches("(.*\\s\\d+|\\d+)");
    }

    @Test
    public void strTest() throws UnsupportedEncodingException {
        byte[] b = {-30, -128, -94, -20, -99, -68, -21, -109, -79, -22, -72, -119, 32, -21, -113, -124, -20, -96, -124, 32, -21, -84, -72, -20, -96, -100, 8};
        b = Arrays.copyOfRange(b, 0, b.length - 1);
        String s = new String(b, "UTF-8");
        System.out.println("s = " + s);
    }

    /**                Ⅲ   Ⅳ     ^M{0,4}(CM|CD|D?C{0,3})(XC|XL|L?X{0,3})(IX|IV|V?I{0,3})$
     * I, II, III ... => 정규식 ^[I, II, III, IV, V, VI, VII, VIII, IX, X, XI, XII]$
     * 1, 2, 3, 4 ....... => 정규식   ^[1-9]$
     * 01, 02, 03 ... => 정규식        ^\d{2}$
     * A, B, C, D ...                ^[A-Z]$
     * 가, 나, 다,                     ^(가|나|다|라|마|바|사|아|자|차|카|타|파|하)$
     * 유형 01, 유형 02, 유형 03, 유형 04 ...   ^유형$
     * •, ●                              ^(•|●).*$
     *  첫째, 둘째, 셋째 ... ^(첫|둘|셋|넷|다섯|여섯|일곱|여덟|아홉|열)째.*$
     * chapter 1, chapter 2, chapter 3 ...I, II, III ... ^(?i)chapter$
     *  1. ~~~~
     *   1[-, ., _] ...                         ^\d((-|.|_)\d)+.*$
     *   1-2.~~
     *  자신 = i
     * part 1, part 2, part 3 ...I, II, III ...     ^(?i)part$
     * 1장, 2장, 3장 ...                                   ^장$
     * appendix 1, appendix 2, appendix 3 ...I, II, III ... ^(?i)appendix$
     * section 1 section 2, section 3 ...I, II, II ... ^(?i)section$
     * case 1, case 2, case 3 ...I, II, III ...         ^(?i)case$
     *
     *
     *
     * 1, 2, 3 순차적 / chapter, 장 고정적
     * 순차적 | 순차적 + 고정적 | 고정적 + 순차적
     *
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
