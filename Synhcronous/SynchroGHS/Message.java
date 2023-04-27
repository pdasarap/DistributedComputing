import java.io.*;

public class Message implements Serializable{
	
	int leaderId;
	MessageCategory msgType;
	String examineResponse;		//ACCEPT-if different leader	 or REJECT-if same leader
	Edge currentEdge;
	Edge mwoeEdge;
	int mwoeSourceId;			//the nodeId on the mwoe in own component
	int mwoeDestinationId;		//the nodeId on the mwoe in the other component
	int newLeaderId;
	int phaseNo;

	public Message(MessageCategory msgType) {
		this.msgType = msgType;
	}
	
	
	public int getLeaderId() {
		return leaderId;
	}

	public void setLeaderId(int leaderId) {
		this.leaderId = leaderId;
	}

	public MessageCategory getMsgType() {
		return msgType;
	}

	public void setMsgType(MessageCategory msgType) {
		this.msgType = msgType;
	}

	public String getExamineResponse() {
		return examineResponse;
	}

	public void setExamineResponse(String examineResponse) {
		this.examineResponse = examineResponse;
	}

	public Edge getCurrentEdge() {
		return currentEdge;
	}

	@Override
	public String toString() {
		return "Message [leaderId=" + leaderId + ", msgType=" + msgType + ", examineResponse=" + examineResponse
				+ ", currentEdge=" + currentEdge + ", mwoeEdge=" + mwoeEdge + ", newLeaderId=" + newLeaderId
				+ ", phaseNo=" + phaseNo + "]";
	}

	public void setCurrentEdge(Edge currentEdge) {
		this.currentEdge = currentEdge;
	}

	public Edge getMwoeEdge() {
		return mwoeEdge;
	}

	public void setMwoeEdge(Edge mwoeEdge) {
		this.mwoeEdge = mwoeEdge;
	}

	public int getMwoeSourceId() {
		return mwoeSourceId;
	}

	public void setMwoeSourceId(int mwoeSourceId) {
		this.mwoeSourceId = mwoeSourceId;
	}

	public int getMwoeDestinationId() {
		return mwoeDestinationId;
	}

	public void setMwoeDestinationId(int mwoeDestinationId) {
		this.mwoeDestinationId = mwoeDestinationId;
	}

	public int getNewLeaderId() {
		return newLeaderId;
	}

	public void setNewLeaderId(int newLeaderId) {
		this.newLeaderId = newLeaderId;
	}

	public int getPhaseNo() {
		return phaseNo;
	}

	public void setPhaseNo(int phaseNo) {
		this.phaseNo = phaseNo;
	}

	/**
	 * @param leaderId
	 * @param searchMwoe
	 */
	public Message(int leaderId, MessageCategory msgType) {
		this.leaderId = leaderId;
		this.msgType = msgType;
	}

	/**
	 * @param examineResponse2
	 * @param string
	 */
	public Message(MessageCategory msgType, String examineResponse) {
		this.msgType = msgType;
		this.examineResponse = examineResponse;
	}

	public Message createMsg(MessageCategory msgType) {
		switch (msgType) {
		case SEARCH_MWOE:
			return mwoeSeacrhingMessage();
		case EXAMINE:
			return examineMsg();
		case EXAMINE_RESPONSE:
			return examineResponseMsg();
		case REPLY_MWOE:
			return mwoe_ackMsg();
		case ADD_MWOE:
			return mwoe_appendMsg();
		case NEW_LEADER:
			return newLeaderMsg();
		default:
			return null;
		}
	}
	
	public Message createResponseMsg(Message msg) {
		if(msg.msgType.equals(MessageCategory.EXAMINE)){
			if(msg.leaderId == Main.leaderId){
				return new Message(MessageCategory.EXAMINE_RESPONSE, "REJECT");
			} else {
				return new Message(MessageCategory.EXAMINE_RESPONSE, "ACCEPT");
			}
		}
		return null;
	}

	/**
	 * @return
	 */
	private Message newLeaderMsg() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @return
	 */
	private Message mwoe_appendMsg() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @return
	 */
	private Message mwoe_ackMsg() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @return
	 */
	private Message examineResponseMsg() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @return
	 */
	private Message examineMsg() {
		return new Message(Main.leaderId, MessageCategory.SEARCH_MWOE);
	}

	/**
	 * @return
	 */
	private Message mwoeSeacrhingMessage() {
		return new Message(Main.leaderId, MessageCategory.SEARCH_MWOE);
	}
	
}
