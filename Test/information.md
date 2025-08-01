
# Sprint 12 - Création des pages d'exception  

## Exemple
- Erreur 404
- ...

# Sprint 13 - Validation des Attributs  

## 🎯 Objectif  
- Implémenter la validation des valeurs des attributs d'une classe.  

## 🔹 Démarche  
1. Créer des annotations de validation (ex: `@Required`).
2. Développer une classe `Validation` pour centraliser la logique et les contraintes associées aux annotations.  

## ⚠️ Remarque  
- Une exception doit être levée si une valeur ne respecte pas les contraintes définies.  

## 🛠️ Exemple d'utilisation dans un contrôleur  
```java
public class Test {
    @Required
    public int age;
}
```
# Sprint 14 - Affichage des Erreurs dans le Formulaire  

## 🎯 Objectif  
- Afficher les erreurs dans le formulaire en cas de non-respect des validations de sprint 13 tout en gardant les anciennes valeurs des champs.  

## 🔹 Démarche  
1. Stocker les erreurs dans une `List<String, String>` avec le nom de l'attribut comme clé.
2. Utiliser `wrappedRequest` pour changer la méthode en GET lors du renvoi à la page précédente.

## ⚠️ Remarque  
- Pour le renvoi à la page précédente, utiliser `req.getHeader("Referer")`.

## 🛠️ Exemple d'utilisation dans un contrôleur  
```java
HttpServletRequest wrappedRequest = new HttpServletRequestWrapper(req) {
    @Override
    public String getMethod() {
        return "GET"; 
    }
};
```

# Sprint 15 - Authentification au Niveau des Fonctions  

## 🎯 Objectif  
- Implémenter l'authentification au niveau des fonctions dans les contrôleurs.  

## 🔹 Démarche  
1. Créer une annotation `Autorisation`.
2. Vérifier s'il y a une autorisation dans la fonction.
3. Comparer l'autorisation de la fonction avec celle de l'utilisateur définie dans `web.xml`.
4. Exécuter la fonction si les autorisations correspondent, sinon lever une exception pour autorisation insuffisante.

## ⚠️ Remarque  
- L'annotation `Autorisation` sera composée de `string[]` pour ajouter plusieurs conditions.
- L'autorisation de l'utilisateur sera stockée dans la session avec une clé définie dans `web.xml`.

## 🛠️ Exemple d'utilisation dans un contrôleur  
```java
@Autorisation() // Définir l'autorisation
public void effet() {
    // Définir la fonction
}
```
# Sprint 16 - Authentification au Niveau des Classes  

## 🎯 Objectif  
- Implémenter l'authentification au niveau des classes dans les contrôleurs.  

## 🔹 Démarche  
1. Modifier l'annotation `Autorisation` pour qu'elle puisse cibler aussi les classes.
2. Appliquer la même logique que pour le sprint 15, mais cette fois-ci pour les classes.

## ⚠️ Remarque  
- La logique d'authentification au niveau des classes est similaire à celle des fonctions, mais cible les classes.

## 🛠️ Exemple d'utilisation dans un contrôleur  
```java
@Autorisation() // Définir l'autorisation
public class AdministrateurControlleur {
    // Définir la classe
}
```
