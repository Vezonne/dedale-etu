package eu.su.mas.dedaleEtu.perso.behaviours.Hunt;

import eu.su.mas.dedale.env.Location;
import eu.su.mas.dedaleEtu.perso.knowledge.AgentsLoc;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

public class ReceiveLocBv extends OneShotBehaviour{

    private static final long serialVersionUID = 1L;

    private AgentsLoc loclist;
    private long waitingTime = 100;

    public ReceiveLocBv(Agent a, AgentsLoc loclist, long waitingTime) {
        super(a);
        this.loclist = loclist;
        this.waitingTime = waitingTime;
    }

    @Override
        public void action() {
        MessageTemplate msgTemplate = MessageTemplate.and(
            MessageTemplate.MatchProtocol("SHARE-LOC"), 
            MessageTemplate.MatchPerformative(ACLMessage.INFORM));
        this.myAgent.doWait(this.waitingTime);
        ACLMessage msg = this.myAgent.receive(msgTemplate);
        if (msg != null) {
            Location loc = null;
            try{
                loc = (Location) msg.getContentObject();
            }catch(UnreadableException e){
                e.printStackTrace();
            }
            this.loclist.updateAgentLocation(msg.getSender().getLocalName(), loc);
            System.out.println(this.myAgent.getLocalName() + " : received loc from " + msg.getSender().getLocalName());
        }
    }
    
}
