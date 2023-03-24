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
import jade.lang.acl.UnreadableException;

/**
 * Агент посетителя ресторана. Может быть несколько экземпляров этого класса.
 * Посетитель может выбирать блюда из меню, добавлять их в заказ, а также удалять из заказа.
 * Посетитель может отменить заказ.
 */
public class VisitorAgent extends Agent {
    protected Menu menu;

    public VisitorAgent() {
        addBehaviour(new RegisterInDFBehaviour(this, "Visitor", "Supervisor"));
        addBehaviour(new MessageReceiver());
        addBehaviour(new RequestMenu());
    }

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

                if (msg.getContent().startsWith("add_dish_to_order_by_id")) {
                    // добавляем блюдо в заказ
                    System.out.println("dish added to order");
                } else if (msg.getContent().startsWith("remove_dish_from_order_by_id")) {
                    // удаляем блюдо из заказа
                    System.out.println("dish removed from order");
                } else if ("cancel_order".equals(msg.getContent())) {
                    // отменяем заказ
                    System.out.println("order canceled");
                } else if (msg.getContent().startsWith("turn_off_menu_dish_by_id")) {
                    ((VisitorAgent) myAgent).turnActivityMenuDishByIdString(
                            msg.getContent().split(" ")[1],
                            false
                    );
                } else if (msg.getContent().startsWith("turn_on_menu_dish_by_id")) {
                    ((VisitorAgent) myAgent).turnActivityMenuDishByIdString(
                            msg.getContent().split(" ")[1],
                            true
                    );
                } else if ("print_menu".equals(msg.getContent())) {
                    ((VisitorAgent) myAgent).outMenu();
                } else {
                    try {
                        if (msg.getContentObject() instanceof Menu) {
                            Menu menu = (Menu) msg.getContentObject();
                            ((VisitorAgent) myAgent).setMenu(menu);
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
