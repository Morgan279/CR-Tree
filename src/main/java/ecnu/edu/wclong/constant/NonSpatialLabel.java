package ecnu.edu.wclong.constant;

import cn.hutool.core.util.RandomUtil;
import org.neo4j.graphdb.Label;

import java.util.stream.Stream;

public enum NonSpatialLabel implements Label {
    NON_SPATIAL_1(1),
    NON_SPATIAL_2(2),
    NON_SPATIAL_3(3),
    NON_SPATIAL_4(4),
    NON_SPATIAL_5(5);
//    NON_SPATIAL_6(6),
//    NON_SPATIAL_7(7),
//    NON_SPATIAL_8(8),
//    NON_SPATIAL_9(9),
//    NON_SPATIAL_10(10),
//    NON_SPATIAL_11(11),
//    NON_SPATIAL_12(12);

    private int value;

    NonSpatialLabel(int value) {
        this.value = value;
    }

    public static int getLength() {
        return NonSpatialLabel.values().length;
    }

    public static NonSpatialLabel getRandomTestLabel() {
        return NonSpatialLabel.toType(RandomUtil.randomInt(1, getLength()));
    }

    public static NonSpatialLabel toType(int value) {
        return Stream.of(NonSpatialLabel.values())
                .filter(v -> v.value == value)
                .findAny()
                .orElse(null);
    }
}
