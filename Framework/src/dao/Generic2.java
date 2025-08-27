/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import annotationClass.Column;
import annotationClass.Table;
import connexion.Connexion;
import modelClass.Utility;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Anthony
 */
public class Generic2 implements Serializable{
    
    
   /*** Connection via une Class normal ou class mapping ***/ 
    /*
    * Fonction qui retourne la connection à partir d'une annotation Table 
    * ou par un class annoter par l'annoation qui recois tout les valeur de l'annotation 
    * nom_table, base, database, user, password 
    */
    private Connection getConnectionViaTable() throws  Exception{
        Table table = this.getClass().getAnnotation(Table.class);
        Connexion connexion = new Connexion(table.base(), table.user(), table.password(), table.database());
        return connexion.getconnection();
    } 
    
    /** base utiliser par le table **/
    private String basetable(){
        Table table = this.getClass().getAnnotation(Table.class);
        return table.base();
    }
    
    /*** name privary key table ***/
    private String getPrimaryKey() throws Exception{
        Field[] champs = this.getClass().getDeclaredFields();
        for (Field champ : champs) {
            if(champ.getAnnotation(Column.class).primaryKey() == true)
                return champ.getAnnotation(Column.class).libelle();
        }
        throw  new Exception("il faut entrer un cle informatique sur une table et la class utilisee");
    }
        
    /***** nom de table dans la base *****/
    private String Table() {
        Table table = this.getClass().getAnnotation(Table.class);
        if (table == null) {
            return this.getClass().getSimpleName();
        }
        return table.libelle();
    }
    
    /****** 
     * Fonction qui return la valeur d'un attribut si cet attribut est annotté par
     * Column
     *****/
    private String getAnnotationColumn(Field champ) {
        if(champ.isAnnotationPresent(Column.class)){
            if (champ.getAnnotation(Column.class).libelle().equals("")) {
                return champ.getName();
            }
            return champ.getAnnotation(Column.class).libelle();
        }
        return null;
    }
    
    /**** valeur des annotations des attributs d'une class 
     *****/
    private List<String> getValueAnnotation() {
        Field[] champs = this.getClass().getDeclaredFields();
        List<String> listeAnnotation = new ArrayList<>();
        for (Field field : champs) {
            String value = getAnnotationColumn(field);
            if (value != null) { listeAnnotation.add(value); }
        }
        return listeAnnotation;
    }
    
    /** attributs qui ont des annotation Column : attribut (methodeField) **/  
    private List<Field> FieldAnnotationColumn(){
        Field[] champs = this.getClass().getDeclaredFields();
        List<Field> fields = new ArrayList<>();
        for (Field field : champs) {
            String value = getAnnotationColumn(field);
            if (value != null) fields.add(field);
        }
        return fields;
    }
    
