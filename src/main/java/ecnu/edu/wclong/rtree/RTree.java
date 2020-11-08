package ecnu.edu.wclong.rtree;


import cn.hutool.core.collection.CollUtil;
import ecnu.edu.wclong.pojo.LabelPath;
import ecnu.edu.wclong.rtree.algorithm.RTreeAlgorithm;
import ecnu.edu.wclong.rtree.algorithm.impl.RTreeAlgorithmImpl;
import ecnu.edu.wclong.rtree.sdo.RTreeEntry;
import ecnu.edu.wclong.rtree.sdo.RTreeNode;
import ecnu.edu.wclong.rtree.sdo.Rectangle;
import ecnu.edu.wclong.rtree.sdo.alterable.RTreeLeafNode;
import ecnu.edu.wclong.rtree.sdo.alterable.RTreeNonLeafNode;
import ecnu.edu.wclong.rtree.sdo.dto.RTreeNodeSplitResult;
import ecnu.edu.wclong.rtree.separator.RTreeNodeSeparator;
import ecnu.edu.wclong.rtree.separator.impl.RTreeNodeQuadraticSeparator;
import ecnu.edu.wclong.rtree.util.RTreeNodeSplitUtil;
import ecnu.edu.wclong.rtree.util.RectangleUtil;

import java.util.HashSet;
import java.util.Set;

public class RTree<T> {

    private RTreeNode<T> rootNode;

    private RTreeAlgorithm rTreeAlgorithm;

    private RTreeNodeSeparator separator;

    //M
    private int loadFactor;

    public RTree() {
        this.loadFactor = 50;
        this.separator = new RTreeNodeQuadraticSeparator();
        this.rTreeAlgorithm = new RTreeAlgorithmImpl();
    }

    public RTree(int loadFactor) {
        checkLoadFactor(loadFactor);

        this.loadFactor = loadFactor;
        this.separator = new RTreeNodeQuadraticSeparator();
        this.rTreeAlgorithm = new RTreeAlgorithmImpl();
    }

    public RTree(int loadFactor, RTreeNodeSeparator separator) {
        checkLoadFactor(loadFactor);

        this.loadFactor = loadFactor;
        this.separator = separator;
        this.rTreeAlgorithm = new RTreeAlgorithmImpl();
    }

    public RTree(int loadFactor, RTreeNodeSeparator separator, RTreeAlgorithm rTreeAlgorithm) {
        checkLoadFactor(loadFactor);

        this.loadFactor = loadFactor;
        this.separator = separator;
        this.rTreeAlgorithm = rTreeAlgorithm;
    }

    public Set<RTreeEntry<T>> search(Rectangle searchRectangle) {
        Set<RTreeEntry<T>> resultSet = new HashSet<>();
        rTreeAlgorithm.search(searchRectangle, rootNode, resultSet);
        return resultSet;
    }

    public Set<RTreeEntry<T>> search(Rectangle searchRectangle, LabelPath queryPath) {
        Set<RTreeEntry<T>> resultSet = new HashSet<>();
        rTreeAlgorithm.search(searchRectangle, queryPath, rootNode, resultSet);
        return resultSet;
    }

    public void insert(RTreeEntry<T> newEntry) {
        if (null == rootNode) {
            this.initRootNode(newEntry);
        }

        long startTime = System.currentTimeMillis();
        RTreeNode<T> targetNode = rTreeAlgorithm.chooseLeaf(rootNode, newEntry);
        newEntry.setLocatedNode(targetNode);
        targetNode.addEntry(newEntry);
        splitNodeIfNeed(targetNode);

        long endTime = System.currentTimeMillis();
        //System.out.println("split spend time：" + (endTime - startTime) + "ms");
    }


    /**
     * @param targetEntry target entry that need be deleted
     * @return true if delete successfully; false, if target entry is not found
     */
    public boolean delete(RTreeEntry<T> targetEntry) {
        RTreeNode<T> targetNode = rTreeAlgorithm.findLeaf(rootNode, targetEntry);
        if (null == targetNode) return false;

        boolean isDeleted = targetNode.removeEntry(targetEntry);
        if (isDeleted) {
            condenseTree(targetNode, new HashSet<>());
            shortenTreeIfNeed();
        }

        return isDeleted;
    }

    public boolean delete(String entryId) {
        RTreeEntry<T> targetEntry = new RTreeEntry<>(entryId);
        return delete(targetEntry);
    }

    /**
     * @param entryId target entry id that need be updated
     * @return true if update value successfully; false, if target entry is not found
     */
    public boolean update(String entryId, T newValue) {
        RTreeEntry<T> targetEntry = new RTreeEntry<>(entryId);
        RTreeNode<T> targetNode = rTreeAlgorithm.findLeaf(rootNode, targetEntry);
        if (null == targetNode) return false;

        for (RTreeEntry<T> entry : targetNode.getEntries()) {
            if (entry.getId().equals(entryId)) {
                entry.setValue(newValue);
                return true;
            }
        }

        return false;
    }

    public RTreeNode<T> getRootNode() {
        return rootNode;
    }

