package Restaurant.Agents;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

/**
 * Класс управляющего агента. Может быть только один экземпляр этого класса.
 */
public class SupervisorAgent {
    private static SupervisorAgent instance;

    public static synchronized SupervisorAgent getInstance() {
        if (instance == null) {
            instance = new SupervisorAgent();
        }
        return instance;
    }
}
