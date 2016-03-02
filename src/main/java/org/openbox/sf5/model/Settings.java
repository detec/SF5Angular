package org.openbox.sf5.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

//import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.validator.constraints.NotEmpty;
import org.openbox.sf5.converters.TimestampAdapter;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "Settings")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Settings extends AbstractDbEntity implements Serializable {

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Settings other = (Settings) obj;
		if (id != other.id) {
			return false;
		}
		return true;
	}

	private static final long serialVersionUID = 7055744176770843683L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@Column(name = "name", unique = false, nullable = false, length = 50)
	@NotEmpty
	@XmlID
	private String name;

	public void setName(String Name) {
		this.name = Name;
	}

	public String getName() {
		return name;
	}

	public long getId() {

		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Override
	public String toString() {
		// return Name;
		// we must convert object to id
		return String.valueOf(id);
	}

	@Column(name = "theLastEntry", unique = false, nullable = true)
	@XmlJavaTypeAdapter(TimestampAdapter.class)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "Europe/Kiev")
	// Will try to use Javascript's milliseconds
	// @JsonFormat(shape = JsonFormat.Shape.STRING, pattern =
	// "yyyy-MM-dd'T'HH:mm:ss.SSSZ", timezone = "Europe/Kiev")
	// @JsonFormat(shape = JsonFormat.Shape.STRING, pattern =
	// "yyyy-MM-dd'T'HH:mm:ss")
	@NotNull
	private Timestamp theLastEntry;

	public Timestamp getTheLastEntry() {
		return theLastEntry;
	}

	public void setTheLastEntry(Timestamp TheLastEntry) {
		this.theLastEntry = TheLastEntry;
	}

	@ManyToOne
	@JoinColumn(name = "\"user\"", unique = false, nullable = false, foreignKey = @ForeignKey(name = "FK_User"))
	@NotNull
	@Valid
	private Users user;

	public Users getUser() {
		return user;
	}

	public void setUser(Users User) {
		this.user = User;
	}

	@OneToMany(mappedBy = "parent_id", fetch = FetchType.EAGER, orphanRemoval = true)
	@Cascade({ CascadeType.SAVE_UPDATE, CascadeType.DELETE })
	@OrderColumn(name = "lineNumber")
	@JsonManagedReference
	private List<SettingsConversion> conversion = new ArrayList<SettingsConversion>();

	public List<SettingsConversion> getConversion() {
		return conversion;
	}

	public void setConversion(List<SettingsConversion> Conversion) {
		this.conversion = Conversion;
	}

	public Settings(String Name, Timestamp lastEntry, Users User, List<SettingsConversion> Conversion) {

		this.name = Name;
		theLastEntry = lastEntry;
		this.user = User;
		this.conversion = Conversion;

	}

	public Settings() {
	}

}