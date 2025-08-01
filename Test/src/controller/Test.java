package controller;
import javax.servlet.http.HttpServletRequest;

import annotationClass.*;
import annotationClass.Numeric;
import modelClass.*;
import model.*;
import service.*;
import exception.*;
import util.*;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

@AnnotationController()
public class Test {
    private String name = "Anthony";

    @Get
    @Url(path = "kozy")
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    @Get
    @Url(path = "test")
    public ModelView getMv() {
        ModelView mv = new ModelView();
        mv.setUrl("test.jsp");
        mv.addObject("message", "Sprint4");
        mv.addObject("etat", "termine");
        return mv;
    }

    @Get
    @Url(path = "form")
    public ModelView Formulaire(){
        ModelView modelView=new ModelView();
        modelView.setUrl("/formulaire.jsp");
        return modelView;
    }

    @Get
    @Url(path = "login")
    public ModelView login(){
        ModelView modelView=new ModelView();
        modelView.setUrl("/login.jsp");
        return modelView;
    }

    @Post
    @Url(path = "demo")
    public ModelView saveUser(@ParamName("user") User user, @ParamName("string") String test, HttpServletRequest request) {
        ModelView modelView = new ModelView();
        // Récupérer les erreurs de validation depuis la requête
        ValidationError validationError = (ValidationError) request.getAttribute("validationError");

        // Si des erreurs sont détectées, retourner au formulaire avec les erreurs
        if (validationError != null) {
            modelView.setUrl("/formulaire.jsp"); 
            modelView.addObject("errors", validationError.getErrors());
            modelView.addObject("previousValues", validationError.getPreviousValues());
            return modelView;
        }

        // Si la validation réussit, traiter les données
        modelView.setUrl("/affichage.jsp");
        modelView.addObject("user", user);
        modelView.addObject("test", test);
        return modelView;
    }

    @Post
    @Url(path = "log")
    public ModelView log(HttpServletRequest request) {
        String identifiant = request.getParameter("identifiant");
        String mdp = request.getParameter("mdp");

        // Supposons que vous ayez une classe `UserService` pour gérer la connexion
        UserService userService = new UserService();
        User user = userService.authentifier(identifiant, mdp); 

        ModelView modelView = new ModelView();
        modelView.setUrl("/checkSession.jsp");

        MySession session = new MySession(request);
        
        if (user != null) {
            session.add("user", user);
            session.add("message", "Bienvenue, " + user.getName());
        } else {
            modelView.addObject("message", "Identifiants incorrects !");
        }
        return modelView;
    }

    @Get
    @Url(path = "log")
    public ModelView log2(HttpServletRequest request) {
        String identifiant = request.getParameter("identifiant");
        String mdp = request.getParameter("mdp");

        // Supposons que vous ayez une classe `UserService` pour gérer la connexion
        UserService userService = new UserService();
        User user = userService.authentifier(identifiant, mdp); 

        ModelView modelView = new ModelView();
        modelView.setUrl("/checkSession.jsp");

        MySession session = new MySession(request);
        
        if (user != null) {
            session.add("user", user);
            session.add("message", "Bienvenue, " + user.getName());
        } else {
            modelView.addObject("message", "Identifiants incorrects !");
        }
        return modelView;
    }

    @Url(path = "deconnexion")
    public ModelView deconnexion(HttpServletRequest request) {
        MySession session = new MySession(request);
        session.destroy();  // Détruire la session
        ModelView modelView = new ModelView();
        modelView.setUrl("/login.jsp");
        return modelView;
    }

    @Post
    @Url(path = "demo2")
    public ModelView demoUser(@ParamName("user") User user, @ParamName("date") String date, HttpServletRequest request) {
        ModelView modelView = new ModelView();

        // Récupérer les erreurs de validation depuis la requête
        ValidationError validationError = (ValidationError) request.getAttribute("validationError");

        // Si des erreurs sont détectées, retourner au formulaire avec les erreurs
        if (validationError != null && validationError.hasErrors()) {
        modelView.setUrl("/formulaire.jsp"); // Retourner au formulaire
            modelView.addObject("errors", validationError.getErrors());
            modelView.addObject("previousValues", validationError.getPreviousValues());
            return modelView;
        }

        // Si la validation réussit, traiter les données
        modelView.setUrl("/affichage.jsp");
        modelView.addObject("user", user);
        modelView.addObject("date", date);
        return modelView;
    }

    @Get
    @Url(path = "checkSession")
    public ModelView checkSession(HttpServletRequest request) {
        MySession session = new MySession(request);
        ModelView modelView = new ModelView();
        modelView.setUrl("/checkSession.jsp");

        // Récupérer les informations utilisateur de la session
        Object user = session.get("user");
        if (user != null) {
            modelView.addObject("message", "Utilisateur connecté : " + user.toString());
        } else {
            modelView.addObject("message", "Aucun utilisateur connecté");
        }
        return modelView;
    }

    
    @Get
    @Restapi
    @Url(path = "rest-api")
    public ModelView getRestApi() {
        ModelView mv = new ModelView();
        mv.addObject("message", "Ceci est une réponse JSON");
        return mv;
    }

    
    @Get
    @Restapi
    @Url(path = "rest-api-string")
    public String getRestApiString() {
        return "Ceci est une réponse JSON simple";
    }
}
