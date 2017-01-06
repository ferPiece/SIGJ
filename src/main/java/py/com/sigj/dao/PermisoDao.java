package py.com.sigj.dao;

import java.util.List;

import py.com.sigj.security.Permiso;
import py.com.sigj.security.Rol;

public interface PermisoDao extends Dao<Permiso> {
	List<Permiso> ListByRol(Rol rol);
}
