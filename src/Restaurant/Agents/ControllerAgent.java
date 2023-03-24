package Restaurant.Agents;

/**
 * Класс управляющего агента. Может быть только один экземпляр этого класса.
 */
public class ControllerAgent {
    private static ControllerAgent instance;

    public static synchronized ControllerAgent getInstance() {
        if (instance == null) {
            instance = new ControllerAgent();
        }
        return instance;
    }

    private ControllerAgent() {

    }
}
