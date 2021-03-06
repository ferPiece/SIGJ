package py.com.sigj.gastos.controllers.form;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.StringTokenizer;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import antlr.collections.List;
import py.com.sigj.controllers.form.FormController;
import py.com.sigj.dao.ClienteDao;
import py.com.sigj.dao.Dao;
import py.com.sigj.expediente.dao.ClienteFacturaDao;
import py.com.sigj.expediente.dao.ExpedienteDao;
import py.com.sigj.expediente.dao.ExpedienteFacturaDao;
import py.com.sigj.expediente.domain.Cliente;
import py.com.sigj.expediente.domain.ClienteFactura;
import py.com.sigj.expediente.domain.Expediente;
import py.com.sigj.expediente.domain.ExpedienteCliente;
import py.com.sigj.expediente.domain.ExpedienteFactura;
import py.com.sigj.gastos.controllers.list.FacturaCabeceraListController;
import py.com.sigj.gastos.controllers.list.ServicioListController;
import py.com.sigj.gastos.dao.FacturaCabeceraDao;
import py.com.sigj.gastos.dao.FacturaDetalleDao;
import py.com.sigj.gastos.dao.ServicioDao;
import py.com.sigj.gastos.domain.FacturaCabecera;
import py.com.sigj.gastos.domain.FacturaDetalle;
import py.com.sigj.gastos.domain.Servicio;

/**
 *
 * @author ariquelme
 *
 */

@Controller
@Scope("request")
@RequestMapping("factura_cabecera")
public class FacturaCabeceraFormController extends FormController<FacturaCabecera> {

	@Autowired
	private ClienteFacturaDao clienteFacturaDao;
	@Autowired
	private FacturaCabeceraDao facturaCabeceraDao;

	@Autowired
	private FacturaCabeceraListController facturaCabeceraList;

	@Autowired
	private ServicioDao servicioDao;
	
	@Autowired
	private ClienteDao clienteDao;
	@Autowired
	private FacturaDetalleDao facturaDetalleDao;
	
	@Autowired
	private ExpedienteDao expedienteDao;
	
	@Autowired
	private ExpedienteFacturaDao expedienteFacturaDao;
	
	@Override
	public String getTemplatePath() {
		return "gastos/factura_cabecera_index";
	}

	@Override
	public String getNombreObjeto() {
		return "facturaCabecera";
	}

	@Override
	public FacturaCabecera getNuevaInstancia() {
		return new FacturaCabecera();
	}

