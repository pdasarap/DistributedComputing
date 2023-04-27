import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.logging.*;

public class Main {
/*global variables */
	final static Logger logger = Logger.getLogger(Main.class.getName());
	static int uid;
	static String myHost;
	static int myPort;
	static int leaderId;
	static int parentId;
	static int level;
	static int totalNodes;
	static AtomicInteger phase = new AtomicInteger(0);
	static boolean ispartOfresultantEdege = false;
	static ArrayList<Edge> basicEdges = new ArrayList<>();
	static ArrayList<Edge> branchEdges = new ArrayList<>();
	static ArrayList<Edge> rejectEdges = new ArrayList<>();
	static HashMap<Integer, ArrayList<String>> id2HostPortMap = new HashMap<>();
	static ConcurrentLinkedQueue<Message> buffer = new ConcurrentLinkedQueue<>(); // message queue

	public static void main(String[] args) throws IOException {
		logger.setUseParentHandlers(false);

		// remove and previousHandlers that will be replaced
		Handler[] previousHandlers = logger.getHandlers();
		for(Handler handler : previousHandlers)
		{
				if(handler.getClass() == ConsoleHandler.class)
					logger.removeHandler(handler);
		}
		logger.setLevel(Level.FINEST);
		ConsoleHandler handler = new ConsoleHandler(); 
		handler.setLevel(Level.FINEST);
		logger.addHandler(handler);
		

		InetAddress inetAddress = InetAddress.getLocalHost();
		myHost = inetAddress.getHostName();

		if (args.length > 0) {
			uid = Integer.parseInt(args[1]);
			myHost = "localhost";
		}
		ReadFile parser = new ReadFile();
		id2HostPortMap = parser.readFile(uid,args[0]);
		myPort = Integer.parseInt(id2HostPortMap.get(uid).get(1));

		//sorting the basic edges
		Collections.sort(basicEdges);
		//initially, uid = leaderId for each node
		leaderId = uid;
		logger.info("My Id: " + Main.uid);
		NodeHeeder listener = new NodeHeeder(myPort);
		Thread thread = new Thread(listener);
		thread.start();
		
		SynchronousGHSalgo ghs = new SynchronousGHSalgo(totalNodes);
		logger.info("Starting MST Construction");
		ghs.buildingMST();
		
		

	}
}
