package eu.su.mas.dedaleEtu.pres.behaviours;

import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;

public class EmptyBv extends OneShotBehaviour{

    private static final long serialVersionUID = 1L;

    public EmptyBv(Agent a) {
        super(a);
    }

    @Override
    public void action() {
    }
    
}
