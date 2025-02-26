package controller;
import javax.servlet.http.HttpServletRequest;

import annotationClass.*;
import modelClass.*;
import model.*;
import service.*;

@AnnotationController()
public class Test {
    private String name = "Anthony";

    @Get("kozy")
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    @Get("test")
    public ModelView getMv() {
        ModelView mv = new ModelView();
        mv.setUrl("test.jsp");
        mv.addObject("message", "Sprint4");
        mv.addObject("etat", "termine");
        return mv;
    }

    @Get("form")
    public ModelView Formulaire(){
        ModelView modelView=new ModelView();
        modelView.setUrl("/formulaire.jsp");
        return modelView;
    }

    @Get("login")
    public ModelView login(){
        ModelView modelView=new ModelView();
        modelView.setUrl("/login.jsp");
        return modelView;
    }

    @Get("demo")
    public ModelView saveUser(@ParamName("user")User user, @ParamName("string") String test) { 
        ModelView modelView = new ModelView();
        modelView.setUrl("/affichage.jsp");
        modelView.addObject("user", user);
        modelView.addObject("test", test);
        return modelView;
    }

    @Post("log")
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

    @Get("log1")
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


    @Get("deconnexion")
    public ModelView deconnexion(HttpServletRequest request) {
        MySession session = new MySession(request);
        session.destroy();  // Détruire la session
        ModelView modelView = new ModelView();
        modelView.setUrl("/login.jsp");
        return modelView;
    }

    @Get("demo2")
    public ModelView demoUser(@ParamName("user")User user,String test) {
        ModelView modelView = new ModelView();
        modelView.setUrl("/affichage.jsp");
        modelView.addObject("user", user);
        modelView.addObject("string", test);
        return modelView;
    }

    @Get("checkSession")
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

    @Restapi
    @Get("rest-api")
    public ModelView getRestApi() {
        ModelView mv = new ModelView();
        mv.addObject("message", "Ceci est une réponse JSON");
        return mv;
    }

    @Restapi
    @Get("rest-api-string")
    public String getRestApiString() {
        return "Ceci est une réponse JSON simple";
    }
}
