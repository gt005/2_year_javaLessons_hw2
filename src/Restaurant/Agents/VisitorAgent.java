package Restaurant.Agents;

import Restaurant.Behaviors.RegisterInDFBehaviour;
import Restaurant.Items.Menu;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.domain.*;
import jade.core.*;
import jade.domain.FIPAAgentManagement.*;

/**
 * Агент посетителя ресторана. Может быть несколько экземпляров этого класса.
 * Посетитель может выбирать блюда из меню, добавлять их в заказ, а также удалять из заказа.
 * Посетитель может отменить заказ.
 */
public class VisitorAgent extends Agent {
    private Menu menu;

    void setMenu(Menu menu) {
        this.menu = menu;
    }

    public VisitorAgent() {
        addBehaviour(new RegisterInDFBehaviour(this, "Visitor", "Supervisor"));
        addBehaviour(new MessageReceiver());
        addBehaviour(new RequestMenu());
    }

    protected void setup() {
        System.out.println("Visitor agent " + getAID().getName() + " is ready.");
    }

    /**
     * Выводит в консоль меню. Требуется только для отладки программы.
     */
    public void outMenu() {
        System.out.println("Menu " + menu.length() + ": ");
        for (int i = 0; i < menu.length(); ++i) {
            if (menu.getMenuDishActive(i)) {
                System.out.println("id " + menu.getMenuDishId(i));
            }
        }
    }

    /**
     * Запросит у управляющего агента меню. Меню будет получено и установлено в поле класса menu.
     */
    private class RequestMenu extends OneShotBehaviour {
        // Не static так как отправляет сообщение
        public void action() {
            // Поиск синглетона агента-управляющего
            DFAgentDescription dfd = new DFAgentDescription();
            ServiceDescription sd = new ServiceDescription();
            sd.setType("Supervisor");
            dfd.addServices(sd);

            try {
                DFAgentDescription[] supervisor = DFService.search(myAgent, dfd);
                ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
                msg.addReceiver(supervisor[0].getName());
                msg.setContent("send_menu"); // добавляем контент сообщения, в данном случае запрос на получение меню
                send(msg);
            } catch (FIPAException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Отвечает за получение и обработку сообщений. Распределяет сообщения по типам и выполняет нужные методы.
     */
    private static class MessageReceiver extends CyclicBehaviour {
        public void action() {
            ACLMessage msg = ((VisitorAgent) myAgent).receive(MessageTemplate.MatchPerformative(ACLMessage.INFORM));
            if (msg != null) {

                if ("add_dish_to_order_by_id".equals(msg.getContent())) {
                    // добавляем блюдо в заказ
                    System.out.println("dish added to order");
                } else if ("remove_dish_from_order_by_id".equals(msg.getContent())) {
                    // удаляем блюдо из заказа
                    System.out.println("dish removed from order");
                } else if ("cancel_order".equals(msg.getContent())) {
                    // отменяем заказ
                    System.out.println("order canceled");
                } else if ("turn_off_menu_dish_by_id".startsWith(msg.getContent())) {
                    if (((VisitorAgent) myAgent).menu.itemByIdExists(Integer.parseInt(msg.getContent().split(" ")[1]))) {
                        ((VisitorAgent) myAgent).menu.setMenuDishActivityById(
                                Integer.parseInt(msg.getContent().split(" ")[1]),
                                false
                        );
                    }
                } else if ("turn_on_menu_dish_by_id".startsWith(msg.getContent())) {
                    if (((VisitorAgent) myAgent).menu.itemByIdExists(Integer.parseInt(msg.getContent().split(" ")[1]))) {
                        ((VisitorAgent) myAgent).menu.setMenuDishActivityById(
                                Integer.parseInt(msg.getContent().split(" ")[1]),
                                true
                        );
                    }
                }
                try {
                    if (msg.getContentObject() instanceof Menu) {
                        Menu menu = (Menu) msg.getContentObject();
                        ((VisitorAgent) myAgent).setMenu(menu);
                        ((VisitorAgent) myAgent).outMenu();
                    }
                } catch (Exception ex) {
                    // Дада, я знаю, что так делать нельзя, но исключение UnreadableException не хочет импортироваться ниоткуда
                    ex.printStackTrace();
                }
            }
            else {
                block();
            }
        }
    }

    protected void takeDown() {
        System.out.println("Visitor agent " + getAID().getName() + " is terminating.");
    }
}
