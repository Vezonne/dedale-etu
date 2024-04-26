package eu.su.mas.dedaleEtu.perso.behaviours.Hunt;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Location;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.env.gs.gsLocation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
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
	private int exitValue;

    public HuntBv(final Agent myAgent, MapRepresentation myMap, AgentsLoc agentsLoc) {
        super(myAgent);
        this.myMap = myMap;
        this.agentsLoc = agentsLoc;
    }

    @Override
    public void action() {
		exitValue = 0;

        Location myPosition=((AbstractDedaleAgent)this.myAgent).getCurrentPosition();

        if (myPosition!=null){

			this.myMap.addNode(myPosition.getLocationId(), MapAttribute.closed);
			List<Couple<Location,List<Couple<Observation,Integer>>>> lobs=((AbstractDedaleAgent)this.myAgent).observe();

            List<String> stenchLocations = new ArrayList<>();
			Iterator<Couple<Location, List<Couple<Observation, Integer>>>> iter=lobs.iterator();

			while(iter.hasNext()){
				Couple<Location, List<Couple<Observation, Integer>>> node = iter.next();

				for(Couple<Observation, Integer> obs : node.getRight()){
					if(obs.getLeft().getName().equals("Stench")){
						stenchLocations.add(node.getLeft().getLocationId());
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
				// Choose a random stench location
				int randomIndex = (int)(Math.random() * stenchLocations.size());
				String targetLocationId = stenchLocations.get(randomIndex);

				while(occupiedNodes.contains(targetLocationId)){
					randomIndex = (int)(Math.random() * stenchLocations.size());
					targetLocationId = stenchLocations.get(randomIndex);
				}
				
				// Move towards the target location
				((AbstractDedaleAgent)this.myAgent).moveTo(new gsLocation(targetLocationId));
				exitValue = 1;
			}
        }
	}
    
	@Override
	public int onEnd() {
		return exitValue;
	}

}