package eu.su.mas.dedaleEtu.perso.behaviours.Hunt;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Location;
import eu.su.mas.dedaleEtu.perso.behaviours.Hunt.PatrolBv;
import eu.su.mas.dedaleEtu.perso.behaviours.ShareMap.ReceiveMapBv;
import eu.su.mas.dedaleEtu.perso.behaviours.ShareMap.SendMapBv;
import eu.su.mas.dedaleEtu.perso.behaviours.Hunt.SendLocBv;
import eu.su.mas.dedaleEtu.perso.behaviours.Hunt.SendGLocBv;
import eu.su.mas.dedaleEtu.perso.behaviours.Hunt.ReceiveLocBv;
import eu.su.mas.dedaleEtu.perso.behaviours.Hunt.ReceiveGLocBv;
import eu.su.mas.dedaleEtu.perso.behaviours.EmptyBv;
import eu.su.mas.dedaleEtu.perso.knowledge.MapRepresentation;
import eu.su.mas.dedaleEtu.perso.knowledge.AgentsLoc;
import jade.core.Agent;
import jade.core.behaviours.FSMBehaviour;
import jade.core.behaviours.OneShotBehaviour;


public class InitPatrolBv extends OneShotBehaviour {
    private static final long serialVersionUID = 1L;

    private MapRepresentation myMap;
    private List<String> list_agentNames;
    //private List<String> list_senderNames = new ArrayList<String>();
    private List<String> list_receivers;
    private List<Couple<String,Location>> agentTab;
    private int nodeCount = 0;
    private long waitingTime = 100;
    private AgentsLoc loclist;



    public InitPatrolBv(final Agent myAgent, AgentsLoc loclist, MapRepresentation myMap, List<String> list_agentsNames, List<String> list_receivers , List<Couple<String,Location>> agentTab, int nodeCount) {
        super(myAgent);
        this.loclist = loclist;
        this.myMap = myMap;
        this.list_agentNames = list_agentsNames;
        this.list_receivers = list_receivers;
        this.agentTab = agentTab;
        this.nodeCount = nodeCount;
    }

    @Override
    public void action() {
        if (this.myMap == null){
            this.myMap = new MapRepresentation();
        }
    
    System.out.println(this.myAgent.getLocalName() + " : " + list_agentNames);

    FSMBehaviour patrolFSM = new FSMBehaviour(this.myAgent);

    String EndPatrol = "EndPatrol";
    String Patrol = "Patrol";
    String ReceiveLoc = "ReceiveLoc";
    String ReceiveGLoc = "ReceiveGLoc";
    String SendLoc = "SendLoc";
    String SendGLoc = "SendGLoc";
    String Hunt = "Hunt";

    patrolFSM.registerFirstState(new PatrolBv(this.myAgent, this.myMap, this.agentTab, this.nodeCount), Patrol);
    patrolFSM.registerLastState(new EmptyBv(null), EndPatrol);
    
    // Mettre les bonnes signatures
    patrolFSM.registerState(new SendLocBv(this.myAgent, this.list_receivers), SendLoc);
    patrolFSM.registerState(new ReceiveLocBv(this.myAgent, this.loclist, waitingTime), ReceiveLoc);

    patrolFSM.registerState(new SendGLocBv(this.myAgent, this.list_receivers), SendGLoc);
    patrolFSM.registerState(new ReceiveGLocBv(this.myAgent, this.loclist, waitingTime), ReceiveGLoc);

    //patrolFSM.registerState(new SendMapBv(this.myAgent, this.myMap, this.list_senderNames), SendMap);
    //patrolFSM.registerState(new ReceiveMapBv(this.myAgent, 150, this.myMap), ReceiveResponse);
   
    //patrolFSM.registerState(new ReceiveLocBv(this.myAgent, 150, "PING", this.list_senderNames), ReceiveLoc);
    //patrolFSM.registerState(new SendLocBv(this.myAgent, this.list_senderNames, "PONG"), SendLoc);
    //patrolFSM.registerState(new ReceiveMapBv(this.myAgent, 150, this.myMap), ReceiveMap);
    //patrolFSM.registerState(new SendMapBv(this.myAgent, this.myMap, this.list_senderNames), SendResponse);

    // TRANSITIONS
    patrolFSM.registerTransition(Patrol, Patrol, 0);
    patrolFSM.registerTransition(Patrol, ReceiveLoc, 1);
    patrolFSM.registerTransition(Patrol, ReceiveGLoc, 2);
    patrolFSM.registerTransition(Patrol, SendGLoc, 3);
    patrolFSM.registerTransition(Patrol, EndPatrol, 4);

    patrolFSM.registerDefaultTransition(ReceiveLoc, SendLoc);

    patrolFSM.registerTransition(ReceiveGLoc, Patrol, 0);
    patrolFSM.registerTransition(ReceiveGLoc, SendGLoc, 1);

    patrolFSM.registerDefaultTransition(SendGLoc, Hunt);

    this.myAgent.addBehaviour(patrolFSM);

    }

}

