package capstone.jakdu.ocrtest;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class MyTextPosition {
    float x;
    float y;
    float endX;
    float height;
    String text;
    int id;
}
