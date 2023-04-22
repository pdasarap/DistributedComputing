import java.io.IOException;
import java.util.*;

public class Synch_GHS {
	boolean isMwoE=true;
	TreeNode curr;
	boolean safe,canMove;
	int count;
	List<Integer> sccsrUnqID= new ArrayList<Integer>(); 
	List<Msg> tmpMsgLst= new ArrayList<Msg>(); 
	Synch_GHS(TreeNode curr)
	{
		
		curr.level=0;
		count=curr.connClnts.size();
		System.out.println("Starting GHS for synchronous systems...");
		this.curr = curr;
		curr.nonTrClnts.putAll(curr.connClnts);
		isMwoE=false;
		resetMWOE();
		System.out.println("Starting FindMWOE...");
		if(curr.mwogEdge!=null)
		{
			System.out.println("MWOE: "+curr.mwogEdge);
		}
		System.out.println("Current level"+curr.level);
		sndFndMWOE();
		curr.level++;
		if(curr.mwogEdge!=null)
		{
			System.out.println("MWOE: "+curr.mwogEdge);
		}
		
		sndTstMsg();
		curr.level++;
		if(curr.mwogEdge!=null)
		{
			System.out.println("MWOE: "+curr.mwogEdge);
		}
		sndAck();
		System.out.println("Ack sent...");
		if(curr.mwogEdge!=null)
		{
			System.out.println("MWOE: "+curr.mwogEdge);
		}
		
		procsAck();
		System.out.println("Ack proccessed..");
		curr.level++;
		if(curr.mwogEdge!=null)
		{
			System.out.println("MWOE: "+curr.mwogEdge);
		}
					sndMWOGETPrdcssor();
					curr.level++;
					System.out.println("Min Outgoing edge to the predecessor is sent..");
					if(curr.mwogEdge!=null)
					{
						System.out.println("MWOE: "+curr.mwogEdge);
					}
					brdCstMWOE();
					curr.level++;
					System.out.println("Broadcast MWOE..");
					if(curr.mwogEdge!=null)
					{
						System.out.println("MWOE: "+curr.mwogEdge);
					}
		int i=0;
		do
		{
			mrg();
			curr.level++;
			System.out.println("Merge completed..");
			if(curr.mwogEdge!=null)
			{
				System.out.println("MWOE: "+curr.mwogEdge);
			}
			curr.ldrUnqID=0;
			chckMrg();
			System.out.println("Merge checked..");
			if(curr.mwogEdge!=null)
			{
				System.out.println("MWOE: "+curr.mwogEdge);
			}
			for(Map.Entry<Integer, ClntRqstHndlr> x:curr.inTrClnts.entrySet())
			{
				System.out.println("..........Intree"+x.getKey());
			}
			sccsrUnqID.clear();
			BrdCstLdr();
			System.out.println("broadcast leader..");
			System.out.println("....Leader is...."+curr.ldrUnqID);
			if(curr.mwogEdge!=null)
			{
				System.out.println("MWOE: "+curr.mwogEdge);
			}
			curr.msgList.clear();
			
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("Starting next level!!!!!!");
			isMwoE=false;
			resetMWOE();
			System.out.println("Starting FindMWOE...");
			if(curr.mwogEdge!=null)
			{
				System.out.println("MWOE: "+curr.mwogEdge);
			}
			System.out.println("Current level"+curr.level);
			sndFndMWOE();
			curr.level++;
			if(curr.mwogEdge!=null)
			{
				System.out.println("MWOE: "+curr.mwogEdge);
			}
			sndTstMsg();
			curr.level++;
			if(curr.mwogEdge!=null)
			{
				System.out.println("MWOE: "+curr.mwogEdge);
			}
			sndAck();
			System.out.println("Ack sent...");
			if(curr.mwogEdge!=null)
			{
				System.out.println("MWOE: "+curr.mwogEdge);
			}
			
			procsAck();
			System.out.println("Ack proccessed..");
			curr.level++;
			if(curr.mwogEdge!=null)
			{
				System.out.println("MWOE: "+curr.mwogEdge);
			}
						sndMWOGETPrdcssor();
						curr.level++;
						System.out.println("Min Outgoing edge to the predecessor is sent..");
						if(curr.mwogEdge!=null)
						{
							System.out.println("MWOE: "+curr.mwogEdge);
						}
						brdCstMWOE();
						curr.level++;
						System.out.println("Broadcast MWOE..");
						if(curr.mwogEdge!=null)
						{
							System.out.println("MWOE: "+curr.mwogEdge.wgt);
						}
						if(curr.mwogEdge!=null)
						if(curr.mwogEdge.wgt!=Integer.MAX_VALUE)
							isMwoE=true;
		}while( isMwoE );
		System.out.println("This is node: "+curr.UnqID);
		System.out.println("Tree edges are: ");
		for(Map.Entry<Integer, ClntRqstHndlr> inTree: curr.inTrClnts.entrySet())
		{
			System.out.println("("+curr.UnqID+","+inTree.getKey()+")");
		}
	}
	public void resetMWOE()
	{
		if(curr.mwogEdge!=null)
		{
			curr.mwogEdge.nonTrNdUnqID=0;
			curr.mwogEdge.trNdUnqID =0;
			curr.mwogEdge.wgt=Integer.MAX_VALUE;
		}
		else
		{
			curr.mwogEdge=new MinWghtOGE();
			curr.mwogEdge.trNdUnqID =0;
			curr.mwogEdge.wgt=Integer.MAX_VALUE;
			curr.mwogEdge.nonTrNdUnqID=0;
		}

	}
	public void sndFndMWOE() 
	{
		brdCst(MsgType.FindMWOE,curr.inTrClnts,curr.UnqID==curr.ldrUnqID,null);
		System.out.println("BroadCast FindMWOE completed");
	}
	