	@Override
	public void agregarValoresAdicionales(ModelMap map) {
		map.addAttribute("columnas", facturaCabeceraList.getColumnas());
		map.addAttribute("columnasStr", facturaCabeceraList.getColumnasStr(null));
		map.addAttribute("listaServicios", servicioDao.getList(0, 20, null));
		map.addAttribute("clienteList", clienteDao.getList(0, 100, null));
		
		super.agregarValoresAdicionales(map);
	}
	
	
	@RequestMapping(value = "/cliente-exp", method = RequestMethod.GET)
	public  String clienteExpediente(HttpServletRequest request,
			@RequestParam(value = "cliente", required = false) String cliente,ModelMap map) throws Exception{
		Long id_cliente = Long.parseLong(cliente);
		Cliente c = new Cliente();
		if(id_cliente != 0){
			c = clienteDao.find(id_cliente);
		}
		java.util.List<ExpedienteCliente> list = expedienteDao.getListByCedulaRucCliente(c.getPersona().getCedula_ruc());
		map.addAttribute("expedienteList",list);
		return "gastos/factura_cabecera_form :: expedienteList";
		//return  new ModelAndView("gastos/factura_cabecera_index","expedienteList", expedienteDao.getListByCedulaRucCliente(c.getPersona().getCedula_ruc()));
	}
	@RequestMapping(value = "/factura/confirmar", method = RequestMethod.GET)
	public @ResponseBody String confirmarFactura(HttpServletRequest request,ModelMap map) throws Exception{
		HttpSession session = request.getSession();
		
		
		logger.info("llego al confirmar");
		logger.info(String.valueOf(session.getAttribute("mapaCabecera")));
		logger.info(String.valueOf(session.getAttribute("mapaDetalle")));
		Map<String, String> mapaFc = (Map<String, String>) session.getAttribute("mapaCabecera");
		Map<String, String> mapaFd = (Map<String, String>) session.getAttribute("mapaDetalle");
		FacturaCabecera fc = new FacturaCabecera();
		
		String montoTotal = session.getAttribute("monto_total").toString();//mapaFd.get("monto_total");
		String totalIvaCinco = session.getAttribute("total_iva_5").toString();
		String totalIvaDiez = session.getAttribute("total_iva_10").toString();
		mapaFd.remove("monto_total", mapaFd.get("monto_total"));
		mapaFd.remove("total_iva_10", mapaFd.get("total_iva_10"));
		mapaFd.remove("total_iva_5", mapaFd.get("total_iva_5"));
		
		logger.info(String.valueOf(session.getAttribute("mapaDetalle")));
		TreeMap ordenado =  new TreeMap(mapaFd);
		
		java.util.List<FacturaDetalle> fd= new ArrayList<>();
		FacturaDetalle fdd = new FacturaDetalle();
		
		String last = ordenado.lastKey().toString().substring(ordenado.lastKey().toString().length()-1);
		int index = Integer.parseInt(last);
		int k=1;
		String j="1";
		int c = 1;
		Set ref = ordenado.keySet();
		Iterator it = ref.iterator();
		java.util.List<Servicio> servicio= new ArrayList<>();
		servicio = servicioDao.getList(0, 20, null);
		
		
		
		try{
			fc.setDireccion(mapaFc.get("direccion"));
			fc.setFechaEmision(mapaFc.get("fecha_emision"));
			fc.setRazonSocial(mapaFc.get("razon_social"));
			fc.setRuc(mapaFc.get("ruc"));
			String telefono = mapaFc.get("telefono");
			int tel = Integer.parseInt(telefono);
			fc.setTelefono(tel);
			fc.setTipoPago(mapaFc.get("tipo_pago"));
			fc.setMontoTotal(Integer.parseInt(montoTotal));
			fc.setTotalIvaCinco(Integer.parseInt(totalIvaCinco));
			fc.setTotalIvaDiez(Integer.parseInt(totalIvaDiez));
			facturaCabeceraDao.create(fc);
			String id =  session.getAttribute("cliente").toString();
			String id_exp =  session.getAttribute("expediente").toString();
			Long id_cliente = null;
			Long id_expediente = null;
			if(id != null && !id.equals("")){
				id_cliente = Long.parseLong(id);
			}
			if(id_exp != null && !id_exp.equals("")){
				id_expediente = Long.parseLong(id_exp);
			}
			
			
			if(id_cliente != null && id_cliente != 0){
				ClienteFactura cf = new ClienteFactura();
				Cliente cliente = clienteDao.find(id_cliente);
				cf.setCliente(cliente);
				cf.setFactura(fc);
				clienteFacturaDao.create(cf);
				if(id_expediente != null && id_expediente != 0){
					ExpedienteFactura ef = new ExpedienteFactura();
					Expediente expediente = expedienteDao.find(id_expediente);
					ef.setFactura(fc);
					ef.setExpediente(expediente);
					expedienteFacturaDao.create(ef);
					
				}
			}
			while(Integer.parseInt(j) <= index){
				k=0;
				fdd = new FacturaDetalle();
				while(k< 4){
					
					ref =  ordenado.keySet();
					it = ref.iterator();
					int n = 0;
					while (it.hasNext()) {
						
						String var1 = String.valueOf(it.next());
						n++;
						
						if(var1.equalsIgnoreCase("Nro_"+j)){
							int nro = Integer.parseInt(ordenado.get(var1).toString());
							fdd.setNro(nro);
							k++;
							
						}
						if(var1.equalsIgnoreCase("Iva 10%_"+j)){
							
							if(!(ordenado.get(var1).toString().equals(""))){
								String iva_10 = ordenado.get(var1).toString();
								fdd.setIvaDiez(iva_10);
								k++;
								
							}
							
						}
						if(var1.equalsIgnoreCase("Iva 5%_"+j)){
							if(!(ordenado.get(var1).toString().equals(""))){
								String iva_5 = ordenado.get(var1).toString();
								fdd.setIvaCinco(iva_5);
								k++;
								
							}
							
						}
						if(var1.equalsIgnoreCase("Precio unitario_"+j)){
							int monto = Integer.parseInt(ordenado.get(var1).toString());
							fdd.setMonto(monto);
							k++;
							
						}
				
						if(var1.equalsIgnoreCase("Descripción"+"_"+j)){
							for(Servicio s :servicio){
								
								Long ser = Long.valueOf(ordenado.get(var1).toString());
								logger.info(String.valueOf(s.getId()));
								if(s.getId() == ser){
									fdd.setServicio(s);
									k++;
									
								}
							}
							
						}
					}
				}
				c++;
				j = Integer.toString(c);
				fdd.setFacturaCabecera(fc);
				facturaDetalleDao.create(fdd);
			}
			
			/*java.util.List<Servicio> servicio= new ArrayList<>();
			servicio = servicioDao.getList(0, 20, null);*/
			
			
			
		} catch (Exception e) {
			throw new Exception("Error cargar los datos");
		}
		return "ok";
	}
	
