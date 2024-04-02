package eu.su.mas.dedaleEtu.perso.behaviours.ShareMap;

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
  private long waitingTime = 100;

  public ReceiveMapBv(Agent a, MapRepresentation myMap) {
    super(a);
    this.myMap = myMap;
  }

  public ReceiveMapBv(Agent a, long waitingTime, MapRepresentation myMap){
    this(a, myMap);
    this.waitingTime = waitingTime;
  }

  @Override
    public void action() {
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
    }
  }

}
