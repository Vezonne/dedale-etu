package eu.su.mas.dedaleEtu.perso.behaviours.ShareMap;

import java.util.List;

import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

public class SendPingBv extends OneShotBehaviour{

    private static final long serialVersionUID = 1L;

    private List<String> receivers;
    private String protocol;

    public SendPingBv(Agent a, List<String> receivers, String protocol) {
        super(a);
        this.receivers = receivers;
        this.protocol = protocol;
    }
    
    @Override
    public void action() {
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setProtocol(protocol);
        msg.setSender(this.myAgent.getAID());
        for (String agent : receivers){
            msg.addReceiver(new AID(agent, AID.ISLOCALNAME));
        }
        ((AbstractDedaleAgent)this.myAgent).sendMessage(msg);
        System.out.println(this.myAgent.getLocalName() + " : sent " + protocol + " to " + receivers);
    }
    
}
