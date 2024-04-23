package eu.su.mas.dedaleEtu.perso.behaviours.ShareMap;

import java.io.IOException;
import java.util.List;

import dataStructures.serializableGraph.SerializableSimpleGraph;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.perso.knowledge.MapRepresentation;
import eu.su.mas.dedaleEtu.perso.knowledge.MapRepresentation.MapAttribute;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

public class SendMapBv extends OneShotBehaviour{

    private static final long serialVersionUID = 1L;

    private MapRepresentation myMap;
    private List<String> receivers;  

    public SendMapBv(Agent a, MapRepresentation myMap, List<String> receivers) {
        super(a);
        this.myMap = myMap;
        this.receivers = receivers;
    }

    @Override
    public void action() {
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setSender(myAgent.getAID());
        msg.setProtocol("SHARE-MAP");
        for(String agent : receivers){
            msg.addReceiver(new AID(agent, AID.ISLOCALNAME));
        }

        SerializableSimpleGraph<String, MapAttribute> sg = this.myMap.getSerializableGraph();

        try {
            msg.setContentObject(sg);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ((AbstractDedaleAgent)this.myAgent).sendMessage(msg);
        System.out.println(this.myAgent.getLocalName() + " : sent map to " + receivers);
        System.out.println(this.myAgent.getLocalName() + " : clear receivers");
        receivers.clear();
    }
    
}
