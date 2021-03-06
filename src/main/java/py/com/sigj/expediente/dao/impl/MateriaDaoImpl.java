package py.com.sigj.expediente.dao.impl;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import py.com.sigj.dao.impl.DaoImpl;
import py.com.sigj.expediente.dao.MateriaDao;
import py.com.sigj.expediente.domain.Materia;

//hibernate recomendia repository para cuando trabaja con transacciones de base de datos, le decis que vas a trabajar con base de datos directos
@Repository
@Scope("session") // para crear una nueva instancia para cada sesion
public class MateriaDaoImpl extends DaoImpl<Materia> implements MateriaDao {

	@Override
	public String getCamposFiltrables() {
		return "codigo||descripcion";
	}

	@Override
	public Materia find(Long id) {
		logger.info("Buscando registro con id: {}", id);
		Materia m = entityManager.find(getEntityClass(), id);
		initializeCollection(m.getListaDespacho());
		initializeCollection(m.getListaProceso());
		return m;

	}

	@Override
	public void refresh(Materia obj) {
		entityManager.refresh(obj);

	}
}
