package capstone.jakdu.ocrtest;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class MyTextPosition {
    public MyTextPosition(float fontSize, float x, String text) {
        this.fontSize = fontSize;
        this.x = x;
        this.text = text;
    }

    public MyTextPosition(String text, int startPage, int endPage, int hierarchyNum) {
        this.text = text;
        this.startPage = startPage;
        this.endPage = endPage;
        this.hierarchyNum = hierarchyNum;
    }

    public MyTextPosition(float x, float y, float endX, float fontSize, float height, String text, int id) {
        this.x = x;
        this.y = y;
        this.endX = endX;
        this.fontSize = fontSize;
        this.height = height;
        this.text = text;
        this.id = id;
    }

    private float x;
    private float y;
    private float endX;
    private float fontSize;
    private float height;
    private String text;
    private int id;
    private int prefixId;
    private int hierarchyNum;

    private int startPage;
    private int endPage = -1;
    private int parentId = -1;

    public int removeStartPage() {

        int n = this.text.length();
        String strPage = "";
        for(int i = n - 1; i >= 0; i--) {
            if(isNumeric(this.text.substring(i,i+1))) {
                strPage = this.text.substring(i, i+1) + strPage;
                this.text = this.text.substring(0,i);
            }
            else {
                break;
            }
        }
        if(strPage.isEmpty()) {
            return -1;
        }
        else {
            return Integer.parseInt(strPage);
        }

    }

    public static boolean isNumeric(String str) {
        return str.matches("\\d+");  //match a number with optional '-' and decimal.
    }


}
