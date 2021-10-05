package capstone.jakdu.ocrtest;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
public class MyTextPosition {
    public MyTextPosition(float fontSize, float x, String text) {
        this.fontSize = fontSize;
        this.x = x;
        this.text = text;
    }

    public MyTextPosition(float x, float y, float endX, float fontSize, String text, int id) {
        this.x = x;
        this.y = y;
        this.endX = endX;
        this.fontSize = fontSize;
        this.text = text;
        this.id = id;
    }

    float x;
    float y;
    float endX;
    float fontSize;
    String text;
    int id;


}
