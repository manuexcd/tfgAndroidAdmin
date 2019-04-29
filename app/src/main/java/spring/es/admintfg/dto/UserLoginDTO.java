package spring.es.admintfg.dto;

import java.io.Serializable;
import java.util.List;

public class UserLoginDTO implements Serializable {
	private static final long serialVersionUID = -4271207730379003188L;
	private long id;
	private List<String> roles;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public List<String> getRoles() {
		return roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}
}
