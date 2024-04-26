package eu.su.mas.dedaleEtu.perso.behaviours;

import java.util.ArrayList;
import java.util.List;

import eu.su.mas.dedaleEtu.perso.behaviours.Explo.ExploCountBv;
import eu.su.mas.dedaleEtu.perso.behaviours.Hunt.HuntBv;
import eu.su.mas.dedaleEtu.perso.behaviours.Hunt.PatrolBv;
import eu.su.mas.dedaleEtu.perso.behaviours.Hunt.ReceiveLocBv;
import eu.su.mas.dedaleEtu.perso.behaviours.Hunt.SendLocBv;
import eu.su.mas.dedaleEtu.perso.behaviours.ShareMap.ReceiveMapBv;
import eu.su.mas.dedaleEtu.perso.behaviours.ShareMap.ReceivePingBv;
import eu.su.mas.dedaleEtu.perso.behaviours.ShareMap.SendMapBv;
import eu.su.mas.dedaleEtu.perso.behaviours.ShareMap.SendPingBv;
import eu.su.mas.dedaleEtu.perso.knowledge.AgentsLoc;
import eu.su.mas.dedaleEtu.perso.knowledge.MapRepresentation;
import jade.core.Agent;
import jade.core.behaviours.FSMBehaviour;
import jade.core.behaviours.OneShotBehaviour;


public class InitFSMBv extends OneShotBehaviour{

    private static final long serialVersionUID = 1L;

	private static final int DELAY = 150;
	private static final int WAITINGTIME = 150;

    private MapRepresentation myMap;
    private AgentsLoc agentsLoc;
    private List<String> list_agentNames;
    private List<String> list_senderNames = new ArrayList<String>();

    public InitFSMBv(Agent a, MapRepresentation myMap, AgentsLoc agentsLoc, List<String> list_agentsNames) {
        super(a);
        this.myMap = myMap;
        this.agentsLoc = agentsLoc;
        this.list_agentNames = list_agentsNames;
    }

    @Override
    public void action() {
        if (this.myMap == null){
            this.myMap = new MapRepresentation();
        }

        if (this.agentsLoc == null){
            this.agentsLoc = new AgentsLoc(list_agentNames, 5);
        }

        //---------- FSM ----------//

        System.out.println(this.myAgent.getLocalName() + " : " + list_agentNames);

        FSMBehaviour bigFSM = new FSMBehaviour(this.myAgent);

        // STATES
        String ExploCount = "ExploCount";

        String ReceivePing = "ReceivePing";
        String SendPing = "SendPing";

        String ReceivePong = "ReceivePong";
        String SendPong = "SendPong";

        String ReceiveMap = "ReceiveMap";
        String SendMap = "SendMap";

        String ReceiveResponse = "ReceiveResponse";
        String SendResponse = "SendResponse";

        String Patrol = "Patrol";
        String Hunt = "Hunt";
        String Hunt2 = "Hunt2";

        String EndExplo = "EndExplo";

        // Exploration
        bigFSM.registerFirstState(new ExploCountBv(this.myAgent, this.myMap, this.agentsLoc, list_senderNames, DELAY), ExploCount);
        // Send Map
        bigFSM.registerState(new ReceivePingBv(this.myAgent, WAITINGTIME, "PING", this.list_senderNames), ReceivePing);
        bigFSM.registerState(new SendPingBv(this.myAgent, this.list_agentNames, "PING"), SendPing);

        bigFSM.registerState(new ReceivePingBv(this.myAgent, WAITINGTIME, "PONG", this.list_senderNames), ReceivePong);
        bigFSM.registerState(new SendPingBv(this.myAgent, this.list_senderNames, "PONG"), SendPong);

        bigFSM.registerState(new ReceiveMapBv(this.myAgent, WAITINGTIME, this.myMap, this.list_senderNames), ReceiveMap);
        bigFSM.registerState(new SendMapBv(this.myAgent, this.myMap, this.list_senderNames), SendMap);

        bigFSM.registerState(new ReceiveMapBv(this.myAgent, WAITINGTIME, this.myMap, this.list_senderNames), ReceiveResponse);
        bigFSM.registerState(new SendMapBv(this.myAgent, this.myMap, this.list_senderNames), SendResponse);

        bigFSM.registerState(new PatrolBv(this.myAgent, this.myMap, list_senderNames, DELAY), Patrol);
        bigFSM.registerState(new HuntBv(this.myAgent, this.myMap, list_agentNames), Hunt);
        bigFSM.registerState(new HuntBv(this.myAgent, this.myMap, list_senderNames), Hunt2);
        // End
        bigFSM.registerLastState(new EmptyBv(this.myAgent), EndExplo);

        // TRANSITIONS
        // bigFSM.registerTransition(ExploCount, ExploCount, 0);
        bigFSM.registerTransition(ExploCount, ReceivePing, 1);
        bigFSM.registerTransition(ExploCount, Hunt2, 0);
        bigFSM.registerTransition(Hunt2, ExploCount, 0);
        bigFSM.registerTransition(Hunt2, Hunt2, 1);

        bigFSM.registerTransition(ReceivePing, SendPong, 1);
        bigFSM.registerDefaultTransition(SendPong, ReceiveMap);
        bigFSM.registerTransition(ReceiveMap, SendResponse, 1);
        bigFSM.registerTransition(ReceiveMap, ExploCount, 0);
        bigFSM.registerDefaultTransition(SendResponse, ExploCount);
        
        bigFSM.registerTransition(ReceivePing, SendPing, 0);
        bigFSM.registerDefaultTransition(SendPing, ReceivePong);
        bigFSM.registerTransition(ReceivePong, ExploCount, 0);
        bigFSM.registerTransition(ReceivePong, SendMap, 1);
        bigFSM.registerDefaultTransition(SendMap, ReceiveResponse);
        bigFSM.registerDefaultTransition(ReceiveResponse, ExploCount);

        bigFSM.registerTransition(ExploCount, Patrol, 2);
        bigFSM.registerDefaultTransition(Patrol, Hunt);
        bigFSM.registerTransition(Hunt, Patrol, 0);
        bigFSM.registerTransition(Hunt, Hunt, 1);

        this.myAgent.addBehaviour(bigFSM);

        FSMBehaviour sharLoc = new FSMBehaviour(this.myAgent);

        String sendLoc = "sendLoc";
        String receiveLoc = "receiveLoc";

        sharLoc.registerFirstState(new ReceiveLocBv(this.myAgent, this.myMap, agentsLoc, DELAY), receiveLoc);
        sharLoc.registerState(new SendLocBv(this.myAgent, list_agentNames), sendLoc);

        sharLoc.registerDefaultTransition(receiveLoc, sendLoc);
        sharLoc.registerDefaultTransition(sendLoc, receiveLoc);

        this.myAgent.addBehaviour(sharLoc);

        // try {
        // 	System.out.println("Press enter in the console to allow the agent "+this.myAgent.getLocalName() +" to execute its next move");
        // 	System.in.read();
        // } catch (IOException e) {
        // 	e.printStackTrace();
        // }
    }
    
}
