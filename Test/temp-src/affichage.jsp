<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.User" %> <!-- Importez la classe User pour le cast -->
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Resultat</title>
</head>
<body>
    <%
        User user = (User) request.getAttribute("user"); // Cast de l'objet user
        String test = (String) request.getAttribute("test"); // Cast de l'objet string
        if (user != null) {
    %>
        <p>Nom: <%= user.getName() %></p>
        <p>Age: <%= user.getAge() %></p>
        <p>Test: <%= test %></p>
    <%
        } else {
    %>
        <p>Erreur : utilisateur non trouvé.</p>
    <%
        }
    %>
</body>
</html>