    /***  methodFieldNotNull 
    *   Fonction qui retourne les attributs qui a une valeur non null 
    *   et annotté par l'annotation Column 
    ***/
    private List<Field> FieldColumnNotNull() throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
        Field[] champs = this.getClass().getDeclaredFields();
        List<Field> fields = new ArrayList<>();
        Method m = null;
        for (Field field : champs) {
            if ( getAnnotationColumn(field) != null ){
                m = this.getClass().getMethod("get" + Utility.capitalword(field.getName()));
                if( m.invoke(this) != null ) fields.add(field);
            }
        }
        return fields;
    }
    
    /// avec parametre valueAnnotation
    private List<Field> methodFieldNotNull(List<String> valueAnnotation) throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
        Field[] champs = this.getClass().getDeclaredFields();
        List<Field> fields = new ArrayList<>();
        Method m = null;
        for (Field field : champs) {
            if (getAnnotationColumn(field)!= null) {
                for (String namecolumn : valueAnnotation) {
                    m = this.getClass().getMethod("get" + Utility.capitalword(field.getName()));
                    if(getAnnotationColumn(field).equals(namecolumn) && m.invoke(this) != null){
                        fields.add(field);
                    }
                }
            }
        }
        return fields;
    }
    
    /***
    *  Fonction qui retourne le nom des attribut qui a une valeur non null 
    *   et annotté par l'annotation Column 
    *   list des libelles de Column
    ***/
    private List<String> methodFieldNotNullAnnotation(List<String> valueAnnotation) throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
        Field[] champs = this.getClass().getDeclaredFields();
        List<String> rep = new ArrayList<>();
        Method m = null;
        for (Field field : champs) {
            String value = getAnnotationColumn(field);
            if (value != null){
                m = this.getClass().getMethod("get" + Utility.capitalword(field.getName()));
                if( m.invoke(this) != null ) rep.add(value);
            }
        }
        return rep;
    }
    
    /***
    *  Fonction qui retourne les attributs qui a une valeur non null 
    *   et annotté par l'annotation Column 
    *   list des libelles de Column
    ***/
    private List<Field> FieldNotNullAnnotation(List<String> valueAnnotation) throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
        Field[] champs = this.getClass().getDeclaredFields();
        List<Field> rep = new ArrayList<>();
        Method m = null;
        for (Field field : champs) {
            String value = getAnnotationColumn(field);
            if (value != null){
                m = this.getClass().getMethod("get" + Utility.capitalword(field.getName()));
                if( m.invoke(this) != null ) rep.add(field);
            }
        }
        return rep;
    }
    
    
