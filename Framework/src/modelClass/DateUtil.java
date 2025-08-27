package modelClass;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;

public class DateUtil {
    // Méthode pour obtenir la date du début du mois
    public static LocalDate getDebutMois(int annee, int mois) {
        return LocalDate.of(annee, mois, 1); // Le premier jour du mois
    }

    // Méthode pour obtenir la date de fin du mois
    public static LocalDate getFinMois(int annee, int mois) {
        return LocalDate.of(annee, mois, 1)
                .withDayOfMonth(LocalDate.of(annee, mois, 1).lengthOfMonth()); // Dernier jour du mois
    }
}
