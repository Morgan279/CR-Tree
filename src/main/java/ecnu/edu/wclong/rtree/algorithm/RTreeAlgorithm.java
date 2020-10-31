package ecnu.edu.wclong.rtree.algorithm;


import ecnu.edu.wclong.rtree.sdo.RTreeEntry;
import ecnu.edu.wclong.rtree.sdo.RTreeNode;
import ecnu.edu.wclong.rtree.sdo.Rectangle;
import ecnu.edu.wclong.rtree.sdo.dto.RTreeNodeSplitResult;
import ecnu.edu.wclong.rtree.separator.RTreeNodeSeparator;

import java.util.List;
import java.util.Set;

public interface RTreeAlgorithm {
    <T> void search(Rectangle searchRectangle, RTreeNode<T> rootNode, Set<RTreeEntry<T>> resultSet);

    <T> RTreeNode<T> chooseLeaf(RTreeNode<T> rootNode, RTreeEntry<T> newEntry);

    <T> RTreeNode<T> findLeaf(RTreeNode<T> rootNode, RTreeEntry<T> targetEntry);

    <T> RTreeNodeSplitResult<T> splitNode(RTreeNodeSeparator rTreeNodeSeparator, List<RTreeEntry<T>> entries);

}
