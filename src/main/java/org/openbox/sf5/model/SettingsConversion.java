package org.openbox.sf5.model;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonBackReference;

/**
 * Settings line.
 *
 * @author Andrii Duplyk
 *
 */
@Entity
@Table(name = "SettingsConversion")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class SettingsConversion extends AbstractDbEntity implements Serializable {

	private static final long serialVersionUID = -399944579251735871L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@ManyToOne
	@JoinColumn(name = "parent_id", unique = false, nullable = false, foreignKey = @ForeignKey(name = "FK_SettingConversion"))
	@JsonBackReference
	@XmlIDREF
	private Settings parent_id;

	private long lineNumber;

	@ManyToOne
	@JoinColumn(name = "transponder", unique = false, nullable = false, foreignKey = @ForeignKey(name = "FK_Transponder"))
	private Transponders transponder;

	@Column(name = "satindex", unique = false, nullable = true, precision = 1)
	private long satindex;

	@Column(name = "tpindex", unique = false, nullable = true, precision = 1)
	private long tpindex;

	@Column(name = "note", unique = false, nullable = true, length = 120)
	private String note;

	@Column(name = "theLineOfIntersection", unique = false, nullable = true, precision = 2)
	private long theLineOfIntersection;

	/**
	 *
	 * @param parent_id
	 * @param transponder
	 * @param satindex
	 * @param tpindex
	 * @param note
	 * @param theLineOfIntersection
	 */
	public SettingsConversion(Settings parent_id, Transponders transponder, long satindex, long tpindex, String note,
			long theLineOfIntersection) {

		this.parent_id = parent_id;
		this.transponder = transponder;
		this.satindex = satindex;
		this.tpindex = tpindex;
		this.note = note;
		this.theLineOfIntersection = theLineOfIntersection;

	}

	/**
	 *
	 */
	public SettingsConversion() {
	}

	/**
	 *
	 * @param parent
	 */
	public SettingsConversion(Settings parent) {
		this.parent_id = parent;

	}

	/**
	 *
	 * @param origObj
	 */
	public SettingsConversion(SettingsConversion origObj) {
		try {
			setObjectFieldsFrom(origObj);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	public long getId() {

		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Transponders getTransponder() {
		return transponder;
	}

	public void setTransponder(Transponders transponder) {
		this.transponder = transponder;
	}

	public long getSatindex() {
		return satindex;
	}

	public void setSatindex(long satindex) {
		this.satindex = satindex;
	}

	public long getTpindex() {
		return tpindex;
	}

	public void setTpindex(long tpindex) {
		this.tpindex = tpindex;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public long getTheLineOfIntersection() {
		return theLineOfIntersection;
	}

	public void setTheLineOfIntersection(long theLineOfIntersection) {
		this.theLineOfIntersection = theLineOfIntersection;
	}

	public long getLineNumber() {
		return lineNumber;
	}

	public void setLineNumber(long lineNumber) {
		this.lineNumber = lineNumber;
	}

	protected void setObjectFieldsFrom(SettingsConversion origObj) throws IllegalAccessException {
		Field[] fields;
		Class<?> curClass = origObj.getClass();

		if (!curClass.isAssignableFrom(this.getClass())) {
			throw new IllegalArgumentException("New object must be the same class or a subclass of original");
		}

		// filter only SettingsConversion fields
		List<String> scClassList = new ArrayList<>();

		Field[] thisClassFieldsArray = SettingsConversion.class.getDeclaredFields();

		List<Field> thisClassFiledList = Arrays.asList(thisClassFieldsArray);
		thisClassFiledList.stream().forEach(t -> {
			String fieldname = t.getName();
			if (!"serialVersionUID".equals(fieldname)) {
				scClassList.add(fieldname);
			}
		});

		// Spin through all fields of the class & all its superclasses
		do {
			fields = thisClassFieldsArray;

			for (int i = 0; i < fields.length; i++) {

				if (scClassList.contains(fields[i].getName())) {
					// add only checked classes

					fields[i].set(this, fields[i].get(origObj));
				}
			}
			curClass = curClass.getSuperclass();
		} while (curClass != null);
	}

	public Settings getParent_id() {
		return parent_id;
	}

	public void setParent_id(Settings parent) {
		this.parent_id = parent;
	}

}