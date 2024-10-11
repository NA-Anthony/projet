package controller;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;

public class FrontController extends HttpServlet {
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //URI /... et URL tout url
        String urlString = request.getRequestURI().toString();
        PrintWriter pw = response.getWriter();
        pw.write(urlString);
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
