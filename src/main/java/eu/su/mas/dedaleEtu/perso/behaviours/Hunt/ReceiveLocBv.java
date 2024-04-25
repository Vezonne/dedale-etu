package eu.su.mas.dedaleEtu.perso.behaviours.Hunt;

import eu.su.mas.dedale.env.Location;
import eu.su.mas.dedale.env.gs.gsLocation;
import eu.su.mas.dedaleEtu.perso.knowledge.AgentsLoc;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.core.behaviours.WakerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

public class ReceiveLocBv extends OneShotBehaviour{

    private static final long serialVersionUID = 1L;

    private long waitingTime;
    private AgentsLoc loclist;

    public ReceiveLocBv(Agent a, AgentsLoc loclist, long waitingTime) {
        super(a);
        this.waitingTime = waitingTime;
        this.loclist = loclist;
    }

    @Override
    public void action() {
        this.block(waitingTime);
        System.out.println(this.myAgent.getLocalName() + " : cheking for loc messages");
        MessageTemplate msgTemplate = MessageTemplate.and(
            MessageTemplate.MatchProtocol("SHARE-LOC"), 
            MessageTemplate.MatchPerformative(ACLMessage.INFORM));
        // this.myAgent.doWait(this.time);
        ACLMessage msg = this.myAgent.receive(msgTemplate);
        while(msg != null){
            Location loc = new gsLocation(msg.getContent());
            this.loclist.updateAgentLocation(msg.getSender().getLocalName(), loc);
            System.out.println(this.myAgent.getLocalName() + " : received loc from " + msg.getSender().getLocalName());
        }
    }
    
}
