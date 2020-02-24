package config;

public enum Env {
    LOCAL_1("local.1"),
    LOCAL_2("local.2");

    private final String connPropName;

    private Env(String connPropName) {
        this.connPropName = connPropName;
    }

    public String getConnPropName() {
        return connPropName;
    }
}


