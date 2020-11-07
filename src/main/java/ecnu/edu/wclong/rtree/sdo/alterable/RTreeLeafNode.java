package ecnu.edu.wclong.rtree.sdo.alterable;


import ecnu.edu.wclong.rtree.sdo.RTreeEntry;
import ecnu.edu.wclong.rtree.sdo.RTreeNode;
import ecnu.edu.wclong.rtree.sdo.Rectangle;

import java.util.List;

public class RTreeLeafNode<T> extends RTreeNode<T> {

    public RTreeLeafNode(List<RTreeEntry<T>> entries, Rectangle rectangle, RTreeEntry<T> parent) {
        this.entries = entries;
        this.rectangle = rectangle;
        this.parent = parent;
        this.pruneMeta = generatePruneMeta(entries);
    }

    public RTreeLeafNode(List<RTreeEntry<T>> entries, Rectangle rectangle) {
        this.entries = entries;
        this.rectangle = rectangle;
        this.pruneMeta = generatePruneMeta(entries);
    }


    @Override
    public boolean isLeafNode() {
        return true;
    }

}
