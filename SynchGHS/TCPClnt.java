import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class TCPClnt {
	TreeNode curr;
	NgbrNode nbrn;
	Socket clntSocket;
	int nbrUnqID;
	
	
	ObjectInputStream input;
	ObjectOutputStream output;
	
	TCPClnt(TreeNode curr, int nbrUnqID, NgbrNode nbrn)
	{
		this.curr = curr;
		this.nbrn = nbrn;
		this.nbrUnqID= nbrUnqID;
	}

	public void lstngMessages()
	{
		try{
			while (true) 
			{
				Msg msg = (Msg) input.readObject();
				this.curr.addMsg(msg);
			} 
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.exit(1);
		}
	}


	public void hndShke()
	{
		try {
			Msg msg =new Msg(curr.UnqID,curr.UnqID,MsgType.HandShake,0);
			output.writeObject(msg);
			output.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void lstnSckt()
	{
		try {
			clntSocket = new Socket(nbrn.hstName,nbrn.prtNumber);
			output = new ObjectOutputStream(clntSocket.getOutputStream());
			output.flush();
			input = new ObjectInputStream(clntSocket.getInputStream());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}		
	}
}
