package eu.su.mas.dedaleEtu.perso.behaviours.Hunt;

import java.util.List;
import java.util.Iterator;

import dataStructures.serializableGraph.SerializableSimpleGraph;
import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Location;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.env.gs.gsLocation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;

import eu.su.mas.dedaleEtu.perso.knowledge.MapRepresentation;
import eu.su.mas.dedaleEtu.perso.knowledge.MapRepresentation.MapAttribute;
import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

public class HuntBv extends SimpleBehaviour {

    private static final long serialVersionUID = 1L;
    private boolean finished = false;
    private boolean isReachable = false;
    private MapRepresentation myMap;
    private List<String> list_agentNames;

    public HuntBv(final Agent myAgent, MapRepresentation myMap, List<String> list_agentNames) {
        super(myAgent);
        this.myMap = myMap;
        this.list_agentNames = list_agentNames;
    }

    @Override
    public void action() {

        Location myPosition=((AbstractDedaleAgent)this.myAgent).getCurrentPosition();

        if (myPosition!=null){
			//List of observable from the agent's current position
			List<Couple<Location,List<Couple<Observation,Integer>>>> lobs=((AbstractDedaleAgent)this.myAgent).observe();

            try {
				this.myAgent.doWait(500);
			} catch (Exception e) {
				e.printStackTrace();
			}

			//1) remove the current node from openlist and add it to closedNodes.
			this.myMap.addNode(myPosition.getLocationId(), MapAttribute.closed);

            String nextNodeId=null;
			Iterator<Couple<Location, List<Couple<Observation, Integer>>>> iter=lobs.iterator();
			while(iter.hasNext()){
				Location accessibleNode=iter.next().getLeft();
				boolean isNewNode=this.myMap.addNewNode(accessibleNode.getLocationId());
				//the node may exist, but not necessarily the edge
				if (myPosition.getLocationId()!=accessibleNode.getLocationId()) {
					this.myMap.addEdge(myPosition.getLocationId(), accessibleNode.getLocationId());
					if (nextNodeId==null && isNewNode) nextNodeId=accessibleNode.getLocationId();
				}
			}

            if (!this.myMap.hasOpenNode()){
				//Explo finished
				finished=true;
				System.out.println(this.myAgent.getLocalName()+" - Exploration successufully done, behaviour removed.");
			}else{
                if (nextNodeId==null){
					//no directly accessible openNode
					//chose one, compute the path and take the first step.
					nextNodeId=this.myMap.getShortestPathToClosestOpenNode(myPosition.getLocationId()).get(0);

                }

                MessageTemplate msgTemplate=MessageTemplate.and(
						MessageTemplate.MatchProtocol("SHARE-TOPO"),
						MessageTemplate.MatchPerformative(ACLMessage.INFORM));
				ACLMessage msgReceived=this.myAgent.receive(msgTemplate);
				if (msgReceived!=null) {
					SerializableSimpleGraph<String, MapAttribute> sgreceived=null;
					try {
						sgreceived = (SerializableSimpleGraph<String, MapAttribute>)msgReceived.getContentObject();
					} catch (UnreadableException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					this.myMap.mergeMap(sgreceived);
				}

				for(int i = 0; i < 5; i++){
                    if(((AbstractDedaleAgent)this.myAgent).moveTo(new gsLocation(nextNodeId))) {
                        isReachable = true;
                        //((AbstractDedaleAgent)this.myAgent).moveTo(new gsLocation(nextNodeId));
                    }
                }
                
                if(!isReachable) {
                    
                }
			}
        }
	}
        

    @Override
    public boolean done() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'done'");
    }
    

}