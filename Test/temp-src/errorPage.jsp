<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>Erreur de validation</title>
</head>
<body>
    <h1>Erreurs de validation :</h1>
    <ul>
        <c:forEach items="${errors}" var="error">
            <li>${error.key}: ${error.value}</li>
        </c:forEach>
    </ul>

    <h2>Valeurs précédentes :</h2>
    <ul>
        <c:forEach items="${previousValues}" var="value">
            <li>${value.key}: ${value.value}</li>
        </c:forEach>
    </ul>

    <a href="javascript:history.back()">Retour</a>
</body>
</html>