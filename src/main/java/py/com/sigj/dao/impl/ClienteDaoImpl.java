package py.com.sigj.dao.impl;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import py.com.sigj.dao.ClienteDao;
import py.com.sigj.expediente.domain.Cliente;

@Repository
@Scope("session")
public class ClienteDaoImpl extends DaoImpl<Cliente> implements ClienteDao {

	@Override
	public String getCamposFiltrables() {
		return "persona.cedula_ruc||persona.nombre_razonSocial||persona.apellido||domicilioProcesal||domicilioActual||domicilioLaboral";
	}

}
