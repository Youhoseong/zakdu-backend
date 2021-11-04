package capstone.jakdu.EpubTest;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import nl.siegmann.epublib.domain.TOCReference;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class EPubTocObject {
    private int hierarchyNum;
    private TOCReference tocReference;
}
