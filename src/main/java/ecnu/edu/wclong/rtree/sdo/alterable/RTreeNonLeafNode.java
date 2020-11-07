package ecnu.edu.wclong.rtree.sdo.alterable;


import ecnu.edu.wclong.rtree.sdo.RTreeEntry;
import ecnu.edu.wclong.rtree.sdo.RTreeNode;
import ecnu.edu.wclong.rtree.sdo.Rectangle;
import ecnu.edu.wclong.rtree.util.RectangleUtil;

import java.util.List;

public class RTreeNonLeafNode<T> extends RTreeNode<T> {

    public RTreeNonLeafNode(List<RTreeEntry<T>> entries, RTreeEntry<T> parent) {
        this.entries = entries;
        this.parent = parent;
        this.rectangle = generateRectangle(entries);
        this.pruneMeta = generatePruneMeta(entries);
    }

    public RTreeNonLeafNode(List<RTreeEntry<T>> entries) {
        this.entries = entries;
        this.parent = null;
        this.rectangle = generateRectangle(entries);
        this.pruneMeta = generatePruneMeta(entries);
    }

    private Rectangle generateRectangle(List<RTreeEntry<T>> entries) {
        return RectangleUtil.getBoundedRectangleByEntries(entries);
    }


    @Override
    public boolean isLeafNode() {
        return false;
    }
}
