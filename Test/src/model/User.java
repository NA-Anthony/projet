package model;

import annotationClass.*;

public class User {
    @Param("username")
    private String name;
    @Param("userage")
    private int age;
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getAge() {
        return age;
    }
    public void setAge(int age) {
        this.age = age;
    }

}