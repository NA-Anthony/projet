package model;

import annotationClass.*;
import java.time.LocalDate;

public class User {
    @Required(message = "Le nom est obligatoire")
    private String name;

    @Numeric(message = "L'âge doit être un nombre")
    private Integer age; 

    @Date(format = "yyyy-MM-dd", message = "La date de naissance doit être au format yyyy-MM-dd")
    private LocalDate birthDate; // Changement de String à LocalDate

    @MinLength(value = 5, message = "Le mot de passe doit contenir au moins 5 caractères")
    @MaxLength(value = 20, message = "Le mot de passe ne doit pas dépasser 20 caractères")
    private String password;

    // Getters et setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}