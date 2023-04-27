import java.io.*;
import java.net.*;
import java.util.logging.*;

public class NodeHeeder implements Runnable {
	private int connector;
	final static Logger logger = Logger.getLogger(NodeHeeder.class.getName());
	static{
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
	}
	public NodeHeeder(int connector) {
		logger.info("Port-Number that is listening->" + connector);
		this.connector = connector;
	}

	public void run() {
		try	{
			ServerSocket listSocket_Server = new ServerSocket(connector);
			logger.finest("Inside NodeHeeder");
			while(true)
			{
				Recipient recipt;
				try {
					logger.finest("Still aiting for client");
					recipt = new Recipient(listSocket_Server.accept());
					Thread t = new Thread(recipt);
					t.start();
				} catch(IOException e) {
					logger.log(Level.SEVERE,"Failure in acceptance");
					System.exit(100);
				}				
			}
			//serverSock.close();
		} catch(IOException ex) {
			logger.log(Level.SEVERE,"",ex);
		}
	}
}
