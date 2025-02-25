<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.User" %>

<%
    User user = (User) session.getAttribute("user");
%>

<html>
<head>
    <title>Vérification de la session</title>
</head>
<body>
    <%
        if (user != null) {
    %>
        <p>Utilisateur connecté : <%= user.getName() %></p>
        <form action="deconnexion" method="post">
            <button type="submit">Déconnexion</button>
        </form>
    <%
        } else {
    %>
        <p>Aucun utilisateur connecté.</p>
        <a href="login">Se connecter</a>
    <%
        }
    %>
</body>
</html>
