package ecnu.edu.wclong.pojo;

import ecnu.edu.wclong.rtree.sdo.PruneMeta;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
public class LabelPathSet implements PruneMeta {

    @Getter
    Set<LabelPath> meta;


    @Override
    public PruneMeta combine(PruneMeta pruneMeta) {
        Set<LabelPath> combinedLabelSet = new HashSet<>(meta);
        combinedLabelSet.addAll(pruneMeta.getLabelPathSet().getMeta());
        return new LabelPathSet(combinedLabelSet);
    }

    @Override
    public boolean isCanBePruned(LabelPath queryPath) {
        return !meta.contains(queryPath);
    }

    @Override
    public PathCode getPathCode() {
        return null;
    }

    @Override
    public LabelPathSet getLabelPathSet() {
        return this;
    }
}
