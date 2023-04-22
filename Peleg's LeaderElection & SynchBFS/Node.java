import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.stream.Stream;
import java.net.*;

public class Node {
	int UID, port, highestUID, leaderUID, disKn, phase;
	HashMap<Integer, NbrNode> UIDNgbrs;
	List<ClntReqHandler> cntClients = Collections.synchronizedList(new ArrayList<ClntReqHandler>());
	String HName;
	ServerSocket srvrSocket;
	List<TCP_Clnt> clntsOnMchn = Collections.synchronizedList(new ArrayList<TCP_Clnt>());
	int clntsOnMchnCnt;
	BlockingQueue<Msg> msgQueue;
	Boolean isLead;
	Deque<PreviousState> previousStates;
	Boolean leadElec;
	int prnt_UID;
	public boolean isDone;
	int nDegree = 0;
	public List<Integer> child_list = new ArrayList<>();

	public Node(int UID, int port, String HName, HashMap<Integer, NbrNode> UIDNgbrs, int phase) {
		this.UID = UID;
		this.HName = HName;
		this.isLead = false;
		this.UIDNgbrs = UIDNgbrs;
		this.port = port;
		this.leadElec = false;
		this.msgQueue = new PriorityBlockingQueue<Msg>();
		this.disKn = 0;// add
		this.highestUID = UID;// add
		this.clntsOnMchnCnt = 0;
		this.phase = phase;
		this.previousStates = new ArrayDeque<>();
	}

	public Node() {
		//
	}

	public void setNode_Lead() {
		this.isLead = true;
		this.leadElec = true;
	}	
	public int getUID_Leader() {
		return this.leaderUID = this.highestUID;

	}

	public int getDistance() {
		return this.disKn;
	}


	public int[] getUID_Dist_Highest_tillnow(int phase) {
		int[] ans = new int[2];
		if (this.msgQueue != null && this.msgQueue.size() > 0) {
			if (!this.msgQueue.stream()
					.anyMatch(t -> t.getMsgType() == MsgType.LEADELEC && t.getMsgPhase() == phase)) {
				Msg msg = getUID_Highest_received(phase);
				TerminationCheckandAdd(msg);
				if (msg.maxUIDRec > this.highestUID) {
					this.disKn = msg.disKn + 1;
					this.highestUID = msg.maxUIDRec;
				} else if (msg.maxUIDRec == this.highestUID) {
					this.disKn = msg.disKn;
					this.highestUID = msg.maxUIDRec;
				}
				} else {
					Optional<Msg> mesg = this.msgQueue.stream()
						.filter(t -> t.getMsgType() == MsgType.LEADELEC && t.getMsgPhase() == phase)
						.findFirst();
					this.highestUID = mesg.get().getmaxUIDRec();
					this.disKn = mesg.get().getdisKn();
					this.leadElec = true;
					if (this.UID == this.highestUID) {
						this.isLead = true;
					}			
				}
			ans[1] = this.disKn;
			ans[0] = this.highestUID;
		}
		return ans;
	}

	public Msg getUID_Highest_received(int phase) {
		Msg mesg = this.msgQueue.stream().filter(t -> t.getMsgPhase() == phase)
				.max(Comparator.comparing(msg -> msg.maxUIDRec)).get();
		return mesg;
	}

	public Boolean TerminationCheckandAdd(Msg msg) {
		Boolean istrmRchd = false;
		if (previousStates.size() == 2) {
			istrmRchd = this.previousStates.stream()
					.allMatch(state -> state.distance == this.disKn && state.UID == this.highestUID);
			if (istrmRchd) {
				this.leadElec = true;
				if (this.UID == this.highestUID) {
					this.isLead = true;
				}
			} else {
				this.previousStates.removeLast();
			}
			return istrmRchd;
		} else if (previousStates.size() < 2) {
			this.previousStates.addFirst(new PreviousState(msg.getdisKn(), msg.maxUIDRec));
		}
		return istrmRchd;
	}

	

	public void addMsgToQue(Msg msg) {
		this.msgQueue.add(msg);
	}

	public void attchSrvrSockt(ServerSocket srvrSocket) {
		this.srvrSocket = srvrSocket;
	}

	public Msg getHeadMsgFrmQue() {
		if (this.msgQueue.peek() != null) {
			Msg msg = this.msgQueue.peek();
			this.msgQueue.remove();
			return msg;
		}
		return null;
	}

	public int getNodeUID() {
		return this.UID;
	}

	

	public String getNodeHName() {
		return this.HName;
	}

	public int getNodePort() {
		return this.port;
	}

	public HashMap<Integer, NbrNode> getNeighbors() {
		return this.UIDNgbrs;
	}

	
	public List<ClntReqHandler> getAllConnectedClients() {
		return this.cntClients;
	}
	public void addClient(ClntReqHandler client) {
		synchronized (cntClients) {
			cntClients.add(client);
		}
	}

}

class PreviousState {
	int UID;
	long distance;
	

	PreviousState(long distance, int UID) {
		this.distance = distance;
		this.UID = UID;
	}
}
