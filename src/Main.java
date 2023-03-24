import Restaurant.Addons.JsonFileHandler;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.core.Agent;
import jade.content.ContentManager;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.OntologyException;
import jade.core.AID;
import jade.wrapper.StaleProxyException;

import Restaurant.Agents.VisitorAgent;
import Restaurant.Agents.SupervisorAgent;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        jade.core.Runtime runtime = jade.core.Runtime.instance();

        Profile profile = new ProfileImpl();
        profile.setParameter(Profile.MAIN_HOST, "localhost");
        profile.setParameter(Profile.GUI, "true");

        ContainerController container = runtime.createMainContainer(profile);

        AgentController supervisorController = null;
        try {
            supervisorController = container.createNewAgent("SupervisorAgent", SupervisorAgent.class.getName(), null);
            supervisorController.start();
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }

        AgentController agentController = null;

        try {
            for (int i = 0; i < 5; i++) {
                agentController = container.createNewAgent(
                        "visitor " + i,
                        "Restaurant.Agents.VisitorAgent",
                        null
                );
                agentController.start();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (supervisorController == null && agentController == null) {
            System.out.println("No agents were created");
            return;
        }

    }
}