/********************** REQUETE D'INSERTION *****************************/    
    /***
     * Fonction qui retourne la requete d'insertion dans un base 
     */
    private String requetinsertColumn(){
        String query = "insert into "+ this.Table() +" ( ";
        int i = 0;
        List<String> listeColumn = this.getValueAnnotation();
        for (String Column : listeColumn) {
            if(i == 0){ query = query + Column ;i++;     
            }else{
                query = query + ","+ Column  ;
            }
        }
        return query;
    }
    
    /**
     * Fonction qui retourne la requete d'insertion des donnees insert 
     **/
     private String requetinsertvalues() throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, Exception{
        int i = 0;
        String query = requetinsertColumn();
        query = query + ") values ( ";
        for (Field field : FieldAnnotationColumn()) {
            if(i == 0){
                if(this.basetable().equals("postgresql")){
                    query = query +"default";
                }else if(this.basetable().equals("oracle")){
                    query = query + getPrimaryKey()+".nextval";
                }
                
            }else{
                query = query +",?";
            }
            i++;
        }
        return  query + " ) ";
    }
     
    /**
     * Fonction qui fait le setting de l'objet dans le preparedStatement
     * pour create
     **/
    private void setObject(PreparedStatement statement, List<Field> valueObject, int debut) throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, SQLException{
        Method m = null;
        for(int j = debut;j<valueObject.size();j++){
            m = this.getClass().getMethod("get" + Utility.capitalword(valueObject.get(j).getName()));
            if(debut == 0){
                if( j==0 && !valueObject.get(j).getType().getSimpleName().equals("String") ){
                    statement.setObject(j+1, m.invoke(this)); 
                }else if(!valueObject.get(j).getType().getSimpleName().equals("String") )
                    statement.setObject(j, m.invoke(this));
            }else{
                if(j==0) statement.setObject(j+1, m.invoke(this)); 
                else statement.setObject(j, m.invoke(this));       
            }
          
        }   
    }
    
    /*** INSERTION DE LA TABLE
     * @param connexion
     * @throws java.lang.NoSuchMethodException
     * @throws java.lang.IllegalAccessException
     * @throws java.lang.reflect.InvocationTargetException
     * @throws java.sql.SQLException ***/
    public void create(Connection connexion) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, SQLException, Exception{
        String query = this.requetinsertvalues();
        boolean open = false;
        PreparedStatement statement = null;
        try {
            if(connexion == null){
                connexion = getConnectionViaTable();
                open = true;
                connexion.setAutoCommit(false);
            }
            statement = connexion.prepareStatement(query);
            setObject(statement, FieldAnnotationColumn(), 1);
            int value = statement.executeUpdate();
            if(open){ connexion.commit(); }
        } catch (Exception e) {
            if(connexion != null && open){
                 connexion.rollback();
            }
            throw e;
        }finally{
            if(open){ connexion.close(); }
            if(statement!= null){ statement.close(); }
        }
    }
    
    /************ CONDITION ***************/
    private String AvecCondition(String query) throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
        query =  query + " where 0=0 ";
        List<Field> listeColumn = FieldNotNullAnnotation(this.getValueAnnotation());
        Method m = null;
        for (Field field : listeColumn) {
            String value = getAnnotationColumn(field);
            if(field.getType().getSimpleName().equals("String")){
                m = this.getClass().getMethod("get" + Utility.capitalword(value));
                query = query + " and " + value + " LIKE '%"+m.invoke(this)+"%'";
            }else{
                query = query + " and " + value + " = ?";
            }
        }
       
        return query;
    }
    
    /****  SELECT ****/
    private String requetselect(String[] column){
        String query = "select ";
        if(column != null){
            for (int i = 0; i < column.length; i++) {
                if(i==0)
                    query = query + column[i];
                else
                    query = query + "," +column[i];
            }
        }else{
            query =  query + " * ";
        }
        return query + " from "+Table();
    }
    
    /**
     * Fonction qui retourne les attributs du column selectionné
     * @param column
     * @return
     * @throws NoSuchMethodException 
     */
    private List<Field> MethodSet(String[] column) throws NoSuchMethodException, Exception{
        if(column != null){
            Field[] champs = this.getClass().getDeclaredFields();
            List<Field> fields = new ArrayList<>();
            for (String string : column) { 
                boolean check = false;
                for (Field champ : champs) {
                    String value = getAnnotationColumn(champ);
                    if(value != null && value.equals(string)) {
                        fields.add(champ);
                        check = true;
                    }
                }
                if(!check) throw  new Exception("Le column "+ string+" n'existe pas dans cette "+this.getClass());
            }
            return fields;
        }
        return FieldAnnotationColumn();
    }
    
    /** Fonction qui transfome le tableau string[] column en List  **/
    private List<String> ColumnLabel(String[] column) throws NoSuchMethodException{
        List<String> annotation = new ArrayList<>();
        if(column != null){
            for (String string : column) {
                annotation.add(string);
            }
            return annotation;
        }
        return getValueAnnotation();
    }
    
    /**** fonction select generalisee ****/
    private List readSelect(Connection connexion,String[] column) throws SQLException, Exception{
        PreparedStatement statement = null;
        boolean open = false;
        List<dao.Generic2> results = new ArrayList<>();
        String query = new String();
        ResultSet resultat = null;
        try {
            if(connexion == null){
                connexion = getConnectionViaTable();
                open = true;
                connexion.setAutoCommit(false);
            }
            query = AvecCondition(requetselect(column));
            statement = connexion.prepareStatement(query);
            setObject(statement,FieldColumnNotNull(), 0);
            dao.Generic2 g = null;
            resultat = statement.executeQuery();
            List<Field> fields =  MethodSet(column);
            List<String> annotation = ColumnLabel(column);
            int i = 0;
            while(resultat.next()){
                g = this.getClass().newInstance();
                for (Field field : fields) {
                    g.getClass().getMethod("set" + Utility.capitalword(field.getName()), field.getType()).invoke(g, resultat.getObject(annotation.get(i)));
                    i++;
                }
                results.add(g);
                i=0;
            }
            return results;
        } catch (Exception e) {
            if(connexion != null)  connexion.rollback();
            e.printStackTrace();

            throw  e;
        }finally{
            if(open) connexion.close();
            if(statement!= null)  statement.close();
            if(resultat != null)  resultat.close();
        }
    }

    public List findAll(Connection connexion) throws Exception{
        return readSelect(connexion,null);
    }
    
    public List find(Connection connexion,String[] column) throws Exception{
        return readSelect(connexion,column);
    }
    
    public Generic2 findById(Connection connexion) throws Exception{
        return (Generic2) findAll(connexion).get(0);
    }
    
    public Generic2 findById(Connection connexion,String[] column) throws Exception{
        return (Generic2) find(connexion, column).get(0);
    }
    