	public void sndTstMsg()
	{
		brdCst(MsgType.Test,curr.nonTrClnts,true,null);
		System.out.println("sendTestMessage completed");
	}
	public void sndAck()
	{
		List<Integer> tmpSntMsg = new ArrayList<Integer>();
		tmpMsgLst.forEach(m->{
			if(m.type==MsgType.Test)
			{
				tmpSntMsg.add(m.sndrUnqID);
				ClntRqstHndlr crh=curr.connClnts.get(m.sndrUnqID);
				if(m.ldrUnqID==curr.ldrUnqID)
				{
					try {
						crh.output.writeObject(new Msg(curr.UnqID,curr.ldrUnqID,MsgType.Reject, curr.level));
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				else
				{
					try {
						crh.output.writeObject(new Msg(curr.UnqID,curr.ldrUnqID,MsgType.Accept, curr.level));
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			});
		tmpMsgLst.clear();
		for(Map.Entry<Integer, ClntRqstHndlr> x: curr.connClnts.entrySet())
		{
			if(!tmpSntMsg.contains(x.getKey()) || x.getKey()==curr.predsrUnqID)
			{
				System.out.println("Sending NULL to "+x.getKey());
				try {
					x.getValue().output.writeObject(new Msg(curr.UnqID,curr.ldrUnqID,MsgType.NULL,curr.mwogEdge ,curr.level));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
				
		}
		int recvd=0;
		while(recvd!=count)
		{
			Msg msg=curr.getHeadMsgFrmQueue();
			if(msg!=null)
			{
				if(msg.type==MsgType.NULL)
				{
					System.out.println(msg.type+" received from "+msg.sndrUnqID);
					recvd++;
				}
				else if(msg.type==MsgType.Accept || msg.type==MsgType.Reject)
				{
					tmpMsgLst.add(msg);
					recvd++;
				}
			}
		}
	}
	public void procsAck()
	{
		for(Msg msg: tmpMsgLst)
		{
			if(msg.type==MsgType.Accept)
			{
				isMwoE=true;
				int nbrUnqID=msg.sndrUnqID;
				NgbrNode n= curr.nbs.get(nbrUnqID);
				if(curr.mwogEdge!=null)
				{
					int a=curr.mwogEdge.nonTrNdUnqID;
					int b=curr.mwogEdge.trNdUnqID;
					int UnqID1=Math.min(a, b);
					int UnqID2 = Math.max(a, b);
					a=curr.UnqID;
					b=n.UnqID;
					int UnqID3=Math.min(a, b);
					int UnqID4 = Math.max(a, b);
					boolean flag=false;
					if(curr.mwogEdge.wgt>n.wgt)
						flag=true;
					else if(curr.mwogEdge.wgt==n.wgt)
					{
						if(UnqID1<UnqID3)
							flag=true;
						else if(UnqID1==UnqID3)
							if(UnqID2<UnqID4)
								flag=true;
					}
					if( flag)
					{
						curr.mwogEdge.wgt=n.wgt;
						curr.mwogEdge.nonTrNdUnqID=nbrUnqID;
						curr.mwogEdge.trNdUnqID=curr.UnqID;
					}
				}
				
			}
			else if(msg.type==MsgType.Reject)
			{
				int clientUID=msg.sndrUnqID;
				curr.nonTrClnts.remove(clientUID);
			}
		}
	}
	public void sndMWOGETPrdcssor()
	{
		int recvd=0;
		int count=0;
		sccsrUnqID.clear();
		if(curr.UnqID==curr.ldrUnqID)
		{
			count=curr.inTrClnts.size();
		}
		else
		{
			count=curr.inTrClnts.size()-1;
		}
		while(recvd!=count)
		{
			for(Msg msg:curr.msgList)
			{
				if(msg.type==MsgType.MWOEFrmScsr)
				{
					sccsrUnqID.add(msg.sndrUnqID);
					recvd++;
					msg.done=true;
					if(msg.mwogEdge!=null && msg.mwogEdge.wgt<curr.mwogEdge.wgt)
					{
						curr.mwogEdge=msg.mwogEdge;
					}
				}
			}
		}
		if(curr.UnqID!=curr.ldrUnqID)
			try {
				curr.connClnts.get(curr.predsrUnqID).output.writeObject(new Msg(curr.UnqID,curr.ldrUnqID,MsgType.MWOEFrmScsr,curr.mwogEdge,curr.level ));
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
	public void brdCstMWOE()
	{
		for(Map.Entry<Integer, ClntRqstHndlr> x: curr.inTrClnts.entrySet())
		{System.out.println("InTREE "+x.getKey());
		}
		if(curr.UnqID==curr.ldrUnqID)
			brdCstM(MsgType.ToMerge,curr.inTrClnts,true,curr.mwogEdge);
		else
		{
			brdCstM(MsgType.ToMerge,curr.inTrClnts,false,curr.mwogEdge);
		}
	}
	
	
	public void brdCstM(MsgType msgType,HashMap<Integer,ClntRqstHndlr> clnts, boolean check,MinWghtOGE min )
	{
		try {
		while(!check)
		{
			Msg msg=curr.getHeadMsgFrmQueue();
			if(msg!=null)
			{
				if(msg.type==msgType)
				{
					System.out.println(msg.type+" received from "+msg.sndrUnqID);
					curr.predsrUnqID= msg.sndrUnqID;
					min=msg.mwogEdge;
					curr.mwogEdge=min;
					check=true;
					if(msgType==MsgType.BrdCstLdr)
					curr.ldrUnqID=msg.ldrUnqID;
				}
			}
		}
		if(check)
		{
				for(Map.Entry<Integer, ClntRqstHndlr> x: clnts.entrySet())
				{
					if(x.getValue().clntUnqID!=curr.predsrUnqID)
					{
						x.getValue().output.writeObject(new Msg(curr.UnqID,curr.ldrUnqID,msgType,null ,curr.level));
					}
				}
		}
	} catch (Exception e) {
		e.printStackTrace();
	}
	}
	
	
	
	
	
	public void brdCst(MsgType msgType,HashMap<Integer,ClntRqstHndlr> clnts,boolean check,MinWghtOGE min )
	{
		try {
			System.out.println("Checking broad cast for "+msgType);
			safe=false;
			canMove=false;
			int recvd=0;
			count = curr.connClnts.size();
			while(recvd!=count)
			{
				while(!check || (msgType!=MsgType.FindMWOE && msgType!=MsgType.Test && msgType!=MsgType.BrdCstLdr && recvd!=count))
				{
					Msg msg=curr.getHeadMsgFrmQueue();
					if(msg!=null)
					{
						if(msg.type==msgType)
						{
							System.out.println(msg.type+" received from "+msg.sndrUnqID);
							curr.predsrUnqID= msg.sndrUnqID;
							min=msg.mwogEdge;
							curr.mwogEdge=min;
							check=true;
							if(msgType==MsgType.BrdCstLdr)
							curr.ldrUnqID=msg.ldrUnqID;
							recvd++;
						}
						else if(msg.type==MsgType.NULL)
						{
							System.out.println(msg.type+" received from "+msg.sndrUnqID);
							recvd++;
						}
					}
				}
				if(!safe)
				{
					{
						for(Map.Entry<Integer, ClntRqstHndlr> x: clnts.entrySet())
						{
							if(x.getKey()!=curr.predsrUnqID)
							{
								if(msgType==MsgType.FindMWOE)
									x.getValue().output.writeObject(new Msg(curr.UnqID,curr.ldrUnqID,msgType,null ,curr.level));
								else
								x.getValue().output.writeObject(new Msg(curr.UnqID,curr.ldrUnqID,msgType,min ,curr.level));
								System.out.println("BroadCast Message send to "+x.getKey()+"	with the message:"+msgType+" with level"+curr.level);
							}
						}
						for(Map.Entry<Integer, ClntRqstHndlr> x: curr.connClnts.entrySet())
						{
							if(!clnts.containsKey(x.getKey()) || x.getKey()==curr.predsrUnqID)
							{
								System.out.println("Sending NULL to "+x.getKey());
								if(msgType==MsgType.FindMWOE)
									x.getValue().output.writeObject(new Msg(curr.UnqID,curr.ldrUnqID,MsgType.NULL,null ,curr.level));
								else
								x.getValue().output.writeObject(new Msg(curr.UnqID,curr.ldrUnqID,MsgType.NULL,min ,curr.level));
							}
								
						}
					}
					safe=true;
				}
				Msg msg=curr.getHeadMsgFrmQueue();
				if(msg!=null)
				{
					if(msg.type==MsgType.NULL)
					{
						System.out.println(msg.type+" received from "+msg.sndrUnqID);
						recvd++;
					}
					else if(msg.type==msgType)
					{
						tmpMsgLst.add(msg);
						recvd++;
					}
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void mrg()
	{
		tmpMsgLst.clear();
		MsgType msgType = MsgType.Merge;
		boolean check = (curr.mwogEdge!=null && curr.UnqID==curr.mwogEdge.trNdUnqID);
		try {
			System.out.println("Checking broad cast for "+msgType);
			safe=false;
			canMove=false;
			count=curr.connClnts.size();
			int recvd=0;
			if(!check)
			{
				for(Map.Entry<Integer, ClntRqstHndlr> x: curr.connClnts.entrySet())
				{
					System.out.println("Sending NULL to "+x.getKey());
					x.getValue().output.writeObject(new Msg(curr.UnqID,curr.ldrUnqID,MsgType.NULL,curr.mwogEdge ,curr.level));
						
				}
			}
			else
			{
				for(Map.Entry<Integer, ClntRqstHndlr> x: curr.connClnts.entrySet())
				{
					if(x.getKey()!=curr.mwogEdge.nonTrNdUnqID)
					{
						System.out.println("Sending NULL to "+x.getKey());
						x.getValue().output.writeObject(new Msg(curr.UnqID,curr.ldrUnqID,MsgType.NULL,curr.mwogEdge ,curr.level));
					}
					else
					{
						curr.connClnts.get(curr.mwogEdge.nonTrNdUnqID).output.writeObject(new Msg(curr.UnqID,curr.ldrUnqID,MsgType.Merge,curr.mwogEdge ,curr.level));
						curr.inTrClnts.put(curr.mwogEdge.nonTrNdUnqID,curr.connClnts.get(curr.mwogEdge.nonTrNdUnqID));
						curr.nonTrClnts.remove(curr.mwogEdge.nonTrNdUnqID);
					}
						
				}
				
			}	
			while(recvd!=count)
			{
				Msg msg=curr.getHeadMsgFrmQueue();
				if(msg!=null)
				{
					if(msg.type==MsgType.NULL)
					{
						System.out.println(msg.type+" received from "+msg.sndrUnqID);
						recvd++;
					}
					else if(msg.type==msgType)
					{
						tmpMsgLst.add(msg);
						recvd++;
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public void chckMrg()
	{
		for(Msg msg: tmpMsgLst)
		{
				if(msg.mwogEdge.trNdUnqID==curr.mwogEdge.nonTrNdUnqID )
				{
					if(msg.sndrUnqID<curr.UnqID)
					{
						curr.ldrUnqID=curr.mwogEdge.trNdUnqID;
						curr.predsrUnqID=curr.UnqID;
					}
				}
				curr.inTrClnts.put(msg.mwogEdge.trNdUnqID, curr.connClnts.get(msg.mwogEdge.trNdUnqID));
				curr.nonTrClnts.remove(msg.mwogEdge.trNdUnqID);
		}
	}
	public void BrdCstLdr()
	{
		brdCastl(MsgType.BrdCstLdr,curr.inTrClnts,curr.UnqID==curr.ldrUnqID,null);
		curr.level++;
	}
	public void brdCastl(MsgType msgType,HashMap<Integer,ClntRqstHndlr> clnts,boolean check, MinWghtOGE min )
	{
		tmpMsgLst.clear();
		try {
			System.out.println("Checking broad cast for "+msgType);
			safe=false;
			canMove=false;
			count=curr.connClnts.size();
			int recvd=0;
			while(!check)
			{
				Msg msg=curr.getHeadMsgFrmQueue();
				if(msg!=null)
				{
					if(msg.type==msgType)
					{
						System.out.println(msg.type+" received from "+msg.sndrUnqID);
						curr.predsrUnqID= msg.sndrUnqID;
						min=msg.mwogEdge;
						curr.mwogEdge=min;
						check=true;
						if(msgType==MsgType.BrdCstLdr)
						curr.ldrUnqID=msg.ldrUnqID;
						recvd++;
					}
					else if(msg.type==MsgType.NULL)
					{
						System.out.println(msg.type+" received from "+msg.sndrUnqID);
						recvd++;
					}
				}
			}
			if(!safe)
			{
				List<Integer> temp= new ArrayList<Integer>();
					for(Map.Entry<Integer, ClntRqstHndlr> x: clnts.entrySet())
					{
						temp.add(x.getKey());
						if(x.getValue().clntUnqID!=curr.predsrUnqID)
						{
							sccsrUnqID.add(x.getKey());
							x.getValue().output.writeObject(new Msg(curr.UnqID,curr.ldrUnqID,msgType,min ,curr.level));
						}
					}
					for(Map.Entry<Integer, ClntRqstHndlr> x: curr.connClnts.entrySet())
					{
						if(!temp.contains(x.getKey()) || (x.getKey()==curr.predsrUnqID && curr.UnqID!=curr.predsrUnqID))
						{
							x.getValue().output.writeObject(new Msg(curr.UnqID,curr.ldrUnqID,MsgType.NULL,min ,curr.level));
						}
					}
					safe=true;
					
			}
			while(recvd!=count)
			{
				Msg msg=curr.getHeadMsgFrmQueue();
				if(msg!=null)
				{
					if(msg.type==MsgType.NULL)
					{
						System.out.println(msg.type+" received from "+msg.sndrUnqID);
						recvd++;
					}
					else if(msg.type==msgType)
					{
						tmpMsgLst.add(msg);
						recvd++;
					}
				}
			}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	public void ldrRcvd()
	{
		System.out.println("LR started");
		boolean check=(sccsrUnqID==null || sccsrUnqID.size()==0);
		int recvd=0;
		int count=curr.inTrClnts.size();
		safe=false;
		while(recvd!=count)
		{

			while(!check)
			{
				int lr=0;
				Msg msg=curr.getHeadMsgFrmQueue();
				{
					if(msg!=null)
					{
						if(msg.type==MsgType.LR)
						{
							System.out.println("LR received from"+msg.sndrUnqID);
							recvd++;
							System.out.println("Received value"+recvd);
							lr++;
							if(lr==sccsrUnqID.size())
							check=true;
						}
						else if(msg.type==MsgType.NULL)
						{
							System.out.println("NULL received from"+msg.sndrUnqID);
							recvd++;
							System.out.println("Received value"+recvd);
							
						}
					}
					
				}
			}
			if(!safe)
			{
				System.out.println("I am HERE 1");
				System.out.println(curr.UnqID);
				System.out.println(curr.ldrUnqID);
				if(curr.UnqID!=curr.ldrUnqID)
				{
					try {
						curr.connClnts.get(curr.predsrUnqID).output.writeObject(new Msg(curr.UnqID,curr.ldrUnqID,MsgType.LR,curr.mwogEdge,curr.level ));
						System.out.println("LR sent to "+curr.predsrUnqID+" with the level "+curr.level);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				for(Map.Entry<Integer, ClntRqstHndlr> x: curr.inTrClnts.entrySet())
				{
					if( (x.getKey()!=curr.predsrUnqID))
					{
						try {
							
							x.getValue().output.writeObject(new Msg(curr.UnqID,curr.ldrUnqID,MsgType.NULL,curr.mwogEdge ,curr.level));
							System.out.println("NULL sent to "+x.getKey()+" with the level "+curr.level);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				safe=true;
			}
			
			Msg msg=curr.getHeadMsgFrmQueue();
			{
				if(msg!=null)
				if(msg.type==MsgType.LR || msg.type==MsgType.NULL)
				{
					System.out.println(msg.type+" received from "+msg.sndrUnqID);
					System.out.println("Received value"+recvd);
					recvd++;
				}
			}
		}
		System.out.println("LEADER RECEIVED!!!!!!!!.........");
	}
}
