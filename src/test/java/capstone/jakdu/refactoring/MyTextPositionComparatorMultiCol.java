package capstone.jakdu.refactoring;

import capstone.jakdu.ocrtest.MyTextPosition;

import java.util.Comparator;

public class MyTextPositionComparatorMultiCol implements Comparator<MyTextPosition> {
    private final float rowSize;
    private final float xMin;

    public MyTextPositionComparatorMultiCol(float pageWidth, int colNum, float xMin) {
        rowSize = pageWidth / colNum;
        this.xMin = xMin;
    }

    @Override
    public int compare(MyTextPosition o1, MyTextPosition o2) {
        // o1과 o2가 서로 다른 열에 있는 경우
        int r1 = (int) ((o1.getX() - xMin) / rowSize);
        int r2 = (int) ((o2.getX() - xMin) / rowSize);

        if(r1 < r2) {
            return -1;
        }
        else if(r1 > r2) {
            return 1;
        }
        // o1과 o2가 같은 열에 있는 경우
        else{
            // o1과 o2가 같은 행에 있는 경우
            if(Math.abs(o1.getY() - o2.getY()) < Math.min(o1.getHeight(), o2.getHeight())) {
                if(o1.getX() < o2.getX()) return -1;
                else if(o1.getX() == o2.getX()) return 0;
                else return 1;
            }
            else {
                if(o1.getY() > o2.getY()) {
                    return 1;
                }
                else if(o1.getY() < o2.getY()) {
                    return -1;
                }
                else { //o1.y = o2.y
                    if(o1.getX() < o2.getX()) {
                        return -1;
                    }
                    else if(o1.getX() == o2.getX()) {
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
