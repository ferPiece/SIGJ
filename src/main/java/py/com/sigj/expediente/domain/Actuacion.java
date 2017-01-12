package py.com.sigj.expediente.domain;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;

import py.com.sigj.main.GenericEntity;

/**
 * Clase que registra todas las actuaciones relacionadas a un expediente.
 * cronológicas
 *
 * @author Luis A. Méndez R.
 *
 */
@Entity
@Table(name = "actuacion")
public class Actuacion extends GenericEntity {
	private static final String SECUENCIA = "actuacion_id_seq";

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SECUENCIA)
	@SequenceGenerator(name = SECUENCIA, sequenceName = SECUENCIA)
	private Long id;

	@ManyToOne
	@NotNull(message = "actuacion.documento.notNull")
	@JoinColumn(foreignKey = @ForeignKey(name = "actuacion_documento_fk"))
	private Documento documento;

	@ManyToOne
	@NotNull(message = "actuacion.tipoActuacion.notNull")
	@JoinColumn(foreignKey = @ForeignKey(name = "actuacion_tipoActuacion_fk"))
	private TipoActuacion tipoActuacion;

	@ManyToOne
	@NotNull(message = "actuacion.expediente.notNull")
	@JoinColumn(foreignKey = @ForeignKey(name = "actuacion_expediente_fk"))
	private Expediente expediente;

	@NotNull(message = "actuacion.descripcion.notNull")
	@NotBlank(message = "actuacion.descripcion.notBlank")
	@Size(max = 100, message = "actuacion.descripcion.size")
	private String descripcion;

	@Temporal(TemporalType.DATE)
	private Date fechaEstado;

	public Actuacion() {

	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	public Documento getDocumento() {
		return documento;
	}

	public void setDocumento(Documento documento) {
		this.documento = documento;
	}

	public TipoActuacion getTipoActuacion() {
		return tipoActuacion;
	}

	public void setTipoActuacion(TipoActuacion tipoActuacion) {
		this.tipoActuacion = tipoActuacion;
	}

	public Expediente getExpediente() {
		return expediente;
	}

	public void setExpediente(Expediente expediente) {
		this.expediente = expediente;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public Date getFechaEstado() {
		return fechaEstado;
	}

	public void setFechaEstado(Date fechaEstado) {
		this.fechaEstado = fechaEstado;
	}

	@Override
	public String toString() {
		return "Actuacion [id=" + id + ", documento=" + documento + ", tipoActuacion=" + tipoActuacion + ", expediente="
				+ expediente + ", descripcion=" + descripcion + ", fechaEstado=" + fechaEstado + "]";
	}

}
