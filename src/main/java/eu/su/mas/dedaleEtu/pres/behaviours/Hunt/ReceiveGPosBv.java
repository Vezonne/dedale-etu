package eu.su.mas.dedaleEtu.pres.behaviours.Hunt;

import eu.su.mas.dedaleEtu.pres.knowledge.AgentsLoc;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class ReceiveGPosBv extends OneShotBehaviour{

    private static final long serialVersionUID = 1L;

    private AgentsLoc agentsloc;
    private long waitingTime;
    private int exitValue;

    public ReceiveGPosBv(Agent a, AgentsLoc agentsLoc, long waitingTime) {
        super(a);
        this.agentsloc = agentsLoc;
        this.waitingTime = waitingTime;
    }

    @Override
        public void action() {
        exitValue = 0;
        
        MessageTemplate msgTemplate = MessageTemplate.and(
            MessageTemplate.MatchProtocol("SHARE-GPOS"), 
            MessageTemplate.MatchPerformative(ACLMessage.INFORM));

        try {
            this.myAgent.doWait(waitingTime);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ACLMessage msg = this.myAgent.receive(msgTemplate);

        if (msg != null) {
            String gPos = msg.getContent();
            this.agentsloc.setGPos(gPos);
            System.out.println(this.myAgent.getLocalName() + " : received gPos: " + gPos + " from " + msg.getSender().getLocalName());
            this.agentsloc.setDest(gPos);
            exitValue = 1;
        }
    }

    @Override
        public int onEnd() {
        return exitValue;
    }
    
}
