import java.io.*;
import java.net.*;

class ClntReqHandler implements Runnable {
	ObjectInputStream in;
	private Socket client;
	ObjectOutputStream out;
	Node dist_sys_node;
	private String clntUID;
	boolean isChild = true;

	public Socket getClntSockt() {
		return this.client;
	}

	public ClntReqHandler(Socket client, Node dist_sys_node) {
		this.client = client;
		this.dist_sys_node = dist_sys_node;
	}

	public ObjectInputStream getInputReader() {
		return this.in;

	}

	public int getClntUID() {
		return Integer.parseInt(this.clntUID);
	}

	
	public ObjectOutputStream getOutputWriter() {
		return this.out;
	}

	public void run() {
		try {
			out = new ObjectOutputStream(client.getOutputStream());
			in = new ObjectInputStream(client.getInputStream());
			out.flush();
		} catch (IOException e) {
			System.out.println("In or out failed");
			System.exit(-1);
		}

		while (true) {
			try {
				Object msg = in.readObject();
				if (msg instanceof Msg) {
					Msg brdcstMsg = (Msg) in.readObject();
					this.dist_sys_node.addMsgToQue(brdcstMsg);
					
				}

				else if (msg instanceof String) {
					String mesg = "";
					mesg = msg.toString();
					String[] msgArr = mesg.split("!");
					this.clntUID = msgArr[1];
					System.out.println("Received text from client: " + this.clntUID);
				}

			} catch (IOException | ClassNotFoundException e) {
				System.out.println("Reading failed");
				System.exit(-1);
			}
		}
	}
}