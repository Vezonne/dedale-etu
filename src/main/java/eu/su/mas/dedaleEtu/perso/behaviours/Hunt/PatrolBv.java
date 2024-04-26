package eu.su.mas.dedaleEtu.perso.behaviours.Hunt;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Location;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.env.gs.gsLocation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.perso.knowledge.MapRepresentation;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;

public class PatrolBv extends OneShotBehaviour{

    private static final long serialVersionUID = 1L;
	
    private MapRepresentation myMap;
	private List<String> list_com;
	private List<String> path;
	private String targetNode;
	private String previousMove;
	private String previousPos;
	private String checkpoint;
	private Random r;

	private int waitingTime;
    private int exitValue;


    public PatrolBv(final Agent myAgent, MapRepresentation myMap, List<String> list_com, int waitingTime) {
        super(myAgent);
        this.myMap = myMap;
		this.list_com = list_com;
		this.waitingTime = waitingTime;

		this.path = new ArrayList<>();
		this.targetNode = null;
		this.previousMove = null;
		this.previousPos = null;
		this.checkpoint = null;
		this.r = new Random();
    }

    @Override
    public void action() {
		exitValue = 0;
		list_com.clear();

        Location myPosition=((AbstractDedaleAgent)this.myAgent).getCurrentPosition();

		try {
			this.myAgent.doWait(waitingTime);
		} catch (Exception e) {
			e.printStackTrace();
		}

        if (myPosition!=null){

			if (checkpoint == null) {
				checkpoint = myPosition.getLocationId();
			}
			
			if (this.previousPos != null && !this.previousPos.equals(myPosition.getLocationId())) {
				this.checkpoint = this.previousPos;
			}

			// List<Couple<Location,List<Couple<Observation,Integer>>>> lobs=((AbstractDedaleAgent)this.myAgent).observe();

			String nextNodeId=null;
			List<String> nodes = this.myMap.getNodes();

			if (targetNode == null || targetNode.equals(myPosition.getLocationId())){
				targetNode = nodes.get(r.nextInt(nodes.size()));
				while (targetNode.equals(myPosition.getLocationId())) {
					targetNode = nodes.get(r.nextInt(nodes.size()));
				}
			}

			path = this.myMap.getShortestPath(myPosition.getLocationId(), targetNode);
			nextNodeId = path.get(0);

			if(previousMove != null && !myPosition.getLocationId().equals(previousMove)){
				System.out.println(this.myAgent.getLocalName() + " : I'm stuck, recalculating path");

				List<String> nodesStuck = new ArrayList<String>();
				nodesStuck.add(previousMove);

				path = this.myMap.getShortestPathWithoutNodes(myPosition.getLocationId(), targetNode, nodesStuck);
				
				if (path == null) {
					System.out.println(this.myAgent.getLocalName() + " : No path found, I'm going back");
					targetNode = nodes.get(r.nextInt(nodes.size()));
					while (targetNode.equals(myPosition.getLocationId())) {
						targetNode = nodes.get(r.nextInt(nodes.size()));
					}
					path = this.myMap.getShortestPath(myPosition.getLocationId(), targetNode);
				}
				nextNodeId = path.get(0);
			}
			
			this.previousMove = nextNodeId;
			this.previousPos = myPosition.getLocationId();
			
			((AbstractDedaleAgent)this.myAgent).moveTo(new gsLocation(nextNodeId));
		}
    }

    @Override
    public int onEnd() {
        return exitValue;
    }
    
}
