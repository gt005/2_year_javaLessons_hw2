package Restaurant.Agents;

import jade.core.Agent;

public class DishAndDrinkAgent extends Agent {
    @Override
    protected void setup() {
        System.out.println("Dish and drink agent " + getAID().getName() + " is ready.");
    }

    @Override
    protected void takeDown() {
        System.out.println("Dish and drink agent " + getAID().getName() + " terminating.");
    }
}
