package tkt.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang.builder.ToStringBuilder;

@Entity
@Table(name = "companies")
public class Company {

	@Id
	private long _id;
	@Column(name = "name")
	private String name;
	@Column(name = "hashLogo")
	private String hashLogo;
	@Column(name = "hashTemplate")
	private String hashTemplate;

	public Company() {

	}

	public Company(long id, String name, String hashLogo, String hashTemplate) {
		this._id = id;
		this.name = name;
		this.hashLogo = hashLogo;
		this.hashTemplate = hashTemplate;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	public long getId() {
		return _id;
	}

	public String getName() {
		return name;
	}

	public String getHashLogo() {
		return hashLogo;
	}

	public String getHashTemplate() {
		return hashTemplate;
	}

	public void set_id(long _id) {
		this._id = _id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setHashLogo(String hashLogo) {
		this.hashLogo = hashLogo;
	}

	public void setHashTemplate(String hashTemplate) {
		this.hashTemplate = hashTemplate;
	}
}
