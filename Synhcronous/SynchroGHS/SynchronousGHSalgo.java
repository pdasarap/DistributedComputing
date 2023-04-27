import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.*;


public class SynchronousGHSalgo {

	final static Logger logger = Logger.getLogger(SynchronousGHSalgo.class.getName());
	static{
		logger.setUseParentHandlers(false);
		// remove previous Handlers 
		Handler[] previousHandlers = logger.getHandlers();
		for(Handler handler : previousHandlers){
			if(handler.getClass() == ConsoleHandler.class)
				logger.removeHandler(handler);
		}
		logger.setLevel(Level.FINEST);
		ConsoleHandler handler = new ConsoleHandler(); 
		handler.setLevel(Level.FINEST);
		// adding new console handler
		logger.addHandler(handler);
	}
	int phasesCount = 0;
	private int acknowledgementCount;
	Edge isEdgeALeader;
	Edge importantEdge;
	boolean ispartOfresultantEdege;
	boolean isCurrentMwoe;
	boolean terminationDetection;

	public SynchronousGHSalgo(int totalNodes) {
		this.phasesCount = totalNodes;
	}

	public void messageSender(Message info, Edge x) {		

		if (x == null || info.getMsgType().equals(null)) {
			return;
		}
		info.setPhaseNo(Main.phase.intValue());
		info.setCurrentEdge(x); 
		ObjectOutputStream os = null;
		boolean testing = true;
		while (testing) {
			try {
				Socket client_soc = new Socket(x.getEdgeHostname(), x.getEdgePort());
				testing = false;
				os = new ObjectOutputStream(client_soc.getOutputStream());
			} catch (ConnectException e) {
				logger.log(Level.SEVERE,"ConnectException: failure of" + x.getEdgeHostname() + " " + x.getEdgePort());
				try {
					Thread.sleep(2000);
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
			os.writeObject(info);
			os.reset();
			logger.fine("Sent: " + info.toString());
		} catch (IOException e) {
			logger.log(Level.SEVERE,"IOException" + e);
		}
	}


	public Edge serachForLocalMwoe() {

		logger.fine("Seraching for Local mwoe");
		boolean isLocalMwoeReceived = false;
		Edge minWghtOutEdge = new Edge();
		while (!isLocalMwoeReceived) {
			
			logger.fine(""+Main.basicEdges.toString());
			if (!Main.basicEdges.isEmpty()) {
				Edge cur_edge = Main.basicEdges.get(0);
				
				Message msgInspection = formationOfInspectMsg(MessageCategory.EXAMINE);
				logger.fine("Dispatching inspection msg");
				messageSender(msgInspection, cur_edge);
				String reply = getExamineResponse();
				if (reply.equals("REJECT")) {
					
					appendingToRejectedList(cur_edge);
				} else if (reply.equals("ACCEPT")) {
					objectReplication(cur_edge, minWghtOutEdge);
					
					isLocalMwoeReceived = true;
				} else {
					logger.severe("An error occured in inspection msg");
				}
			} else {
				isLocalMwoeReceived = true;
				
				logger.fine("Local mwoe not found!");
				minWghtOutEdge = new Edge(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
			}
		}
		return minWghtOutEdge;
	}

	private Message formationOfReplyMwoeMessages(MessageCategory  mc, Edge selfMwoe, int sourceUID) {
		Message info = new Message( mc);
		info.setMwoeEdge(selfMwoe);
		info.setLeaderId(Main.leaderId);
		info.setMwoeSourceId(sourceUID);
		int mwoeDestinationId = selfMwoe.getVertex1() == sourceUID ? selfMwoe.getVertex2() : selfMwoe.getVertex1();
		info.setMwoeDestinationId(mwoeDestinationId);
		return info;
	}


	private Message GeneratingMwoeSearchMessage(MessageCategory  mc) {
		Message info = new Message( mc);
		info.setLeaderId(Main.leaderId);
		return info;
	}

	private String getExamineResponse() {

		boolean obtainedInspectionMsg = false;
		String inspectedReply = null;
		while (!obtainedInspectionMsg) {
			if (!Main.buffer.isEmpty()) {
				for (Message info : Main.buffer) {
					if (obtainedInspectionMsg) {
						break;
					}
					if (info.getMsgType().equals(MessageCategory.EXAMINE_RESPONSE)) {
						logger.fine("Inspection-reply message is obtained");
						obtainedInspectionMsg = true;
						inspectedReply = info.getExamineResponse();
						Main.buffer.remove(info);
					} else {
						Main.buffer.offer(Main.buffer.poll());
					}
				}
				if (obtainedInspectionMsg) {
					break;
				}
			}
			
			logger.fine("Anticipating Inspection-reply message");
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				logger.log(Level.SEVERE,"",e);
			}
		}
		if(inspectedReply == null) {
			logger.severe("An error occured in inspection notification");
		}
		return inspectedReply;
	}


	private Message formationOfInspectMsg(MessageCategory  mc) {
		Message info = new Message( mc);
		info.setLeaderId(Main.leaderId);
		return info;
	}

	private Message formtaionOfEmptyMessage(MessageCategory  mc) {
		Message info = new Message( mc);
		info.setLeaderId(Main.leaderId);
		return info;
	}

	
	private void currentEdgeAdditionToBranch(Edge x) {		

		boolean test = false;
		Iterator<Edge> iteratorr = Main.basicEdges.iterator();
		while (iteratorr.hasNext()) {
			Edge b_Edge = iteratorr.next();
			if (areEdgesEqual(b_Edge, x)) {
				b_Edge.setEdgeStatus(EdgeStatus.BRANCH);
				Main.branchEdges.add(b_Edge);
				iteratorr.remove();
				test = true;
				return;
			}
		}
		if (!test) {
			Iterator<Edge> it = Main.branchEdges.iterator();
			while (it.hasNext()) {
				if (areEdgesEqual(it.next(), x)) {
					logger.fine("Edge is already designated as Branch");
					return;
				}
			}
			logger.severe("An error occured! ");
		}
	}

	private Message formtaionOfmsgJoin(MessageCategory  mc) {
		Message info = new Message( mc);
		info.setLeaderId(Main.leaderId);
		return info;
	}
	
	private void appendingToRejectedList(Edge unecessaryEdge) {
		synchronized (Main.basicEdges) {
			
			Iterator<Edge> iteratorr = Main.basicEdges.iterator();
			while(iteratorr.hasNext()) {
				Edge x = iteratorr.next();
				if(x.getVertex1() == unecessaryEdge.getVertex1() && x.getVertex2() == unecessaryEdge.getVertex2()) {
					x.setEdgeStatus(EdgeStatus.REJECTED);
					Main.rejectEdges.add(x);
					iteratorr.remove();
					
				}
			}
		}

	}

	private void objectReplication(Edge startingE, Edge endingE) {
		
		endingE.setVertex2(startingE.getVertex2());
		endingE.setVertex1(startingE.getVertex1());
		endingE.setWeight(startingE.getWeight());
		endingE.setEdgeStatus(startingE.getEdgeStatus());
		
		endingE.setEdgeHostname(startingE.getEdgeHostname());
		endingE.setEdgePort(startingE.getEdgePort());
	}

	private Message createNewLeaderMsg(MessageCategory  mc, int currentLeaderUID) {
		Message info = new Message( mc);
		info.setLeaderId(Main.leaderId);
		info.setNewLeaderId(currentLeaderUID);
		return info;
	}

	

	private boolean areEdgesEqual(Edge e1, Edge e2) {
		if (e1.getVertex1() == e2.getVertex1() && e1.getVertex2() == e2.getVertex2()) {
			return true;
		}
		return false;
	}


	public void buildingMST() {

		while (Main.phase.intValue() < phasesCount) {
			acknowledgementCount = 0;
			isEdgeALeader = new Edge();
			importantEdge = new Edge();
			ispartOfresultantEdege = false;
			isCurrentMwoe = false;
			terminationDetection = false;
			Edge infoGivenByEdge = new Edge();

			if (Main.uid == Main.leaderId) {
				Message mwoeSeacrhingMessage = GeneratingMwoeSearchMessage(MessageCategory.SEARCH_MWOE);
				for (Edge x : Main.branchEdges) {
					messageSender(mwoeSeacrhingMessage, x);
				}
			} else {
				
				boolean gotMwoe_serachMsg = false;
				while (!gotMwoe_serachMsg) {
					logger.fine("Anticipating a searchMwoe notification");
					if (!Main.buffer.isEmpty()) {
						for (Message info : Main.buffer) {
							if (gotMwoe_serachMsg) {
								break;
							}
							if (info.getMsgType().equals(MessageCategory.SEARCH_MWOE)) {
								gotMwoe_serachMsg = true;
								logger.fine("Obtained searchMwoe Notification");
								
								infoGivenByEdge = info.getCurrentEdge();
								for (Edge x : Main.branchEdges) {
									
									if (areEdgesEqual(x, infoGivenByEdge)) {
										objectReplication(x, infoGivenByEdge);
										continue;
									}
									messageSender(info, x);
								}
								Main.buffer.remove(info);
							} else {
								Main.buffer.offer(Main.buffer.poll());
							}
						}
						if (gotMwoe_serachMsg) {
							break;
						}
					}
					
					logger.fine("Still anticipating a searchMwoe notification");
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						logger.log(Level.SEVERE,"",e);
					}
				}
			} 

			Edge selfMwoe = serachForLocalMwoe();
			Message mwoe_ackMsg = formationOfReplyMwoeMessages(MessageCategory.REPLY_MWOE, selfMwoe, Main.uid);
			Edge acquired_mwoe = new Edge(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
			Message mwoe_acquiredMsg = new Message(MessageCategory.REPLY_MWOE);
			if(Main.uid == Main.leaderId) {
				acknowledgementCount = Main.branchEdges.size();
			} else {
				acknowledgementCount = Main.branchEdges.size() - 1; 
			}
			if (acknowledgementCount == 0 && Main.uid != Main.leaderId) {
				if(infoGivenByEdge == null) {
					logger.severe("An issue with the null parent edge");
				}
				messageSender(mwoe_ackMsg, infoGivenByEdge);
			} else {
				
				while (acknowledgementCount > 0) { 
					if (!Main.buffer.isEmpty()) {
						for (Message info : Main.buffer) {
							if (acknowledgementCount <= 0) {
								break;
							}
							
							if (info.getMsgType().equals(MessageCategory.REPLY_MWOE)) {
								logger.fine("Obtained REPLYMwoe Notification");
								acknowledgementCount--;
								
								if (info.getMwoeEdge().compareTo(acquired_mwoe) < 0) {
									acquired_mwoe = info.getMwoeEdge();
									mwoe_acquiredMsg = info;
								} else {
									mwoe_acquiredMsg.setMwoeEdge(acquired_mwoe);
								}
								Main.buffer.remove(info);
							} else {
								Main.buffer.offer(Main.buffer.poll());
							}
						}
						if (acknowledgementCount <= 0) {
							break;
						}
					}
					
					logger.fine("Anticipating REPLYMwoe Notification");
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						logger.log(Level.SEVERE,"",e);
					}
				} 
				if (Main.uid != Main.leaderId) {
					if (acquired_mwoe.compareTo(selfMwoe) == 0) {
						logger.fine("Mwoe not found!");
						logger.fine(""+mwoe_acquiredMsg);
						logger.fine(""+infoGivenByEdge);
						messageSender(mwoe_acquiredMsg, infoGivenByEdge);
					}
					else if (acquired_mwoe.compareTo(selfMwoe) < 0) {
						logger.fine("The mwoe obtained is less than local mwoe");
						messageSender(mwoe_acquiredMsg, infoGivenByEdge);
					} else {
						logger.fine("Transmitting Local mwoe");
						messageSender(mwoe_ackMsg, infoGivenByEdge);
					}
				}
			} 

			if (Main.uid == Main.leaderId) {
				if (acquired_mwoe.compareTo(selfMwoe) == 0) {
					logger.fine("This component has no mwoe");
					terminationDetection = true;
					
					logger.fine("Broadcasting for addition of mwoe");
					Message mwoe_appendMsg = fomrationOfAppendMwoeMsg(MessageCategory.ADD_MWOE, acquired_mwoe);
					for (Edge x : Main.branchEdges) {
						messageSender(mwoe_appendMsg, x);
					}
				} else if (acquired_mwoe.compareTo(selfMwoe) < 0) {
					logger.fine("mwoe obtained is the minimum of this component");
					
					logger.fine("Broadcasting for addition of mwoe");
					Message mwoe_appendMsg = fomrationOfAppendMwoeMsg(MessageCategory.ADD_MWOE, acquired_mwoe);
					for (Edge x : Main.branchEdges) {
						messageSender(mwoe_appendMsg, x);
					}

					
					Message emptyMessage = formtaionOfEmptyMessage(MessageCategory.NULL);
					for (Edge x : Main.basicEdges) {
						messageSender(emptyMessage, x);
					}
				} else { 
					logger.fine("Local mwoe of the leader is minimum in this component");
					Message mwoe_appendMsg = fomrationOfAppendMwoeMsg(MessageCategory.ADD_MWOE, selfMwoe);
					for (Edge x : Main.branchEdges) {
						messageSender(mwoe_appendMsg, x);
					}
					currentEdgeAdditionToBranch(selfMwoe); 
					Message info = formtaionOfmsgJoin(MessageCategory.JOIN);
					messageSender(info, selfMwoe);
					isCurrentMwoe = true;
					objectReplication(selfMwoe, isEdgeALeader);
					logger.fine("Prospective leader candidate -> "+ isEdgeALeader.toString());
					Message emptyMessage = formtaionOfEmptyMessage(MessageCategory.NULL);
					for (Edge x : Main.basicEdges) {
						messageSender(emptyMessage, x);
					}
				}
			} else {
				
				boolean isAppendMsgObtained = false;
				Message mwoe_appendMsg = new Message(MessageCategory.ADD_MWOE);
				while (!isAppendMsgObtained) {
					if (!Main.buffer.isEmpty()) {
						for (Message info : Main.buffer) {
							if (isAppendMsgObtained) {
								break;
							}
							if (info.getMsgType().equals(MessageCategory.ADD_MWOE)) {
								logger.fine("AddMwoe-message Obtained ");
								isAppendMsgObtained = true;
								mwoe_appendMsg = fomrationOfAppendMwoeMsg(MessageCategory.ADD_MWOE, info.getMwoeEdge());
								logger.fine("Transmission of addMwoe-message to children");
								for (Edge x : Main.branchEdges) {
									if (areEdgesEqual(info.getCurrentEdge(), x)) {
										continue;
									}
									messageSender(info, x);
								}
								Main.buffer.remove(info);
							} else {
								Main.buffer.offer(Main.buffer.poll());
							}
						}
						if (isAppendMsgObtained) {
							break;
						}
					}
					logger.fine("Anticipating a AddMwoe Notification");
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						logger.log(Level.SEVERE,"",e);
					}

				}
				if(mwoe_appendMsg.getMwoeEdge().getVertex1() == Integer.MAX_VALUE && mwoe_appendMsg.getMwoeEdge().getVertex2() == Integer.MAX_VALUE) {
					terminationDetection = true;
				}
				if(!terminationDetection) {
					if (mwoe_appendMsg.getMwoeEdge().getVertex1() == Main.uid
							|| mwoe_appendMsg.getMwoeEdge().getVertex2() == Main.uid) {
						logger.fine("Need to include component mwoe");
						
						Edge minWgtOutEdge = new Edge();
						for(Edge x : Main.basicEdges) {
							if(areEdgesEqual(x, mwoe_appendMsg.getMwoeEdge())) {
								objectReplication(x, minWgtOutEdge);
							}
						}
						
						currentEdgeAdditionToBranch(minWgtOutEdge);
						Message info = formtaionOfmsgJoin(MessageCategory.JOIN);
						messageSender(info, minWgtOutEdge);
						isCurrentMwoe = true;
						objectReplication(minWgtOutEdge, isEdgeALeader);
					}
					Message emptyMessage = formtaionOfEmptyMessage(MessageCategory.NULL);
					for (Edge x : Main.basicEdges) {
						messageSender(emptyMessage, x);
					}
				}
			} 
			if (terminationDetection) {
				break;
			}

			int emptyAppendMsgCount;
			if (isCurrentMwoe) {
				
				emptyAppendMsgCount = Main.basicEdges.size() + 1;
			} else {
				emptyAppendMsgCount = Main.basicEdges.size();
			}
			logger.fine("count of empty join messages -> " + emptyAppendMsgCount);

			while (emptyAppendMsgCount > 0) {
				if (!Main.buffer.isEmpty()) {
					for (Message info : Main.buffer) {
						if (emptyAppendMsgCount == 0) {
							break;
						}
						if (info.getMsgType().equals(MessageCategory.NULL)) {
							emptyAppendMsgCount--;
							Main.buffer.remove(info);
						} else if (info.getMsgType().equals(MessageCategory.JOIN)) {
							Edge edgeAppend = new Edge();
							for(Edge x : Main.basicEdges) {
								if(areEdgesEqual(x, info.getCurrentEdge())) {
									objectReplication(x, edgeAppend);
								}
							}
							if(edgeAppend.getVertex1() == edgeAppend.getVertex2()) {
								for(Edge x : Main.branchEdges) {
									if(areEdgesEqual(x, info.getCurrentEdge())) {
										objectReplication(x, edgeAppend);
									}
								}
							}
							logger.fine("Join-Edge..." + edgeAppend.toString());
							currentEdgeAdditionToBranch(edgeAppend);
							emptyAppendMsgCount--;
							boolean isEdgeMain = areEdgesEqual(isEdgeALeader, edgeAppend);
							if (isEdgeMain) {
								logger.fine("On core edge..");
								objectReplication(edgeAppend, importantEdge);
								ispartOfresultantEdege = true;
							}
							Main.buffer.remove(info);
						} else {
							Main.buffer.offer(Main.buffer.poll());
						}
					}
					if (emptyAppendMsgCount == 0) {
						break;
					}
				}
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					logger.log(Level.SEVERE,"",e);
				}
			}

			

			if (ispartOfresultantEdege) {
				
				Main.leaderId = importantEdge.getVertex2();
				Message currentLeaderMsg = createNewLeaderMsg(MessageCategory.NEW_LEADER, Main.leaderId);
				logger.fine("New elected leader message broadcast");
				for (Edge x : Main.branchEdges) {
					if (areEdgesEqual(importantEdge, x)) {
						continue;
					}
					messageSender(currentLeaderMsg, x);
				}
			} else {
				boolean iscurrentLeaderMsgObtained = false;

				while (!iscurrentLeaderMsgObtained) {
					if (!Main.buffer.isEmpty()) {
						for (Message info : Main.buffer) {
							if (iscurrentLeaderMsgObtained) {
								break;
							}
							if (info.getMsgType().equals(MessageCategory.NEW_LEADER)) {
								iscurrentLeaderMsgObtained = true;
								Main.leaderId = info.getNewLeaderId();
								objectReplication(info.getCurrentEdge(), infoGivenByEdge);
								for (Edge x : Main.branchEdges) {
									if (areEdgesEqual(infoGivenByEdge, x)) {
										continue;
									}
									messageSender(info, x);
								}
								Main.buffer.remove(info);
							} else {
								Main.buffer.offer(Main.buffer.poll());
							}
						}
						if (iscurrentLeaderMsgObtained) {
							break;
						}
					}
					logger.fine("Anticipating New elected leader Notification");
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						logger.log(Level.SEVERE,"",e);
					}
				}
			}
			Main.phase.incrementAndGet(); 
			
