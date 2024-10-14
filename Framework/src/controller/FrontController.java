package controller;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.lang.reflect.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import annotationClass.*;
import modelClass.*;

public class FrontController extends HttpServlet {
    private HashMap<String, Mapping> hashMap = new HashMap<>();

    @Override
    public void init() throws ServletException {
        super.init(); // Appeler la méthode init de la superclasse HttpServlet
        try {
            initialisation();
        } catch (Exception e) {
            throw new ServletException(e.getMessage());
        }
    }

    private void initialisation() throws Exception {
        // Récupération des classes et méthodes annotées
        String packageName = getServletContext().getInitParameter("AnnotationController");
        List<Class<?>> classes = FrontController.getClasses(packageName);

        if (classes.size()==0) {
            throw new ServletException("Package vide ou inexistant");
        }

        for (int j = 0; j < classes.size(); j++) {
            if (this.hasAnnotation(classes.get(j), AnnotationController.class)) {
                Method[] methods = classes.get(j).getMethods();
                for (Method method : methods) {
                    if (method.isAnnotationPresent(Get.class)) {
                        String url = method.getAnnotation(Get.class).value();
                        String className = classes.get(j).getName();
                        String methodName = method.getName();

                        // Création d'une instance de Mapping et ajout au HashMap
                        Mapping mapping = new Mapping(className, methodName);
                        if(hashMap.containsKey(url)) {
                            throw new ServletException("Duplication d'url");
                        }
                        hashMap.put(url, mapping);
                    }
                }
            }
        }
    }

    private boolean hasAnnotation(Class<?> classes, Class<? extends AnnotationController> annotation) {
        return classes.isAnnotationPresent(annotation);
    }

