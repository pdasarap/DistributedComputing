import java.io.*;
import java.net.*;

public class TCP_Srvr {
	String HName;
	int PNumber, UID;
	ServerSocket srvrSocket;
	Node dist_sys_node;

	public TCP_Srvr(int UID, int serverPort, String HName) {
		this.HName = HName;
		this.PNumber = serverPort;
		this.UID = UID;
	}

	public TCP_Srvr(Node dist_sys_node) {
		this(dist_sys_node.UID, dist_sys_node.port, dist_sys_node.HName);
		this.dist_sys_node = dist_sys_node;
	}

	public void lstnSockt() {
		try {
			srvrSocket = new ServerSocket(PNumber);
		} catch (IOException e) {
			System.out.println(e);
			System.exit(-1);
		}
		while (true) {
			ClntReqHandler reqHandler;
			try {
				Socket clientreqSocket;
				clientreqSocket = srvrSocket.accept();
				reqHandler = new ClntReqHandler(clientreqSocket, this.dist_sys_node);

				dist_sys_node.addClient(reqHandler);

				Thread thrd;
				thrd = new Thread(reqHandler);
				thrd.start();

			} catch (IOException e) {
				System.out.println("Accept failed");
				System.exit(-1);
			}
		}
	}
}
