package spring.es.admintfg.dto;

import java.io.Serializable;

public class ImageDTO implements Serializable {
	private static final long serialVersionUID = 2868430411020089105L;

	private long id;
	private String url;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
