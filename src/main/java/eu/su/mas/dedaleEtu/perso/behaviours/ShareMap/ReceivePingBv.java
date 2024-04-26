package eu.su.mas.dedaleEtu.perso.behaviours.ShareMap;

import java.util.List;

import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class ReceivePingBv extends OneShotBehaviour{

    private static final long serialVersionUID = 1L;

    private List<String> senders;
    private String protocol;
    private long waitingTime;
    private long period;
    private int exitValue;

    public ReceivePingBv(Agent a, List<String> senders, String protocol, long waitingTime, long period){
        super(a);
        this.senders = senders;
        this.protocol = protocol;
        this.waitingTime = waitingTime;
        this.period = period;
    }

    @Override
    public void action() {
        if(this.protocol.equals("PING")){
            senders.clear();
            this.block(period);
        }
        exitValue = 0;

        MessageTemplate msgTemplate = MessageTemplate.and(
            MessageTemplate.MatchProtocol(protocol), 
            MessageTemplate.MatchPerformative(ACLMessage.INFORM));

        this.myAgent.doWait(waitingTime);
        ACLMessage msg = this.myAgent.receive(msgTemplate);

        while (msg != null) {
            System.out.println(this.myAgent.getLocalName() + " : received " + protocol + " from " + msg.getSender().getLocalName());

            if (!senders.contains(msg.getSender().getLocalName())){
                senders.add(msg.getSender().getLocalName());
            }

            msg = this.myAgent.receive(msgTemplate);
            exitValue = 1;
        }
    }
    
    @Override
    public int onEnd() {
        return exitValue;
    }

}
