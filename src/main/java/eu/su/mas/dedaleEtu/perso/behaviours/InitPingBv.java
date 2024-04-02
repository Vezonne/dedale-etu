package eu.su.mas.dedaleEtu.perso.behaviours;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import eu.su.mas.dedaleEtu.perso.behaviours.Explo.ExploCountBv;
import eu.su.mas.dedaleEtu.perso.behaviours.ShareMap.ReceiveMapBv;
import eu.su.mas.dedaleEtu.perso.behaviours.ShareMap.ReceivePingBv;
import eu.su.mas.dedaleEtu.perso.behaviours.ShareMap.SendMapBv;
import eu.su.mas.dedaleEtu.perso.behaviours.ShareMap.SendPingBv;
import eu.su.mas.dedaleEtu.perso.knowledge.MapRepresentation;
import jade.core.Agent;
import jade.core.behaviours.FSMBehaviour;
import jade.core.behaviours.OneShotBehaviour;

public class InitPingBv extends OneShotBehaviour{

    private static final long serialVersionUID = 1L;

    private MapRepresentation myMap;
    private List<String> list_agentNames;
    private List<String> list_senderNames = new ArrayList<String>();
    private int nodeCount = 0;

    public InitPingBv(Agent a, MapRepresentation myMap, List<String> list_agentsNames) {
        super(a);
        this.myMap = myMap;
        this.list_agentNames = list_agentsNames;
    }

    @Override
    public void action() {
        if (this.myMap == null){
            this.myMap = new MapRepresentation();
        }

        //---------- FSM ----------//

        FSMBehaviour exploFSM = new FSMBehaviour(this.myAgent);

        // STATES
        String ExploCount = "ExploCount";
        String EndExplo = "EndExplo";
        String SendPing = "SendPing";
        String ReceivePong = "ReceivePong";
        String ReceiveMap = "ReceiveMap";
        String SendMap = "SendMap";
        String ReceivePing = "ReceivePing";
        String SendPong = "SendPong";

        // Exploration
        exploFSM.registerFirstState(new ExploCountBv(this.myAgent, this.myMap, this.nodeCount), ExploCount);
        exploFSM.registerLastState(new EmptyBv(null), EndExplo);
        // Send Map
        exploFSM.registerState(new SendPingBv(this.myAgent, this.list_agentNames, "PING"), SendPing);
        exploFSM.registerState(new ReceivePingBv(this.myAgent, 150, "PONG", this.list_senderNames), ReceivePong);
        exploFSM.registerState(new SendMapBv(this.myAgent, this.myMap, this.list_senderNames), SendMap);
        // Receive Ping
        exploFSM.registerState(new ReceivePingBv(this.myAgent, 150, "PING", this.list_senderNames), ReceivePing);
        exploFSM.registerState(new SendPingBv(this.myAgent, this.list_senderNames, "PONG"), SendPong);
        exploFSM.registerState(new ReceiveMapBv(this.myAgent, 150, this.myMap), ReceiveMap);

        // TRANSITIONS
        exploFSM.registerTransition(ExploCount, ExploCount, 0);
        exploFSM.registerTransition(ExploCount, ReceivePing, 1);

        exploFSM.registerTransition(ReceivePing, SendPong, 1);
        exploFSM.registerDefaultTransition(SendPong, ReceiveMap);
        exploFSM.registerDefaultTransition(ReceiveMap, ExploCount);

        exploFSM.registerTransition(ReceivePing, SendPing, 0);
        exploFSM.registerDefaultTransition(SendPing, ReceivePong);
        exploFSM.registerTransition(ReceivePong, ExploCount, 0);
        exploFSM.registerTransition(ReceivePong, SendMap, 1);
        exploFSM.registerDefaultTransition(SendMap, ExploCount);

        exploFSM.registerTransition(ExploCount, EndExplo, 2);

        this.myAgent.addBehaviour(exploFSM);

        try {
        	System.out.println("Press enter in the console to allow the agent "+this.myAgent.getLocalName() +" to execute its next move");
        	System.in.read();
        } catch (IOException e) {
        	e.printStackTrace();
        }
    }
    
}
