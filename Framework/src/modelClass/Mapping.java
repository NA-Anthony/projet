package modelClass;

import java.util.ArrayList;

public class Mapping {
    private String classe;
    private ArrayList<Verb> verbs;

    public Mapping() {
        this.verbs = new ArrayList<>();
    }

    public Mapping(String classe, ArrayList<Verb> verbs) {
        this.classe = classe;
        this.verbs = verbs;
    }

    public String getClasse() {
        return classe;
    }

    public void setClasse(String classe) {
        this.classe = classe;
    }

    public ArrayList<Verb> getVerbs() {
        return verbs;
    }

    public void setVerbs(ArrayList<Verb> verbs) {
        this.verbs = verbs;
    }

    public void addVerb(Verb verb) {
        this.verbs.add(verb);
    }

    public Verb findVerbByType(String verbType) {
        for (Verb verb : verbs) {
            if (verb.getVerb().equalsIgnoreCase(verbType)) {
                return verb;
            }
        }
        return null;
    }
}