package org.openbox.sf5.model;

import java.io.Serializable;
import java.lang.reflect.Field;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Transponders entity.
 *
 * @author Andrii Duplyk
 *
 */
@Entity
@Table(name = "Transponders")
@XmlRootElement
public class Transponders extends AbstractDbEntity implements Serializable {

	private static final long serialVersionUID = -3945460836260580586L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@Column(name = "frequency", unique = false, nullable = false, precision = 5)
	@Min(value = 2000)
	private long frequency;

	@Enumerated(EnumType.ORDINAL)
	@Column(nullable = false)
	@NotNull
	private Polarization polarization;

	@Enumerated(EnumType.ORDINAL)
	@Column(nullable = true)
	@JsonProperty("FEC")
	private TypesOfFEC FEC;

	@Enumerated(EnumType.ORDINAL)
	@Column(nullable = false)
	@NotNull
	private CarrierFrequency carrier;

	@Column(name = "speed", unique = false, nullable = false, precision = 5)
	@Min(value = 1000)
	private long speed;

	@Enumerated(EnumType.ORDINAL)
	@Column(nullable = false)
	@NotNull
	private DVBStandards versionOfTheDVB;

	@Enumerated(EnumType.ORDINAL)
	@Column(nullable = false)
	@NotNull
	private RangesOfDVB rangeOfDVB;

	@ManyToOne
	@JoinColumn(name = "satellite", unique = false, nullable = false, foreignKey = @ForeignKey(name = "FK_SatelliteTransponders"))
	@NotNull
	private Satellites satellite;

	/**
	 *
	 * @param frequency
	 * @param polarization
	 * @param FEC
	 * @param carrier
	 * @param speed
	 * @param versionOfTheDVB
	 * @param rangeOfDVB
	 * @param satellite
	 */
	public Transponders(long frequency, Polarization polarization, TypesOfFEC FEC, CarrierFrequency carrier, long speed,
			DVBStandards versionOfTheDVB, RangesOfDVB rangeOfDVB, Satellites satellite) {

		this.frequency = frequency;
		this.polarization = polarization;
		this.FEC = FEC;
		this.carrier = carrier;
		this.speed = speed;
		this.versionOfTheDVB = versionOfTheDVB;
		this.rangeOfDVB = rangeOfDVB;
		this.satellite = satellite;

	}

	/**
	 *
	 */
	public Transponders() {
	}

	/**
	 *
	 * @param origObj
	 */
	public Transponders(Transponders origObj) {
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

	public long getFrequency() {
		return frequency;
	}

	public void setFrequency(long frequency) {
		this.frequency = frequency;
	}

	@Override
	public String toString() {
		return String.valueOf(frequency);
	}

	public Polarization getPolarization() {
		return polarization;
	}

	public void setPolarization(Polarization polarization) {
		this.polarization = polarization;
	}

	public TypesOfFEC getFEC() {
		return FEC;
	}

	public void setFEC(TypesOfFEC FEC) {
		this.FEC = FEC;
	}

	public CarrierFrequency getCarrier() {
		return carrier;
	}

	public void setCarrier(CarrierFrequency Carrier) {
		this.carrier = Carrier;
	}

	public long getSpeed() {
		return speed;
	}

	public void setSpeed(long Speed) {
		this.speed = Speed;
	}

	public DVBStandards getVersionOfTheDVB() {
		return versionOfTheDVB;
	}

	public void setVersionOfTheDVB(DVBStandards VersionOfTheDVB) {
		this.versionOfTheDVB = VersionOfTheDVB;
	}

	public RangesOfDVB getRangeOfDVB() {
		return rangeOfDVB;
	}

	public void setRangeOfDVB(RangesOfDVB RangeOfDVB) {
		this.rangeOfDVB = RangeOfDVB;
	}

	public Satellites getSatellite() {
		return satellite;
	}

	public void setSatellite(Satellites Satellite) {
		this.satellite = Satellite;
	}

	private void setObjectFieldsFrom(Transponders origObj) throws IllegalAccessException {
		Field[] fields;
		Class<?> curClass = origObj.getClass();

		if (!curClass.isAssignableFrom(getClass())) {
			throw new IllegalArgumentException("New object must be the same class or a subclass of original");
		}

		// Spin through all fields of the class & all its superclasses
		do {
			fields = curClass.getDeclaredFields();

			for (int i = 0; i < fields.length; i++) {
				if ("serialVersionUID".equals(fields[i].getName())) {
					continue;
				}
				fields[i].set(this, fields[i].get(origObj));
			}
			curClass = curClass.getSuperclass();
		} while (curClass != null);
	}

}