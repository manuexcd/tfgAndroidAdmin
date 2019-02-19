package spring.es.admintfg.model;

public class JwtBody {
    private String sub;
    private String exp;

    public JwtBody(String sub, String exp) {
        this.sub = sub;
        this.exp = exp;
    }

    public String getSub() {
        return sub;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }

    public String getExp() {
        return exp;
    }

    public void setExp(String exp) {
        this.exp = exp;
    }
}
