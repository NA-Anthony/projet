/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package connexion;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author Anthony
 */
public class Connexion implements Serializable{
    private  String base;     // nom de la base de donnée
    private  String user;
    private  String password;
    private  String database; // nom de la database 
    
    // base de donnee; user; password; database
    public Connexion(String base, String user, String password, String database) {
        this.base = base;
        this.user = user;
        this.password = password;
        this.database = database;
    }

    public Connexion() {
    }
    
    
    
    public  Connection getconnection() throws  Exception{
        Connection connexion;
        try {
            Class.forName(this.ClassforName());
            connexion = DriverManager.getConnection(this.DriverManager(), this.getUser(), this.getPassword());
            connexion.setAutoCommit(false);
            return connexion;
        } catch (ClassNotFoundException | SQLException e) {
            throw  e;
        }
    }

    public String getDatabase() throws Exception {
        if(database == null) throw new Exception("Il faut entrer la base de donnee utiliser; postgresql ou oracle");
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }
    
    private String ClassforName() throws Exception{
        if("oracle".equals(this.getBase())){
            return "oracle.jdbc.driver.OracleDriver";
        }else if("postgresql".equals(this.getBase())){
            return "org.postgresql.Driver";
        }else if("mysql".equals(this.getBase())){
            return "com.mysql.cj.jdbc.Driver";
        }
        return null;
    }
     
    private String DriverManager() throws Exception{
         if("oracle".equals(this.getBase())){
            return "jdbc:oracle:thin:@localhost:1521:"+this.getDatabase();
        }else if("postgresql".equals(this.getBase())){
            return "jdbc:postgresql://localhost:5432/"+this.getDatabase();
        }else if("mysql".equals(this.getBase())){
             return "jdbc:mysql://localhost:3306/"+this.getDatabase();
         }
        return null;
    } 
     
    public String getBase() throws Exception {
        if(base == null) throw  new Exception("Il faut entrer le nom de la base de donnée ");
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public String getUser() throws Exception {
        if(user == null ) throw  new Exception("Il faut entrer le nom de l'utilisateur de la base de donnee");
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() throws Exception {
        if(password == null) throw  new Exception("Il faut entrer le mot de passe de l'utilisateur");
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    
}
