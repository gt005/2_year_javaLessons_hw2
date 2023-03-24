package Restaurant.Behaviors;

import jade.core.*;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.*;
import jade.domain.FIPAAgentManagement.*;

public class RegisterInDFBehaviour extends OneShotBehaviour {
    private final String serviceType;
    private final String ownership;

    public RegisterInDFBehaviour(Agent agent, String serviceType, String ownership) {
        super(agent);
        this.serviceType = serviceType;
        this.ownership = ownership;
    }

    @Override
    public void action() {
        ServiceDescription sd = new ServiceDescription();
        sd.setType(serviceType);
        sd.setOwnership(ownership);
        sd.setName(myAgent.getName());

        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(myAgent.getAID());
        dfd.addServices(sd);

        try {
            DFAgentDescription[] dfds = DFService.search(myAgent, dfd);
            if (dfds.length > 0) {
                DFService.deregister(myAgent, dfd);
            }
            DFService.register(myAgent, dfd);
            System.out.println(myAgent.getLocalName() + " is ready.");
        } catch (Exception ex) {
            System.out.println("Failed registering in DF! Shutting down...");
            ex.printStackTrace();
            myAgent.doDelete();
        }
    }
}