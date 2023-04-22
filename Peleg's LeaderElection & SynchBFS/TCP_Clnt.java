import java.io.*;
import java.net.*;

public class TCP_Clnt {

	String srvrH_Name, cli_HName;
	int srvrPN, UID, srvrUID;
	Socket clientsocket;
	ObjectInputStream in;
	ObjectOutputStream out;
	Node dist_sys_node;

	public void HandShkMsgSend() {

		try {
			System.out.println("Sending data to server " + this.srvrUID + ".....");
			String msg = "Hi!" + this.UID;
			out.writeObject(msg);
			out.flush();
		} catch (IOException e) {
			System.out.println("Transmission Error" + e);
			System.exit(1);
		}
	}
	public void lstnSockt() {
		try {
			clientsocket = new Socket(srvrH_Name, srvrPN, InetAddress.getByName(cli_HName), 0);
			out = new ObjectOutputStream(clientsocket.getOutputStream());
			out.flush();

			in = new ObjectInputStream(clientsocket.getInputStream());
			System.out.println("After inputStream, listenSocket");
		} catch (UnknownHostException e) {
			System.out.println("Host Unknown:" + srvrH_Name);
			System.exit(1);
		} catch (IOException e) {
			System.out.println("I/O Error" + e);
			System.exit(1);
		}
	}

	public TCP_Clnt(int UID, int serverPort, String srvrH_Name, String cli_HName, int srvrUID,
		Node dist_sys_node) {
		this.srvrH_Name = srvrH_Name;
		this.srvrPN = serverPort;
		this.UID = UID;
		this.cli_HName = cli_HName;
		this.srvrUID = srvrUID;
		this.dist_sys_node = dist_sys_node;
		System.out.println("ServerHostName: " + srvrH_Name);
		System.out.println("ServerPort: " + serverPort);
		System.out.println("UID: " + UID);
		System.out.println("clientHostName: " + cli_HName);
		System.out.println("ServerUID: " + srvrUID);
	}

	
	public void listenBrdcstMsg() {
		try {
			while (true) {
				Msg brdcstMsg = (Msg) in.readObject();
				// add received messages to Blocking queue
				this.dist_sys_node.addMsgToQue(brdcstMsg);

				if (brdcstMsg.getMsgType() != MsgType.DEGREE
						&& brdcstMsg.getMsgType() != MsgType.PDEGREE) {
					System.out.println("Broadcast Message from :" + brdcstMsg.sndrUID + " - { UID:"
							+ brdcstMsg.sndrUID + ", Distance:" + brdcstMsg.getdisKn()
							+ ", Phase:" + brdcstMsg.getMsgPhase() + " ,HighestUID:"
							+ brdcstMsg.maxUIDRec + " MessageType:" + brdcstMsg.getMsgType()
							+ "}");
				} else {
					System.out.println("Degree of Broadcast from " + brdcstMsg.sndrUID
							+ " - { UID:" + brdcstMsg.sndrUID + " ,HighestDegree:"
							+ brdcstMsg.nDegree + " ,HighestDegreeUID: " + brdcstMsg.highestDegreeUID
							+ " ,MessageType:" + brdcstMsg.getMsgType() + "}");
				}
			}
		} catch (Exception e) {
			System.out.println("Transmission Error" + e);
			System.exit(1);
		}
	}
}
