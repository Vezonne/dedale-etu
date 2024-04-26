package eu.su.mas.dedaleEtu.perso.agents;

import java.util.ArrayList;
import java.util.List;

import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedale.mas.agent.behaviours.platformManagment.startMyBehaviours;
import eu.su.mas.dedaleEtu.perso.knowledge.AgentsLoc;
import eu.su.mas.dedaleEtu.perso.knowledge.MapRepresentation;
import eu.su.mas.dedaleEtu.perso.behaviours.InitFSMBv;
import jade.core.behaviours.Behaviour;

public class ExploPingA extends AbstractDedaleAgent{

    private static final long serialVersionUID = 1L;

    private static final int COM_RANGE = 4;
    
    private MapRepresentation myMap;
    private AgentsLoc myLocs;
    private String gPos;

    protected void setup(){
        super.setup();

        final Object[] args = getArguments();

        List<String> list_agentNames = new ArrayList<String>();

        if(args.length == 0){
            System.out.println("Error while creating the agent, names of agent to contact expected");
            System.exit(-1);
        }else{
            int i = 2;
            while (i < args.length) {
                list_agentNames.add((String)args[i]);
                i++;
            }
        }

        List<Behaviour> lb = new ArrayList<Behaviour>();
        lb.add(new InitFSMBv(this, this.myMap, this.myLocs, this.gPos, list_agentNames));

        addBehaviour(new startMyBehaviours(this, lb));

        System.out.println("the agent" + this.getLocalName() + " is started");
    }

    public int getCOM_RANGE(){
        return COM_RANGE;
    }

    public String getGPos(){
        return this.gPos;
    }

    public void setGPos(String gPos){
        this.gPos = gPos;
    }
}
