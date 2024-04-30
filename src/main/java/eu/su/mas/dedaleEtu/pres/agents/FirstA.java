package eu.su.mas.dedaleEtu.pres.agents;

import java.util.ArrayList;
import java.util.List;

import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedale.mas.agent.behaviours.platformManagment.startMyBehaviours;

import eu.su.mas.dedale.mas.agent.knowledge.MapRepresentation;
import eu.su.mas.dedaleEtu.pres.behaviours.FirstBv;
import jade.core.behaviours.Behaviour;

public class FirstA extends AbstractDedaleAgent{

    private static final long serialVersionUID = 1L;

    private MapRepresentation myMap;

    protected void setup() {

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

        lb.add(new FirstBv(this, this.myMap, list_agentNames));

        addBehaviour(new startMyBehaviours(this, lb));

        System.out.println("the agent" + this.getLocalName() + " is started");

    }
    
}