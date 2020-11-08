package ecnu.edu.wclong.rtree;

import cn.hutool.core.collection.CollUtil;
import ecnu.edu.wclong.constant.NonSpatialLabel;
import ecnu.edu.wclong.pojo.LabelPath;
import ecnu.edu.wclong.pojo.LabelPathSet;
import ecnu.edu.wclong.pojo.PathCode;
import ecnu.edu.wclong.processor.PathProcessor;
import ecnu.edu.wclong.rtree.sdo.Bound;
import ecnu.edu.wclong.rtree.sdo.BoundVector;
import ecnu.edu.wclong.rtree.sdo.RTreeEntry;
import ecnu.edu.wclong.rtree.sdo.Rectangle;
import ecnu.edu.wclong.rtree.separator.impl.RTreeNodeLinearSeparator;
import ecnu.edu.wclong.util.RandomUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.neo4j.graphdb.Label;

import java.util.ArrayList;
import java.util.List;

class RTreeTest {

    private RTree<Integer> pathCodeRTree;

    private RTree<Integer> labelPathRTree;

    private PathProcessor pathProcessor;

    @BeforeEach
    public void init() {
        this.labelPathRTree = new RTree<>(100, new RTreeNodeLinearSeparator());
        this.pathCodeRTree = new RTree<>(100, new RTreeNodeLinearSeparator());
        this.pathProcessor = new PathProcessor(NonSpatialLabel.getLength(), 5);
    }

    @Test
    public void insert() {
        for (int i = 0; i < 5000; ++i) {
            List<RTreeEntry<Integer>> entries = generateRandomEntry();
            labelPathRTree.insert(entries.get(0));
            pathCodeRTree.insert(entries.get(1));
        }
    }

    @Test
    void search() {
        insert();
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

        System.out.println(labelPathRTree.search(queryRectangle, labelPath));
        System.out.println(pathCodeRTree.search(queryRectangle, labelPath));

        long endTime = System.currentTimeMillis();
        System.out.println("rtree search spend time：" + (endTime - startTime) + "ms");
    }


    private List<RTreeEntry<Integer>> generateRandomEntry() {
        int dimension = 2;
        int value = RandomUtil.getIntegerRandomNumber(dimension, 100);
        BoundVector boundVector = new BoundVector(dimension);
        for (int i = 0; i < dimension; ++i) {
            boundVector.setDimensionBound(i, new Bound(value - 1 - i, value + 1 + i));
        }
        Rectangle rectangle = new Rectangle(boundVector);
        LabelPath labelPath = generateRandomLabelPath(RandomUtil.getIntegerRandomNumber(2, 5));
        return CollUtil.newArrayList(
                new RTreeEntry<>(rectangle, new LabelPathSet(CollUtil.newHashSet(labelPath)), value),
                new RTreeEntry<>(rectangle, new PathCode(CollUtil.newArrayList(pathProcessor.encode(labelPath)), pathProcessor), value)
        );
    }

    private LabelPath generateRandomLabelPath(int hop) {
        List<Label> labels = new ArrayList<>();
        for (int i = 0; i < hop; ++i) {
            labels.add(NonSpatialLabel.getRandomTestLabel());
        }
        return new LabelPath(labels);
    }

    private RTreeEntry<Integer> generateRandomPathCodeEntry() {
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
