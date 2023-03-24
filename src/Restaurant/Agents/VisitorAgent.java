package Restaurant.Agents;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class VisitorAgent extends Agent {
    protected void setup() {
        System.out.println("Visitor agent " + getAID().getName() + " is ready.");

        addBehaviour(new CyclicBehaviour(this) {
            public void action() {
                MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
                ACLMessage msg = myAgent.receive(mt);

                if (msg != null) {
                    // Обработка сообщений от других агентов
                } else {
                    block();
                }
            }
        });
    }

    protected void takeDown() {
        System.out.println("Visitor agent " + getAID().getName() + " is terminating.");
    }
}
