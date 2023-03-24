package Restaurant.Agents;

import Restaurant.Behaviors.RegisterInDFBehaviour;
import Restaurant.Items.Menu;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.domain.*;
import jade.core.*;
import jade.domain.FIPAAgentManagement.*;
import java.io.IOException;

/**
 * Класс управляющего агента. Может быть только один экземпляр этого класса.
 */
public class SupervisorAgent extends Agent {
    private static SupervisorAgent instance;

    private SupervisorAgent() {
        addBehaviour(new RegisterInDFBehaviour(this, "Supervisor", "Restaurant"));
        addBehaviour(new MessageReceiver());
        addBehaviour(new MenuRequestReceiver());
    }

    public static synchronized SupervisorAgent getInstance() {
        if (instance == null) {
            instance = new SupervisorAgent();
        }
        return instance;
    }

    protected void setup() {
        System.out.println("Supervisor agent " + getAID().getName() + " is ready.");
    }

    private class MenuRequestReceiver extends CyclicBehaviour {
        public void action() {
            // ожидаем сообщение
            ACLMessage msg = receive(MessageTemplate.MatchPerformative(ACLMessage.REQUEST));
            if (msg != null) {
                String response = msg.getContent();

                System.out.println("\n------------------\nSupervisor get\t" + response + " \n--------------------\n");

                if ("send_menu".equals(response)) {
                    try {
                        ACLMessage message = new ACLMessage(ACLMessage.INFORM);
                        message.setContentObject(Menu.getInstance());
                        message.addReceiver(msg.getSender());
                        send(message);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
            else {
                // если сообщения нет, то поведение блокируется на команде receive
                block();
            }
        }
    }

    private class MessageReceiver extends CyclicBehaviour {
        public void action() {
            // ожидаем сообщение
            ACLMessage msg = receive(MessageTemplate.MatchPerformative(ACLMessage.INFORM));
            if (msg != null) {
                String response = msg.getContent();

                System.out.println("\n------------------\nSupervisor get\t" + response + " \n--------------------\n");

                DFAgentDescription dfd = new DFAgentDescription();
                ServiceDescription sd = new ServiceDescription();
                sd.setType("Visitor");
                dfd.addServices(sd);

                try {
                    DFAgentDescription[] result = DFService.search(myAgent, dfd);
                    for (int i = 0; i < result.length; i++) {
                        AID aid = result[i].getName();
                        ACLMessage message = new ACLMessage(ACLMessage.INFORM);
                        message.addReceiver(aid);
                        message.setContent("from supervisor " + response + " " + i);
                        send(message);
                    }
                } catch (FIPAException ex) {
                    ex.printStackTrace();
                }
            }
            else {
                // если сообщения нет, то поведение блокируется на команде receive
                block();
            }
        }
    }

    protected void takeDown() {
        System.out.println("Supervisor agent " + getAID().getName() + " is terminating.");
    }
}
