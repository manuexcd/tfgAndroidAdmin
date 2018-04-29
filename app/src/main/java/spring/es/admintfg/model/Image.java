package spring.es.admintfg.model;

public class Image {

    private long id;
    private String url;

    public Image() {
    }

    public Image(String url) {
        super();
        this.setUrl(url);
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
