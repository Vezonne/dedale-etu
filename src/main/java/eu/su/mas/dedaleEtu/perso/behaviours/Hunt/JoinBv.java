package eu.su.mas.dedaleEtu.perso.behaviours.Hunt;

import java.util.ArrayList;
import java.util.List;

import eu.su.mas.dedaleEtu.perso.knowledge.MapRepresentation;
import eu.su.mas.dedale.env.Location;
import eu.su.mas.dedale.env.gs.gsLocation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.perso.agents.ExploPingA;
import eu.su.mas.dedaleEtu.perso.knowledge.AgentsLoc;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;

public class JoinBv extends OneShotBehaviour {
    
    private static final long serialVersionUID = 1L;

    private MapRepresentation myMap;
    private AgentsLoc agentsLoc;
    private long waitingTime;
    private int exitValue;
    
    public JoinBv(final Agent a, MapRepresentation myMap, AgentsLoc agentsLoc, long waitingTime) {
        super(a);
        this.myMap = myMap;
        this.agentsLoc = agentsLoc;
        this.waitingTime = waitingTime;
    }
    
    @Override
    public void action() {
        exitValue = 0;

        try {
            this.myAgent.doWait(waitingTime);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Location myPosition=((AbstractDedaleAgent)this.myAgent).getCurrentPosition();
        // List<String> occupiedNodes=new ArrayList<String>();

        // for (String agent: agentsLoc.getCloseAgents()) {
        //     Location loc = agentsLoc.getAgentLocation(agent);
        //     if (loc != null) {
        //         occupiedNodes.add(loc.getLocationId());
        //     }
        // }
        // List<String> path = this.myMap.getShortestPathWithoutNodes(myPosition.getLocationId(), ((ExploPingA)this.myAgent).getGPos(), occupiedNodes);
        List<String> path = this.myMap.getShortestPath(myPosition.getLocationId(), ((ExploPingA)this.myAgent).getGPos());

        if (path != null && path.size() > 1) {
            String nextNode = path.get(0);
            ((AbstractDedaleAgent)this.myAgent).moveTo(new gsLocation(nextNode));

            exitValue = 1;
        }
    }

    @Override
    public int onEnd() {
        return exitValue;
    }
}