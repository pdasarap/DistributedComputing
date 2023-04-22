import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

public class TreeNode {
	int UnqID=-1;
	int ldrUnqID;
	String hstName="";
	int level;
	int prtNumber=-1;
	int ttlNoOfNodes=0;
	MinWghtOGE mwogEdge;
	int predsrUnqID;
	HashMap<Integer, NgbrNode> nbs = new HashMap<Integer, NgbrNode>();		
	BlockingQueue<Msg> msgList = new PriorityBlockingQueue<Msg>();
	
	HashMap<Integer,ClntRqstHndlr> connClnts = new HashMap<Integer,ClntRqstHndlr>();
	
	HashMap<Integer,ClntRqstHndlr> inTrClnts=new HashMap<Integer,ClntRqstHndlr>();
	HashMap<Integer,ClntRqstHndlr> nonTrClnts=new HashMap<Integer,ClntRqstHndlr>();
	

	public void addClient(int clntUnqID,ClntRqstHndlr reqHandler)
	{
		this.connClnts.put(clntUnqID,reqHandler);
		System.out.println("Client added: "+clntUnqID);
	}
	
	public void addMsg(Msg msg)
	{
		this.msgList.add(msg);
	}
	public Msg getHeadMsgFrmQueue() {
		if (this.msgList.peek() != null && this.msgList.peek().level==this.level) {
			Msg msg = this.msgList.peek();
			System.out.println("TOP most in the queue is: from:"+msg.sndrUnqID+" at level "+msg.level+" with message "+msg.type);
			this.msgList.remove();
			return msg;
		}
		else if(this.msgList.peek() != null && this.msgList.peek().level==this.level-1)
		{
			this.msgList.remove();
		}
		return null;
	}
}
