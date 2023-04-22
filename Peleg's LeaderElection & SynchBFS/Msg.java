import java.io.Serializable;

public class Msg implements Serializable, Comparable<Msg> {
	private static final long serialVersionUID = 1L;
	int sndrUID;
	int maxUIDRec;
	int phase;
	MsgType msgtype;
	int disKn;
	int nDegree;
	int highestDegreeUID;

	public Msg(int sndrUID, int phase, int disKn, int maxUIDRec, MsgType msgtype) {
		this.phase = phase;
		this.maxUIDRec = maxUIDRec;
		this.msgtype = msgtype;
		this.disKn = disKn;
		this.phase = phase;
		this.sndrUID = sndrUID;
	}

	public Msg(Msg mesg) {
		this(mesg.sndrUID, mesg.phase, mesg.disKn, mesg.maxUIDRec, mesg.msgtype);
	}
    
	public Msg(int sndrUID, MsgType bfsMsgType, int degree, int highestDegreeUID) {
		this.nDegree = degree;
		this.msgtype = bfsMsgType;
		this.highestDegreeUID = highestDegreeUID;
		this.sndrUID = sndrUID;
	}


	public Msg(int sndrUID, MsgType bfsMsgType) {
		this.sndrUID = sndrUID;
		this.msgtype = bfsMsgType;
	}

	
	public int getSenderUID() {
		return this.sndrUID;
	}

	

	public int getdisKn() {
		return this.disKn;
	}

	public MsgType getMsgType() {
		return this.msgtype;
	}

	public int getmaxUIDRec() {
		return this.maxUIDRec;
	}

	public int getMsgPhase() {
		return this.phase;
	}

	@Override
	public int compareTo(Msg msg) {
		if (this.phase > msg.phase) {
			return 1;
		}
		if (this.phase < msg.phase) {
			return -1;
		}

		
		return 0;
	}
}
