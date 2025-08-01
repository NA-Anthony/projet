package exception;
import java.util.HashMap;
import java.util.Map;

public class ValidationError {
    private Map<String, String> errors = new HashMap<>();
    private Map<String, String> previousValues = new HashMap<>();

    // Méthode pour ajouter une erreur et la valeur précédente
    public void addError(String fieldName, String errorMessage, String previousValue) {
        errors.put(fieldName, errorMessage);
        previousValues.put(fieldName, previousValue);
    }

    public void addError(String fieldName, String errorMessage, Object previousValue) {
        errors.put(fieldName, errorMessage);
        previousValues.put(fieldName, previousValue != null ? previousValue.toString() : null);
    }

    // Vérifie s'il y a des erreurs
    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    // Vérifie si tous les messages d'erreur sont null
    public boolean allErrorsAreNull() {
        for (String errorMessage : errors.values()) {
            if (errorMessage != null) {
                return false; // Au moins un message d'erreur n'est pas null
            }
        }
        return true; // Tous les messages d'erreur sont null
    }

    // Retourne la map des erreurs
    public Map<String, String> getErrors() {
        return errors;
    }

    // Retourne la map des valeurs précédentes
    public Map<String, String> getPreviousValues() {
        return previousValues;
    }

    // Fusionne les erreurs d'un autre ValidationError
    public void merge(ValidationError other) {
        if (other != null) {
            this.errors.putAll(other.getErrors());
            this.previousValues.putAll(other.getPreviousValues());
        }
    }

    // Retourne les erreurs formatées sous forme de chaîne
    public String formatErrors() {
        StringBuilder formattedErrors = new StringBuilder();
        for (Map.Entry<String, String> entry : errors.entrySet()) {
            formattedErrors.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        return formattedErrors.toString();
    }

    // Vérifie si un champ spécifique contient une erreur
    public boolean hasError(String fieldName) {
        return errors.containsKey(fieldName);
    }
}

