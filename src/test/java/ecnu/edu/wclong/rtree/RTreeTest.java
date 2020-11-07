package ecnu.edu.wclong.rtree;

import cn.hutool.core.collection.CollUtil;
import ecnu.edu.wclong.constant.NonSpatialLabel;
import ecnu.edu.wclong.pojo.LabelPath;
import ecnu.edu.wclong.pojo.PathCode;
import ecnu.edu.wclong.processor.PathProcessor;
import ecnu.edu.wclong.rtree.sdo.Bound;
import ecnu.edu.wclong.rtree.sdo.BoundVector;
import ecnu.edu.wclong.rtree.sdo.RTreeEntry;
import ecnu.edu.wclong.rtree.sdo.Rectangle;
import ecnu.edu.wclong.util.RandomUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.neo4j.graphdb.Label;

import java.util.ArrayList;
import java.util.List;

class RTreeTest {

    private RTree<Integer> rTree;

    private PathProcessor pathProcessor;

    @BeforeEach
    public void init() {
        this.rTree = new RTree<>(5);
        this.pathProcessor = new PathProcessor(NonSpatialLabel.getLength(), 5);
    }

    @Test
    void insert() {
        for (int i = 0; i < 1000; ++i) {
            RTreeEntry<Integer> entry = generateRandomEntry();
            rTree.insert(entry);
        }
    }

    @Test
    void search() {
        insert();
        System.out.println("insert complete");
        long startTime = System.currentTimeMillis();
        int dimension = 2;
        int value = RandomUtil.getIntegerRandomNumber(dimension, 100);
        BoundVector boundVector = new BoundVector(dimension);
        for (int i = 0; i < dimension; ++i) {
            boundVector.setDimensionBound(i, new Bound(value - 1 - i, value + 1 + i));
        }
        Rectangle queryRectangle = new Rectangle(boundVector);
        List<Label> labels = new ArrayList<>();
        for (int i = 0; i < 2; ++i) {
            labels.add(NonSpatialLabel.getRandomTestLabel());
        }
        LabelPath labelPath = new LabelPath(labels);
        System.out.println(rTree.search(queryRectangle, labelPath));
        System.out.println(rTree.search(queryRectangle).size());
        long endTime = System.currentTimeMillis();
        System.out.println("rtree search spend time：" + (endTime - startTime) + "ms");
    }

    private RTreeEntry<Integer> generateRandomEntry() {
        int dimension = 2;
        int value = RandomUtil.getIntegerRandomNumber(dimension, 100);
        BoundVector boundVector = new BoundVector(dimension);
        for (int i = 0; i < dimension; ++i) {
            boundVector.setDimensionBound(i, new Bound(value - 1 - i, value + 1 + i));
        }
        Rectangle rectangle = new Rectangle(boundVector);
        long code = RandomUtil.getIntegerRandomNumber(0, 1000);
        PathCode pathCode = new PathCode(CollUtil.newArrayList(code), pathProcessor);
        return new RTreeEntry<>(rectangle, pathCode, value);
    }
}
