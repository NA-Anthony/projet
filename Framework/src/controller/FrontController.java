package controller;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import annotationClass.*;

public class FrontController extends HttpServlet {
    private boolean check = false;
    private List<String> classNames = new ArrayList<>();

    public List<String> getClassNames() {
        return classNames;
    }

    public void setClassNames(List<String> classNames) {
        this.classNames = classNames;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }

    public boolean isCheck() {
        return check;
    }

    private boolean hasAnnotation(Class<?> classes, Class<? extends AnnotationController> annotation) {
        return classes.isAnnotationPresent(annotation);
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //URI /... et URL tout url
        String urlString = request.getRequestURI().toString();
        PrintWriter pw = response.getWriter();
        String packageName = request.getServletContext().getInitParameter("AnnotationController");
        List<Class<?>> classes = FrontController.getClasses(packageName,pw);
        if (check == false) {
            for (int j = 0; j < classes.size(); j++) {
                if(hasAnnotation(classes.get(j), AnnotationController.class)) {
                    classNames.add(classes.get(j).getName());
                }
            }
            setCheck(true);
        }
        pw.println(urlString);
        printClasses(pw, classNames);
    }

    private void printClasses(PrintWriter printWriter, List<String> list) {
        for (int i = 0; i < list.size(); i++) {
            printWriter.println(list.get(i));
        }
    }

    public static List<Class<?>> getClasses(String packageName,PrintWriter printWriter) {
        List<Class<?>> classes = new ArrayList<>();
        URL path = Thread.currentThread().getContextClassLoader().getResource(packageName.replace('.', File.separatorChar));
        if (path == null) {
            printWriter.println("Ressource not found for package: "+packageName);
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
            printWriter.println("Directory not found: " + path.toString());
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
