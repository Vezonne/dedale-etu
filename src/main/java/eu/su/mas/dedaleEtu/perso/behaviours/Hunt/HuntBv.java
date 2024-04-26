package eu.su.mas.dedaleEtu.perso.behaviours.Hunt;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Location;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.env.gs.gsLocation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.perso.agents.ExploPingA;
import eu.su.mas.dedaleEtu.perso.knowledge.AgentsLoc;
import eu.su.mas.dedaleEtu.perso.knowledge.MapRepresentation;
import eu.su.mas.dedaleEtu.perso.knowledge.MapRepresentation.MapAttribute;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SimpleBehaviour;

public class HuntBv extends OneShotBehaviour {

    private static final long serialVersionUID = 1L;

    private MapRepresentation myMap;
	private AgentsLoc agentsLoc;
	private long waitingTime;

	private String previousPos;
	private String previousMove;
	private int exitValue;

    public HuntBv(final Agent myAgent, MapRepresentation myMap, AgentsLoc agentsLoc, long waitingTime) {
        super(myAgent);
        this.myMap = myMap;
        this.agentsLoc = agentsLoc;
		this.waitingTime = waitingTime;

		this.previousPos = null;
		this.previousMove = null;
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

        if (myPosition!=null){

			this.myMap.addNode(myPosition.getLocationId(), MapAttribute.closed);
			List<Couple<Location,List<Couple<Observation,Integer>>>> lobs=((AbstractDedaleAgent)this.myAgent).observe();

            List<String> stenchLocations = new ArrayList<>();
			List<String> clearLocations = new ArrayList<>();
			Iterator<Couple<Location, List<Couple<Observation, Integer>>>> iter=lobs.iterator();

			while(iter.hasNext()){
				Couple<Location, List<Couple<Observation, Integer>>> node = iter.next();

				for(Couple<Observation, Integer> obs : node.getRight()){
					if(obs.getLeft().getName().equals("Stench")){
						stenchLocations.add(node.getLeft().getLocationId());
					}
					else{
						clearLocations.add(node.getLeft().getLocationId());
					}
				}
				Location accessibleNode=node.getLeft();
				this.myMap.addNewNode(accessibleNode.getLocationId());

				if (myPosition.getLocationId()!=accessibleNode.getLocationId()) {
					this.myMap.addEdge(myPosition.getLocationId(), accessibleNode.getLocationId());
				}
				
			}
			// Check if stench was detected
			if(!stenchLocations.isEmpty()) {
				// System.out.println(this.myAgent.getLocalName() + " : ÇA PUUUUUUE !!!!!!!!!!!!!!!!!");

				List<String> occupiedNodes = new ArrayList<String>();

				for (String agent : this.agentsLoc.getCloseAgents()){
					occupiedNodes.add(this.agentsLoc.getAgentLocation(agent).getLocationId());
				}
				
				String nextNodeId;
				if (this.previousMove != null && !this.previousMove.equals(myPosition.getLocationId()) && !occupiedNodes.contains(previousMove)) {
					((ExploPingA)this.myAgent).setGPos(previousMove);
					nextNodeId = previousMove;
					exitValue = 2;
				}
				else{

					int randomIndex = (int)(Math.random() * stenchLocations.size());
					nextNodeId = stenchLocations.get(randomIndex);
					
					if (occupiedNodes.containsAll(stenchLocations) && clearLocations.size()>0){
						randomIndex = (int)(Math.random() * clearLocations.size());
						nextNodeId = clearLocations.get(randomIndex);
					}
					else{
						while(occupiedNodes.contains(nextNodeId)){
							randomIndex = (int)(Math.random() * stenchLocations.size());
							nextNodeId = stenchLocations.get(randomIndex);
						}
					}

					previousMove = nextNodeId;
					previousPos = myPosition.getLocationId();
					// Move towards the target location
					exitValue = 1;
				}
				((AbstractDedaleAgent)this.myAgent).moveTo(new gsLocation(nextNodeId));

			}
        }
	}
    
	@Override
	public int onEnd() {
		return exitValue;
	}

}