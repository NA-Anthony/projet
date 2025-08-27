<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<% 
    String message = (String) request.getAttribute("message"); 
    String etat = (String) request.getAttribute("etat"); 
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Example ModelView</title>
</head>
<body>
    <%-- Récupération de l'attribut "message" --%>
    <%-- Affichage du message --%>
    <h1><%= message %>: <%= etat %></h1>
</body>
</html>
