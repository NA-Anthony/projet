package controller;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.lang.reflect.Method;
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

                // Récupération de la méthode à invoquer
                Method method = controllerClass.getMethod(mapping.getMethode());
                // Invocation de la méthode
                Object result = method.invoke(controllerInstance);
                out.println((String)result);
            } catch (Exception e) {
                out.println("Erreur lors de l'invoquation de la méthode: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            out.println("URL non existante");
        }
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
