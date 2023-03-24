package Restaurant.Agents;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.ArrayList;
import java.util.List;

public class OrderAgent extends Agent {

    private final List<String> orderList = new ArrayList<>();

    @Override
    protected void setup() {
        // Регистрация агента в системе JADE
        registerAgent();

        // Поведение агента - обработка сообщений от управляющего агента
        addBehaviour(new CyclicBehaviour(this) {
            @Override
            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    // Обработка сообщения от управляющего агента
                    String content = msg.getContent();
                    switch (msg.getPerformative()) {
                        case ACLMessage.REQUEST:
                            // Отправка сообщения агенту посетителю о времени ожидания заказа
                            ACLMessage reply = new ACLMessage(ACLMessage.INFORM);
                            reply.addReceiver(msg.getSender());
                            reply.setContent("Estimated wait time: 10 minutes");
                            send(reply);
                            break;
                        case ACLMessage.QUERY_REF:
                            // Запрос у агентов процессов о времени ожидания готовности блюд / напитков
                            ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
                            request.addReceiver(getAID("ProcessAgent"));
                            request.setContent(content);
                            send(request);
                            break;
                        case ACLMessage.INFORM_REF:
                            // Обработка ответа от агентов процессов о времени ожидания готовности блюд / напитков
                            String[] orders = content.split(",");
                            for (String order : orders) {
                                orderList.add(order.trim());
                            }
                            break;
                        case ACLMessage.CANCEL:
                            // Отмена резервирования определенного ресурса (при отмене заказа)
                            String[] cancelOrders = content.split(",");
                            for (String order : cancelOrders) {
                                orderList.remove(order.trim());
                            }
                            break;
                    }
                } else {
                    block();
                }
            }
        });
    }

    private void registerAgent() {
        // Регистрация агента в системе JADE
        // ...
    }

    @Override
    protected void takeDown() {
        for (String order : orderList) {
            ACLMessage cancel = new ACLMessage(ACLMessage.CANCEL);
            cancel.addReceiver(getAID("ProductAgent"));
            cancel.setContent(order);
            send(cancel);
        }
    }
}
