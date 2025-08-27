# Mon Framework Java pour le Web

Ce projet consiste à créer un framework Java personnalisé pour le développement d'applications web. Il inclut des fonctionnalités telles que la gestion des URL, la validation des données, la gestion des sessions, et bien plus encore.

---

## Fonctionnalités principales

### 1. **Gestion des URL**
- Les URLs sont définies à l'aide d'annotations personnalisées comme `@Url`, `@Get`, et `@Post`.
- Le `FrontController` centralise toutes les requêtes et les redirige vers les méthodes appropriées des contrôleurs.

### 2. **Validation des données**
- Les champs des modèles peuvent être validés à l'aide d'annotations comme :
  - `@Required` : Champ obligatoire.
  - `@Numeric` : Champ devant contenir un nombre.
  - `@MinLength` et `@MaxLength` : Validation de la longueur des chaînes.
  - `@Date` : Validation des dates avec un format spécifique.
- Les erreurs de validation sont transmises à la vue pour être affichées à côté des champs concernés.

### 3. **Gestion des sessions**
- Une classe personnalisée `MySession` permet de gérer les sessions utilisateur.
- Les données de session peuvent être ajoutées, mises à jour ou supprimées facilement.

### 4. **Sérialisation JSON**
- Les objets Java peuvent être convertis en JSON et vice-versa grâce à la classe `MyJson` et au support de `Gson`.
- Les dates (`LocalDate`) sont sérialisées et désérialisées avec un format personnalisé.

### 5. **Gestion des erreurs**
- Les erreurs de validation sont centralisées dans la classe `ValidationError`.
- Les exceptions comme `NotFoundException` et `InternalServerErrorException` sont gérées pour fournir des messages d'erreur clairs.

---

## Structure du projet

### **Framework**
- Contient les classes principales du framework, comme :
  - `FrontController` : Contrôleur principal pour gérer les requêtes.
  - `Validator` : Classe utilitaire pour valider les données des modèles.
  - `Utility` : Classe utilitaire pour diverses opérations (parsing, JSON, etc.).
  - `MySession` : Gestion personnalisée des sessions.

### **Test**
- Contient les contrôleurs, modèles, et vues pour tester le framework.
- Exemple de contrôleurs :
  - `Test` : Contrôleur principal avec des méthodes pour gérer les formulaires et les connexions.
- Exemple de modèles :
  - `User` : Modèle utilisateur avec des annotations pour la validation.

---

## Installation et exécution

### **Prérequis**
- Java 8 ou supérieur.
- Apache Tomcat (version 9 ou supérieur).
- Maven (optionnel, pour gérer les dépendances).

### **Étapes**
1. Clonez le dépôt :
   ```bash
   git clone https://github.com/votre-utilisateur/votre-projet.git
   cd votre-projet