			for(Message info : Main.buffer) {
				if(info.msgType.equals(MessageCategory.EXAMINE) && info.getPhaseNo()==Main.phase.intValue()) {
					Edge edgeInspection = new Edge();
					Message message;
					for(Edge x : Main.basicEdges) {
						if(areEdgesEqual(x, info.getCurrentEdge())) {
							objectReplication(x, edgeInspection);
						}
					}
					if(edgeInspection.getVertex1() == edgeInspection.getVertex2()) {
						for(Edge x : Main.branchEdges) {
							if(areEdgesEqual(x, info.getCurrentEdge())) {
								objectReplication(x, edgeInspection);
							}
						}
					}
					if(edgeInspection.getVertex1() == edgeInspection.getVertex2()) {
						for(Edge x : Main.rejectEdges) {
							
							if(areEdgesEqual(x, info.getCurrentEdge())) {
								objectReplication(x, edgeInspection);
							}
						}
					}
					if(info.getLeaderId() == Main.leaderId) {
						appendingToRejectedList(edgeInspection);
						message = formationOfInspectAckMsg(MessageCategory.EXAMINE_RESPONSE, "REJECT");
					} else {
						message = formationOfInspectAckMsg(MessageCategory.EXAMINE_RESPONSE, "ACCEPT");
					}
					messageSender(message, edgeInspection);
				}
			}
			
		} 
		logger.info("My ID: " + Main.uid);
		logger.info("*****Obtained Result is***** : " + Main.branchEdges.toString());
	} 	

	private Message formationOfInspectAckMsg(MessageCategory  mc, String reply) {
		Message info = new Message( mc);
		info.setLeaderId(Main.leaderId);
		info.setExamineResponse(reply);
		return info;
	}


	private Message fomrationOfAppendMwoeMsg(MessageCategory  mc, Edge acquired_mwoe) {
		Message info = new Message( mc);
		info.setLeaderId(Main.leaderId);
		info.setMwoeEdge(acquired_mwoe);
		return info;
	}	

}
