package Restaurant;

import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Класс для работы с сериализации и десериализации JSON объектов в/из файлов
 */
public class JsonFileHandler {

    /**
     * Читает текстовое представление json из файла и десериализирует его в объект.
     * @param fileName имя файла в качестве абсолютного пути
     * @return объект JSONObject, считанный из файла
     * @throws IOException если не удалось прочитать файл
     */
    public static JSONObject readJsonFromFile(String fileName) throws IOException {
        String content = new String(Files.readAllBytes(Paths.get(fileName)));
        return new JSONObject(content);
    }

    /**
     * Сериализует объект JSONObject в текстовое представление и записывает его в файл.
     * @param fileName имя файла в качестве абсолютного пути
     * @param jsonObject объект JSONObject для сериализации
     * @throws IOException если не удалось записать в файл
     */
    public static void writeJsonToFile(String fileName, JSONObject jsonObject) throws IOException {
        try (FileWriter fileWriter = new FileWriter(new File(fileName))) {
            fileWriter.write(jsonObject.toString());
        }
    }
}
