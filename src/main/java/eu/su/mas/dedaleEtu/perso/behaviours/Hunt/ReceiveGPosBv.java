package eu.su.mas.dedaleEtu.perso.behaviours.Hunt;

import eu.su.mas.dedaleEtu.perso.agents.ExploPingA;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class ReceiveGPosBv extends OneShotBehaviour{

    private static final long serialVersionUID = 1L;

    private long waitingTime;
    private int exitValue;

    public ReceiveGPosBv(Agent a, long waitingTime) {
        super(a);
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
            ((ExploPingA)this.myAgent).setGPos(gPos);
            System.out.println(this.myAgent.getLocalName() + " : received gPos: " + gPos + " from " + msg.getSender().getLocalName());
            exitValue = 1;
        }
    }

    @Override
        public int onEnd() {
        return exitValue;
    }
    
}
