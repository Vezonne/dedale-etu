package eu.su.mas.dedaleEtu.perso.behaviours.Hunt;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Location;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.agents.dummies.explo.ExploreCoopAgent;
import eu.su.mas.dedaleEtu.perso.knowledge.MapRepresentation;
import eu.su.mas.dedaleEtu.perso.knowledge.MapRepresentation.MapAttribute;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

public class HuntWumpusBehaviour extends OneShotBehaviour{
	/**
	 * 
	 */
	private static final long serialVersionUID = 267531971032817028L;

	private MapRepresentation myMap;
	
	private String nodeGoal = "";
	private int timer, nb_move_fail;
	private double max_move_fail=1;
	private String oldNode="";
	private boolean firstTime=true;
	private List<String> temp;
	private List<String> leavePath;
	private String receiveAgentName;
	private boolean modeLeavePath = false;
	private boolean SuccessMove;

	private int exitValue;
	
/**
 * 
 * @param myAgent
 * @param myMap known map of the world the agent is living in
 * @param agentNames name of the agents to share the map with
 */
	public HuntWumpusBehaviour(Agent myAgent, MapRepresentation myMap, List<String> agentNames) {
		super(myAgent);
		this.myMap=myMap;
		this.timer=timer;
	}

	@Override
	public void action() {
		this.exitValue = 0;
		//If Someone have a special Mision, I will try to not block him
		if(modeLeavePath) {
			//If I'm not on the path i will juste stop a few time
			if(!leavePath.contains(((AbstractDedaleAgent)this.myAgent).getCurrentPosition())) {
				try {
					this.myAgent.doWait(((AgentFSM)this.myAgent).AgentSpeed * 3);
				} catch (Exception e) {
					e.printStackTrace();
				}
				modeLeavePath = false;
				leavePath= null;
				return ; 
			}
			Location myPosition=((AbstractDedaleAgent)this.myAgent).getCurrentPosition();

			List<Couple<Location,List<Couple<Observation,Integer>>>> lobs=((AbstractDedaleAgent)this.myAgent).observe();//myPosition

			//1) remove the current node from openlist and add it to closedNodes.
			this.myMap.addNode(myPosition.getLocationId(), MapAttribute.closed);

			//2) get the surrounding nodes and, if not in closedNodes, add them to open nodes.
			String nextNode=null;
			Iterator<Couple<String, List<Couple<Observation, Integer>>>> iter=lobs.iterator();
			while(iter.hasNext()){
				Couple<String, List<Couple<Observation, Integer>>> node = iter.next();
				String nodeId= node.getLeft();

				if (myPosition.getLocationId()!=nodeId) {
					this.myMap.addEdge(myPosition.getLocationId(), nodeId);
					if (nextNode==null && leavePath.contains(nodeId)) nextNode=nodeId;
				}
			}
			//If no nextNode finded i will just says to him
			if (nextNode == null) {
				System.out.println(this.myAgent.getLocalName()+" ---> Sorry I'm not smart enough to find a knot to let you through");
				sendSorryMsg(); // Send of the Sorry msg
				modeLeavePath = false;
				leavePath= null;
				try {
					this.myAgent.doWait(((AgentFSM)this.myAgent).AgentSpeed * 3);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return ; 
			}else{
				
				((AgentFSM)this.myAgent).nextNode=nextNode;
				((AgentFSM)this.myAgent).updateMap(this.myMap);
				SuccessMove = ((AbstractDedaleAgent)this.myAgent).moveTo(nextNode);	
				
				if(SuccessMove) {
					System.out.println(this.myAgent.getLocalName()+" ---> I find a node to back ! Good luck to your important mision ");
					modeLeavePath = false;
					leavePath= null;
				}else {
					System.out.println(this.myAgent.getLocalName()+" ---> Sorry I find a node to back, but something block me ");
					sendSorryMsg();
					modeLeavePath = false;
					leavePath= null;
				}
			}
			return ;
		}
		//If I receive a special mission to MoveTo, go to MoveTo protocole
		if (((AgentFSM)this.myAgent).moveTo != null) {
			this.exitValue = 5;
		}
		//Go to check
		if(((AgentFSM)this.myAgent).needToCheck) {
			((AgentFSM)this.myAgent).needToCheck = false;
			this.exitValue = 3;
			return ;
		}
		
		if (firstTime && ((AgentFSM)this.myAgent).endExplo) {
			firstTime=false;
			System.out.println(this.myAgent.getLocalName()+" Start Chase !");
		}
		//Check every msg
		if(checkMsg()) {
			//clearMail();
			return ;
		}

		this.myMap = ((AgentFSM)this.myAgent).getMap();
		
		if (this.myMap.hasOpenNode()){
			this.exitValue = 8;//Back to exploration
			System.out.println(this.myAgent.getLocalName()+" ---> Exploration not done, go back to exploration");
			return ;
		}

		//0) Retrieve the current position
		String myPosition=((AbstractDedaleAgent)this.myAgent).getCurrentPosition();
		
		if (myPosition.equals(((AgentFSM)this.myAgent).moveTo)) {
			((AgentFSM)this.myAgent).moveTo = null;
		}

		if (myPosition!=null){
			//List of observable from the agent's current position
			List<Couple<String,List<Couple<Observation,Integer>>>> lobs=((AbstractDedaleAgent)this.myAgent).observe();//myPosition

			/**
			 * Just added here to let you see what the agent is doing, otherwise he will be too quick
			 */
			try {
				this.myAgent.doWait(((AgentFSM)this.myAgent).AgentSpeed);
			} catch (Exception e) {
				e.printStackTrace();
			}

			//1) remove the current node from openlist and add it to closedNodes.
			this.myMap.addNode(myPosition, MapAttribute.closed);
			
			//2) get the surrounding nodes and, if we a Wumpus noise 
			String nextNode=null;
			Iterator<Couple<String, List<Couple<Observation, Integer>>>> iter=lobs.iterator();
			while(iter.hasNext()){
				Couple<String, List<Couple<Observation, Integer>>> node = iter.next();
				String nodeId= node.getLeft();
				List<Couple<Observation, Integer>> list = node.getRight();
				if (myPosition!=nodeId && nodeId!=oldNode && list.size()>0 ) {
					if (nextNode==null && !((AgentFSM)this.myAgent).GolemPoop.contains(nextNode)) nextNode=nodeId;
				}
			}
			//If I m back from ImNotWumpus exitValue = 1, I have a very high chance to block someone, so I change my nodeGoal
			int lastExit = ((AgentFSM)this.myAgent).getFSM().getLastExitValue();
			if (lastExit == 1) {
				nodeGoal = "";
			}

			if (nextNode==null || lastExit == 1 || ((AgentFSM)this.myAgent).forceChangeNode){
				((AgentFSM)this.myAgent).forceChangeNode = false;
				while(nodeGoal.equals("") || myPosition.equals(nodeGoal)) {
					List<String> closednodes=this.myMap.getClosedNodes();
					Random rand = new Random();
					nodeGoal = closednodes.get(rand.nextInt(closednodes.size()));
					System.out.println(this.myAgent.getLocalName()+" ---> Init a new nodeGoal("+nodeGoal+") to search Golem");
				}
				temp = this.myMap.getShortestPath(myPosition, nodeGoal, ((AgentFSM)this.myAgent).blockedAgent);
				if (temp != null) {
					if( temp.size()>0 ) {
						nextNode = this.myMap.getShortestPath(myPosition, nodeGoal, ((AgentFSM)this.myAgent).blockedAgent).get(0);
					}else {
						((AgentFSM)this.myAgent).blockedAgent.clear();
						return; //reset
					}
				}else {
					((AgentFSM)this.myAgent).blockedAgent.clear();
					return ;//reset
				}
				if(nextNode.equals(nodeGoal)) {
					nodeGoal = "";
					System.out.println(this.myAgent.getLocalName()+" ---> Arrived to nodeGoal("+nodeGoal+")");
				}

			}else {
				//System.out.println(this.myAgent.getLocalName()+" ---> Find a Golem's poop");
			}

			((AgentFSM)this.myAgent).nextNode=nextNode;
			((AgentFSM)this.myAgent).updateMap(this.myMap);
			SuccessMove = ((AbstractDedaleAgent)this.myAgent).moveTo(nextNode);
		}
		
		
		if (!SuccessMove) {
			//mas_move_fail = the Agent sensibility
			if ( nb_move_fail >= max_move_fail) {
				nb_move_fail = 0;
				System.out.println(this.myAgent.getLocalName() + " --> Something block me ! (stop move)");
				this.exitValue = 3;
				return ;
			}
			nb_move_fail++;
			//System.out.println(this.myAgent.getLocalName()+" : nb_move = "+nb_move_fail);
		}else {
			oldNode = myPosition;
			nb_move_fail = 0;
		}
		
	}
	
	//check msg	
	public boolean checkMsg() {
		
    	final MessageTemplate msgSomeone = MessageTemplate.and(MessageTemplate.MatchProtocol("ProtocoleSomeone"),
				MessageTemplate.MatchPerformative(ACLMessage.INFORM));	
    	final ACLMessage msgS = this.myAgent.receive(msgSomeone);
    	
    	if (msgS != null) {
    		String m =msgS.getContent();//sender next node
    		//If his nextNode is my current pos
    		if ( m.equals(((AbstractDedaleAgent)this.myAgent).getCurrentPosition())) {
        		System.out.println(this.myAgent.getLocalName() + " --> Receive a check Someone msg");
        		((AgentFSM)this.myAgent).agentToContact = msgS.getSender().getLocalName();
        		exitValue = 7;
        		return true;
    		}
    	}
		
		while(true) {
			MessageTemplate msgTemplatelp=MessageTemplate.and(
					MessageTemplate.MatchProtocol("ProtocoleLeavePath"),
					MessageTemplate.MatchPerformative(ACLMessage.INFORM));
		
			ACLMessage msglp=this.myAgent.receive(msgTemplatelp);
			
			if(msglp!=null) {
				try {
					leavePath = (List<String>) msglp.getContentObject();
				} catch (UnreadableException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				receiveAgentName = msglp.getSender().getLocalName();
				modeLeavePath  = true;
			}
			else {
				break;
			}
		}
		if (modeLeavePath) {
			return true;
		}

		final MessageTemplate msgTemplate = MessageTemplate.and(MessageTemplate.MatchProtocol("ProtocolePoke"), 
				MessageTemplate.MatchPerformative(ACLMessage.INFORM) );	
		final ACLMessage msg = this.myAgent.receive(msgTemplate);

		//If receive a message, don't move
		if (msg != null) {
			System.out.println(this.myAgent.getLocalName() + " --> Receive a poke (stop move)");
			this.exitValue = 4;//Go to share map
			return false;
		}

		final MessageTemplate msgTemplateBlock = MessageTemplate.and(MessageTemplate.MatchProtocol("ProtocoleHelpBlockWumpus"),
				MessageTemplate.MatchPerformative(ACLMessage.INFORM));	
    	final ACLMessage msgBlock = this.myAgent.receive(msgTemplateBlock);
    	
    	if (msgBlock != null) {
    		List<String> NodeToBlock = new ArrayList<String>();
			try {
				NodeToBlock = (List<String>) msgBlock.getContentObject();
			} catch (UnreadableException e) {
				e.printStackTrace();			
			}

			((AgentFSM)this.myAgent).NodeToBlock = NodeToBlock;
			String AgentPos = ((AgentFSM)this.myAgent).NodeToBlock.get(0);
			((AgentFSM)this.myAgent).blockedAgent.add(AgentPos);
			((AgentFSM)this.myAgent).NodeToBlock.remove(0);
			
			String AgentNextPos = ((AgentFSM)this.myAgent).NodeToBlock.get(0);
			((AgentFSM)this.myAgent).NodeToBlock.remove(0);
			
			//If I'm already on a NodeToBlock
			if(((AgentFSM)this.myAgent).NodeToBlock.contains(((AbstractDedaleAgent)this.myAgent).getCurrentPosition())) {
				System.out.println(this.myAgent.getLocalName() + " --> I'm already on the Node to block ("+((AbstractDedaleAgent)this.myAgent).getCurrentPosition()+")");
				((AgentFSM)this.myAgent).nextNode = AgentNextPos;
				((AgentFSM)this.myAgent).NodeToBlock.remove(((AbstractDedaleAgent)this.myAgent).getCurrentPosition());
				
				exitValue = 6;
				return true;
			}
			//If the Sender want to come to my position
			if(AgentNextPos.equals(((AbstractDedaleAgent)this.myAgent).getCurrentPosition())) {
				ACLMessage sendMsg=new ACLMessage(ACLMessage.INFORM);
				sendMsg.setProtocol("ProtocoleByPass");
				sendMsg.setSender(this.myAgent.getAID());
				sendMsg.setContent(((AbstractDedaleAgent)this.myAgent).getCurrentPosition());
		
				sendMsg.addReceiver(new AID(msgBlock.getSender().getLocalName(),AID.ISLOCALNAME));

				((AbstractDedaleAgent)this.myAgent).sendMessage(sendMsg);
				
				System.out.println(this.myAgent.getLocalName() + " --> I'm not a Wumpus "+AgentNextPos+" ! (ExploCoopBehaviour)");
				return false;
			}
			/*
			if(!AgentNextPos.equals(((fsmAgent)this.myAgent).nextNode)) {
				System.out.println(this.myAgent.getLocalName() + " --> Ignore it's didn't concern me at "+AgentNextPos+" (ExploCoopBehaviour)");
				return false;
			}*/
			//Go to the NodeToBlock on MoveTo
			System.out.println(this.myAgent.getLocalName() + " --> NodeToBlock "+((AgentFSM)this.myAgent).NodeToBlock+" (ExploCoopBehaviour)");
			if(((AgentFSM)this.myAgent).NodeToBlock.size()>0) {
				((AgentFSM)this.myAgent).moveTo = ((AgentFSM)this.myAgent).NodeToBlock.get(0);
				((AgentFSM)this.myAgent).NodeToBlock.remove(0);
				((AgentFSM)this.myAgent).blockedAgent.add(AgentNextPos);
				((AgentFSM)this.myAgent).WumpusPos = AgentNextPos;
				System.out.println(this.myAgent.getLocalName() + " --> Go to MoveToBehaviour "+((AgentFSM)this.myAgent).moveTo);
				exitValue = 5;
				return true;
			}
			return false;
			
    	}
    	
    	final MessageTemplate msgBy = MessageTemplate.and(MessageTemplate.MatchProtocol("ProtocoleByPass"),
				MessageTemplate.MatchPerformative(ACLMessage.INFORM));	
    	final ACLMessage msgB = this.myAgent.receive(msgBy);
    	//Receive a msg whit the position of the agent where he is blocked
    	if (msgB != null) {
    		String m =msgB.getContent();
    		if ( m.equals(((AgentFSM)this.myAgent).nextNode)) {
        		System.out.println(this.myAgent.getLocalName() + " --> Ok it's not a Golem, I will try to by pass you "+msgB.getSender().getLocalName());
        		((AgentFSM)this.myAgent).blockedAgent.add(m);
        		return false;
    		}
    	}
    	
		return false;
	}
	
	public void sendSorryMsg() {
		if (receiveAgentName != null) {
			ACLMessage sendMsg=new ACLMessage(ACLMessage.INFORM);
			sendMsg.setProtocol("ProtocoleSorry");
			sendMsg.setSender(this.myAgent.getAID());

			sendMsg.setContent(((AbstractDedaleAgent)this.myAgent).getCurrentPosition());

			sendMsg.addReceiver(new AID(receiveAgentName,AID.ISLOCALNAME));

			//Mandatory to use this method (it takes into account the environment to decide if someone is reachable or not)
			((AbstractDedaleAgent)this.myAgent).sendMessage(sendMsg);
		}
	}
	public void clearMail() {
		while(true) {
			MessageTemplate msgTemplate= MessageTemplate.MatchPerformative(ACLMessage.INFORM);
			ACLMessage msg=this.myAgent.receive(msgTemplate);
			
			if(msg == null) {
				break;
			}
		}
	}
	@Override
	public int onEnd() {
		return exitValue;
	}

}