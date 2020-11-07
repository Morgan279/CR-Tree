package ecnu.edu.wclong.rtree.sdo;

import ecnu.edu.wclong.pojo.LabelPath;
import ecnu.edu.wclong.pojo.LabelPathSet;
import ecnu.edu.wclong.pojo.PathCode;

public interface PruneMeta {
    PruneMeta combine(PruneMeta pruneMeta);

    boolean isCanBePruned(LabelPath queryPath);

    PathCode getPathCode();

    LabelPathSet getLabelPathSet();
}
