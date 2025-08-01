package util;

import annotationClass.*;
import exception.*;
import java.lang.reflect.Field;
import modelClass.*;
import java.util.Map;

public class Validator {

    public static ValidationError validateWithInputs(Object object, Map<String, String> inputs) throws IllegalAccessException {
        ValidationError validationError = new ValidationError();
        Class<?> clazz = object.getClass();

        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            String fieldName = field.getName();
            String rawValue = inputs.get(fieldName);

            // Toujours sauvegarder la valeur précédente
            validationError.addError(fieldName, null, rawValue);
            // Validation des champs requis
            if (rawValue == null || rawValue.isEmpty()) {
                if (field.isAnnotationPresent(Required.class)) {
                    Required required = field.getAnnotation(Required.class);
                    validationError.addError(fieldName, required.message(), rawValue);
                }
                continue;
            }

            try {
                // Validation des types numériques
                if (field.isAnnotationPresent(Numeric.class)) {
                    Numeric numericAnnotation = field.getAnnotation(Numeric.class);
                    try {
                        Utility.parseValue(rawValue, field.getType());
                    } catch (Exception e) {
                        validationError.addError(fieldName, numericAnnotation.message(), rawValue);
                        continue;
                    }
                }

                // Validation des dates
                if (field.isAnnotationPresent(Date.class)) {
                    Date dateAnnotation = field.getAnnotation(Date.class);
                    try {
                        Utility.parseValue(rawValue, field.getType());
                    } catch (Exception e) {
                        validationError.addError(fieldName, dateAnnotation.message(), rawValue);
                        continue;
                    }
                }

                // Validation de la longueur minimale
                if (field.isAnnotationPresent(MinLength.class)) {
                    MinLength minLengthAnnotation = field.getAnnotation(MinLength.class);
                    if (rawValue.length() < minLengthAnnotation.value()) {
                        validationError.addError(fieldName, minLengthAnnotation.message(), rawValue);
                        continue;
                    }
                }

                // Validation de la longueur maximale
                if (field.isAnnotationPresent(MaxLength.class)) {
                    MaxLength maxLengthAnnotation = field.getAnnotation(MaxLength.class);
                    if (rawValue.length() > maxLengthAnnotation.value()) {
                        validationError.addError(fieldName, maxLengthAnnotation.message(), rawValue);
                        continue;
                    }
                }

                // Si aucune erreur, setter la valeur dans l'objet
                Object parsedValue = Utility.parseValue(rawValue, field.getType());
                field.set(object, parsedValue);

            } catch (Exception e) {
                validationError.addError(fieldName, "Erreur lors de la validation ou du parsing", rawValue);
            }
        }

        return validationError.allErrorsAreNull() ? null : validationError;
    }
}