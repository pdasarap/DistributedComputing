import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Iterator;

// import org.apache.log4j.Logger;
import java.util.logging.*;

public class Recipient implements Runnable {

	private Socket client;
	final static Logger logger = Logger.getLogger(Recipient.class.getName());
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
	
	
	public Recipient(Socket client){
		// logger.setLevel(Level.FINEST);
		// ConsoleHandler handler = new ConsoleHandler(); 
		// handler.setLevel(Level.FINEST);
		// logger.addHandler(handler);
		// logger.setUseParentHandlers(false);

		this.client = client;
	}

	@Override
	public void run() {

		// logger.setLevel(Level.FINEST);
		// ConsoleHandler handler = new ConsoleHandler(); 
		// handler.setLevel(Level.FINEST);
		// logger.addHandler(handler);
		// logger.setUseParentHandlers(false);


		Message message;
		ObjectInputStream in;
		try{
			boolean addFlag = true;
			in = new ObjectInputStream(client.getInputStream());
			message = (Message) in.readObject();
			logger.fine("Received "+message.toString());
			// immediately send Reject if the Examine msg received from same component
			if(message.getMsgType().equals(MessageCategory.EXAMINE)) {
				if(message.getPhaseNo() == Main.phase.intValue()) {
					addFlag= false;
					Message msg;
					synchronized (this) {
						Edge responseEdge = new Edge();
						for(Edge edge : Main.basicEdges) {
							//this loop is required to find proper endpoint host and post 
							if(areEdgesEqual(edge, message.getCurrentEdge())) {
								objectReplication(edge, responseEdge);
							}
						}
						if(responseEdge.getVertex1() == responseEdge.getVertex2()) {
							for(Edge edge : Main.branchEdges) {
								//this loop is required to find proper endpoint host and post 
								if(areEdgesEqual(edge, message.getCurrentEdge())) {
									objectReplication(edge, responseEdge);
								}
							}
						}
						if(responseEdge.getVertex1() == responseEdge.getVertex2()) {
							for(Edge edge : Main.rejectEdges) {
								//this loop is required to find proper endpoint host and post 
								if(areEdgesEqual(edge, message.getCurrentEdge())) {
									objectReplication(edge, responseEdge);
								}
							}
						}
						if(message.getLeaderId() == Main.leaderId){
							appendingToRejectedList(responseEdge);
							msg = formationOfInspectAckMsg(MessageCategory.EXAMINE_RESPONSE, "REJECT");
							
						} else {
							msg = formationOfInspectAckMsg(MessageCategory.EXAMINE_RESPONSE, "ACCEPT");
						}
						messageSender(msg, responseEdge);
					}
				}
			}
			if(addFlag){
				Main.buffer.offer(message);
			}
		} catch(IOException e) {
			logger.log(Level.SEVERE,"",e);
		} catch (ClassNotFoundException e) {
			logger.log(Level.SEVERE,"",e);
		} 
		
	}

	/**
	 * @param currentEdge
	 */
	private synchronized void appendingToRejectedList(Edge currentEdge) {
		synchronized (Main.basicEdges) {
			/*Main.basicEdges.remove(currentEdge);
			currentEdge.setEdgeStatus(Edge_Type.REJECTED);
			Main.rejectEdges.add(currentEdge);*/
			Iterator<Edge> itr = Main.basicEdges.iterator();
			while(itr.hasNext()) {
				Edge edge = itr.next();
				if(edge.getVertex1() == currentEdge.getVertex1() && edge.getVertex2() == currentEdge.getVertex2()) {
					edge.setEdgeStatus(EdgeStatus.REJECTED);
					Main.rejectEdges.add(edge);
					itr.remove();
					//Main.basicEdges.remove(edge);
				}
			}
		}
	}

	/**
	 * @param mc
	 * @param string
	 * @return
	 */
	private Message formationOfInspectAckMsg(MessageCategory mc, String response) {
		Message msg = new Message(mc);
		msg.setLeaderId(Main.leaderId);
		msg.setExamineResponse(response);
		return msg;
	}
	
	private boolean areEdgesEqual(Edge edge1, Edge edge2) {
		if (edge1.getVertex1() == edge2.getVertex1() && edge1.getVertex2() == edge2.getVertex2()) {
			return true;
		}
		return false;
	}
	
	/**
	 * @param sourceEdge
	 * @param destEdge
	 */
	private void objectReplication(Edge sourceEdge, Edge destEdge) {
		// only minId and maxId properties are required to check a unique edge
		destEdge.setVertex2(sourceEdge.getVertex2());
		destEdge.setVertex1(sourceEdge.getVertex1());
		destEdge.setWeight(sourceEdge.getWeight());
		destEdge.setEdgeStatus(sourceEdge.getEdgeStatus());
		// copy the host and port of the end point
		destEdge.setEdgeHostname(sourceEdge.getEdgeHostname());
		destEdge.setEdgePort(sourceEdge.getEdgePort());
	}
	
	public void messageSender(Message msg, Edge edge) {


		// logger.setLevel(Level.FINEST);
		// ConsoleHandler handler = new ConsoleHandler(); 
		// handler.setLevel(Level.FINEST);
		// logger.addHandler(handler);
		// logger.setUseParentHandlers(false);


		if (edge == null) {
			return;
		}
		msg.setPhaseNo(Main.phase.intValue());
		msg.setCurrentEdge(edge); // the edge from which the message is being
									// sent
		ObjectOutputStream outputStream = null;
		boolean scanning = true;
		while (scanning) {
			try {
				Socket clientSocket = new Socket(edge.getEdgeHostname(), edge.getEdgePort());
				scanning = false;
				outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
			} catch (ConnectException e) {
				logger.severe("ConnectException: failed with" + edge.getEdgeHostname() + " " + edge.getEdgePort());
				try {
					Thread.sleep(2000);// wait for 2 seconds before trying next
				} catch (InterruptedException ie) {
					ie.printStackTrace();
				}
			} catch (UnknownHostException e) {
				logger.log(Level.SEVERE,"UnknownHostException" + e);
			} catch (IOException e) {
				logger.log(Level.SEVERE,"IOException" + e);
			}
		}
		try {
			outputStream.writeObject(msg);
			outputStream.reset();
			logger.fine(edge.getEdgeHostname()+ ":" + edge.getEdgePort() + " sent "+ msg.toString());
		} catch (IOException e) {
			logger.log(Level.SEVERE,"IOException"+e);
		}
	}
	
}
