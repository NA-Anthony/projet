package modelClass;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.time.LocalDate;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javax.servlet.http.*;

public class Utility {
    private static final Gson gson = new Gson();
    public static boolean isPrimitive(Class<?> type) {
        return type.isPrimitive() || 
               type.equals(Integer.class) || 
               type.equals(Double.class) || 
               type.equals(Float.class) || 
               type.equals(Long.class) || 
               type.equals(Short.class) || 
               type.equals(Byte.class) || 
               type.equals(Boolean.class) || 
               type.equals(Character.class);
    }

    public static String capitalize(String str) {
        char firstChar = str.charAt(0);
        return Character.toUpperCase(firstChar) + str.substring(1);
    }

    public static Object parseValue(String value, Class<?> type) {
        if (value == null || value.trim().isEmpty()) {
            return null; // Retourne null si la valeur est vide
        }

        try {
            if (type.equals(Integer.class) || type.equals(int.class)) {
                return Integer.parseInt(value);
            } else if (type.equals(Double.class) || type.equals(double.class)) {
                return Double.parseDouble(value);
            } else if (type.equals(Boolean.class) || type.equals(boolean.class)) {
                return Boolean.parseBoolean(value);
            } else if (type.equals(LocalDate.class)) {
                // Conversion de String en LocalDate
                return LocalDate.parse(value, java.time.format.DateTimeFormatter.ISO_LOCAL_DATE);
            } else {
                return value; // Retourne la chaîne brute pour les autres types
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Impossible de convertir la valeur '" + value + "' en " + type.getSimpleName(), e);
        } catch (java.time.format.DateTimeParseException e) {
            throw new IllegalArgumentException("Impossible de convertir la valeur '" + value + "' en LocalDate", e);
        }
    }

    public static String objectToJson(Object obj) {
        MyJson myJson = new MyJson();
        return myJson.getGson().toJson(obj);
    }

    // Méthode pour convertir une chaîne JSON en objet Java
    public static <T> T jsonToObject(String jsonString, Class<T> clazz) {
        MyJson myJson = new MyJson();
        return myJson.getGson().fromJson(jsonString, new TypeToken<T>(){}.getType());
    }

    public static String modelViewToJson(ModelView modelView) {
        MyJson myJson = new MyJson();
        return myJson.getGson().toJson(modelView.getData());
    }

    // Méthode pour transformer les données du ModelView en JSON séparé
    public static String modelViewDataToJson(ModelView modelView) {
        HashMap<String, Object> data = modelView.getData();
        MyJson myJson = new MyJson();
        return myJson.getGson().toJson(data);
    }

    public static ChangeSession HttpSessionToCustomSession(HttpSession httpSession) {
        ChangeSession changeSession = new ChangeSession();

        // Copier tous les attributs de la session HTTP vers la session personnalisée
        Enumeration<?> attrNames = httpSession.getAttributeNames();
        while (attrNames.hasMoreElements()) {
            String attributeName = (String) attrNames.nextElement();
            Object attributeValue = httpSession.getAttribute(attributeName);
            changeSession.add(attributeName, attributeValue);
        }

        return changeSession;
    }

    public static void CustomSessionToHttpSession(ChangeSession changeSession, HttpServletRequest request) {
        // Créer une nouvelle session HTTP
        HttpSession httpSession = request.getSession(true);

        // Ajouter tous les attributs de la session personnalisée à la session HTTP
        for (Map.Entry<String, Object> entry : changeSession.hashMap.entrySet()) {
            httpSession.setAttribute(entry.getKey(), entry.getValue());
        }
        // return httpSession;
    }
}
