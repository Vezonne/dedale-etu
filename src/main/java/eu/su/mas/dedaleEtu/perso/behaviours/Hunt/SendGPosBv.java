package eu.su.mas.dedaleEtu.perso.behaviours.Hunt;

import java.util.List;

import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;


public class SendGPosBv extends OneShotBehaviour{
    private static final long serialVersionUID = 1L;

    private List<String> receivers;
    private String gPos;  

    public SendGPosBv(Agent a, List<String> receivers, String gPos) {
        super(a);
        this.receivers = receivers;
        this.gPos = gPos;
    }

    @Override
    public void action() {
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setSender(myAgent.getAID());
        msg.setProtocol("SHARE-GPOS");
        for(String agent : receivers){
            msg.addReceiver(new AID(agent, AID.ISLOCALNAME));
        }
        
        msg.setContent(gPos);

        ((AbstractDedaleAgent)this.myAgent).sendMessage(msg);
        System.out.println(this.myAgent.getLocalName() + " : sent loc to " + receivers);
    }
}
