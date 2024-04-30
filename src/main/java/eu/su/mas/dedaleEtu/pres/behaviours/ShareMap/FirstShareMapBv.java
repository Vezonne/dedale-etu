package eu.su.mas.dedaleEtu.pres.behaviours.ShareMap;

import java.io.IOException;
import java.util.List;

import dataStructures.serializableGraph.SerializableSimpleGraph;

import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedale.mas.agent.knowledge.MapRepresentation;
import eu.su.mas.dedale.mas.agent.knowledge.MapRepresentation.MapAttribute;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;

public class FirstShareMapBv extends TickerBehaviour{

    private static final long serialVersionUID = 1L;

    private MapRepresentation myMap;
    private List<String> receivers;

    public FirstShareMapBv(Agent a, long period,MapRepresentation mymap, List<String> receivers) {
        super(a, period);
        this.myMap=mymap;
        this.receivers=receivers;
    }

    @Override
    protected void onTick(){
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setProtocol("SHARE-TOPO");
        msg.setSender(this.myAgent.getAID());
        for (String agent : receivers){
            msg.addReceiver(new AID(agent, AID.ISLOCALNAME));
        }

        SerializableSimpleGraph<String, MapAttribute> sg = this.myMap.getSerializableGraph();
        try {
            msg.setContentObject(sg);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ((AbstractDedaleAgent)this.myAgent).sendMessage(msg);
    }
    
}
