package py.com.sigj.expediente.domain;

import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;

import py.com.sigj.main.GenericEntity;
import py.com.sigj.rrhh.domain.Empleado;

/**
 * Clase que registra datos de los abogados.
 *
 * @author Luis A. Méndez R.
 *
 */
@Entity
@Table(uniqueConstraints = { @UniqueConstraint(name = "abogado_empleado_uk", columnNames = { "empleado_id" }) })
public class Abogado extends GenericEntity {

	private static final String SECUENCIA = "abogado_id_seq";

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SECUENCIA)
	@SequenceGenerator(name = SECUENCIA, sequenceName = SECUENCIA)
	private Long id;

	@NotNull(message = "cliente.domicilioLaboral.notNull")
	@NotBlank(message = "cliente.domicilioLaboral.notBlank")
	@Size(max = 1000, message = "cliente.domicilioLaboral.size")
	private String domicilioLaboral;

	@NotNull(message = "cliente.domicilioActual.notNull")
	@NotBlank(message = "cliente.domicilioActual.notBlank")
	@Size(max = 1000, message = "cliente.domicilioActual.size")
	private String domicilioActual;

	@ManyToOne
	@JoinColumn(foreignKey = @ForeignKey(name = "abogado_empleado_fk"))
	private Empleado empleado;

	public Abogado() {
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	public String getDomicilioLaboral() {
		return domicilioLaboral;
	}

	public void setDomicilioLaboral(String domicilioLaboral) {
		this.domicilioLaboral = domicilioLaboral;
	}

	public String getDomicilioActual() {
		return domicilioActual;
	}

	public void setDomicilioActual(String domicilioActual) {
		this.domicilioActual = domicilioActual;
	}

	public Empleado getEmpleado() {
		return empleado;
	}

	public void setEmpleado(Empleado empleado) {
		this.empleado = empleado;
	}

	@Override
	public String toString() {
		return "Abogado [id=" + id + ", domicilioLaboral=" + domicilioLaboral + ", domicilioActual=" + domicilioActual
				+ ", empleado=" + empleado + "]";
	}

}