<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Document</title>
</head>
<body>
    <form action="demo" method="post">
        <label for="name">Nom :</label>
        <input type="text" id="name" name="user.name" value="${previousValues.name}">
        <span style="color: red;">${errors.name}</span><br>
    
        <label for="age">Age :</label>
        <input type="text" id="age" name="user.age" value="${previousValues.age}">
        <span style="color: red;">${errors.age}</span><br>
    
        <label for="birthDate">Date de naissance :</label>
        <input type="text" id="birthDate" name="user.birthDate" value="${previousValues.birthDate}">
        <span style="color: red;">${errors.birthDate}</span><br>
    
        <label for="password">Mot de passe :</label>
        <input type="password" id="password" name="user.password" value="${previousValues.password}">
        <span style="color: red;">${errors.password}</span><br>

        <label for="test">Test :</label>
        <input type="text" id="test" name="string" value="${previousValues.test}">
        <span style="color: red;">${errors.test}</span><br>

        <input type="submit" value="Envoyer">
    </form>
</body>
</html>