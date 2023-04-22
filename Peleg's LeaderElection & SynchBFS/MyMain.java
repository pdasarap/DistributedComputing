import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Map.Entry;

public class MyMain {
	public static void main(String[] args) {

		String cli_HName = "";
		try {
			cli_HName = InetAddress.getLocalHost().getHostName();
		} catch (Exception e) {
			e.printStackTrace();
		}

		Node dist_sys_node = CreateNode(cli_HName);
		System.out.println("Initializing UID as server: " + dist_sys_node.UID);
		
		Runnable obj_srv_run = new Runnable() {
			public void run() {
				TCP_Srvr srvr = new TCP_Srvr(dist_sys_node);
				srvr.lstnSockt();
			}
		};
		
		Thread srvr_thrd = new Thread(obj_srv_run);
		srvr_thrd.start();

		System.out.println("Server started and listening to client requests.........");

		for (Entry<String, Node> newnode : ConfParser.nodeList.entrySet()) {
			if (newnode.getValue().HName.equals(cli_HName) ) {
				newnode.getValue().UIDNgbrs.entrySet().forEach((neighbour) -> {
						dist_sys_node.clntsOnMchnCnt = dist_sys_node.clntsOnMchnCnt + 1;
						Runnable obj_clnt_run = new Runnable() {
							public void run() {
								try {
									Thread.sleep(10000);
								} catch (Exception e) {
									e.printStackTrace();
								}
								TCP_Clnt clnt = new TCP_Clnt(newnode.getValue().getNodeUID(), neighbour.getValue().PNumber,
										neighbour.getValue().HName, newnode.getValue().HName, neighbour.getKey(),
										dist_sys_node);
								clnt.lstnSockt();
								clnt.HandShkMsgSend();
								clnt.listenBrdcstMsg();
							}
						};
						Thread clntthread = new Thread(obj_clnt_run);
						clntthread.start();
					
				});
				break;

			}

		}
		try {
			Thread.sleep(15000);
		} catch (Exception e) {
			e.printStackTrace();
		}
		LdrElecAlgo algorithm = new LdrElecAlgo(dist_sys_node);
		algorithm.ElectionStart();

		BFS_Tree BFS_Tree = new BFS_Tree(dist_sys_node);
		BFS_Tree.startBfs();
		System.out.println("BFS Tree is constructed");
		try {
			Thread.sleep(10000);
		} catch (Exception e) {
			e.printStackTrace();
		}
		BFS_Tree.initiateDegQury();	
		System.out.println("Execution finished......");
		

	}

	public static Node CreateNode(String cli_HName) {
		Node dist_sys_node = new Node();
		try {
			dist_sys_node = ConfParser.read("/home/012/p/px/pxd210008/configGHS.txt",cli_HName);
		} catch (Exception e) {
			throw new RuntimeException("Unable to get nodeList", e);
		}
		return dist_sys_node;
	}
}
