package eu.su.mas.dedaleEtu.perso.behaviours.Hunt;

import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class ReceiveGPosBv extends OneShotBehaviour{

    private static final long serialVersionUID = 1L;

    private String gPos;
    private long waitingTime;
    private int exitValue;

    public ReceiveGPosBv(Agent a, String gPos, long waitingTime) {
        super(a);
        this.gPos = gPos;
        this.waitingTime = waitingTime;
    }

    @Override
        public void action() {
        exitValue = 0;
        
        MessageTemplate msgTemplate = MessageTemplate.and(
            MessageTemplate.MatchProtocol("SHARE-GPOS"), 
            MessageTemplate.MatchPerformative(ACLMessage.INFORM));

        this.myAgent.doWait(this.waitingTime);
        ACLMessage msg = this.myAgent.receive(msgTemplate);

        if (msg != null) {
            gPos = msg.getContent();
            System.out.println(this.myAgent.getLocalName() + " : received gPos: " + gPos + " from " + msg.getSender().getLocalName());
            exitValue = 1;
        }
    }

    @Override
        public int onEnd() {
        return exitValue;
    }
    
}
