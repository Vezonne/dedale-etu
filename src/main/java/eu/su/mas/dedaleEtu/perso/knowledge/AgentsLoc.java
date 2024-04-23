package eu.su.mas.dedaleEtu.perso.knowledge;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Location;

public class AgentsLoc implements Serializable{

    private static final long serialVersionUID = 1L;
    
    private List<Couple<String,Location>> agentsLoc;
    private int size;

    public AgentsLoc(int size, List<String> agents){
        this.size = size;
        this.agentsLoc = new ArrayList<Couple<String,Location>>(size);
        for (String agent : agents){
            this.agentsLoc.add(new Couple<String,Location>(agent, null));
        }
    }

    public int size(){
        return this.size;
    }

    public Location getAgentLocation(String agent){
        return this.agentsLoc.stream().filter(c -> c.getLeft().equals(agent)).findFirst().get().getRight();
    }

    public void updateAgentLocation(String agent, Location loc){
        this.agentsLoc.remove(this.agentsLoc.stream().filter(c -> c.getLeft().equals(agent)).findFirst().get());
        this.agentsLoc.add(new Couple<String,Location>(agent, loc));
    }

    public List<String> getCloseAgents(){
        List<String> closeAgents = new ArrayList<String>();
        for (Couple<String,Location> agent : this.agentsLoc){
            if (agent.getRight() != null){
                closeAgents.add(agent.getLeft());
            }
        }
        return closeAgents;
    }
}
