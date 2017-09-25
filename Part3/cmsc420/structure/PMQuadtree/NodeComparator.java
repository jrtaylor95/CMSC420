package cmsc420.structure.PMQuadtree;

import java.util.Comparator;

public class NodeComparator implements Comparator<PMQuadtreeNode> {

	@Override
	public int compare(PMQuadtreeNode o1, PMQuadtreeNode o2) {
		if (o1.getType() == PMQuadtreeNode.BLACK && o2.getType() == PMQuadtreeNode.BLACK) {
			return 0;
		} else if (o1.getType() == PMQuadtreeNode.BLACK && o2.getType() == PMQuadtreeNode.GRAY) {
			return 1;
		} else {
			return 1;
		}
	}

}
