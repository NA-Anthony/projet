package controller;

import javax.servlet.*;
import javax.servlet.http.*;

import java.io.*;
import java.lang.reflect.*;
import java.net.*;
import java.util.*;
import annotationClass.*;
import annotationClass.Date;
import modelClass.*;
import exception.*;
import util.Validator;

public class FrontController extends HttpServlet {
    private HashMap<String, Mapping> hashMap = new HashMap<>();

    @Override
    public void init() throws ServletException {
        super.init();
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
        String url = method.getAnnotation(Url.class).path();
        String className = controllerClass.getName();
        String methodName = method.getName();

        String httpMethod = "GET";
        if (method.isAnnotationPresent(Post.class)) {
            httpMethod = "POST";
        }

        Verb verb = new Verb(httpMethod, methodName);
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
            } catch (NotFoundException e) {
                handleException(e, response);
            } catch (Exception e) {
                handleException(new InternalServerErrorException(e.getMessage()), response);
            }
        } else {
            handleException(new NotFoundException("La ressource demandée n'a pas été trouvée : " + url), response);
        }
    }

    private void handleRequestMapping(Mapping mapping, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String httpMethod = request.getMethod();
        Verb verb = mapping.findVerbByType(httpMethod);

        if (verb != null) {
            Class<?> controllerClass = Class.forName(mapping.getClasse());
            Object controllerInstance = createControllerInstance(controllerClass, request);

            Method method = findMethod(controllerClass, verb.getMethod());
            Parameter[] parameters = method.getParameters();
            Object[] args = new Object[parameters.length];

            // Validation des inputs
            ValidationError validationError = null;

            for (int i = 0; i < parameters.length; i++) {
                Parameter parameter = parameters[i];
                if(Utility.isPrimitive(parameter.getType())) {
                    args[i] = resolvePrimitiveParameter(parameter, request);
                } 
                else if (parameter.isAnnotationPresent(ParamName.class)) {
                    ParamName paramNameAnnotation = parameter.getAnnotation(ParamName.class);
                    String paramPrefix = paramNameAnnotation.value();

                    // Créer une instance de l'objet
                    Object paramInstance = parameter.getType().getDeclaredConstructor().newInstance();

                    // Créer un Map pour les inputs
                    Map<String, String> inputs = new HashMap<>();
                    for (Field field : paramInstance.getClass().getDeclaredFields()) {
                        String fieldName = paramPrefix + "." + field.getName();
                        String value = request.getParameter(fieldName);
                        inputs.put(field.getName(), value);
                    }

                    // Valider les données avec Validator
                    validationError = Validator.validateWithInputs(paramInstance, inputs);

                    // Si des erreurs sont détectées, les transmettre à la requête
                    if (validationError != null && validationError.hasErrors()) {
                        request.setAttribute("validationError", validationError);
                    }

                    args[i] = paramInstance;
                } else if (parameter.getType().equals(HttpServletRequest.class)) {
                    args[i] = request;
                } else if (parameter.getType().equals(HttpServletResponse.class)) {
                    args[i] = response;
                } else {
                    throw new ServletException("Paramètre non pris en charge : " + parameter.getName());
                }
            }

            // Appeler la méthode du contrôleur
            if (method.isAnnotationPresent(Restapi.class)) {
                handleRestApiMethod(method, controllerInstance, args, response);
            } else {
                handleRegularMethod(method, controllerInstance, args, request, response);
            }
        } else {
            response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        }
    }

    private ValidationError validateInputs(Object[] args) throws IllegalAccessException {
        ValidationError validationError = new ValidationError();

        for (Object arg : args) {
            if (arg != null) {
                // Valider uniquement les objets complexes
                Map<String, String> rawData = new HashMap<>();
                for (Field field : arg.getClass().getDeclaredFields()) {
                    field.setAccessible(true);
                    Object value = field.get(arg);
                    rawData.put(field.getName(), value != null ? value.toString() : null);
                }

                // Valider l'objet avec Validator
                ValidationError objectErrors = Validator.validateWithInputs(arg, rawData);
                if (objectErrors != null && objectErrors.hasErrors()) {
                    validationError.merge(objectErrors); // Fusionner les erreurs
                }
            }
        }

        return validationError;
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

    private Object[] resolveMethodArguments(Method method, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Parameter[] parameters = method.getParameters();
        Object[] args = new Object[parameters.length];

        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];

            if (parameter.getType().equals(HttpServletRequest.class)) {
                args[i] = request;
            } else if (parameter.getType().equals(HttpServletResponse.class)) {
                args[i] = response;
            } else {
                if(Utility.isPrimitive(parameter.getType())) {
                    args[i] = resolvePrimitiveParameter(parameter, request);
                } else if (parameter.isAnnotationPresent(ParamName.class)) {
                    args[i] = resolveComplexParameter(parameter, request);
                } else {
                    throw new ServletException("Le paramètre doit être annoté avec @ParamName ou être de type primitif");
                }
            }
        }

        return args;
    }

    private Object resolvePrimitiveParameter(Parameter parameter, HttpServletRequest request) throws ServletException {
        if (parameter.isAnnotationPresent(ParamName.class)) {
            ParamName annotation = parameter.getAnnotation(ParamName.class);
            String paramValue = annotation.value();
            String value = request.getParameter(paramValue);

            if (value == null || value.isEmpty()) {
                return null;
            }

            try {
                return Utility.parseValue(value, parameter.getType());
            } catch (Exception e) {
                return null;
            }
        } else {
            throw new ServletException("ETU002444: Le paramètre doit être annoté avec @ParamName");
        }
    }

    private Object resolveComplexParameter(Parameter parameter, HttpServletRequest request) throws ServletException {
        Class<?> parameterType = parameter.getType();
        Object obj;

        try {
            obj = parameterType.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new ServletException("Erreur lors de la création de l'objet : " + e.getMessage());
        }

        if (parameter.isAnnotationPresent(ParamName.class)) {
            ParamName paramNameAnnotation = parameter.getAnnotation(ParamName.class);
            String paramPrefix = paramNameAnnotation.value();

            // Collecter les données brutes depuis la requête
            Map<String, String> rawData = new HashMap<>();
            for (Field field : obj.getClass().getDeclaredFields()) {
                String fieldName = paramPrefix + "." + field.getName();
                String value = request.getParameter(fieldName);
                rawData.put(field.getName(), value);
            }
        }

        return obj;
    }

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

            request.getRequestDispatcher(viewUrl).forward(request, response);
        } else {
            throw new ServletException("Ce type de retour ne peut pas être géré pour le moment");
        }
    }

    private void handleException(Exception e, HttpServletResponse response) throws IOException {
        if (e instanceof NotFoundException) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().println("<h1>404 Not Found</h1>");
            response.getWriter().println("<p>" + e.getMessage() + "</p>");
        } else if (e instanceof InternalServerErrorException) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("<h1>500 Internal Server Error</h1>");
            response.getWriter().println("<p>" + e.getMessage() + "</p>");
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("<h1>500 Internal Server Error</h1>");
            response.getWriter().println("<p>Une erreur inattendue s'est produite : " + e.getMessage() + "</p>");
        }
    }

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