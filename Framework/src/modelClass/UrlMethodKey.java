package modelClass;

public class UrlMethodKey {
    private String url;
    private String httpMethod; // GET, POST, etc.

    public UrlMethodKey(String url, String httpMethod) {
        this.url = url;
        this.httpMethod = httpMethod;
    }

    // Getters
    public String getUrl() {
        return url;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    // Override equals et hashCode pour permettre l'utilisation dans un HashMap
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UrlMethodKey that = (UrlMethodKey) o;
        return url.equals(that.url) && httpMethod.equals(that.httpMethod);
    }

    @Override
    public int hashCode() {
        return url.hashCode() + httpMethod.hashCode();
    }
}