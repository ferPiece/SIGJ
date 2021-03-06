package py.com.sigj.gastos.controllers.form;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import py.com.sigj.controllers.form.FormController;
import py.com.sigj.dao.Dao;
import py.com.sigj.gastos.domain.IngresoEgreso;
import py.com.sigj.gastos.controllers.list.IngresoEgresoListController;
import py.com.sigj.gastos.dao.IngresoEgresoDao;
import py.com.sigj.gastos.domain.IngresoEgreso;

/**
 *
 * @author ariquelme
 *
 */

@Controller
@Scope("request")
@RequestMapping("ingreso_egreso")
public class IngresoEgresoFormController extends FormController<IngresoEgreso> {

	@Autowired
	private IngresoEgresoDao ingresoEgresoDao;

	@Autowired
	private IngresoEgresoListController ingresoEgresoList;

	@Override
	public String getTemplatePath() {
		return "gastos/ingreso_egreso_index";
	}

	@Override
	public String getNombreObjeto() {
		return "ingresoEgreso";
	}

	@Override
	public IngresoEgreso getNuevaInstancia() {
		return new IngresoEgreso();
	}

	@Override
	public void agregarValoresAdicionales(ModelMap map) {
		map.addAttribute("columnas", ingresoEgresoList.getColumnas());
		map.addAttribute("columnasStr", ingresoEgresoList.getColumnasStr(null));
		super.agregarValoresAdicionales(map);
	}

	@Override
	public Dao<IngresoEgreso> getDao() {
		return ingresoEgresoDao;
	}

	@RequestMapping(value = "accion2", method = RequestMethod.POST)
	public String accion2(ModelMap map, @Valid IngresoEgreso obj, BindingResult bindingResult,
			@RequestParam(value="monto_format", required = false) String monto,
			@RequestParam(required = false) String accion,
			@RequestParam(value = "id_objeto", required = false) Long id_objeto) {
		if (StringUtils.equals(accion, "save")) {
			return guardar_listado(map, obj, bindingResult,monto);
		} else if (StringUtils.equals(accion, "edit")) {
			logger.info("OBJETO Ingreso Egreso {}", obj);
			return editar_listado(map, obj, bindingResult,monto);
		} else if (id_objeto != null) {
			return eliminar_listado(map, id_objeto);

		}
		return getTemplatePath();

	}

	
	@RequestMapping(value = "save_listado", method = RequestMethod.POST)
	public String guardar_listado(ModelMap map, @Valid IngresoEgreso obj, BindingResult bindingResult,String monto) {
		try {
			if (obj.getId() == null) {
				monto = monto.replaceAll("\\.", "");
				obj.setMonto(Integer.valueOf(monto));
				getDao().createOrUpdate(obj);

				map.addAttribute("msgExito", msg.get("Registro agregado"));
				logger.info("Se crea un nuevo Ingreso Egreso -> {}", obj);
			}

		} catch (Exception ex) {
			obj.setId(null);
			map.addAttribute("error", getErrorFromException(ex));
		}
		map.addAttribute(getNombreObjeto(), obj);
		agregarValoresAdicionales(map);
		return getTemplatePath();

	}

	@RequestMapping(value = "editar_listado", method = RequestMethod.POST)
	public String editar_listado(ModelMap map, @Valid IngresoEgreso obj, BindingResult bindingResult,String monto) {
		try {
			if (obj != null) {
				monto = monto.replaceAll("\\.", "");
				obj.setMonto(Integer.valueOf(monto));
				getDao().createOrUpdate(obj);
				logger.info("Estado Externo Interno Actualizado {}", obj);
				map.addAttribute("msgExito", msg.get("Registro Actualizado"));
			}
		} catch (Exception ex) {
			obj.setId(null);
			map.addAttribute("error", getErrorFromException(ex));
			map.addAttribute(getNombreObjeto(), obj);
		}
		agregarValoresAdicionales(map);
		return getTemplatePath();
	}

	@RequestMapping(value = "eliminar_listado", method = RequestMethod.POST)
	public String eliminar_listado(ModelMap map, @RequestParam("id_objeto") Long id_objeto) {
		
		IngresoEgreso estado = null;
		try {
			logger.info("ID DE OBJ {}", id_objeto);
			if (id_objeto != null) {
				estado = getDao().find(id_objeto);
				
				getDao().destroy(estado);

				logger.info("Estado Externo Interno eliminado  {}", estado);
				map.addAttribute("msgExito", msg.get("Registro Eliminado"));
			}
		} catch (UnexpectedRollbackException ex) {
			estado.setId(null);
			map.addAttribute("error", "No puede borrar el Objeto");
			map.addAttribute(getNombreObjeto(), estado);
		}
		map.addAttribute(getNombreObjeto(), getNuevaInstancia());
		agregarValoresAdicionales(map);
		return getTemplatePath();

	}
}