/** Update **/
    //Requete pour update
    private String requetupdate() throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
        List<String> annotations = this.methodFieldNotNullAnnotation(this.getValueAnnotation());
        String query = "update "+Table() +" set ";
        int i = 0;
        if( annotations.size() == 1 ){
            query = query + annotations.get(0) +" = ? ";
        }else {
            for (String annotation : annotations) {
                if( i == (annotations.size()-1) ) query = query + annotation +" = ?";
                else{
                    query = query + annotation +" = ? , ";
                } i += 1;
            }
        }
        
        return query; 
    }
    
    /**
     * Fonction qui fait le setting de l'objet dans le preparedStatement
     * pour update
     **/
    private void setObject(PreparedStatement statement, List<Field> valueObjectUpdate, List<Field> valueObject, Generic2 objupd) throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, SQLException{
        Method m = null;
        int j = 0,fin = 0;
        for(;j<valueObjectUpdate.size();j++){
            m = objupd.getClass().getMethod("get" + Utility.capitalword(valueObjectUpdate.get(j).getName()));
            fin = j+1;
            statement.setObject(fin, m.invoke(objupd));
        }
        for (int i = 0; i <  valueObject.size(); i++) {
            if(!valueObject.get(i).getType().getSimpleName().equals("String") ){
                m = this.getClass().getMethod("get" + Utility.capitalword(valueObject.get(i).getName())); 
                fin += 1;
                statement.setObject(fin, m.invoke(this));
            }
        }
    }
    
    //fonction update

    /**
     *
     * @param connexion
     * @param objupdate (object qu'on fait la modification avec l'object appellant)
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     * @throws SQLException
     * @throws Exception
     */
    public void update(Connection connexion,Generic2 objupdate) throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, SQLException, Exception{
        String query = this.AvecCondition(objupdate.requetupdate());
        List<String> listeColumn = this.getValueAnnotation();
        List<String> listeColumnObjUpadte =  objupdate.methodFieldNotNullAnnotation(objupdate.getValueAnnotation());;
        boolean open = false;
        PreparedStatement statement = null;
        try {
            if(connexion == null){
                connexion = getConnectionViaTable();
                open = true;
                connexion.setAutoCommit(false);
            }
            statement = connexion.prepareStatement(query);
            setObject(statement, objupdate.methodFieldNotNull(listeColumnObjUpadte), this.methodFieldNotNull(listeColumn), objupdate);
            int value = statement.executeUpdate();
            if(open) connexion.commit();
        } catch (Exception e) {
            if(connexion != null) connexion.rollback();
            throw e;
        }finally{
            if(open) connexion.close();
            if(statement!= null) statement.close();     
        }
    }
    
    /**** Delete ***/
    private String requetedelete() throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
        String query = "delete from "+Table() + " ";
        return AvecCondition(query);
    }
    
    public void delete(Connection connexion) throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, SQLException, Exception{
        String query = requetedelete();
        List<String> listeColumn = this.getValueAnnotation();
        boolean open = false;
        PreparedStatement statement = null;
        try {
            if(connexion == null){
                connexion = getConnectionViaTable();
                open = true;
                connexion.setAutoCommit(false);
            }
            statement = connexion.prepareStatement(query);
            setObject(statement,this.methodFieldNotNull(listeColumn),0);
            int value = statement.executeUpdate();
            if(open)  connexion.commit();
        } catch (Exception e) {
            if(connexion != null) connexion.rollback();
            throw  e;
        }finally{
            if(open) connexion.close();
            if(statement!= null)statement.close();
        }
    }
    
    public List select(Connection connexion,String query) throws SQLException, Exception{
        PreparedStatement statement = null;
        boolean open = false;
        List<dao.Generic2> results = new ArrayList<>();
        ResultSet resultat = null;
        try {
            if(connexion == null){
                connexion = getConnectionViaTable();
                open = true;
                connexion.setAutoCommit(false);
            }
            statement = connexion.prepareStatement(query); 
            //setObject(statement,FieldColumnNotNull(), 0);
            dao.Generic2 g = null;
            resultat = statement.executeQuery();
            List<Field> fields =  MethodSet(null);
            List<String> annotation = ColumnLabel(null);
            int i = 0;
            while(resultat.next()){
                g = this.getClass().newInstance();
                for (Field field : fields) {
                    g.getClass().getMethod("set" + Utility.capitalword(field.getName()), field.getType()).invoke(g, resultat.getObject(annotation.get(i)));
                    i++;
                }
                results.add(g);
                i=0;
            }
            return results;
        } catch (Exception e) {
            if(connexion != null)  connexion.rollback();
            e.printStackTrace();
            
            throw  e;
        }finally{
            if(open) connexion.close();
            if(statement!= null)  statement.close();
            if(resultat != null)  resultat.close();
        }
    }
    
    public void delete(Connection connexion, String query) throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, SQLException, Exception{
        List<String> listeColumn = this.getValueAnnotation();
        boolean open = false;
        PreparedStatement statement = null;
        try {
            if(connexion == null){
                connexion = getConnectionViaTable();
                open = true;
                connexion.setAutoCommit(false);
            }
            statement = connexion.prepareStatement(query);
            int value = statement.executeUpdate();
            if(open)  connexion.commit();
        } catch (Exception e) {
            if(connexion != null) connexion.rollback();
            throw  e;
        }finally{
            if(open) connexion.close();
            if(statement!= null)statement.close();
        }
    }

    public <T extends Generic2> List<T> importerFichier(String cheminFichier, Class<T> classe) throws Exception {
        List<T> listeObjets = new ArrayList<>();
        boolean open = false;

        try (BufferedReader br = new BufferedReader(new FileReader(cheminFichier))) {
            String ligne;
            String[] nomsColonnes = br.readLine().split(","); // Lecture des en-têtes

            while ((ligne = br.readLine()) != null) {
                String[] valeurs = ligne.split(",");
                T objet = classe.getDeclaredConstructor().newInstance();

                // Assigner les valeurs aux champs de l'objet
                for (int i = 0; i < nomsColonnes.length; i++) {
                    String nomColonne = nomsColonnes[i].trim();
                    String valeur = valeurs[i].trim();

                    Field champ = classe.getDeclaredField(nomColonne);
                    champ.setAccessible(true);

                    // Vérifier et définir la valeur en fonction du type
                    if (champ.getType().equals(Integer.class)) {
                        champ.set(objet, Integer.parseInt(valeur));
                    } else if (champ.getType().equals(Double.class)) {
                        champ.set(objet, Double.parseDouble(valeur));
                    } else {
                        champ.set(objet, valeur);
                    }
                }
                listeObjets.add(objet);
            }
        }catch (Exception e){
            throw e;
        }
        return listeObjets;
    }

    public <T extends Generic2> void importerFichier(Connection connexion,String cheminFichier, Class<T> classe) throws Exception {
        boolean open = false;
        boolean erreur = false;
        try {
            if(connexion == null){
                connexion = getConnectionViaTable();
                open = true;
                connexion.setAutoCommit(false);
            }
            List<T> list = importerFichier(cheminFichier,classe);
            for (T t : list){
                t.create(connexion);
            }
        } catch (Exception e) {
            if(connexion != null) connexion.rollback();
            erreur = true;
            throw e;
        }finally{
            if(!erreur){
                connexion.commit();
            }
            if(open){
                connexion.close();
            }
        }
    }
}
