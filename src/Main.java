import Restaurant.Addons.JsonFileHandler;
import Restaurant.Agents.VisitorAgent;
import Restaurant.Agents.SupervisorAgent;
import Restaurant.Items.Parcers.CreateMenuFromJSON;

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
            supervisorController = container.acceptNewAgent(
                    "SupervisorAgent",
                    SupervisorAgent.getInstance(5, container)
            );
            supervisorController.start();
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
    }
}
