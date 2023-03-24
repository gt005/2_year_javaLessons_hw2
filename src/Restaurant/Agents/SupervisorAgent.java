package Restaurant.Agents;

import Restaurant.Addons.JsonFileHandler;
import Restaurant.Behaviors.RegisterInDFBehaviour;
import Restaurant.Items.Menu;

import Restaurant.Items.Parcers.CreateMenuFromJSON;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
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
        addBehaviour(new createMenu());
        addBehaviour(new MessageReceiver());
        addBehaviour(new MenuRequestReceiver());
//        addBehaviour(new createAllRequiredAgents());
    }

    /**
     * Возвращает экземпляр класса. Если экземпляр класса не был создан, то он будет создан.
     * @return экземпляр класса
     */
    public static synchronized SupervisorAgent getInstance() {
        if (instance == null) {
            instance = new SupervisorAgent();
        }
        return instance;
    }

    protected void setup() {
        System.out.println("Supervisor agent " + getAID().getName() + " is ready.");
    }

    /**
     * Поведение, которое обрабатывает запросы меню и выдает заданное при запуске программы меню.
     */
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

    /**
     * Отвечает за получение и обработку сообщений. Распределяет сообщения по типам и выполняет нужные методы.
     */
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

    /**
     * Загражает из файла и инициализирует объект меню.
     */
    private static class createMenu extends OneShotBehaviour {
        public void action() {
            try {
                CreateMenuFromJSON.create(
                        JsonFileHandler.readJsonFromFile(
                                "input_data/menu_dishes.txt"
                        )
                );
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    protected void takeDown() {
        System.out.println("Supervisor agent " + getAID().getName() + " is terminating.");
    }
}
