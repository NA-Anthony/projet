package test;

import model.User;
import util.Validator;
import exception.ValidationError;

import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        // Créer une instance de User
        User user = new User();

        // Simuler des données brutes provenant d'un formulaire
        Map<String, String> inputs = new HashMap<>();
        inputs.put("name", "John");
        inputs.put("age", "douze");
        inputs.put("birthDate", "2023-12-01");
        inputs.put("password", "1234567");

        try {
            // Valider les données avec Validator
            ValidationError validationError = Validator.validateWithInputs(user, inputs);

            // Vérifier si des erreurs sont présentes
            if (validationError != null && validationError.hasErrors()) {
                System.out.println("Erreurs détectées :");
                Map<String, String> errors = validationError.getErrors();

                // Accéder à des erreurs spécifiques
                System.out.println("Erreur pour 'name' : " + errors.get("name"));
                System.out.println("Erreur pour 'age' : " + errors.get("age"));
                System.out.println("Erreur pour 'birthDate' : " + errors.get("birthDate"));
                System.out.println("Erreur pour 'password' : " + errors.get("password"));

                // Afficher les valeurs précédentes
                System.out.println("Valeurs précédentes :");
                validationError.getPreviousValues().forEach((key, value) -> {
                    System.out.println(key + ": " + value);
                });
            } else {
                System.out.println("Aucune erreur détectée !");
                System.out.println("Utilisateur validé : " + user);
            }
        } catch (IllegalAccessException e) {
            System.err.println("Erreur lors de la validation : " + e.getMessage());
        }
    }
}

// java -cp "/Users/nakanyanthony/Documents/GitHub/projet/Test/temp-src/WEB-INF/classes:/Users/nakanyanthony/Documents/GitHub/projet/Test/temp-src/WEB-INF/lib/*" test.Main