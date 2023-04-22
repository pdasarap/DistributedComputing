import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;

public class TCPSrvr {

	TreeNode curr;
	ServerSocket svrSocket;

	TCPSrvr(TreeNode curr) {
		this.curr = curr;
	}

	public void lstnSckt() {
		try {
			svrSocket = new ServerSocket(curr.prtNumber);
		} catch (IOException e) {
			e.printStackTrace();
		}
		while (true) {
			try {
				Socket clntReqSocket = svrSocket.accept();				
				ClntRqstHndlr reqHandler = new ClntRqstHndlr(clntReqSocket, curr);				
	
				Thread t = new Thread(reqHandler);
				t.start();				

			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}
}