    protected String getURLSplit(String str) {
        String[] string = str.split("/");
        return string[4];
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //URI /... et URL tout url
        String url = request.getRequestURL().toString();
        PrintWriter out = response.getWriter();
        out.println(request.getRequestURI().toString());
        String lastPart = getURLSplit(url);
        // Vérification si l'URL existe dans le HashMap
        if (hashMap.containsKey(lastPart)) {
            Mapping mapping = hashMap.get(lastPart);
            out.println("Controller: " + mapping.getClasse() + ", Methode: " + mapping.getMethode());

            // Récupération de l'instance de la classe du contrôleur
            try {
                Class<?> controllerClass = Class.forName(mapping.getClasse());
                Object controllerInstance = controllerClass.getDeclaredConstructor().newInstance();

                Method[] declarMethods = controllerClass.getDeclaredMethods();

                Method maMethod = null;

                for (Method method : declarMethods) {
                    if (method.getName().equals(mapping.getMethode())) {
                        maMethod = method;
                        break;
                    }
                }

                Parameter[] parameters = maMethod.getParameters();
                Object[] args = new Object[parameters.length];

                ArrayList<String> parameterNames = new ArrayList<>();
                String paramValue = null;

                for (int i = 0; i < parameters.length; i++) {
                    Parameter parameter = parameters[i];
                    parameterNames.add(parameter.getName());
                    if (Utility.isPrimitive(parameter.getType())) {
                        if (parameter.isAnnotationPresent(ParamName.class)) {
                            ParamName annotation = parameter.getAnnotation(ParamName.class);
                            // Utiliser le nom spécifié dans l'annotation pour récupérer la valeur de la requête
                            paramValue = request.getParameter(annotation.value());
                            args[i] = paramValue;
                        }
                        else{
                            throw new ServletException("ETU002444: Erreur");
                        }
                    }
                    else {
                        if (parameter.isAnnotationPresent(ParamName.class)) {
                            // Vérifier si le paramètre est un objet
                            Class<?> parameterType = parameter.getType();
                                                                    
                            Object obj = parameterType.getDeclaredConstructor().newInstance();
                            Field[] fields = obj.getClass().getDeclaredFields();
                            Method[] methods = obj.getClass().getDeclaredMethods();

                            for (Field field : fields) {
                                Object value = Utility.parseValue(request.getParameter(parameter.getAnnotation(ParamName.class).value() + "." + field.getName()), field.getType());
                                setObjectField(obj, methods, field, value);
                            }
                            args[i] = obj;
                        }
                        else {
                            // Vérifier si le paramètre est un objet
                            Class<?> parameterType = parameter.getType();
                                                                    
                            Object obj = parameterType.getDeclaredConstructor().newInstance();
                            Field[] fields = obj.getClass().getDeclaredFields();
                            Method[] methods = obj.getClass().getDeclaredMethods();

                            for (Field field : fields) {
                                // System.out.println(parameter.getName() + "." + field.getName());
                                Object value = Utility.parseValue(request.getParameter(parameter.getName() + "." + field.getName()), field.getType());
                                setObjectField(obj, methods, field, value);
                            }
                            args[i] = obj;
                            
                        }
                        
                    }
                }
                // Invocation de la méthode
                Object result = maMethod.invoke(controllerInstance,args);
                switch (result) {
                    case String string -> {
                        response.setContentType("text/plain");
                        out.println(string);
                    }
                    case ModelView mv -> {
                        String viewUrl = mv.getUrl();
                        HashMap<String, Object> data = mv.getData();
                        // Transférer les données vers la vue
                        for (String key : data.keySet()) {
                            request.setAttribute(key, data.get(key));
                        }
                        try {
                            // Faire une redirection vers la vue associée
                            request.getRequestDispatcher(viewUrl).forward(request, response);
                        } catch (ServletException e) {
                            // En cas d'erreur lors de la redirection, renvoyer une erreur 500
                            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                            out.println("<h1>500 Internal Server Error</h1>");
                            out.println(
                                    "<p>Une erreur s'est produite lors de l'appel à la vue : " + e.getMessage() + "</p>");
                            e.printStackTrace(out);
                        }
                    }
                    default -> throw new ServletException("Ce type de retour ne peut pas etre gere pour le moment");
                }
            } catch (Exception e) {
                out.println("Erreur lors de l'invoquation de la méthode: " + e.getMessage());
            }
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    public static Object setObjectField(Object obj, Method[] methods, Field field, Object value) throws Exception {
        String setterMethod = "set" + Utility.capitalize(field.getName());

        for (Method method : methods) {
            if (!method.getName().equals(setterMethod)) {
                continue;
            }   
            return method.invoke(obj, value);
        }
        throw new Exception("Aucun setter trouvé pour l'attribut: " + field.getName());
    }

    private void printClasses(PrintWriter printWriter, List<String> list) {
        for (int i = 0; i < list.size(); i++) {
            printWriter.println(list.get(i));
        }
    }

    public static List<Class<?>> getClasses(String packageName) {
        List<Class<?>> classes = new ArrayList<>();
        URL path = Thread.currentThread().getContextClassLoader().getResource(packageName.replace('.', File.separatorChar));
        if (path == null) {
            return classes;
        }
        File directory;
        try {
            directory = new File(URLDecoder.decode(path.getFile(), "UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
            return classes;
        }

        if (!directory.exists()) {
            return classes;
        }

        collectClasses(packageName, directory, classes);
        return classes;
    }

    private static void collectClasses(String packageName, File directory, List<Class<?>> classes) {
        File[] files = directory.listFiles();
        if (files == null) {
            return;
        }
        for(File file : files) {
            if(file.isDirectory()) {
                collectClasses(packageName + '.' + file.getName(), file, classes);
            }
            else if(file.getName().endsWith(".class"))
            {
                System.out.println("Error");
                try {
                    String className = packageName + '.' + file.getName().substring(0, file.getName().length()-6);
                    classes.add(Class.forName(className));
                } catch (Exception e) {
                    // TODO: handle exception
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }
}
