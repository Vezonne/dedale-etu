package eu.su.mas.dedaleEtu.pres.behaviours.Explo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

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

public class ExploCountBv extends OneShotBehaviour{

    private static final long serialVersionUID = 1L;
	
    private MapRepresentation myMap;
	private AgentsLoc agentsLoc;
	private String checkpoint;
	private List<String> path;
	private Random r;

	private int waitingTime;
	private int nodeCount;
    private int exitValue;
	private int limit;


    public ExploCountBv(final Agent myAgent, MapRepresentation myMap, AgentsLoc agentsLoc, int waitingTime) {
        super(myAgent);
        this.myMap = myMap;
		this.agentsLoc = agentsLoc;
		this.waitingTime = waitingTime;

		this.checkpoint = null;
		this.r = new Random();
        this.nodeCount = 0;
		this.limit = r.nextInt(4) + 3;
    }

    @Override
    public void action() {
		exitValue = 0;

        Location myPosition=((AbstractDedaleAgent)this.myAgent).getCurrentPosition();

		this.nodeCount++;

		if (this.nodeCount > limit){
			System.out.println(this.myAgent.getLocalName() + " : " + nodeCount + " nodes explored");
			this.nodeCount = 0;
			this.limit = r.nextInt(4) + 3;
			exitValue = 0;
		}

		/**
		 * Just added here to let you see what the agent is doing, otherwise he will be too quick
		 */
		try {
			this.myAgent.doWait(waitingTime);
		} catch (Exception e) {
			e.printStackTrace();
		}

        if (myPosition!=null){

			String nextNodeId=null;

			if (checkpoint == null) {
				checkpoint = myPosition.getLocationId();
				this.agentsLoc.setPrevMove(checkpoint);
				this.agentsLoc.setPrevPos(checkpoint);
			}
			
			if (!this.agentsLoc.getPrevPos().equals(myPosition.getLocationId())) {
				this.checkpoint = this.agentsLoc.getPrevPos();
			}

			//List of observable from the agent's current position
			List<Couple<Location,List<Couple<Observation,Integer>>>> lobs=((AbstractDedaleAgent)this.myAgent).observe();//myPosition

			//1) remove the current node from openlist and add it to closedNodes.
			this.myMap.addNode(myPosition.getLocationId(), MapAttribute.closed);

			//2) get the surrounding nodes and, if not in closedNodes, add them to open nodes.
			Iterator<Couple<Location, List<Couple<Observation, Integer>>>> iter=lobs.iterator();
			while(iter.hasNext()){
				Location accessibleNode=iter.next().getLeft();
				boolean isNewNode=this.myMap.addNewNode(accessibleNode.getLocationId());
				//the node may exist, but not necessarily the edge
				if (myPosition.getLocationId()!=accessibleNode.getLocationId()) {
					this.myMap.addEdge(myPosition.getLocationId(), accessibleNode.getLocationId());
					if (nextNodeId==null && isNewNode){
						nextNodeId=accessibleNode.getLocationId();
					}
				}
			}

            //3) while openNodes is not empty, continues.
			if (!this.myMap.hasOpenNode()){
				//Explo finished
				System.out.println(this.myAgent.getLocalName()+" - Exploration successufully done");
                exitValue = 2;
			}

			else{

				if(!myPosition.getLocationId().equals(this.agentsLoc.getPrevMove())){
					System.out.println(this.myAgent.getLocalName() + " : I'm stuck, recalculating path");
	
					List<String> occupiedNodes = new ArrayList<String>();
					occupiedNodes.add(this.agentsLoc.getPrevMove());
					for (String agent: this.agentsLoc.getCloseAgents()){
						occupiedNodes.add(this.agentsLoc.getAgentLocation(agent).getLocationId());
					}

					try{
						path = this.myMap.getShortestPathWithoutNodes(myPosition.getLocationId(), this.agentsLoc.getDest(), occupiedNodes);
						nextNodeId = path.get(0);
					}catch(Exception e){
						path = null;
					}

					if (path == null && !checkpoint.equals(myPosition.getLocationId())) {
						System.out.println(this.myAgent.getLocalName() + " : No path found, I'm going back");

						List<String> openNodes = this.myMap.getOpenNodes();
						this.agentsLoc.setDest(openNodes.get(r.nextInt(openNodes.size())));
						// this.agentsLoc.setDest(this.checkpoint);
						path = this.myMap.getShortestPath(myPosition.getLocationId(), this.agentsLoc.getDest());
						nextNodeId = path.get(0);
					}
				}

				else if (this.agentsLoc.getDest() != null){
					
					if (this.agentsLoc.getDest().equals(myPosition.getLocationId())) {
						this.agentsLoc.setDest(null);
					}
					else{
						path = this.myMap.getShortestPath(myPosition.getLocationId(), this.agentsLoc.getDest());
						nextNodeId=path.get(0);
					}
				}

				//4) select next move.
				//4.1 If there exist one open node directly reachable, go for it,
				//	 otherwise choose one from the openNode list, compute the shortestPath and go for it
				if (nextNodeId==null){
					path = this.myMap.getShortestPathToClosestOpenNode(myPosition.getLocationId());
					this.agentsLoc.setDest(path.get(path.size()-1));
					nextNodeId=path.get(0);
				}

				
				this.agentsLoc.setPrevMove(nextNodeId);
				this.agentsLoc.setPrevPos(myPosition.getLocationId());

				((AbstractDedaleAgent)this.myAgent).moveTo(new gsLocation(nextNodeId));
			}
		}
    }

    @Override
    public int onEnd() {
        return exitValue;
    }
    
}
