package modelClass;

import java.util.Arrays;
import java.util.List;

public class Utility {
    public static boolean isPrimitive(Class<?> clazz) {
        List<Class<?>> primitiveTypes = Arrays.asList(
                boolean.class,
                byte.class,
                short.class,
                char.class,
                int.class,
                long.class,
                float.class,
                double.class,
                String.class
        );

        return primitiveTypes.contains(clazz);
    }

    public static String capitalize(String str) {
        char firstChar = str.charAt(0);
        return Character.toUpperCase(firstChar) + str.substring(1);
    }

    public static Object parseValue(String value, Class<?> type) throws Exception {
        if (type == int.class) {
            return Integer.valueOf(value);
        } else if (type == double.class) {
            return Double.valueOf(value);
        } else if (type == boolean.class) {
            return Boolean.valueOf(value);
        } else {
            return value;
        }
    }
}
