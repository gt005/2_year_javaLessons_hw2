package Restaurant.Items;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс синглетон меню. Содержит список блюд
 */
public class Menu implements Serializable {
    private static final long serialVersionUID = 1L;
    private static Menu instance;
    private final List<Item> items;

    private Menu() {
        items = new ArrayList<>();
    }

    public static synchronized Menu getInstance() {
        if (instance == null) {
            instance = new Menu();
        }
        return instance;
    }

    public void addItem(int menuDishId, int menuDishCard, int menuDishPrice, boolean menuDishActive) {
        items.add(new Item(menuDishId, menuDishCard, menuDishPrice, menuDishActive));
    }

    public int length() {
        return items.size();
    }

    public int getMenuDishId(int index) {
        return items.get(index).menuDishId;
    }

    public int getMenuDishCard(int index) {
        return items.get(index).menuDishCard;
    }

    public int getMenuDishPrice(int index) {
        return items.get(index).menuDishPrice;
    }

    public boolean getMenuDishActive(int index) {
        return items.get(index).menuDishActive;
    }

    /**
     * Устанавливает активность блюда по его id. Если id не найден, то ничего не делает.
     * @param id id блюда
     * @param menuDishActive активность для показа блюда
     */
    public void setMenuDishActivityById(int id, boolean menuDishActive) {
        System.out.println("setMenuDishActivityById " + id + " " + menuDishActive);
        for (Item item : items) {
            if (item.menuDishId == id) {
                item.menuDishActive = menuDishActive;
                return;
            }
        }
    }

    public boolean itemByIdExists(int menuDishId) {
        for (Item item : items) {
            if (item.menuDishId == menuDishId) {
                return true;
            }
        }
        return false;
    }

    private static class Item implements Serializable {
        private static final long serialVersionUID = 1L;
        public int menuDishId;
        public int menuDishCard;
        public int menuDishPrice;
        public boolean menuDishActive;

        public Item(int menuDishId, int menuDishCard, int menuDishPrice, boolean menuDishActive) {
            this.menuDishId = menuDishId;
            this.menuDishCard = menuDishCard;
            this.menuDishPrice = menuDishPrice;
            this.menuDishActive = menuDishActive;
        }
    }
}

