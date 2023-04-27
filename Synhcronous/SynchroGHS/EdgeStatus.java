/*Basic is the initial edge, Branch is if it is included in Spanning tree, rejected if its not part of spanning tree*/
enum EdgeStatus {
	BASIC, BRANCH, REJECTED
}