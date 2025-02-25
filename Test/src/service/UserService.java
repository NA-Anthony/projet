package service;
import model.*;
public class UserService {
    public User authentifier(String identifiant, String mdp) {
        // Remplacez cette partie par votre logique de base de données
        if ("Anthony".equals(identifiant) && "123456".equals(mdp)) {
            User user = new User();
            user.setName(identifiant);
            return user;
        }
        return null;
    }
}
