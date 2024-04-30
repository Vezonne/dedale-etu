package eu.su.mas.dedaleEtu.pres.behaviours.Hunt;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Location;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.env.gs.gsLocation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.pres.knowledge.AgentsLoc;
import eu.su.mas.dedaleEtu.pres.knowledge.MapRepresentation;
import eu.su.mas.dedaleEtu.pres.knowledge.MapRepresentation.MapAttribute;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;

public class HuntBv extends OneShotBehaviour {

    private static final long serialVersionUID = 1L;

    private MapRepresentation myMap;
	private AgentsLoc agentsLoc;
	private List<String> path;
	private long waitingTime;

	private int exitValue;

    public HuntBv(final Agent myAgent, MapRepresentation myMap, AgentsLoc agentsLoc, long waitingTime) {
        super(myAgent);
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

        if (myPosition!=null){

			// Observe the nodes
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
				String nextNodeId = null;

				for (String agent : this.agentsLoc.getCloseAgents()){
					occupiedNodes.add(this.agentsLoc.getAgentLocation(agent).getLocationId());
				}
				for (String gollem : this.agentsLoc.getGBlock()){
					occupiedNodes.add(gollem);
				}

				if (this.agentsLoc.getGPos() != null){
					List<String> arroundG = this.myMap.getNeighbors(this.agentsLoc.getGPos());

					if (!occupiedNodes.containsAll(arroundG)){
						int randomIndex = (int)(Math.random() * arroundG.size());
						String dest = arroundG.get(randomIndex);

						while (occupiedNodes.contains(dest)){
							randomIndex = (int)(Math.random() * arroundG.size());
							dest = arroundG.get(randomIndex);
						}
						occupiedNodes.add(this.agentsLoc.getGPos());

						try{
							path = this.myMap.getShortestPathWithoutNodes(myPosition.getLocationId(), dest, occupiedNodes);
							nextNodeId = path.get(0);
						} catch(Exception e1){
							try{
								path = this.myMap.getShortestPath(myPosition.getLocationId(), dest);
								nextNodeId = path.get(0);
							} catch(Exception e2){
								nextNodeId = null;
							}
						}
					}
					else{
						nextNodeId = this.agentsLoc.getPrevMove();
						exitValue = 0;
					}
				}
				else{
					if (!this.agentsLoc.getPrevMove().equals(myPosition.getLocationId())) {
						if (!occupiedNodes.contains(this.agentsLoc.getPrevMove())){
							this.agentsLoc.setGPos(this.agentsLoc.getPrevMove());
							nextNodeId = this.agentsLoc.getPrevMove();
							exitValue = 2;
						}
					}
				}

				if (nextNodeId == null){

					if (occupiedNodes.containsAll(stenchLocations)){
						nextNodeId = this.agentsLoc.getPrevMove();
						exitValue = 0;
					}
					else{
						int randomIndex = (int)(Math.random() * stenchLocations.size());
						nextNodeId = stenchLocations.get(randomIndex);
						while(occupiedNodes.contains(nextNodeId)){
							randomIndex = (int)(Math.random() * stenchLocations.size());
							nextNodeId = stenchLocations.get(randomIndex);
						}
						exitValue = 1;
					}

					exitValue = 1;
					this.agentsLoc.setPrevMove(nextNodeId);
					this.agentsLoc.setPrevPos(myPosition.getLocationId());
					((AbstractDedaleAgent)this.myAgent).moveTo(new gsLocation(nextNodeId));
				}
			}
        }
	}
    
	@Override
	public int onEnd() {
		return exitValue;
	}

}