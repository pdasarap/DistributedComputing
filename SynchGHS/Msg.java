import java.io.Serializable;
public class Msg implements Serializable, Comparable<Msg> {
	MsgType type;
	int sndrUnqID;
	int ldrUnqID;
	boolean done;
	MinWghtOGE mwogEdge;
	int level;
	Msg(int UnqID, int ldrUnqID, MsgType type, MinWghtOGE min, int level)
	{
		this.sndrUnqID=UnqID;
		this.ldrUnqID= ldrUnqID;
		this.type=type;
		this.mwogEdge=min;
		this.done=false;
		this.level=level;
	}

	Msg(int UnqID, int ldrUnqID, MsgType type, int level)
	{
		this.sndrUnqID=UnqID;
		this.ldrUnqID= ldrUnqID;
		this.type=type;
		this.mwogEdge=new MinWghtOGE();
		this.done=false;
		this.level=level;
	}
	

	public int compareTo(Msg msg) {
		
		if (this.level > msg.level) {
			return 1;
		}

		if (this.level < msg.level) {
			return -1;
		}
		return 0;
	}
	
}
