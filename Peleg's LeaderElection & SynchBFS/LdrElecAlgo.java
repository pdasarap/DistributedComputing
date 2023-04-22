import java.io.IOException;
import java.util.List;

public class LdrElecAlgo {
	Node dist_sys_node;

	public LdrElecAlgo(Node dist_sys_node) {
		this.dist_sys_node = dist_sys_node;
	}

	public void ElectionStart() {
		
		System.out.println("Initiating Leader Election on this node..........................");
		this.dist_sys_node.phase = 0;
		while (!this.dist_sys_node.isLead && !this.dist_sys_node.leadElec) {
			BrdcastToAllClnts();
		}
		if (this.dist_sys_node.leadElec) {
			System.out.println("leader elected , now leader is broadcasted");
			BrdcastToAllClntsIsLeadr();
		}
	}

	public void BrdcastToAllClnts() {
		Msg msg;
		if (this.dist_sys_node.phase == 0) {
			msg = new Msg(this.dist_sys_node.UID, 0, 0, this.dist_sys_node.UID, MsgType.MSG_SENT);
			this.dist_sys_node.previousStates.add(new PreviousState(0, this.dist_sys_node.UID));
			BrdcastToAllClnts(msg);
			this.dist_sys_node.phase = this.dist_sys_node.phase + 1;
		} else {
			if (this.dist_sys_node.msgQueue != null && this.dist_sys_node.msgQueue.size() > 0) {
				long msgCountforPhase = this.dist_sys_node.msgQueue.stream()
						.filter(t -> t.getMsgPhase() == this.dist_sys_node.phase - 1).count();
				if (msgCountforPhase == this.dist_sys_node.clntsOnMchnCnt && !this.dist_sys_node.leadElec) {

					int[] UID_Dist_Highest = this.dist_sys_node.getUID_Dist_Highest_tillnow(this.dist_sys_node.phase - 1);

					if (UID_Dist_Highest != null && UID_Dist_Highest.length > 0 && !this.dist_sys_node.isLead) {
						msg = new Msg(this.dist_sys_node.UID, this.dist_sys_node.phase, UID_Dist_Highest[1],
								UID_Dist_Highest[0], MsgType.MSG_SENT);

						BrdcastToAllClnts(msg);
						this.dist_sys_node.phase = this.dist_sys_node.phase + 1;
					}
				}
			}
		}
	}

	public void BrdcastToAllClnts(Msg msg) {
		dist_sys_node.cntClients.forEach((clientHandler) -> {
			try {

				if (this.dist_sys_node.phase == 0) {
					System.out.println(
							"Broadcast message sent to " + clientHandler.getClntUID() + " - { UID:" + this.dist_sys_node.UID
									+ ", Distance:" + this.dist_sys_node.getDistance() + ", Phase:" + this.dist_sys_node.phase
									+ " ,HighestUID:" + this.dist_sys_node.UID + " MessageType:" + MsgType.MSG_SENT + "}");
				} else {
					System.out.println("Broadcast message sent to " + clientHandler.getClntUID() + " - { UID:"
							+ this.dist_sys_node.UID + ", Distance: " + this.dist_sys_node.getDistance() + ", Phase:"
							+ this.dist_sys_node.phase + ", HighestUID: " + this.dist_sys_node.highestUID + ", MessageType:"
							+ MsgType.MSG_SENT + "}");
				}

				clientHandler.getOutputWriter().writeObject(msg);

			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}

	public void BrdcastToAllClntsIsLeadr() {
		Msg msg;
		msg = new Msg(dist_sys_node.UID, dist_sys_node.phase, dist_sys_node.getDistance(), dist_sys_node.getUID_Leader(),
				MsgType.LEADELEC);
		if (this.dist_sys_node.isLead) {
			System.out.println("I am the leader " + dist_sys_node.UID);
		} else {
			System.out.println("leader elected: " + dist_sys_node.getUID_Leader());
		}
		dist_sys_node.cntClients.forEach((clientHandler) -> {
			try {
				clientHandler.getOutputWriter().writeObject(msg);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}
}
