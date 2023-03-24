import Restaurant.Addons.JsonFileHandler;

import Restaurant.Items.Menu;

import Restaurant.Items.Parcers.CreateMenuFromJSON;
import org.json.JSONObject;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        JSONObject jsonObject = JsonFileHandler.readJsonFromFile("/Users/karimhamid/IdeaProjects/kpo_IDZ_2year/input_data/menu_dishes.txt");

        Menu menu = CreateMenuFromJSON.create(jsonObject);

        for (int i = 0; i < menu.length(); i++) {
            System.out.println(
                    menu.getMenuDishId(i) + " " +
                    menu.getMenuDishCard(i) + " " +
                    menu.getMenuDishPrice(i) + " " +
                    menu.getMenuDishActive(i) + "\n"
            );
        }
    }
}
