package eu.su.mas.dedaleEtu.perso.behaviours.Hunt;

import java.util.Random;
import java.util.Iterator;
import java.util.List;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Location;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.env.gs.gsLocation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.perso.knowledge.MapRepresentation;
import eu.su.mas.dedaleEtu.perso.knowledge.MapRepresentation.MapAttribute;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;

public class PatrolBv extends OneShotBehaviour{


    private static final long serialVersionUID = 1L;

	private boolean hasMoved = false;
	private int nodeCount;
    private int exitValue;
    private MapRepresentation myMap;
    private List<Couple<String,Location>> agentTab;
	private Random r;
	private int limit;

    public PatrolBv(final Agent myAgent, MapRepresentation myMap, List<Couple<String,Location>> agentTab, int nodeCount) {
        super(myAgent);
        this.myMap = myMap;
        this.agentTab = agentTab;
        this.nodeCount = nodeCount;
    }

    @Override
    public void action() {
		exitValue = 0;
        Location myPosition=((AbstractDedaleAgent)this.myAgent).getCurrentPosition();

        if (myPosition!=null){
			//List of observable from the agent's current position
			List<Couple<Location,List<Couple<Observation,Integer>>>> lobs=((AbstractDedaleAgent)this.myAgent).observe();//myPosition

			/**
			 * Just added here to let you see what the agent is doing, otherwise he will be too quick
			 */
			// try {
			// 	this.myAgent.doWait(500);
			// } catch (Exception e) {
			// 	e.printStackTrace();
			// }

			//1) remove the current node from openlist and add it to closedNodes.
			this.myMap.addNode(myPosition.getLocationId(), MapAttribute.closed);

			//2) get the surrounding nodes and, if not in closedNodes, add them to open nodes.
			String nextNodeId=null;
			Iterator<Couple<Location, List<Couple<Observation, Integer>>>> iter=lobs.iterator();
			while(iter.hasNext()){
				Location accessibleNode=iter.next().getLeft();
				boolean isNewNode=this.myMap.addNewNode(accessibleNode.getLocationId());
				//the node may exist, but not necessarily the edge
				if (myPosition.getLocationId()!=accessibleNode.getLocationId()) {
					this.myMap.addEdge(myPosition.getLocationId(), accessibleNode.getLocationId());
					if (nextNodeId==null && isNewNode) nextNodeId=accessibleNode.getLocationId();
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
					nextNodeId=this.myMap.getShortestPathToClosestOpenNode(myPosition.getLocationId()).get(0);//getShortestPath(myPosition,this.openNodes.get(0)).get(0);
					//System.out.println(this.myAgent.getLocalName()+"-- list= "+this.myMap.getOpenNodes()+"| nextNode: "+nextNode);
				}
				// try {
				// 	System.out.println("Press enter in the console to allow the agent "+this.myAgent.getLocalName() +" to execute its next move");
				// 	System.in.read();
				// } catch (IOException e) {
				// 	e.printStackTrace();
				// }
				this.hasMoved = ((AbstractDedaleAgent)this.myAgent).moveTo(new gsLocation(nextNodeId));
                if (this.hasMoved) {
					this.nodeCount++;

					this.r = new Random();
					this.limit = r.nextInt(4) + 1;

                    if (this.nodeCount > limit){
						System.out.println(this.myAgent.getLocalName() + " : " + nodeCount + " nodes explored");
						this.nodeCount = 0;
						exitValue = 1;
                    }
                }
			}

		}
    }

    @Override
    public int onEnd() {
        return exitValue;
    }
    
}
