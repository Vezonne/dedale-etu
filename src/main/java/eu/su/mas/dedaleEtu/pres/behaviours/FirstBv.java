package eu.su.mas.dedaleEtu.pres.behaviours;

import java.util.List;
import java.util.Iterator;

import dataStructures.serializableGraph.SerializableSimpleGraph;
import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Location;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.env.gs.gsLocation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;

import eu.su.mas.dedale.mas.agent.knowledge.MapRepresentation;
import eu.su.mas.dedale.mas.agent.knowledge.MapRepresentation.MapAttribute;
import eu.su.mas.dedaleEtu.pres.behaviours.ShareMap.FirstShareMapBv;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

public class FirstBv extends SimpleBehaviour {

    private static final long serialVersionUID = 1L;

    private boolean finished = false;
    private MapRepresentation myMap;
    private List<String> list_agentNames;

    public FirstBv(final AbstractDedaleAgent myAgent, MapRepresentation myMap, List<String> agentNames) {
        super(myAgent);
        this.myMap = myMap;
        this.list_agentNames = agentNames;
    }

    @Override
    public void action() {

        if(this.myMap == null){
            this.myMap = new MapRepresentation();
            this.myAgent.addBehaviour(new FirstShareMapBv(this.myAgent, 500, this.myMap, list_agentNames));
        }

        Location myPosition = ((AbstractDedaleAgent)this.myAgent).getCurrentPosition();

        if (myPosition!=null){
            List<Couple<Location,List<Couple<Observation,Integer>>>> lobs=((AbstractDedaleAgent)this.myAgent).observe();

            try {
                this.myAgent.doWait(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }

            this.myMap.addNode(myPosition.getLocationId(), MapAttribute.closed);

            String nextNodeId = null;
            Iterator<Couple<Location,List<Couple<Observation,Integer>>>> iter = lobs.iterator();
            while (iter.hasNext()) {
                Location accessibleNode = iter.next().getLeft();
                boolean isNewNode = this.myMap.addNewNode(accessibleNode.getLocationId());
                if (myPosition.getLocationId() != accessibleNode.getLocationId() && isNewNode) {
                    this.myMap.addEdge(myPosition.getLocationId(), accessibleNode.getLocationId());
                    if (nextNodeId == null) nextNodeId = accessibleNode.getLocationId();
                }
            }

            if (!this.myMap.hasOpenNode()){
                this.finished = true;
                System.out.println(this.myAgent.getLocalName() + " - Exploration successfully done, behaviour removed. ");
            }else{
                if (nextNodeId == null) {
                    nextNodeId = this.myMap.getShortestPathToClosestOpenNode(myPosition.getLocationId()).get(0);
                }

                MessageTemplate msgTemplate = MessageTemplate.and(
                    MessageTemplate.MatchProtocol("SHARE-TOPO"), 
                    MessageTemplate.MatchPerformative(ACLMessage.INFORM));
                ACLMessage msgReceived = this.myAgent.receive(msgTemplate);
                if (msgReceived!= null) {
                    SerializableSimpleGraph<String,MapAttribute> sgreceived = null;
                    try{
                        sgreceived = (SerializableSimpleGraph<String,MapAttribute>) msgReceived.getContentObject();
                    }catch(UnreadableException e){
                        e.printStackTrace();
                    }
                    this.myMap.mergeMap(sgreceived);
                }

                ((AbstractDedaleAgent)this.myAgent).moveTo(new gsLocation(nextNodeId));
            }
        }
    }

    @Override
    public boolean done() {
        return finished;
    }

}
