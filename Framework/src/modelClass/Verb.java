package modelClass;

public class Verb {
    private String verb;
    private String method;

    public Verb(String verbString,String methodString){
        setVerb(verbString);
        setMethod(methodString);
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getMethod() {
        return method;
    }

    public void setVerb(String verb) {
        this.verb = verb;
    }

    public String getVerb() {
        return verb;
    }
}
