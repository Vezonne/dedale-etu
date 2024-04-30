package eu.su.mas.dedaleEtu.pres.knowledge;

import java.io.Serializable;
import java.util.List;

import java.util.ArrayList;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Location;

public class AgentsLoc implements Serializable{

    private static final long serialVersionUID = 1L;
    
    private List<Couple<String,Location>> agentsLoc;
    private int size;
    private int range;
    private String gPos;
    private String prevPos;
    private String prevMove;
    private String dest;
    private List<String> gBlock;

    public AgentsLoc(List<String> agents, int range){
        this.size = 0;
        this.agentsLoc = new ArrayList<Couple<String,Location>>();
        this.range = range;
        this.gBlock = new ArrayList<String>();
    }

    public int size(){
        return this.size;
    }

    public String getGPos(){
        return this.gPos;
    }
    
    public String getPrevPos(){
        return this.prevPos;
    }

    public String getPrevMove(){
        return this.prevMove;
    }

    public String getDest(){
        return this.dest;
    }

    public void setGPos(String gPos){
        this.gPos = gPos;
    }

    public void setPrevPos(String prevPos){
        this.prevPos = prevPos;
    }

    public void setPrevMove(String prevMove){
        this.prevMove = prevMove;
    }

    public void setDest(String dest){
        this.dest = dest;
    }

    public List<String> getGBlock(){
        return this.gBlock;
    }

    public void addGBlock(String gPos){
        this.gBlock.add(gPos);
    }

    public Location getAgentLocation(String agent){
        return this.agentsLoc.stream().filter(c -> c.getLeft().equals(agent)).findFirst().get().getRight();
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
    

    public void updateAgentLocation(String agent, Location loc){
        if (this.getCloseAgents().contains(agent)){
            Couple<String,Location> coupleToUpdate = this.agentsLoc.stream().filter(c -> c.getLeft().equals(agent)).findFirst().get();
            this.agentsLoc.remove(coupleToUpdate);
            size --;
        }
        this.agentsLoc.add(new Couple<String,Location>(agent, loc));
        size ++;
    }

    public void checkAgentsInRange(Location myLoc, MapRepresentation myMap){
        List<String> agentsToRemove = new ArrayList<String>();
        for (Couple<String,Location> agent : this.agentsLoc){
            if (agent.getRight() != null && myMap.getNodes().contains(agent.getRight().getLocationId())){
                List<String> path = myMap.getShortestPath(myLoc.getLocationId(), agent.getRight().getLocationId());
                if (path != null && path.size() > this.range){
                    agentsToRemove.add(agent.getLeft());
                }
            }
        }
        for (String agent : agentsToRemove){
            this.agentsLoc.removeIf(c -> c.getLeft().equals(agent));
            size --;
        }
    }

    
    public String toString(){
        if (size == 0){
            return "[]";
        }

        String str = "[";
        for (Couple<String,Location> agent : this.agentsLoc){
            str += agent.getLeft() + ":" + agent.getRight() + ", ";
        }
        return str + "\b\b]";
    }
}