	@RequestMapping(value = "/factura", method = RequestMethod.GET)
	public String generarFactura(HttpServletRequest request,ModelMap map,
			@RequestParam(value = "cliente", required = true) String cliente,
			@RequestParam(value = "expediente", required = true) String expediente,
			@RequestParam(value = "factura_cabecera", required = true) String facturaCabecera,
			@RequestParam(value = "factura_detalle", required = true) String facturaDetalle) throws JsonParseException, JsonMappingException, IOException {
			
			
			
			logger.info(String.valueOf(facturaCabecera));
			HttpSession session = request.getSession();
			
			String [] aux = facturaCabecera.split(",");
			for(int i=0;i<aux.length;i++){
				aux[i] = aux[i].replace("[","");
				aux[i] = aux[i].replace("]","");
				aux[i]=  aux[i].replaceAll("\"", "");
				
			}
			
			logger.info(String.valueOf(aux));
			String [] aux2 = facturaDetalle.split(",");
			for(int i=0;i<aux2.length;i++){
				aux2[i] = aux2[i].replace("[","");
				aux2[i] = aux2[i].replace("]","");
				aux2[i]=  aux2[i].replaceAll("\"", "");
			}
			Map<String, String> mapaFc = new HashMap<>();
			Map<String, String> mapaFd = new HashMap<>();
			logger.info(String.valueOf(aux.length));
			for(int i=0;i<aux.length;i+=2){
				logger.info((String.valueOf(i)));
				logger.info(String.valueOf(aux[i]));
				mapaFc.put(aux[i], aux[i+1]); 
				
					
				if(i == aux.length-2){
					mapaFc.put(aux[i], aux[i+1]); 
				}
				
			}
			
			logger.info(String.valueOf(aux2.length));
			for(int i=0;i<aux2.length;i+=2){
				logger.info((String.valueOf(i)));
				logger.info(String.valueOf(aux2[i]));
				logger.info(String.valueOf(aux2[i+1]));
				mapaFd.put(aux2[i], aux2[i+1]); 
				
					
				if(i == aux2.length-2){
					mapaFd.put(aux2[i], aux2[i+1]);
					logger.info(String.valueOf(aux2[i+1]));
				}
				
			}
			
			logger.info(String.valueOf(aux));
			logger.info(String.valueOf(mapaFc.get("ruc")));
			logger.info(String.valueOf(mapaFd));
			map.addAttribute("mapaCabecera", mapaFc);
			map.addAttribute("mapaDetalle",mapaFd);
			session.setAttribute("mapaCabecera", mapaFc);
			session.setAttribute("mapaDetalle", mapaFd);
			
			String montoTotal = mapaFd.get("monto_total");
			String totalIvaCinco = mapaFd.get("total_iva_5");
			String totalIvaDiez = mapaFd.get("total_iva_10");
			session.setAttribute("monto_total", montoTotal);
			session.setAttribute("total_iva_5", totalIvaCinco);
			session.setAttribute("cliente", cliente);
			session.setAttribute("expediente", expediente);
			session.setAttribute("total_iva_10", totalIvaDiez);
			mapaFd.remove("monto_total", mapaFd.get("monto_total"));
			mapaFd.remove("total_iva_10", mapaFd.get("total_iva_10"));
			mapaFd.remove("total_iva_5", mapaFd.get("total_iva_5"));
			map.addAttribute("monto_total", montoTotal);
			map.addAttribute("total_iva_5", totalIvaCinco);
			
			map.addAttribute("total_iva_10", totalIvaDiez);
			TreeMap ordenado =  new TreeMap(mapaFd);
			
			java.util.List<FacturaDetalle> fd= new ArrayList<>();
			FacturaDetalle fdd = new FacturaDetalle();
			
			String last = ordenado.lastKey().toString().substring(ordenado.lastKey().toString().length()-1);
			int index = Integer.parseInt(last);
			int k=1;
			String j="1";
			int c = 1;
			Set ref = ordenado.keySet();
			Iterator it = ref.iterator();
			java.util.List<Servicio> servicio= new ArrayList<>();
			servicio = servicioDao.getList(0, 20, null);
			
			
			/*Set ref1 = nz.keySet();
			Iterator it3 = ref1.iterator();
			while(it3.hasNext()) { 
	            String s = (String)it3.next();
	            String s1 = (String)nz.get(s);
	            logger.info(s);
	            logger.info(s1);
	        }
			*/
			
				
				while(Integer.parseInt(j) <= index){
					k=0;
					fdd = new FacturaDetalle();
					while(k< 4){
						
						ref =  ordenado.keySet();
						it = ref.iterator();
						int n = 0;
						while (it.hasNext()) {
							
							String var1 = String.valueOf(it.next());
							n++;
							
							if(var1.equalsIgnoreCase("Nro_"+j)){
								int nro = Integer.parseInt(ordenado.get(var1).toString());
								fdd.setNro(nro);
								k++;
								
							}
							if(var1.equalsIgnoreCase("Iva 10%_"+j)){
								
								if(!(ordenado.get(var1).toString().equals(""))){
									String iva_10 = ordenado.get(var1).toString();
									fdd.setIvaDiez(iva_10);
									k++;
									
								}
								
							}
							if(var1.equalsIgnoreCase("Iva 5%_"+j)){
								if(!(ordenado.get(var1).toString().equals(""))){
									String iva_5 = ordenado.get(var1).toString();
									fdd.setIvaCinco(iva_5);
									k++;
									
								}
								
							}
							if(var1.equalsIgnoreCase("Precio unitario_"+j)){
								int monto = Integer.parseInt(ordenado.get(var1).toString());
								fdd.setMonto(monto);
								k++;
								
							}
					
							if(var1.equalsIgnoreCase("Descripción"+"_"+j)){
								for(Servicio s :servicio){
									
									Long ser = Long.valueOf(ordenado.get(var1).toString());
									logger.info(String.valueOf(s.getId()));
									if(s.getId() == ser){
										fdd.setServicio(s);
										k++;
										
									}
								}
								
							}
						}
					}
					c++;
					j = Integer.toString(c);
					fd.add(fdd);
					
				}
				logger.info(String.valueOf(fd));
				
				map.addAttribute("lista_detalle", fd);
		return "gastos/factura_confirmar";
	}
	@Override
	public Dao<FacturaCabecera> getDao() {
		return facturaCabeceraDao;
	}

}
