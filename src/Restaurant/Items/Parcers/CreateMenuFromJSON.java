package Restaurant.Items.Parcers;

import Restaurant.Items.Menu;

import org.json.JSONObject;
import org.json.JSONArray;


/**
 * Класс фабрика для создания объекта меню из JSON
 */
public class CreateMenuFromJSON {
    public static Menu create(JSONObject jsonObject) {
        Menu menu = Menu.getInstance();

        JSONArray menuDishesArray = jsonObject.getJSONArray("menu_dishes");
        for (int i = 0; i < menuDishesArray.length(); i++) {
            JSONObject menuDish = menuDishesArray.getJSONObject(i);

            int menuDishId = menuDish.getInt("menu_dish_id");
            int menuDishCard = menuDish.getInt("menu_dish_card");
            int menuDishPrice = menuDish.getInt("menu_dish_price");
            boolean menuDishActive = menuDish.getBoolean("menu_dish_active");

            menu.addItem(menuDishId, menuDishCard, menuDishPrice, menuDishActive);
        }

        return menu;
    }
}
