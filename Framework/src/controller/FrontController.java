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

    // Initialisation des contrôleurs et des méthodes
    private void initialisation() throws Exception {
        String packageName = getServletContext().getInitParameter("AnnotationController");
        List<Class<?>> classes = getClasses(packageName);

        if (classes.isEmpty()) {
            throw new ServletException("Package vide ou inexistant");
        }

        for (Class<?> controllerClass : classes) {
            if (hasAnnotation(controllerClass, AnnotationController.class)) {
                processControllerClass(controllerClass);
            }
        }
    }

    private void processControllerClass(Class<?> controllerClass) throws Exception {
        Method[] methods = controllerClass.getMethods();

        for (Method method : methods) {
            if (method.isAnnotationPresent(Url.class)) {
                processControllerMethod(controllerClass, method);
            }
        }
    }

    private void processControllerMethod(Class<?> controllerClass, Method method) throws Exception {
        String url = method.getAnnotation(Url.class).path(); // Récupérer le chemin de l'URL
        String className = controllerClass.getName();
        String methodName = method.getName();
    
        // Par défaut, on suppose que c'est une méthode GET
        String httpMethod = "GET";
    
        // Créer un nouveau Verb
        Verb verb = new Verb(httpMethod, methodName);
    
        // Enregistrer le mapping
        registerMapping(url, className, verb);
    }

    private void registerMapping(String url, String className, Verb verb) throws ServletException {
        if (hashMap.containsKey(url)) {
            Mapping existingMapping = hashMap.get(url);
            existingMapping.addVerb(verb);
        } else {
            ArrayList<Verb> verbs = new ArrayList<>();
            verbs.add(verb);
            Mapping mapping = new Mapping(className, verbs);
            hashMap.put(url, mapping);
        }
    }

    // Gestion des requêtes
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String url = getURLSplit(request.getRequestURL().toString());
    
        if (hashMap.containsKey(url)) {
            Mapping mapping = hashMap.get(url);
    
            try {
                handleRequestMapping(mapping, request, response);
            } catch (Exception e) {
                throw new ServletException("Erreur lors de l'invocation de la méthode: " + e.getMessage());
            }
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND); // 404 Not Found
        }
    }

    private void handleRequestMapping(Mapping mapping, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String httpMethod = "GET"; // Par défaut, on utilise GET
        Verb verb = mapping.findVerbByType(httpMethod);
    
        if (verb != null) {
            Class<?> controllerClass = Class.forName(mapping.getClasse());
            Object controllerInstance = createControllerInstance(controllerClass, request);
    
            Method method = findMethod(controllerClass, verb.getMethod());
            Object[] args = resolveMethodArguments(method, request);
    
            if (method.isAnnotationPresent(Restapi.class)) {
                handleRestApiMethod(method, controllerInstance, args, response);
            } else {
                handleRegularMethod(method, controllerInstance, args, request, response);
            }
        } else {
            response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED); // 405 Method Not Allowed
        }
    }

    private Object createControllerInstance(Class<?> controllerClass, HttpServletRequest request) throws Exception {
        Object controllerInstance = controllerClass.getDeclaredConstructor().newInstance();

        for (Field field : controllerClass.getDeclaredFields()) {
            if (field.getType().equals(MySession.class)) {
                field.setAccessible(true);
                field.set(controllerInstance, new MySession(request));
            }
        }

        return controllerInstance;
    }

    private Method findMethod(Class<?> controllerClass, String methodName) throws NoSuchMethodException {
        for (Method method : controllerClass.getDeclaredMethods()) {
            if (method.getName().equals(methodName)) {
                return method;
            }
        }
        throw new NoSuchMethodException("Méthode non trouvée: " + methodName);
    }

    // Gestion des arguments
    private Object[] resolveMethodArguments(Method method, HttpServletRequest request) throws Exception {
        Parameter[] parameters = method.getParameters();
        Object[] args = new Object[parameters.length];

        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            if (parameter.getType().equals(HttpServletRequest.class)) {
                args[i] = request;
            } else if (Utility.isPrimitive(parameter.getType())) {
                args[i] = resolvePrimitiveParameter(parameter, request);
            } else {
                args[i] = resolveComplexParameter(parameter, request);
            }
        }

        return args;
    }

    private Object resolvePrimitiveParameter(Parameter parameter, HttpServletRequest request) throws ServletException {
        if (parameter.isAnnotationPresent(Param.class)) {
            Param annotation = parameter.getAnnotation(Param.class);
            return request.getParameter(annotation.value());
        } else {
            throw new ServletException("ETU002444: Erreur");
        }
    }

    private Object resolveComplexParameter(Parameter parameter, HttpServletRequest request) throws Exception {
        Class<?> parameterType = parameter.getType();
        Object obj = parameterType.getDeclaredConstructor().newInstance();

        for (Field field : obj.getClass().getDeclaredFields()) {
            Object value = Utility.parseValue(request.getParameter(parameter.getName() + "." + field.getName()), field.getType());
            setObjectField(obj, obj.getClass().getDeclaredMethods(), field, value);
        }

        return obj;
    }

    // Gestion des réponses
    private void handleRestApiMethod(Method method, Object controllerInstance, Object[] args, HttpServletResponse response) throws Exception {
        Object result = method.invoke(controllerInstance, args);

        if (result instanceof ModelView) {
            ModelView modelView = (ModelView) result;
            String jsonData = Utility.modelViewToJson(modelView);
            response.setContentType("text/json");
            response.getWriter().print(jsonData);
        } else {
            String jsonData = Utility.objectToJson(result);
            response.setContentType("text/json");
            response.getWriter().print(jsonData);
        }
    }

    private void handleRegularMethod(Method method, Object controllerInstance, Object[] args, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Object result = method.invoke(controllerInstance, args);

        if (result instanceof String) {
            response.setContentType("text/plain");
            response.getWriter().println(result);
        } else if (result instanceof ModelView) {
            ModelView modelView = (ModelView) result;
            String viewUrl = modelView.getUrl();
            HashMap<String, Object> data = modelView.getData();

            for (String key : data.keySet()) {
                request.setAttribute(key, data.get(key));
            }

            try {
                request.getRequestDispatcher(viewUrl).forward(request, response);
            } catch (ServletException e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().println("<h1>500 Internal Server Error</h1>");
                response.getWriter().println("<p>Une erreur s'est produite lors de l'appel à la vue : " + e.getMessage() + "</p>");
                e.printStackTrace(response.getWriter());
            }
        } else {
            throw new ServletException("Ce type de retour ne peut pas etre gere pour le moment");
        }
    }

    // Méthodes utilitaires
    private boolean hasAnnotation(Class<?> classes, Class<? extends AnnotationController> annotation) {
        return classes.isAnnotationPresent(annotation);
    }

    protected String getURLSplit(String str) {
        String[] string = str.split("/");
        return string[4];
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
        for (File file : files) {
            if (file.isDirectory()) {
                collectClasses(packageName + '.' + file.getName(), file, classes);
            } else if (file.getName().endsWith(".class")) {
                try {
                    String className = packageName + '.' + file.getName().substring(0, file.getName().length() - 6);
                    classes.add(Class.forName(className));
                } catch (Exception e) {
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