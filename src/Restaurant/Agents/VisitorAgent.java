package Restaurant.Agents;

import Restaurant.Agents.OrderAgent;

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
import jade.lang.acl.UnreadableException;
import org.w3c.dom.ls.LSOutput;

/**
 * Агент посетителя ресторана. Может быть несколько экземпляров этого класса.
 * Посетитель может выбирать блюда из меню, добавлять их в заказ, а также удалять из заказа.
 * Посетитель может отменить заказ.
 */
public class VisitorAgent extends Agent {
    private Menu menu;
    private OrderAgent orderAgent;

    public VisitorAgent() {
        addBehaviour(new RegisterInDFBehaviour(this, "Visitor", "Supervisor"));
        addBehaviour(new InformMessageReceiver());
        addBehaviour(new RequestSupervisor("send_menu"));
        addBehaviour(new RequestSupervisor("create_order_agent"));
    }

    @Override
    protected void setup() {
        System.out.println("Visitor agent " + getAID().getName() + " is ready.");
    }

    /**
     * Устанавливает объект меню для класса.
     * @param menu объект меню
     */
    protected void setMenu(Menu menu) {
        this.menu = menu;
    }

    /**
     * Устанавливает объект заказа для класса.
     * @param orderAgent объект заказа
     */
    protected void setOrderAgent(OrderAgent orderAgent) {
        this.orderAgent = orderAgent;
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
     * Задает видимость блюда с заданным строкой id.
     * @param dishIdString строка с id блюда. Если строка не является числом, то ничего не произойдет.
     * @param isActivity видимость блюда
     */
    protected void turnActivityMenuDishByIdString(String dishIdString, boolean isActivity) {
        int dishId = -1;
        try {
            dishId = Integer.parseInt(dishIdString);
        } catch (NumberFormatException e) {
            System.out.println("Wrong dish id");
        }

        if (menu.itemByIdExists(dishId)) {
            menu.setMenuDishActivityById(
                    dishId,
                    isActivity
            );
        }
    }

    /**
     * Создает request сообщение для агента-управляющего и отправляет его.
     */
    private class RequestSupervisor extends OneShotBehaviour {
        // Не static так как отправляет сообщение

        String messageToSend;
        public RequestSupervisor(String message) {
            this.messageToSend = message;
        }

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
                msg.setContent(messageToSend);
                send(msg);
            } catch (FIPAException ex) {
                ex.printStackTrace();
            }
        }
    }


    /**
     * Отвечает за получение и обработку сообщений. Распределяет сообщения по типам и выполняет нужные методы.
     */
    private static class InformMessageReceiver extends CyclicBehaviour {
        public void action() {
            ACLMessage msg = ((VisitorAgent) myAgent).receive(MessageTemplate.MatchPerformative(ACLMessage.INFORM));
            if (msg != null) {

                if (msg.getContent().startsWith("add_dish_to_order_by_id")) {
                    // TODO: добавляем блюдо в заказ
                    System.out.println("dish added to order");
                } else if (msg.getContent().startsWith("remove_dish_from_order_by_id")) {
                    // TODO: удаляем блюдо из заказа
                    System.out.println("dish removed from order");
                } else if ("cancel_order".equals(msg.getContent())) {
                    // TODO: отменяем заказ
                    System.out.println("order canceled");
                } else if (msg.getContent().startsWith("turn_off_menu_dish_by_id")) {
                    // Делаем блюдо неактивным
                    ((VisitorAgent) myAgent).turnActivityMenuDishByIdString(
                            msg.getContent().split(" ")[1],
                            false
                    );
                } else if (msg.getContent().startsWith("turn_on_menu_dish_by_id")) {
                    // Делаем блюдо активным
                    ((VisitorAgent) myAgent).turnActivityMenuDishByIdString(
                            msg.getContent().split(" ")[1],
                            true
                    );
                } else if ("print_menu".equals(msg.getContent())) {
                    // Выводим меню в консоль
                    ((VisitorAgent) myAgent).outMenu();
                } else {
                    // Если получаем объект в сообщении, то обрабатываем его
                    try {
                        if (msg.getContentObject() instanceof Menu) {
                            Menu menu = (Menu) msg.getContentObject();
                            ((VisitorAgent) myAgent).setMenu(menu);
                        } else if (msg.getContentObject() instanceof OrderAgent) {
                            OrderAgent orderAgent = (OrderAgent) msg.getContentObject();
                            ((VisitorAgent) myAgent).setOrderAgent(orderAgent);
                            System.out.println("Длина агента заказа " + ((VisitorAgent) myAgent).orderAgent.dishesAndDrinkListLength());
                        }
                    } catch (UnreadableException ex) {
                        System.out.println("Неизвестный запрос");
                    }
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
