package Restaurant.Agents;

import Restaurant.Agents.DishAndDrinkAgent;

import Restaurant.Behaviors.RegisterInDFBehaviour;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.ACLMessage;

import java.util.ArrayList;
import java.util.List;

public class OrderAgent extends Agent {
    private List<DishAndDrinkAgent> dishesAndDrinks;

    public OrderAgent() {
        addBehaviour(new RegisterInDFBehaviour(this, "Order", "Supervisor"));
        dishesAndDrinks = new ArrayList<>();
        addBehaviour(new InformMessageReceiver());
    }

    /**
     * Получить размер списка блюд и напитков.
     * @return размер списка блюд и напитков
     */
    public int dishesAndDrinkListLength() {
        return dishesAndDrinks.size();
    }

    /**
     * Получить блюдо или напиток по индексу из списка блюд и напитков.
     * @param index индекс
     * @return блюдо или напиток
     */
    public DishAndDrinkAgent getDishAndDrinkByIndex(int index) {
        return dishesAndDrinks.get(index);
    }

    @Override
    protected void setup() {
        System.out.println("Order agent " + getAID().getName() + " is ready.");
    }

    @Override
    protected void takeDown() {
        System.out.println("Order agent " + getAID().getName() + " terminating.");
    }

    private static class InformMessageReceiver extends CyclicBehaviour {
        @Override
        public void action() {
            ACLMessage msg = myAgent.receive(MessageTemplate.MatchPerformative(ACLMessage.INFORM));
            if (msg != null) {
                if (msg.getContent().startsWith("add")) {
                    // TODO: Нужно зарезервировать на складе и склад вернет нам объект блюда или напитка
                } else if (msg.getContent().startsWith("remove")) {
                    // TODO: Нужно освободить на складе и склад вернет нам объект блюда или напитка
                } else if (msg.getContent().startsWith("prepare_time_answer")) {
                    // TODO: Получение времени приготовления блюда или напитка от процесса
                } else if (msg.getContent().startsWith("add_answer")) {
                    // TODO: Получение ответа от склада о том, что блюдо или напиток зарезервирован
                }
            } else {
                block();
            }
        }
    }

}