    private void initRootNode(RTreeEntry<T> newEntry) {
        this.rootNode = new RTreeLeafNode<>(CollUtil.newArrayList(newEntry), newEntry.getRectangle());
    }

    private boolean isNeedSplit(int nodeSize) {
        return nodeSize == loadFactor + 1;
    }

    private void splitNodeIfNeed(RTreeNode<T> targetNode) {
        if (isNeedSplit(targetNode.getEntries().size())) {
            splitNode(targetNode);
        }
    }

    private void splitNode(RTreeNode<T> targetNode) {
        RTreeNodeSplitResult<T> rTreeNodeSplitResult = rTreeAlgorithm.splitNode(
                separator,
                targetNode.getEntries()
        );

        if (targetNode.isLeafNode()) {
            splitLeafNode(targetNode, rTreeNodeSplitResult);
        } else {
            splitNonLeafNode(targetNode, rTreeNodeSplitResult);
        }
    }

    private void splitLeafNode(RTreeNode<T> targetNode, RTreeNodeSplitResult<T> rTreeNodeSplitResult) {
        createNewNodeIfSplitNodeIsRootNode(targetNode);

        RTreeEntry<T> targetNodeParent = targetNode.getParent();
        targetNode = new RTreeLeafNode<>(
                rTreeNodeSplitResult.getSplitList1(),
                RectangleUtil.getBoundedRectangleByEntries(
                        rTreeNodeSplitResult.getSplitList1()
                ),
                targetNodeParent
        );
        targetNodeParent.setChildren(targetNode);
        RTreeNodeSplitUtil.setSplitEntriesLocatedNode(targetNode.getEntries(), targetNode);

        RTreeNode<T> newNode = new RTreeLeafNode<>(
                rTreeNodeSplitResult.getSplitList2(),
                RectangleUtil.getBoundedRectangleByEntries(
                        rTreeNodeSplitResult.getSplitList2()
                )
        );
        RTreeNodeSplitUtil.setSplitEntriesLocatedNode(newNode.getEntries(), newNode);

        updateNewNodeParentNodeAndAscend(newNode, targetNodeParent);
    }

    private void splitNonLeafNode(RTreeNode<T> targetNode, RTreeNodeSplitResult<T> rTreeNodeSplitResult) {
        createNewNodeIfSplitNodeIsRootNode(targetNode);

        RTreeEntry<T> targetNodeParent = targetNode.getParent();
        targetNode = new RTreeNonLeafNode<>(
                rTreeNodeSplitResult.getSplitList1(),
                targetNodeParent
        );
        targetNodeParent.setChildren(targetNode);
        RTreeNodeSplitUtil.setSplitEntriesLocatedNode(targetNode.getEntries(), targetNode);

        RTreeNode<T> newNode = new RTreeNonLeafNode<>(
                rTreeNodeSplitResult.getSplitList2()
        );
        RTreeNodeSplitUtil.setSplitEntriesLocatedNode(newNode.getEntries(), newNode);

        updateNewNodeParentNodeAndAscend(newNode, targetNodeParent);
    }

    private void createNewNodeIfSplitNodeIsRootNode(RTreeNode<T> targetNode) {
        if (targetNode == rootNode) {
            RTreeEntry<T> newRootNodeEntry = new RTreeEntry<>(targetNode);
            RTreeNode<T> newRootNode = new RTreeNonLeafNode<>(CollUtil.newArrayList(newRootNodeEntry));
            newRootNodeEntry.setLocatedNode(newRootNode);
            targetNode.setParent(newRootNodeEntry);
            rootNode = newRootNode;
        }
    }

    private void updateNewNodeParentNodeAndAscend(RTreeNode<T> newNode, RTreeEntry<T> targetNodeParent) {
        RTreeEntry<T> newNodeParent = new RTreeEntry<>(newNode);
        newNodeParent.setLocatedNode(targetNodeParent.getLocatedNode());
        newNode.setParent(newNodeParent);
        newNodeParent.getLocatedNode().addEntry(newNodeParent);
        splitNodeIfNeed(newNodeParent.getLocatedNode());
    }

    private void condenseTree(RTreeNode<T> node, Set<RTreeNode<T>> eliminatedNodeSet) {
        if (node == rootNode) {
            eliminatedNodeSet.forEach(eliminatedNode -> {
                for (RTreeEntry<T> entry : eliminatedNode.getEntries()) {
                    this.insert(entry);
                }
            });
            return;
        }

        if (node.getEntries().size() < loadFactor / 2) {
            node.getParent().setChildren(null);
            if (!node.isLeafNode()) {
                node.getParent().getLocatedNode().removeEntry(node.getParent());
            }
            eliminatedNodeSet.add(node);
        }

        condenseTree(node.getParent().getLocatedNode(), eliminatedNodeSet);
    }


    private void shortenTreeIfNeed() {
        if (rootNode.getEntries().size() == 1) {
            //shorten tree
            RTreeNode<T> rootChildNode = rootNode.getEntries().get(0).getLocatedNode();
            rootChildNode.setParent(null);
            rootNode = rootChildNode;
        }
    }

    private void checkLoadFactor(int loadFactor) {
        if (loadFactor < 1) {
            throw new IllegalArgumentException("load factor must greater than 0");
        }
    }

}
