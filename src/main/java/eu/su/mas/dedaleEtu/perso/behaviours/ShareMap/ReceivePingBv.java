package eu.su.mas.dedaleEtu.perso.behaviours.ShareMap;

import java.util.List;

import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class ReceivePingBv extends OneShotBehaviour{

    private static final long serialVersionUID = 1L;

    private List<String> senders;
    private int exitValue;
    private String protocol = "PING";
    private long waitingTime = 0;

    public ReceivePingBv(Agent a, String protocol, List<String> senders){
        super(a);
        this.protocol = protocol;
        this.senders = senders;
        exitValue = 0;
    }

    public ReceivePingBv(Agent a, long waitingTime, String protocol, List<String> senders){
        this(a, protocol, senders);
        this.waitingTime = waitingTime;
    }

    @Override
    public void action() {
        System.out.println(this.myAgent.getLocalName() + " : waiting for " + protocol);

        MessageTemplate msgTemplate = MessageTemplate.and(
            MessageTemplate.MatchProtocol(protocol), 
            MessageTemplate.MatchPerformative(ACLMessage.INFORM));
        this.myAgent.doWait(waitingTime);
        ACLMessage msg = this.myAgent.receive(msgTemplate);
        if (msg != null) {
            System.out.println(this.myAgent.getLocalName() + " : received " + protocol + " from " + msg.getSender().getLocalName());
            if (!senders.contains(msg.getSender().getLocalName()))
                System.out.println(this.myAgent.getLocalName() + " : new sender " + msg.getSender().getLocalName());
                senders.add(msg.getSender().getLocalName());
            exitValue = 1;
        }
    }
    
    @Override
    public int onEnd() {
        return exitValue;
    }

}
