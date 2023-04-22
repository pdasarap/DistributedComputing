
public class MainProg {
	public static void main(String[] args) {
		System.out.println("STARTS NOW...");
		TreeNode curr = new TreeNode();
		curr.level=0;
		String path = "/home/012/p/px/pxd210008/SynchGHS/configGHS.txt";
		
		ParsConf pconf = new ParsConf(curr, path, args[0]);
		
		System.out.println("Server is initialized with UID: " + curr.UnqID);
		
		//Starting the server
		Runnable srvrRnble = new Runnable() {
			public void run()
			{
				TCPSrvr srvr = new TCPSrvr(curr);
				srvr.lstnSckt();
			}
		};
		Thread srvrThrd = new Thread(srvrRnble);
		srvrThrd.start();
		
		System.out.println("Server started and listening to the client requests......");
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		curr.nbs.forEach((k,v)->{
			Runnable clntRunnable = new Runnable() {
				public void run()
				{
					TCPClnt clnt = new TCPClnt(curr,k,v);
					clnt.lstnSckt();
					clnt.hndShke();
					clnt.lstngMessages();
				}
			};
			Thread clntThread = new Thread(clntRunnable);
			clntThread.start();
		});
		
		try {
			Thread.sleep(20000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Synch_GHS ghs= new Synch_GHS(curr);
		
	}
}
