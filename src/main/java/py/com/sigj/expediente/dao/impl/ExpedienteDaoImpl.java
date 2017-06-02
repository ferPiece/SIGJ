package py.com.sigj.expediente.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import py.com.sigj.dao.impl.DaoImpl;
import py.com.sigj.expediente.dao.ExpedienteDao;
import py.com.sigj.expediente.domain.Expediente;

//hibernate recomendia repository para cuando trabaja con transacciones de base de datos, le decis que vas a trabajar con base de datos directos
@Repository
@Scope("session") // para crear una nueva instancia para cada sesion
public class ExpedienteDaoImpl extends DaoImpl<Expediente> implements ExpedienteDao {

	@Override
	public String getCamposFiltrables() {
		return "caratula||nroExpediente||anho||moneda||objetoCausa||nroLiquidación";
	}

	@Override
	public List<Expediente> getListByCedulaRuc(Long cedula) {
		logger.info("Obteniendo lista de expedientes..");

		String sql = "SELECT object(#ENTITY#) FROM #ENTITY# AS Expediente WHERE id = (SELECT id FROM ExpedienteCliente WHERE cliente_id = ?1 )"; 
//		INNER JOIN ExpedienteCliente WHERE id = expediente_id";
//				+ " WHERE ExpedienteCliente.cliente_id = ?1";
//				+ "ON e.id = ec.expediente_id JOIN Cliente AS c "
//				+ "ON c.id = ec.cliente_id JOIN Persona AS p "
//				+ "ON p.id = c.persona_id";
		sql = sql.replace("#ENTITY#", getEntityName());
		Query query = null;
		// Usuario no envió ningún filtro

		if (cedula!= null) {
			query = entityManager.createQuery(sql);
			query.setParameter(1,(long)2);		
			} else {
			sql = sql + " WHERE ec.cliente_id = ?1" ;
			query = entityManager.createQuery(sql);
			query.setParameter(1,(long)2);
		}
		List<Expediente> list = query.getResultList();
		logger.info("Documentos encontrados: {}", list);
		return list;
	}
}
