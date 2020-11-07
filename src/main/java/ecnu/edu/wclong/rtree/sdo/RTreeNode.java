package ecnu.edu.wclong.rtree.sdo;


import cn.hutool.core.collection.CollUtil;
import ecnu.edu.wclong.rtree.util.PruneUtil;
import ecnu.edu.wclong.rtree.util.RectangleUtil;
import lombok.Getter;

import java.util.List;

public abstract class RTreeNode<T> {
    protected List<RTreeEntry<T>> entries;

    protected Rectangle rectangle;

    protected RTreeEntry<T> parent;

    @Getter
    protected PruneMeta pruneMeta;

    public void addEntry(RTreeEntry<T> newEntry) {
        this.entries.add(newEntry);
        //update rectangle when new Entry is added
        this.updateParentOnEntryChange();
    }

    public void updateParentOnEntryChange() {
        //update current node when new Entry is changed
        this.rectangle = RectangleUtil.getBoundedRectangleByEntries(entries);
        this.pruneMeta = PruneUtil.combineEntryPruneMeta(entries);
        //update parent when new Entry is changed
        if (null != this.parent) {
            this.parent.onChildrenChange();
        }
    }

    public boolean removeEntry(RTreeEntry<T> targetEntry) {
        boolean isDeleted = this.entries.remove(targetEntry);
        if (isDeleted) {
            this.updateParentOnEntryChange();
        }
        return isDeleted;
    }

    protected PruneMeta generatePruneMeta(List<RTreeEntry<T>> entries) {
        if (CollUtil.isNotEmpty(entries)) {
            return PruneUtil.combineEntryPruneMeta(entries);
        }
        return null;
    }

    public List<RTreeEntry<T>> getEntries() {
        return this.entries;
    }

    public Rectangle getRectangle() {
        return this.rectangle;
    }

    public RTreeEntry<T> getParent() {
        return this.parent;
    }

    public void setParent(RTreeEntry<T> parent) {
        this.parent = parent;
    }

    public abstract boolean isLeafNode();
}
