package eu.su.mas.dedaleEtu.perso.behaviours.ShareMap;

import java.util.List;

import dataStructures.serializableGraph.SerializableSimpleGraph;
import eu.su.mas.dedaleEtu.perso.knowledge.MapRepresentation.MapAttribute;
import eu.su.mas.dedaleEtu.perso.knowledge.MapRepresentation;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

public class ReceiveMapBv extends OneShotBehaviour{

  private static final long serialVersionUID = 1L;

  private MapRepresentation myMap;
  private List<String> senders;
  private long waitingTime = 100;
  private int exitValue;

  public ReceiveMapBv(Agent a, long waitingTime, MapRepresentation myMap, List<String> senders) {
    super(a);
    this.waitingTime = waitingTime;
    this.myMap = myMap;
    this.senders = senders;
  }

  @Override
  public void action() {
    exitValue = 0;

    MessageTemplate msgTemplate = MessageTemplate.and(
        MessageTemplate.MatchProtocol("SHARE-MAP"), 
        MessageTemplate.MatchPerformative(ACLMessage.INFORM));
      this.myAgent.doWait(this.waitingTime);
    ACLMessage msg = this.myAgent.receive(msgTemplate);
    if (msg != null) {
      SerializableSimpleGraph<String,MapAttribute> sgreceived = null;
      try{
          sgreceived = (SerializableSimpleGraph<String,MapAttribute>) msg.getContentObject();
      }catch(UnreadableException e){
          e.printStackTrace();
      }
      this.myMap.mergeMap(sgreceived);
      System.out.println(this.myAgent.getLocalName() + " : received map from " + msg.getSender().getLocalName());
      if (!senders.contains(msg.getSender().getLocalName())){
        senders.add(msg.getSender().getLocalName());
      }
      exitValue = 1;
    }
  }

  @Override
    public int onEnd() {
    return exitValue;
  }

}
