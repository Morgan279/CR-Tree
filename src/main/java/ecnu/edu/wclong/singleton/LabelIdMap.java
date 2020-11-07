package ecnu.edu.wclong.singleton;

import org.neo4j.graphdb.Label;

import java.util.HashMap;
import java.util.Map;

public class LabelIdMap {

    private static Map<Label, Integer> labelIdMap = new HashMap<>();

    private LabelIdMap() {
    }

    public static void put(Label label, Integer id) {
        labelIdMap.put(label, id);
    }

    public static Integer get(Label label) {
        return labelIdMap.get(label);
    }
}
