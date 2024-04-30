package eu.su.mas.dedaleEtu.pres.behaviours.Hunt;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import eu.su.mas.dedale.env.Location;
import eu.su.mas.dedale.env.gs.gsLocation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.pres.knowledge.AgentsLoc;
import eu.su.mas.dedaleEtu.pres.knowledge.MapRepresentation;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;

public class PatrolBv extends OneShotBehaviour{

    private static final long serialVersionUID = 1L;
	
    private MapRepresentation myMap;
	private AgentsLoc agentsLoc;
	private List<String> list_com;
	private List<String> path;
	private Random r;

	private int waitingTime;
    private int exitValue;


    public PatrolBv(final Agent myAgent, MapRepresentation myMap, AgentsLoc agentsLoc, List<String> list_com, int waitingTime) {
        super(myAgent);
        this.myMap = myMap;
		this.agentsLoc = agentsLoc;
		this.list_com = list_com;
		this.waitingTime = waitingTime;

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
			
			// if (!this.agentsLoc.getPrevPos().equals(myPosition.getLocationId())) {
			// 	this.checkpoint = this.agentsLoc.getPrevPos();
			// }

			// List<Couple<Location,List<Couple<Observation,Integer>>>> lobs=((AbstractDedaleAgent)this.myAgent).observe();

			String nextNodeId=null;
			List<String> nodes = this.myMap.getNodes();

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

			if (nextNodeId == null) {
				String dest = nodes.get(r.nextInt(nodes.size()));
				while (dest.equals(myPosition.getLocationId())) {
					dest = nodes.get(r.nextInt(nodes.size()));
				}
				this.agentsLoc.setDest(dest);

				path = this.myMap.getShortestPath(myPosition.getLocationId(), this.agentsLoc.getDest());
				nextNodeId = path.get(0);
			}
			
			this.agentsLoc.setPrevMove(nextNodeId);
			this.agentsLoc.setPrevPos(myPosition.getLocationId());
			
			((AbstractDedaleAgent)this.myAgent).moveTo(new gsLocation(nextNodeId));
		}
    }

    @Override
    public int onEnd() {
        return exitValue;
    }
    
}
