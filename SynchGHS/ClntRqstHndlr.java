import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.net.Socket;


public class ClntRqstHndlr implements Runnable {

	TreeNode curr;
	int clntUnqID;
	ObjectInputStream input;
	Socket clnt;
	ObjectOutputStream output;

	ClntRqstHndlr(Socket rqstHndlr, TreeNode curr) {
		this.curr = curr;
		this.clnt = rqstHndlr;
	}

	public ObjectOutputStream getOutputWriter() {
		return output;
	}

	public Socket getClient() {
		return clnt;
	}

	public ObjectInputStream getInputReader() {
		return input;
	}

	

	public void run() {
		try {
			input = new ObjectInputStream(clnt.getInputStream());
			output = new ObjectOutputStream(clnt.getOutputStream());
			output.flush();
			Msg msg = (Msg) input.readObject();
			if (msg.type == MsgType.HandShake) {
				this.clntUnqID = msg.sndrUnqID;
				System.out.println("Text received from client: " + this.clntUnqID);
				curr.addClient(this.clntUnqID, this);
			} else {
				this.curr.addMsg(msg);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public int getClientUID() {
		return this.clntUnqID;
	}
}
