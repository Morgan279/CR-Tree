package ecnu.edu.wclong.rtree.algorithm.impl;


import ecnu.edu.wclong.pojo.LabelPath;
import ecnu.edu.wclong.rtree.algorithm.RTreeAlgorithm;
import ecnu.edu.wclong.rtree.sdo.RTreeEntry;
import ecnu.edu.wclong.rtree.sdo.RTreeNode;
import ecnu.edu.wclong.rtree.sdo.Rectangle;
import ecnu.edu.wclong.rtree.sdo.dto.RTreeNodeSplitResult;
import ecnu.edu.wclong.rtree.separator.RTreeNodeSeparator;
import ecnu.edu.wclong.rtree.util.RectangleUtil;

import java.util.List;
import java.util.Set;

public class RTreeAlgorithmImpl implements RTreeAlgorithm {

    @Override
    public <T> void search(Rectangle searchRectangle, RTreeNode<T> rootNode, Set<RTreeEntry<T>> resultSet) {
        //pruning directly if the node's rectangle does not overlap the search rectangle
        if (null == rootNode || !searchRectangle.isOverlap(rootNode.getRectangle())) return;

        for (RTreeEntry<T> entry : rootNode.getEntries()) {
            if (!searchRectangle.isOverlap(entry.getRectangle())) continue;

            if (rootNode.isLeafNode()) {
                resultSet.add(entry);
            } else {
                search(searchRectangle, entry.getChildren(), resultSet);
            }
        }
    }

    @Override
    public <T> void search(Rectangle searchRectangle, LabelPath queryPath, RTreeNode<T> rootNode, Set<RTreeEntry<T>> resultSet) {
        if (null == rootNode || !searchRectangle.isOverlap(rootNode.getRectangle()) || rootNode.getPruneMeta().isCanBePruned(queryPath)) {
            return;
        }

        for (RTreeEntry<T> entry : rootNode.getEntries()) {
            if (!searchRectangle.isOverlap(entry.getRectangle()) || entry.getPruneMeta().isCanBePruned(queryPath)) {
                continue;
            }

            if (rootNode.isLeafNode() && entry.getPruneMeta().isEntrySatisfied(queryPath)) {
                resultSet.add(entry);
            } else {
                search(searchRectangle, queryPath, entry.getChildren(), resultSet);
            }
        }
    }

    @Override
    public <T> RTreeNode<T> chooseLeaf(RTreeNode<T> rootNode, RTreeEntry<T> newEntry) {
        if (rootNode.isLeafNode()) return rootNode;

        RTreeNode<T> selectedNode = null;
        double selectNodeEnlargeArea = Double.MAX_VALUE;
        for (RTreeEntry<T> entry : rootNode.getEntries()) {
            Rectangle newRectangle = RectangleUtil.getBoundedRectangleByTwoRectangles(
                    entry.getRectangle(),
                    newEntry.getRectangle()
            );

            double entryArea = entry.getRectangle().getArea();
            double enlargeArea = newRectangle.getArea() - entryArea;

            if (enlargeArea < selectNodeEnlargeArea
                    || (enlargeArea == selectNodeEnlargeArea
                    && entryArea < selectedNode.getRectangle().getArea()
            )) {
                selectNodeEnlargeArea = enlargeArea;
                selectedNode = entry.getChildren();
            }
        }

        return chooseLeaf(selectedNode, newEntry);
    }


    @Override
    public <T> RTreeNode<T> findLeaf(RTreeNode<T> rootNode, RTreeEntry<T> targetEntry) {
        for (RTreeEntry<T> entry : rootNode.getEntries()) {
            if (entry.getRectangle().isOverlap(targetEntry.getRectangle())) {
                return findLeaf(entry.getChildren(), targetEntry);
            }

            if (rootNode.isLeafNode() && entry.equals(targetEntry)) {
                return rootNode;
            }
        }
        return null;
    }

    @Override
    public <T> RTreeNodeSplitResult<T> splitNode(RTreeNodeSeparator rTreeNodeSeparator, List<RTreeEntry<T>> entries) {
        return rTreeNodeSeparator.split(entries);
    }


}
