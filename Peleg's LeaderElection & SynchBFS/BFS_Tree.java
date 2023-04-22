import java.io.IOException;

public class BFS_Tree {
	int totalAcksReceived = 0;
	Msg msg;
	Node node;
	int newDegree, highestDegreeUID = 0;

	public BFS_Tree(Node mynode) {
		this.node = mynode;
	}

	public void startBfs() {
		if (this.node.isLead) {
			this.node.isDone = true;
			this.node.msgQueue.clear();
			SearchSend(MsgType.SRCH);
			System.out.println("BFS Started................................");
		}

		while (true) {
			msg = node.getHeadMsgFrmQue();
			if (msg != null && msg.getMsgType() == MsgType.SRCH && !this.node.isDone) {
				this.node.prnt_UID = msg.sndrUID;
				this.node.isDone = true;
				SearchSend(MsgType.SRCH);
			} else if (msg != null && msg.getMsgType() == MsgType.NACK && this.node.isDone) {
				totalAcksReceived = totalAcksReceived + 1;
				if (totalAcksReceived == this.node.UIDNgbrs.size()) {
					PosAckSend();
					break;
				}
			} else if (msg != null && msg.getMsgType() == MsgType.PACK && this.node.isDone) {
				totalAcksReceived = totalAcksReceived + 1;
				node.child_list.add(msg.sndrUID);
				if (totalAcksReceived == this.node.UIDNgbrs.size()) {
					PosAckSend();
					break;
				}
			} else if (msg != null && msg.getMsgType() == MsgType.SRCH && this.node.isDone) {
				NegAckSend();
			} 
		}
		if (node.isLead) {
			this.node.nDegree = node.child_list.size();
			System.out.println("Node: " + node.UID + " is elected as the leader and is the bfs root node");
			System.out.println("Degree of root node :" + this.node.nDegree);
		} else {
			this.node.nDegree = node.child_list.size() + 1;
			System.out.println("Node: " + node.UID + " , its parent: " + this.node.prnt_UID);
			System.out.println("Degree of node :" + this.node.nDegree);
		}
		for (ClntReqHandler y : this.node.cntClients) {
			if (node.child_list != null && !node.child_list.isEmpty() && node.child_list.contains(y.getClntUID())) {
				System.out.println(" Node: " + node.UID + " , its Child: " + y.getClntUID());
			} else {
				y.isChild = false;
			}
		}

		
	}

	public void NegAckSend() {
		for (ClntReqHandler x : node.cntClients) {
			try {
				if (x.getClntUID() == msg.sndrUID) {
					System.out.println("BFS negative ACK message sent to " + x.getClntUID() + " - { UID:"
							+ this.node.UID + " ,MessageType:" + MsgType.NACK + "}");
					x.out.writeObject(new Msg(this.node.UID, MsgType.NACK));
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void PosAckSend() {
		for (ClntReqHandler x : node.cntClients) {
			if (x.getClntUID() == this.node.prnt_UID)
				try {
					System.out.println("BFS positive ACK message sent to " + x.getClntUID() + " - { UID:"
							+ this.node.UID + " ,MessageType:" + MsgType.PACK + "}");
					x.out.writeObject(new Msg(this.node.UID, MsgType.PACK));
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}

	public void PosAckDegSend() {
		for (ClntReqHandler x : node.cntClients) {
			if (x.getClntUID() == this.node.prnt_UID)
				try {
					System.out.println("Degree broadcast message sent to " + x.getClntUID() + " - { UID:"
							+ this.node.UID + " ,HighestDegree:" + this.newDegree + " ,HighestDegreeUID: "
							+ this.highestDegreeUID + " ,MessageType:" + MsgType.PDEGREE + "}");
					x.out.writeObject(
							new Msg(this.node.UID, MsgType.PDEGREE, this.newDegree, this.highestDegreeUID));
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}

	public void SearchSend(MsgType mesg) {
								try {
									Thread.sleep(500);
								} catch (InterruptedException e) {
									e.printStackTrace();
								} 				
			for (ClntReqHandler x : node.cntClients) {
			try {
				if (mesg == MsgType.SRCH) {
					x.out.writeObject(new Msg(this.node.UID, MsgType.SRCH));
				} else if (mesg == MsgType.DEGREE) {
					if (x.isChild) {
						System.out.println("Degree broadcast message sent to " + x.getClntUID() + " - { UID:"
								+ this.node.UID + " ,HighestDegree:" + this.newDegree + " ,HighestDegreeUID: "
								+ this.highestDegreeUID + " ,MessageType:" + MsgType.DEGREE + "}");
						x.out.writeObject(
								new Msg(this.node.UID, MsgType.DEGREE, this.newDegree, this.highestDegreeUID));
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void initiateDegQury() {
		this.totalAcksReceived = 0;
		if (this.node.isLead) {
			this.node.msgQueue.clear();
			this.newDegree = node.child_list.size();
			this.highestDegreeUID = this.node.UID;
			System.out.println("Broadcast initiated to find max degree...............................");
			SearchSend(MsgType.DEGREE);
		}

		while (true) {
			int newDegree = 0;
			msg = this.node.getHeadMsgFrmQue();
			if (msg != null && msg.getMsgType() == MsgType.DEGREE) {
				if (!this.node.child_list.isEmpty() && msg.nDegree < this.node.child_list.size() + 1) {
					this.highestDegreeUID = node.UID;
					this.newDegree = node.child_list.size() + 1;
					SearchSend(MsgType.DEGREE);
				} else if (!node.child_list.isEmpty() && msg.nDegree >= node.child_list.size() + 1) {
					this.highestDegreeUID = msg.highestDegreeUID;
					this.newDegree = msg.nDegree;
					SearchSend(MsgType.DEGREE);
				} else if (msg != null && node.child_list.isEmpty()) {
					this.newDegree = msg.nDegree;
					this.highestDegreeUID = msg.highestDegreeUID;
					System.out.println("Convergecast started to find max degree.....");
					PosAckDegSend();
					break;
				}
			} else if (msg != null && msg.getMsgType() == MsgType.PDEGREE && this.node.isLead) {
				totalAcksReceived = totalAcksReceived + 1;
				System.out.println("TotalAck :" + totalAcksReceived + " MessageUID : " + msg.sndrUID);
				if (msg.nDegree > this.newDegree) {
					this.newDegree = msg.nDegree;
					this.highestDegreeUID = msg.highestDegreeUID;
				}

				if (totalAcksReceived == this.node.child_list.size()) {
					System.out.println("Maximum degree of any node in the BFS tree is " + this.newDegree + " for node "
							+ this.highestDegreeUID);
					break;
				}
			} else if (msg != null && msg.getMsgType() == MsgType.PDEGREE && !this.node.isLead) {
				totalAcksReceived = totalAcksReceived + 1;
				if (msg.nDegree > this.newDegree) {
					this.newDegree = msg.nDegree;
					this.highestDegreeUID = msg.highestDegreeUID;
				}
				if (totalAcksReceived == this.node.child_list.size()) {
					System.out.println("Convergecast initiated to find max degree.....");
					PosAckDegSend();
					break;
				}
			}
		}
	}
}

