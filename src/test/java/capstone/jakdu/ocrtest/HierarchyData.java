package capstone.jakdu.ocrtest;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@AllArgsConstructor
@Getter
@Setter
class HierarchyData {
    private float fontSize;
    private int prefixNum;


    @Override
    public String toString() {
        return String.format("%s %s",this.fontSize,this.prefixNum);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fontSize, prefixNum);
    }

    @Override
    public boolean equals(Object obj) {
        //p1.equals(p2)
        if(obj instanceof HierarchyData) {
            HierarchyData p = (HierarchyData) obj;
            return this.fontSize == p.fontSize &&
                    this.prefixNum == p.prefixNum;
        }
        return false;
    }
}