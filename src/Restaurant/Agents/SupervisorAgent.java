package Restaurant.Agents;

import Restaurant.Behaviors.RegisterInDFBehaviour;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.domain.*;
import jade.core.*;
import jade.domain.FIPAAgentManagement.*;

/**
 * Класс управляющего агента. Может быть только один экземпляр этого класса.
 */
public class SupervisorAgent extends Agent {
    private static SupervisorAgent instance;

    public static synchronized SupervisorAgent getInstance() {
        if (instance == null) {
            instance = new SupervisorAgent();
        }
        return instance;
    }

    protected void setup() {
        System.out.println("Supervisor agent " + getAID().getName() + " is ready.");
        addBehaviour(new RegisterInDFBehaviour(this, "Supervisor", "Restaurant"));

        addBehaviour(new CyclicBehaviour(this) {
            public void action() {
                // ожидаем сообщение о недоступности пункта меню
                ACLMessage msg = receive(MessageTemplate.MatchPerformative(ACLMessage.INFORM));
                if (msg != null) {
                    String response = msg.getContent();

                    System.out.println("\n------------------\nSupervisor get\r" + response + " \n--------------------\n");

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


//                    ACLMessage message = new ACLMessage(ACLMessage.INFORM);
//
//                    // добавляем получателей сообщения
//                    AID receiverAID = new AID("ReceiverAgent1", AID.ISLOCALNAME);
//                    AID receiverAID2 = new AID("ReceiverAgent2", AID.ISLOCALNAME);
//                    message.addReceiver(receiverAID);
//                    message.addReceiver(receiverAID2);
//
//                    // устанавливаем содержимое сообщения
//                    String myString = "Hello World!";
//                    int myNumber = 42;
//                    message.setContent(myString + " " + myNumber);
//
//                    // отправляем сообщение
//                    send(message);
                }
                else {
                    // если сообщения нет, то поведение блокируется на команде receive
                    block();
                }
            }
        });
    }

    protected void takeDown() {
        System.out.println("Supervisor agent " + getAID().getName() + " is terminating.");
    }
}
