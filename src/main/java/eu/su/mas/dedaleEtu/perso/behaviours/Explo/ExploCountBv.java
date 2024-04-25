package eu.su.mas.dedaleEtu.perso.behaviours.Explo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Location;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.env.gs.gsLocation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.perso.knowledge.MapRepresentation;
import eu.su.mas.dedaleEtu.perso.knowledge.MapRepresentation.MapAttribute;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import java_cup.production_part;

public class ExploCountBv extends OneShotBehaviour{

    private static final long serialVersionUID = 1L;

	private boolean hasMoved = false;
	private int nodeCount;
    private int exitValue;
    private MapRepresentation myMap;
	private Random r;
	private int limit;
	private List<String> list_com;

    public ExploCountBv(final Agent myAgent, MapRepresentation myMap, int nodeCount, List<String> list_com) {
        super(myAgent);
        this.myMap = myMap;
        this.nodeCount = nodeCount;
		this.r = new Random();
		this.list_com = list_com;
		System.out.println(myAgent.getLocalName() + " is counting nodes");	

    }

    @Override
    public void action() {
		exitValue = 0;
		list_com.clear();

        Location myPosition=((AbstractDedaleAgent)this.myAgent).getCurrentPosition();

        if (myPosition!=null){
			//List of observable from the agent's current position
			List<Couple<Location,List<Couple<Observation,Integer>>>> lobs=((AbstractDedaleAgent)this.myAgent).observe();//myPosition

			/**
			 * Just added here to let you see what the agent is doing, otherwise he will be too quick
			 */
			try {
				this.myAgent.doWait(500);
			} catch (Exception e) {
				e.printStackTrace();
			}
			//1) remove the current node from openlist and add it to closedNodes.
			this.myMap.addNode(myPosition.getLocationId(), MapAttribute.closed);

			//2) get the surrounding nodes and, if not in closedNodes, add them to open nodes.
			String nextNodeId=null;
			List<String> path = null;
			Iterator<Couple<Location, List<Couple<Observation, Integer>>>> iter=lobs.iterator();
			while(iter.hasNext()){
				Location accessibleNode=iter.next().getLeft();
				boolean isNewNode=this.myMap.addNewNode(accessibleNode.getLocationId());
				//the node may exist, but not necessarily the edge
				if (myPosition.getLocationId()!=accessibleNode.getLocationId()) {
					this.myMap.addEdge(myPosition.getLocationId(), accessibleNode.getLocationId());
					if (nextNodeId==null && isNewNode) nextNodeId=accessibleNode.getLocationId();
					path = new ArrayList<String>();
					path.add(nextNodeId);
				}
			}

            //3) while openNodes is not empty, continues.
			if (!this.myMap.hasOpenNode()){
				//Explo finished
                exitValue = 2;
				System.out.println(this.myAgent.getLocalName()+" - Exploration successufully done, behaviour removed.");
			}else{
				//4) select next move.
				//4.1 If there exist one open node directly reachable, go for it,
				//	 otherwise choose one from the openNode list, compute the shortestPath and go for it
				if (nextNodeId==null){
					//no directly accessible openNode
					//chose one, compute the path and take the first step.
					path = this.myMap.getShortestPathToClosestOpenNode(myPosition.getLocationId());
					nextNodeId=path.get(0);//getShortestPath(myPosition,this.openNodes.get(0)).get(0);
					//System.out.println(this.myAgent.getLocalName()+"-- list= "+this.myMap.getOpenNodes()+"| nextNode: "+nextNode);
				}

				while(!((AbstractDedaleAgent)this.myAgent).moveTo(new gsLocation(nextNodeId))){
					System.out.println(this.myAgent.getLocalName() + " : Try to move to "+nextNodeId+ " and I'm stuck");
					List<String> nodes = new ArrayList<String>();
					nodes.add(nextNodeId);
					path = this.myMap.getShortestPathToClosestOpenNode(myPosition.getLocationId());
					path = this.myMap.getShortestPathWithoutNodes(myPosition.getLocationId(), path.get(path.size()-1), nodes);
					if (path == null) {
						System.out.println(this.myAgent.getLocalName() + " : No path found, I'm stuck");
						break;
					}
					nextNodeId = path.get(0);
                }
				this.nodeCount++;

				this.limit = r.nextInt(4) + 1;

				if (this.nodeCount > limit){
					System.out.println(this.myAgent.getLocalName() + " : " + nodeCount + " nodes explored");
					this.nodeCount = 0;
					exitValue = 1;
				}
			}

		}
    }

    @Override
    public int onEnd() {
        return exitValue;
    }
    
}
