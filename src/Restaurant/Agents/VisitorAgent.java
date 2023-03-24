package Restaurant.Agents;

import Restaurant.Behaviors.RegisterInDFBehaviour;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class VisitorAgent extends Agent {
    protected void setup() {
        System.out.println("Visitor agent " + getAID().getName() + " is ready.");
        addBehaviour(new RegisterInDFBehaviour(this, "Visitor", "Supervisor"));

        addBehaviour(new CyclicBehaviour(this) {
            public void action() {
                // ожидаем сообщение о недоступности пункта меню
                ACLMessage msg = receive(MessageTemplate.MatchPerformative(ACLMessage.INFORM));
                if (msg != null) {
                    String menuItemId = msg.getContent();

                    switch (menuItemId) {
                        case "add_dish_to_order_by_id":

                    }
                    System.out.println("\n------------------\n " + menuItemId + " \n--------------------\n");
//                     отключаем недоступный пункт меню
//                    menu.disableItem(menuItemId);
                }
                else {
                    // если сообщения нет, то поведение блокируется на команде receive
                    block();
                }
            }
        });
    }

    protected void takeDown() {
        System.out.println("Visitor agent " + getAID().getName() + " is terminating.");
    }
}
