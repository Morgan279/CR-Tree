package ecnu.edu.wclong.rtree.util;

import ecnu.edu.wclong.rtree.sdo.PruneMeta;
import ecnu.edu.wclong.rtree.sdo.RTreeEntry;

import java.util.List;

public class PruneUtil {
    public static <T> PruneMeta combineEntryPruneMeta(List<RTreeEntry<T>> entries) {

        return entries.stream()
                .filter(item -> null != item.getPruneMeta())
                .map(RTreeEntry::getPruneMeta)
                .reduce(PruneMeta::combine)
                .orElseThrow(() -> new RuntimeException("combine failed"));
    }
}
