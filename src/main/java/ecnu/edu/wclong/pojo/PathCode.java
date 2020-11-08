package ecnu.edu.wclong.pojo;

import ecnu.edu.wclong.processor.PathProcessor;
import ecnu.edu.wclong.rtree.sdo.PruneMeta;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class PathCode implements PruneMeta {

    @Getter
    private List<Long> pathCodes;

    private PathProcessor pathProcessor;

    @Override
    public PruneMeta combine(PruneMeta pruneMeta) {
        return new PathCode(combinePathCode(pruneMeta.getPathCode()), pathProcessor);
    }

    @Override
    public boolean isCanBePruned(LabelPath queryPath) {
        long queryCode = pathProcessor.encode(queryPath);
        for (Long pathCode : pathCodes) {
            if (pathProcessor.isSubPath(queryCode, pathCode)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isEntrySatisfied(LabelPath queryPath) {
        return pathCodes.get(0) == pathProcessor.encode(queryPath);
    }


    private List<Long> combinePathCode(PathCode pathCode) {
        List<Long> currentPathCodes = new ArrayList<>(this.pathCodes);
        List<Long> newPathCodes = pathCode.getPathCodes();

        for (long newCode : newPathCodes) {
            boolean isSubPath = false;

            for (int j = 0; j < currentPathCodes.size(); ++j) {
                long currentCode = currentPathCodes.get(j);
                if (pathProcessor.isSubPath(newCode, currentCode)) {
                    currentPathCodes.set(j, Math.max(newCode, currentCode));
                    isSubPath = true;
                    break;
                }
            }

            if (!isSubPath) {
                currentPathCodes.add(newCode);
            }
        }

        return currentPathCodes;
    }

    @Override
    public PathCode getPathCode() {
        return this;
    }

    @Override
    public LabelPathSet getLabelPathSet() {
        return null;
    }

}
