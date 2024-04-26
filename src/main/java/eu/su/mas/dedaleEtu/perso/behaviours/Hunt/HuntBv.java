package eu.su.mas.dedaleEtu.perso.behaviours.Hunt;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Location;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.env.gs.gsLocation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;

import eu.su.mas.dedaleEtu.perso.knowledge.MapRepresentation;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SimpleBehaviour;

public class HuntBv extends OneShotBehaviour {

    private static final long serialVersionUID = 1L;
    private MapRepresentation myMap;
    private List<String> list_agentNames;
	private int exitValue;

    public HuntBv(final Agent myAgent, MapRepresentation myMap, List<String> list_agentNames) {
        super(myAgent);
        this.myMap = myMap;
        this.list_agentNames = list_agentNames;
    }

    @Override
    public void action() {
		exitValue = 0;

        Location myPosition=((AbstractDedaleAgent)this.myAgent).getCurrentPosition();

        if (myPosition!=null){

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
				boolean isNewNode=this.myMap.addNewNode(accessibleNode.getLocationId());
				//the node may exist, but not necessarily the edge
				if (myPosition.getLocationId()!=accessibleNode.getLocationId()) {
					this.myMap.addEdge(myPosition.getLocationId(), accessibleNode.getLocationId());
				}
				
			}
			// Check if stench was detected
			if(!stenchLocations.isEmpty()) {
				// System.out.println(this.myAgent.getLocalName() + " : ÇA PUUUUUUE !!!!!!!!!!!!!!!!!");
				// Choose a random stench location
				int randomIndex = (int)(Math.random() * stenchLocations.size());
				String targetLocationId = stenchLocations.get(randomIndex);
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