package eu.su.mas.dedaleEtu.pres.behaviours;

import java.util.ArrayList;
import java.util.List;

import eu.su.mas.dedaleEtu.pres.agents.ExploPingA;
import eu.su.mas.dedaleEtu.pres.behaviours.Explo.ExploCountBv;
import eu.su.mas.dedaleEtu.pres.behaviours.Hunt.HuntBv;
import eu.su.mas.dedaleEtu.pres.behaviours.Hunt.JoinBv;
import eu.su.mas.dedaleEtu.pres.behaviours.Hunt.PatrolBv;
import eu.su.mas.dedaleEtu.pres.behaviours.Hunt.ReceiveGPosBv;
import eu.su.mas.dedaleEtu.pres.behaviours.Hunt.ReceiveLocBv;
import eu.su.mas.dedaleEtu.pres.behaviours.Hunt.SendGPosBv;
import eu.su.mas.dedaleEtu.pres.behaviours.Hunt.SendLocBv;
import eu.su.mas.dedaleEtu.pres.behaviours.ShareMap.ReceiveMapBv;
import eu.su.mas.dedaleEtu.pres.behaviours.ShareMap.ReceivePingBv;
import eu.su.mas.dedaleEtu.pres.behaviours.ShareMap.SendMapBv;
import eu.su.mas.dedaleEtu.pres.behaviours.ShareMap.SendPingBv;
import eu.su.mas.dedaleEtu.pres.knowledge.AgentsLoc;
import eu.su.mas.dedaleEtu.pres.knowledge.MapRepresentation;
import jade.core.Agent;
import jade.core.behaviours.FSMBehaviour;
import jade.core.behaviours.OneShotBehaviour;


public class InitFSMBv extends OneShotBehaviour{

    private static final long serialVersionUID = 1L;

	private static final int DELAY = 50;
	private static final int WAITINGTIME = 400;
    private static final int PERIOD = 50;

    private MapRepresentation myMap;
    private AgentsLoc agentsLoc;
    private List<String> agentNames;
    private List<String> listCom = new ArrayList<String>();

    public InitFSMBv(Agent a, MapRepresentation myMap, AgentsLoc agentsLoc, List<String> list_agentsNames) {
        super(a);
        this.myMap = myMap;
        this.agentsLoc = agentsLoc;
        this.agentNames = list_agentsNames;
    }

