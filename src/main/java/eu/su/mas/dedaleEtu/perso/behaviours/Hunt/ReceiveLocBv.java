package eu.su.mas.dedaleEtu.perso.behaviours.Hunt;

import eu.su.mas.dedale.env.Location;
import eu.su.mas.dedale.env.gs.gsLocation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.perso.knowledge.AgentsLoc;
import eu.su.mas.dedaleEtu.perso.knowledge.MapRepresentation;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class ReceiveLocBv extends OneShotBehaviour{

    private static final long serialVersionUID = 1L;

    private MapRepresentation myMap;
    private long waitingTime;
    private AgentsLoc loclist;

    public ReceiveLocBv(Agent a, MapRepresentation myMap, AgentsLoc loclist, long waitingTime) {
        super(a);
        this.myMap = myMap;
        this.waitingTime = waitingTime;
        this.loclist = loclist;
    }

    @Override
    public void action() {
        this.block(waitingTime);
        loclist.checkAgentsInRange(((AbstractDedaleAgent)this.myAgent).getCurrentPosition(), this.myMap);
        // System.out.println(this.myAgent.getLocalName() + " : I know these loc " + this.loclist);

        MessageTemplate msgTemplate = MessageTemplate.and(
            MessageTemplate.MatchProtocol("SHARE-LOC"), 
            MessageTemplate.MatchPerformative(ACLMessage.INFORM));

        ACLMessage msg = this.myAgent.receive(msgTemplate);
        while(msg != null){
            Location loc = new gsLocation(msg.getContent());
            this.loclist.updateAgentLocation(msg.getSender().getLocalName(), loc);

            // System.out.println(this.myAgent.getLocalName() + " : received loc from " + msg.getSender().getLocalName());
            msg = this.myAgent.receive(msgTemplate);
        }
    }
    
}
