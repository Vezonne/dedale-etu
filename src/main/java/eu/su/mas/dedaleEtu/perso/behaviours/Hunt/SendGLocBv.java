package eu.su.mas.dedaleEtu.perso.behaviours.Hunt;

import java.io.IOException;
import java.util.List;

import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;


public class SendGLocBv extends OneShotBehaviour{
    private static final long serialVersionUID = 1L;

    private List<String> receivers;  

    public SendGLocBv(Agent a, List<String> receivers) {
        super(a);
        this.receivers = receivers;
    }

    @Override
    public void action() {
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setSender(myAgent.getAID());
        msg.setProtocol("SHARE-LOC");
        for(String agent : receivers){
            msg.addReceiver(new AID(agent, AID.ISLOCALNAME));
        }

        try {
            msg.setContentObject(((AbstractDedaleAgent)this.myAgent).getCurrentPosition());
        } catch (IOException e) {
            e.printStackTrace();
        }

        ((AbstractDedaleAgent)this.myAgent).sendMessage(msg);
        System.out.println(this.myAgent.getLocalName() + " : sent loc to " + receivers);
    }
}