    @Override
    public void action() {
        if (this.myMap == null){
            this.myMap = new MapRepresentation();
        }

        if (this.agentsLoc == null){
            this.agentsLoc = new AgentsLoc(agentNames, ((ExploPingA)this.myAgent).getCOM_RANGE());
        }

        //---------- FSM ----------//

        System.out.println(this.myAgent.getLocalName() + " : " + agentNames);

        FSMBehaviour bigFSM = new FSMBehaviour(this.myAgent);

        // STATES
        String ExploCount = "ExploCount";
        String Patrol = "Patrol";
        String Hunt = "Hunt";
        String SendGPos = "SendGPos";
        String ReceiveGPos = "ReceiveGPos";
        String Join = "Join";

        // String EndExplo = "EndExplo";

        // Exploration
        bigFSM.registerFirstState(new ExploCountBv(this.myAgent, this.myMap, this.agentsLoc, DELAY), ExploCount);

        bigFSM.registerState(new PatrolBv(this.myAgent, this.myMap, this.agentsLoc, listCom, DELAY), Patrol);
        bigFSM.registerState(new HuntBv(this.myAgent, this.myMap, this.agentsLoc, WAITINGTIME), Hunt);
        bigFSM.registerState(new SendGPosBv(this.myAgent, this.agentsLoc, this.agentNames), SendGPos);
        bigFSM.registerState(new ReceiveGPosBv(this.myAgent, this.agentsLoc, WAITINGTIME), ReceiveGPos);
        bigFSM.registerState(new JoinBv(this.myAgent, this.myMap, this.agentsLoc, WAITINGTIME), Join);
        // End
        // bigFSM.registerLastState(new EmptyBv(this.myAgent), EndExplo);

        // TRANSITIONS
        // bigFSM.registerTransition(ExploCount, ExploCount, 0);
        // bigFSM.registerTransition(ExploCount, ReceivePing, 1);
        bigFSM.registerTransition(ExploCount, ExploCount, 0);

        bigFSM.registerTransition(ExploCount, Patrol, 2);
        bigFSM.registerTransition(Hunt, Patrol, 0);
        bigFSM.registerTransition(Hunt, Hunt, 1);

        bigFSM.registerTransition(Hunt, SendGPos, 2);
        bigFSM.registerDefaultTransition(SendGPos, Hunt);

        bigFSM.registerTransition(ReceiveGPos, Patrol,1);
        // bigFSM.registerTransition(Join, Patrol, 1);
        // bigFSM.registerTransition(Join, Hunt, 0);
        bigFSM.registerDefaultTransition(Patrol, ReceiveGPos);
        bigFSM.registerTransition(ReceiveGPos, Hunt, 0);
        // bigFSM.registerTransition(Hunt, Patrol, 0);
        // bigFSM.registerTransition(Hunt, Hunt, 1);

        this.myAgent.addBehaviour(bigFSM);

        FSMBehaviour shareMap = new FSMBehaviour(this.myAgent);

        String ReceivePing = "ReceivePing";
        String SendPing = "SendPing";

        String ReceivePong = "ReceivePong";
        String SendPong = "SendPong";

        String ReceiveMap = "ReceiveMap";
        String SendMap = "SendMap";

        String ReceiveResponse = "ReceiveResponse";
        String SendResponse = "SendResponse";

        shareMap.registerFirstState(new ReceivePingBv(this.myAgent, this.listCom, "PING", WAITINGTIME, PERIOD), ReceivePing);
        shareMap.registerState(new SendPingBv(this.myAgent, this.agentNames, "PING"), SendPing);

        shareMap.registerState(new ReceivePingBv(this.myAgent, this.listCom, "PONG", WAITINGTIME, DELAY), ReceivePong);
        shareMap.registerState(new SendPingBv(this.myAgent, this.listCom, "PONG"), SendPong);

        shareMap.registerState(new ReceiveMapBv(this.myAgent, this.myMap, this.listCom, WAITINGTIME), ReceiveMap);
        shareMap.registerState(new SendMapBv(this.myAgent, this.myMap, this.listCom), SendMap);

        shareMap.registerState(new ReceiveMapBv(this.myAgent, this.myMap, this.listCom, WAITINGTIME), ReceiveResponse);
        shareMap.registerState(new SendMapBv(this.myAgent, this.myMap, this.listCom), SendResponse);

        shareMap.registerTransition(ReceivePing, SendPong, 1);
        shareMap.registerDefaultTransition(SendPong, ReceiveMap);
        shareMap.registerTransition(ReceiveMap, SendResponse, 1);
        shareMap.registerTransition(ReceiveMap, ReceivePing, 0);
        shareMap.registerDefaultTransition(SendResponse, ReceivePing);
        
        shareMap.registerTransition(ReceivePing, SendPing, 0);
        shareMap.registerDefaultTransition(SendPing, ReceivePong);
        shareMap.registerTransition(ReceivePong, ReceivePing, 0);
        shareMap.registerTransition(ReceivePong, SendMap, 1);
        shareMap.registerDefaultTransition(SendMap, ReceiveResponse);
        shareMap.registerDefaultTransition(ReceiveResponse, ReceivePing);

        this.myAgent.addBehaviour(shareMap);

        FSMBehaviour shareLoc = new FSMBehaviour(this.myAgent);

        String sendLoc = "sendLoc";
        String receiveLoc = "receiveLoc";

        shareLoc.registerFirstState(new ReceiveLocBv(this.myAgent, this.myMap, agentsLoc, DELAY), receiveLoc);
        shareLoc.registerState(new SendLocBv(this.myAgent, agentNames), sendLoc);

        shareLoc.registerDefaultTransition(receiveLoc, sendLoc);
        shareLoc.registerDefaultTransition(sendLoc, receiveLoc);

        this.myAgent.addBehaviour(shareLoc);

        // try {
        // 	System.out.println("Press enter in the console to allow the agent "+this.myAgent.getLocalName() +" to execute its next move");
        // 	System.in.read();
        // } catch (IOException e) {
        // 	e.printStackTrace();
        // }
    }
    
}
