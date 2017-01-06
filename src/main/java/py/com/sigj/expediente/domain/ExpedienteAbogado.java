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

import py.com.sigj.main.GenericEntity;

@Entity
@Table(uniqueConstraints = {
		@UniqueConstraint(name = "expediente_abogado_uk", columnNames = { "expediente_id", "abogado_id" }) })
public class ExpedienteAbogado extends GenericEntity {
	private static final String SECUENCIA = "expedienteAbogado_id_seq";

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SECUENCIA)
	@SequenceGenerator(name = SECUENCIA, sequenceName = SECUENCIA)
	private Long id;

	@ManyToOne
	@NotNull(message = "expedienteAbogado.expediente.notNull")
	@JoinColumn(foreignKey = @ForeignKey(name = "expedienteAbogado_expediente_fk"))
	private Expediente expediente;

	@ManyToOne
	@NotNull(message = "expedienteAbogado.abogado.notNull")
	@JoinColumn(foreignKey = @ForeignKey(name = "expedienteAbogado_abogado_fk"))
	private Abogado abogado;

	public ExpedienteAbogado() {
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	public Expediente getExpediente() {
		return expediente;
	}

	public void setExpediente(Expediente expediente) {
		this.expediente = expediente;
	}

	public Abogado getAbogado() {
		return abogado;
	}

	public void setAbogado(Abogado abogado) {
		this.abogado = abogado;
	}

	@Override
	public String toString() {
		return "ExpedienteAbogado [id=" + id + ", abogado=" + abogado + ", expediente=" + expediente + "]";
	}

}
