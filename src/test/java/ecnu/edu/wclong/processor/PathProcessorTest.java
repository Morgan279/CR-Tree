package ecnu.edu.wclong.processor;

import ecnu.edu.wclong.constant.GeneralLabel;
import ecnu.edu.wclong.pojo.LabelPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.neo4j.graphdb.Label;

import java.util.ArrayList;
import java.util.List;

class PathProcessorTest {

    private PathProcessor pathProcessor;

    @BeforeEach
    public void init() {
        this.pathProcessor = new PathProcessor(3, 5);
    }

    @Test
    public void testSubPath() {
        System.out.println(pathProcessor.isSubPath(13, 5));
    }

    @Test
    public void encodeTest() {
        List<Label> labelPaths = new ArrayList<>();
        labelPaths.add(GeneralLabel.SPATIAL);
        labelPaths.add(GeneralLabel.GROUP);
        labelPaths.add(GeneralLabel.GROUP);
        LabelPath labelPath = new LabelPath(labelPaths);
        System.out.println(pathProcessor.encode(labelPath));
    }

}
