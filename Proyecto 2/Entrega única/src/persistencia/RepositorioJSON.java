package persistencia;
import java.io.File;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import logica.deportes.DisciplinaIndividual;
import logica.deportes.InscripcionDisciplina;
import logica.deportes.Instalacion;
import logica.deportes.Reserva;
import logica.deportes.Asistencia;
import logica.deportes.Competencia;
import logica.deportes.DeporteConjunto;
import logica.deportes.Equipo;
import logica.deportes.Estadistica;
import logica.deportes.EstadisticaBaloncesto;
import logica.deportes.EstadisticaFutbol;
import logica.deportes.EstadisticaTenisMesa;
import logica.deportes.EstadisticaTenisPadel;
import logica.deportes.EstadisticaVoleibol;
import logica.deportes.InscripcionCompetencia;
import logica.deportes.InscripcionEquipo;
import logica.deportes.Partido;
import logica.deportes.PartidoCompetencia;
import logica.deportes.PartidoInterno;
import logica.deportes.PartidoPractica;
import logica.deportes.SesionEntrenamiento;
import logica.tienda.Sugerencia;
import logica.usuarios.SolicitudCambioTurno;
import logica.usuarios.Turno;
import logica.tienda.Articulo;
import logica.tienda.Bebida;
import logica.tienda.LineaVenta;
import logica.tienda.Producto;
import logica.tienda.Snack;
import logica.tienda.Venta;
import logica.tienda.VentaCafeteria;
import logica.tienda.VentaTienda;
import logica.usuarios.Administrador;
import logica.usuarios.Empleado;
import logica.usuarios.Entrenador;
import logica.usuarios.Fisioterapeuta;
import logica.usuarios.Socio;
import logica.usuarios.Usuario;
public class RepositorioJSON implements RepositorioDatos {
	 private final File carpeta;

	 public RepositorioJSON(String carpetaDatos) {
	        this.carpeta = new File(carpetaDatos);
	    }
	 @Override
	    public void guardarUsuarios(Collection<Usuario> usuarios) throws Exception {
	        JSONArray arreglo = new JSONArray();
	        for (Usuario u : usuarios) {
	            JSONObject json = new JSONObject();
	            json.put("login", u.getLogin());
	            json.put("password", u.getPassword());
	            json.put("puntos", u.getPuntosFidelidad());

	            if (u instanceof Socio) {
	                Socio socio = (Socio) u;
	                json.put("tipo", "SOCIO");
	                json.put("edad", socio.getEdad());
	                if (socio.getFechaFinSuspension() != null) {
	                    json.put("fechaFinSuspension", socio.getFechaFinSuspension().toString());
	                }
	                if (socio.getEmpleadoDescuento() != null) {
	                    json.put("empleadoDescuento", socio.getEmpleadoDescuento().getLogin());
	                }
	            } else if (u instanceof Administrador) {
	                json.put("tipo", "ADMINISTRADOR");
	            } else if (u instanceof Empleado) {
	                Empleado empleado = (Empleado) u;
	                if (u instanceof Entrenador) {
	                    json.put("tipo", "ENTRENADOR");
	                } else if (u instanceof Fisioterapeuta) {
	                    json.put("tipo", "FISIOTERAPEUTA");
	                } else {
	                    json.put("tipo", "EMPLEADO");
	                }
	                json.put("codigoDescuento", empleado.getCodigoDescuento());
	            }
	            arreglo.put(json);
	        }
	        escribir("usuarios.json", arreglo);
	    }

	    @Override
	    public Map<String, Usuario> cargarUsuarios() throws Exception {
	        Map<String, Usuario> usuarios = new LinkedHashMap<String, Usuario>();
	        JSONArray arreglo = leer("usuarios.json");

	       
	        for (int i = 0; i < arreglo.length(); i++) {
	            JSONObject json = arreglo.getJSONObject(i);
	            String login = json.getString("login");
	            String password = json.getString("password");
	            Usuario usuario;
	            switch (json.getString("tipo")) {
	            case "SOCIO": {
	                Socio nuevo = new Socio(login, password, json.getInt("edad"));
	                if (json.has("fechaFinSuspension")) {
	                    nuevo.setFechaFinSuspension(LocalDate.parse(json.getString("fechaFinSuspension")));
	                }
	                usuario = nuevo;
	                break;
	            }
	            case "ADMINISTRADOR":
	                usuario = new Administrador(login, password);
	                break;
	            case "ENTRENADOR":
	                usuario = new Entrenador(login, password, json.getString("codigoDescuento"));
	                break;
	            case "FISIOTERAPEUTA":
	                usuario = new Fisioterapeuta(login, password, json.getString("codigoDescuento"));
	                break;
	            default:
	                usuario = new Empleado(login, password, json.getString("codigoDescuento"));
	            }
	            usuario.acreditaPuntos(json.getInt("puntos"));
	            usuarios.put(login, usuario);
	        }

	        
	        for (int i = 0; i < arreglo.length(); i++) {
	            JSONObject json = arreglo.getJSONObject(i);
	            if (json.has("empleadoDescuento")) {
	                Socio socio = (Socio) usuarios.get(json.getString("login"));
	                Empleado empleado = (Empleado) usuarios.get(json.getString("empleadoDescuento"));
	                socio.setEmpleadoDescuento(empleado);
	            }
	        }
	        return usuarios;
	    }
	    @Override
	    public void guardarDisciplinas(Collection<DisciplinaIndividual> disciplinas) throws Exception {
	        JSONArray arreglo = new JSONArray();
	        for (DisciplinaIndividual d : disciplinas) {
	            JSONObject jd = new JSONObject();
	            jd.put("nombre", d.getNombre());
	            jd.put("maxInstalacionesSimultaneas", d.getMaxInstalacionesSimultaneas());

	            JSONArray inscripciones = new JSONArray();
	            for (InscripcionDisciplina i : d.getInscripciones()) {
	                JSONObject ji = new JSONObject();
	                ji.put("socio", i.getSocio().getLogin());
	                ji.put("nivel", i.getNivel());
	                ji.put("puntajeRanking", i.getPuntajeRanking());
	                inscripciones.put(ji);
	            }
	            jd.put("inscripciones", inscripciones);

	            JSONArray instalaciones = new JSONArray();
	            for (Instalacion inst : d.getInstalaciones()) {
	                JSONObject jin = new JSONObject();
	                jin.put("nombre", inst.getNombre());
	                jin.put("id", inst.getId());
	                jin.put("capacidad", inst.getCapacidad());
	                jin.put("techada", inst.isTechada());
	                jin.put("tipoCancha", inst.getTipoCancha());

	                JSONArray reservas = new JSONArray();
	                for (Reserva r : inst.getReservas()) {
	                    JSONObject jr = new JSONObject();
	                    jr.put("socio", r.getSocio().getLogin());
	                    jr.put("fecha", r.getFecha().toString());
	                    jr.put("horaInicio", r.getHoraInicio().toString());
	                    jr.put("horaFin", r.getHoraFin().toString());
	                    jr.put("cantidadJugadores", r.getCantidadJugadores());
	                    jr.put("modalidad", r.getModalidad());
	                    JSONArray invitados = new JSONArray();
	                    for (Socio invitado : r.getInvitados()) {
	                        invitados.put(invitado.getLogin());
	                    }
	                    jr.put("invitados", invitados);
	                    reservas.put(jr);
	                }
	                jin.put("reservas", reservas);
	                instalaciones.put(jin);
	            }
	            jd.put("instalaciones", instalaciones);
	            arreglo.put(jd);
	        }
	        escribir("disciplinas.json", arreglo);
	    }

	    @Override
	    public ArrayList<DisciplinaIndividual> cargarDisciplinas(Map<String, Usuario> usuarios) throws Exception {
	        ArrayList<DisciplinaIndividual> disciplinas = new ArrayList<DisciplinaIndividual>();
	        JSONArray arreglo = leer("disciplinas.json");
	        for (int i = 0; i < arreglo.length(); i++) {
	            JSONObject jd = arreglo.getJSONObject(i);
	            DisciplinaIndividual disciplina = new DisciplinaIndividual(jd.getString("nombre"),
	                    jd.getInt("maxInstalacionesSimultaneas"));

	            JSONArray inscripciones = jd.getJSONArray("inscripciones");
	            for (int j = 0; j < inscripciones.length(); j++) {
	                JSONObject ji = inscripciones.getJSONObject(j);
	                Socio socio = (Socio) usuarios.get(ji.getString("socio"));
	                disciplina.agregarInscripcion(
	                        new InscripcionDisciplina(socio, ji.getString("nivel"), ji.getDouble("puntajeRanking")));
	            }

	            JSONArray instalaciones = jd.getJSONArray("instalaciones");
	            for (int j = 0; j < instalaciones.length(); j++) {
	                JSONObject jin = instalaciones.getJSONObject(j);
	                Instalacion instalacion = new Instalacion(jin.getInt("id"), jin.getString("nombre"),
	                        jin.getInt("capacidad"), jin.getBoolean("techada"), jin.getString("tipoCancha"));

	                JSONArray reservas = jin.getJSONArray("reservas");
	                for (int k = 0; k < reservas.length(); k++) {
	                    JSONObject jr = reservas.getJSONObject(k);
	                    Socio socio = (Socio) usuarios.get(jr.getString("socio"));
	                    Reserva reserva = new Reserva(socio, LocalDate.parse(jr.getString("fecha")),
	                            LocalTime.parse(jr.getString("horaInicio")), LocalTime.parse(jr.getString("horaFin")),
	                            jr.getInt("cantidadJugadores"), jr.getString("modalidad"));
	                    JSONArray invitados = jr.getJSONArray("invitados");
	                    for (int m = 0; m < invitados.length(); m++) {
	                        reserva.agregarInvitado((Socio) usuarios.get(invitados.getString(m)));
	                    }
	                    instalacion.agregarReserva(reserva);
	                }
	                disciplina.agregarInstalacion(instalacion);
	            }
	            disciplinas.add(disciplina);
	        }
	        return disciplinas;
	    }



	    @Override
	    public void guardarProductos(Collection<Producto> productos) throws Exception {
	        JSONArray arreglo = new JSONArray();
	        for (Producto p : productos) {
	            arreglo.put(productoAJson(p));
	        }
	        escribir("productos.json", arreglo);
	    }

	    @Override
	    public ArrayList<Producto> cargarProductos() throws Exception {
	        ArrayList<Producto> productos = new ArrayList<Producto>();
	        JSONArray arreglo = leer("productos.json");
	        for (int i = 0; i < arreglo.length(); i++) {
	            productos.add(jsonAProducto(arreglo.getJSONObject(i)));
	        }
	        return productos;
	    }

	    private JSONObject productoAJson(Producto p) {
	        JSONObject json = new JSONObject();
	        json.put("id", p.getIdProducto());
	        json.put("nombre", p.getNombre());
	        json.put("precio", p.getPrecio());
	        if (p instanceof Articulo) {
	            Articulo a = (Articulo) p;
	            json.put("tipo", "ARTICULO");
	            json.put("categoria", a.getCategoria());
	            json.put("cantidadDisponible", a.getCantidadDisponible());
	            json.put("ubicacion", a.getUbicacion());
	        } else if (p instanceof Bebida) {
	            json.put("tipo", "BEBIDA");
	            json.put("caliente", ((Bebida) p).isCaliente());
	        } else {
	            json.put("tipo", "SNACK");
	            json.put("alergenos", new JSONArray(((Snack) p).getAlergenos()));
	        }
	        return json;
	    }

	    private Producto jsonAProducto(JSONObject json) {
	        int id = json.getInt("id");
	        String nombre = json.getString("nombre");
	        double precio = json.getDouble("precio");
	        switch (json.getString("tipo")) {
	        case "ARTICULO":
	            return new Articulo(id, nombre, precio, json.getString("categoria"), json.getInt("cantidadDisponible"),
	                    json.getString("ubicacion"));
	        case "BEBIDA":
	            return new Bebida(id, nombre, precio, json.getBoolean("caliente"));
	        default:
	            ArrayList<String> alergenos = new ArrayList<String>();
	            JSONArray ja = json.getJSONArray("alergenos");
	            for (int j = 0; j < ja.length(); j++) {
	                alergenos.add(ja.getString(j));
	            }
	            return new Snack(id, nombre, precio, alergenos);
	        }
	    }

	    @Override
	    public void guardarVentas(Collection<Venta> ventas) throws Exception {
	        JSONArray arreglo = new JSONArray();
	        for (Venta v : ventas) {
	            JSONObject json = new JSONObject();
	            json.put("fecha", v.getFecha().toString());
	            json.put("comprador", v.getComprador().getLogin());
	            json.put("puntosRedimidos", v.getPuntosRedimidos());
	            if (v instanceof VentaCafeteria) {
	                json.put("tipo", "CAFETERIA");
	                json.put("propina", ((VentaCafeteria) v).getPorcentajePropina());
	            } else {
	                json.put("tipo", "TIENDA");
	            }
	            JSONArray lineas = new JSONArray();
	            for (LineaVenta l : v.getLineas()) {
	                JSONObject jl = new JSONObject();
	                jl.put("producto", l.getProducto().getIdProducto());
	                jl.put("cantidad", l.getCantidad());
	                jl.put("precioUnitario", l.getPrecioUnitario());
	                lineas.put(jl);
	            }
	            json.put("lineas", lineas);
	            arreglo.put(json);
	        }
	        escribir("ventas.json", arreglo);
	    }

	    @Override
	    public ArrayList<Venta> cargarVentas(Map<String, Usuario> usuarios, Map<Integer, Producto> productos)
	            throws Exception {
	        ArrayList<Venta> ventas = new ArrayList<Venta>();
	        JSONArray arreglo = leer("ventas.json");
	        for (int i = 0; i < arreglo.length(); i++) {
	            JSONObject json = arreglo.getJSONObject(i);
	            Usuario comprador = usuarios.get(json.getString("comprador"));
	            Venta venta;
	            if (json.getString("tipo").equals("CAFETERIA")) {
	                VentaCafeteria vc = new VentaCafeteria(comprador);
	                vc.setPorcentajePropina(json.getDouble("propina"));
	                venta = vc;
	            } else {
	                venta = new VentaTienda(comprador);
	            }
	            venta.setFecha(LocalDateTime.parse(json.getString("fecha")));
	            venta.setPuntosRedimidos(json.getInt("puntosRedimidos"));
	            JSONArray lineas = json.getJSONArray("lineas");
	            for (int j = 0; j < lineas.length(); j++) {
	                JSONObject jl = lineas.getJSONObject(j);
	                venta.agregarLinea(productos.get(jl.getInt("producto")), jl.getInt("cantidad"),
	                        jl.getDouble("precioUnitario"));
	            }
	            ventas.add(venta);
	        }
	        return ventas;
	    }


	    // ------------------------------------------------------------ deportes y equipos
	    @Override
	    public void guardarDeportes(Collection<DeporteConjunto> deportes, Collection<Usuario> usuarios)
	            throws Exception {
	        JSONArray arreglo = new JSONArray();
	        for (DeporteConjunto d : deportes) {
	            JSONObject jd = new JSONObject();
	            jd.put("nombre", d.getNombre());
	            jd.put("minimoJugadores", d.getMinimoJugadores());
	            JSONArray equipos = new JSONArray();
	            for (Equipo e : d.getEquipos()) {
	                JSONObject je = new JSONObject();
	                je.put("nombre", e.getNombre());
	                je.put("categoria", e.getCategoria());
	                je.put("cupoMaximo", e.getCupoMaximo());
	                for (Usuario u : usuarios) {
	                    if (u instanceof Entrenador && ((Entrenador) u).dirige(e)) {
	                        je.put("entrenador", u.getLogin());
	                    }
	                }
	                JSONArray plantilla = new JSONArray();
	                for (InscripcionEquipo i : e.getPlantilla()) {
	                    JSONObject ji = new JSONObject();
	                    ji.put("socio", i.getSocio().getLogin());
	                    ji.put("posicion", i.getPosicion());
	                    ji.put("camiseta", i.getNumeroCamiseta());
	                    plantilla.put(ji);
	                }
	                je.put("plantilla", plantilla);
	                equipos.put(je);
	            }
	            jd.put("equipos", equipos);
	            arreglo.put(jd);
	        }
	        escribir("deportes.json", arreglo);
	    }

	    @Override
	    public ArrayList<DeporteConjunto> cargarDeportes(Map<String, Usuario> usuarios) throws Exception {
	        ArrayList<DeporteConjunto> deportes = new ArrayList<DeporteConjunto>();
	        JSONArray arreglo = leer("deportes.json");
	        for (int i = 0; i < arreglo.length(); i++) {
	            JSONObject jd = arreglo.getJSONObject(i);
	            DeporteConjunto deporte = new DeporteConjunto(jd.getString("nombre"), jd.getInt("minimoJugadores"));
	            JSONArray equipos = jd.getJSONArray("equipos");
	            for (int j = 0; j < equipos.length(); j++) {
	                JSONObject je = equipos.getJSONObject(j);
	                Equipo equipo = new Equipo(je.getString("nombre"), je.getString("categoria"),
	                        je.getInt("cupoMaximo"), deporte);
	                JSONArray plantilla = je.getJSONArray("plantilla");
	                for (int k = 0; k < plantilla.length(); k++) {
	                    JSONObject ji = plantilla.getJSONObject(k);
	                    Socio socio = (Socio) usuarios.get(ji.getString("socio"));
	                    equipo.agregarJugador(new InscripcionEquipo(socio, ji.getString("posicion"),
	                            ji.getInt("camiseta")));
	                }
	                if (je.has("entrenador")) {
	                    ((Entrenador) usuarios.get(je.getString("entrenador"))).agregarEquipo(equipo);
	                }
	                deporte.agregarEquipo(equipo);
	            }
	            deportes.add(deporte);
	        }
	        return deportes;
	    }

	    // ------------------------------------------------------------------ competencias
	    @Override
	    public void guardarCompetencias(Collection<Competencia> competencias) throws Exception {
	        JSONArray arreglo = new JSONArray();
	        for (Competencia c : competencias) {
	            JSONObject jc = new JSONObject();
	            jc.put("nombre", c.getNombre());
	            jc.put("entidad", c.getEntidadOrganizadora());
	            jc.put("temporada", c.getTemporada());
	            jc.put("categoria", c.getCategoriaHabilitada());
	            jc.put("formato", c.getFormatoJuego());
	            jc.put("nominaMinima", c.getNominaMinima());
	            jc.put("deporte", c.getDeporte().getNombre());
	            JSONArray inscripciones = new JSONArray();
	            for (InscripcionCompetencia i : c.getInscripciones()) {
	                JSONObject ji = new JSONObject();
	                ji.put("equipo", i.getEquipo().getNombre());
	                JSONArray nomina = new JSONArray();
	                for (Socio s : i.getNomina()) {
	                    nomina.put(s.getLogin());
	                }
	                ji.put("nomina", nomina);
	                inscripciones.put(ji);
	            }
	            jc.put("inscripciones", inscripciones);
	            arreglo.put(jc);
	        }
	        escribir("competencias.json", arreglo);
	    }

	    @Override
	    public ArrayList<Competencia> cargarCompetencias(ArrayList<DeporteConjunto> deportes,
	            Map<String, Usuario> usuarios) throws Exception {
	        ArrayList<Competencia> competencias = new ArrayList<Competencia>();
	        JSONArray arreglo = leer("competencias.json");
	        for (int i = 0; i < arreglo.length(); i++) {
	            JSONObject jc = arreglo.getJSONObject(i);
	            Competencia competencia = new Competencia(jc.getString("nombre"), jc.getString("entidad"),
	                    jc.getString("temporada"), jc.getString("categoria"), jc.getString("formato"),
	                    jc.getInt("nominaMinima"), buscarDeporte(deportes, jc.getString("deporte")));
	            JSONArray inscripciones = jc.getJSONArray("inscripciones");
	            for (int j = 0; j < inscripciones.length(); j++) {
	                JSONObject ji = inscripciones.getJSONObject(j);
	                Equipo equipo = buscarEquipo(deportes, ji.getString("equipo"));
	                ArrayList<Socio> nomina = new ArrayList<Socio>();
	                JSONArray jn = ji.getJSONArray("nomina");
	                for (int k = 0; k < jn.length(); k++) {
	                    nomina.add((Socio) usuarios.get(jn.getString(k)));
	                }
	                equipo.getInscripcionesCompetencia().add(InscripcionCompetencia.crearEquipo(equipo, competencia, nomina));
	            }
	            competencias.add(competencia);
	        }
	        return competencias;
	    }

	    // --------------------------------------------------------------------- partidos
	    @Override
	    public void guardarPartidos(Collection<Partido> partidos) throws Exception {
	        JSONArray arreglo = new JSONArray();
	        for (Partido p : partidos) {
	            JSONObject jp = new JSONObject();
	            jp.put("fecha", p.getFecha().toString());
	            jp.put("marcador", p.getMarcador());
	            JSONArray alineados = new JSONArray();
	            for (Socio s : p.getAlineados()) {
	                alineados.put(s.getLogin());
	            }
	            jp.put("alineados", alineados);
	            if (p instanceof PartidoCompetencia) {
	                PartidoCompetencia pc = (PartidoCompetencia) p;
	                jp.put("tipo", "COMPETENCIA");
	                jp.put("institucionRival", pc.getInstitucionRival());
	                jp.put("escenario", pc.getEscenario());
	                jp.put("fase", pc.getFase());
	                jp.put("competencia", pc.getCompetencia().getNombre());
	                jp.put("equipo", pc.getEquipo().getNombre());
	            } else if (p instanceof PartidoInterno) {
	                PartidoInterno pi = (PartidoInterno) p;
	                jp.put("tipo", "INTERNO");
	                jp.put("local", pi.getLocal().getNombre());
	                jp.put("visitante", pi.getVisitante().getNombre());
	            } else {
	                PartidoPractica pp = (PartidoPractica) p;
	                jp.put("tipo", "PRACTICA");
	                jp.put("modalidad", pp.getModalidad());
	                jp.put("disciplina", pp.getDisciplina().getNombre());
	                if (pp.getRival() != null) {
	                    jp.put("rival", pp.getRival().getLogin());
	                }
	                JSONArray jugadores = new JSONArray();
	                for (Socio s : pp.getJugadores()) {
	                    jugadores.put(s.getLogin());
	                }
	                jp.put("jugadores", jugadores);
	            }
	            JSONArray estadisticas = new JSONArray();
	            for (Estadistica e : p.getEstadisticas()) {
	                estadisticas.put(estadisticaAJson(e));
	            }
	            jp.put("estadisticas", estadisticas);
	            arreglo.put(jp);
	        }
	        escribir("partidos.json", arreglo);
	    }

	    @Override
	    public ArrayList<Partido> cargarPartidos(Map<String, Usuario> usuarios, ArrayList<DeporteConjunto> deportes,
	            ArrayList<Competencia> competencias, ArrayList<DisciplinaIndividual> disciplinas) throws Exception {
	        ArrayList<Partido> partidos = new ArrayList<Partido>();
	        JSONArray arreglo = leer("partidos.json");
	        for (int i = 0; i < arreglo.length(); i++) {
	            JSONObject jp = arreglo.getJSONObject(i);
	            LocalDate fecha = LocalDate.parse(jp.getString("fecha"));
	            Partido partido;
	            switch (jp.getString("tipo")) {
	            case "COMPETENCIA": {
	                Competencia competencia = null;
	                for (Competencia c : competencias) {
	                    if (c.getNombre().equals(jp.getString("competencia"))) {
	                        competencia = c;
	                    }
	                }
	                partido = new PartidoCompetencia(fecha, jp.getString("institucionRival"),
	                        jp.getString("escenario"), jp.getString("fase"), competencia,
	                        buscarEquipo(deportes, jp.getString("equipo")));
	                break;
	            }
	            case "INTERNO":
	                partido = new PartidoInterno(fecha, buscarEquipo(deportes, jp.getString("local")),
	                        buscarEquipo(deportes, jp.getString("visitante")));
	                break;
	            default: {
	                ArrayList<Socio> jugadores = new ArrayList<Socio>();
	                JSONArray jj = jp.getJSONArray("jugadores");
	                for (int k = 0; k < jj.length(); k++) {
	                    jugadores.add((Socio) usuarios.get(jj.getString(k)));
	                }
	                PartidoPractica pp = new PartidoPractica(fecha, jp.getString("modalidad"),
	                        buscarDisciplina(disciplinas, jp.getString("disciplina")), jugadores);
	                if (jp.has("rival")) {
	                    pp.setRival((Entrenador) usuarios.get(jp.getString("rival")));
	                }
	                partido = pp;
	            }
	            }
	            partido.setMarcador(jp.getString("marcador"));
	            JSONArray alineados = jp.getJSONArray("alineados");
	            ArrayList<Socio> lista = new ArrayList<Socio>();
	            for (int k = 0; k < alineados.length(); k++) {
	                lista.add((Socio) usuarios.get(alineados.getString(k)));
	            }
	            if (!lista.isEmpty()) {
	                partido.registroAlineacion(lista);
	            }
	            JSONArray estadisticas = jp.getJSONArray("estadisticas");
	            for (int k = 0; k < estadisticas.length(); k++) {
	                JSONObject je = estadisticas.getJSONObject(k);
	                Socio socio = (Socio) usuarios.get(je.getString("socio"));
	                Estadistica estadistica = jsonAEstadistica(je, socio, partido);
	                partido.agregarEstadistica(estadistica);
	                socio.agregarEstadistica(estadistica);
	            }
	            partidos.add(partido);
	        }
	        return partidos;
	    }

	    private JSONObject estadisticaAJson(Estadistica e) {
	        JSONObject je = new JSONObject();
	        je.put("socio", e.getSocio().getLogin());
	        if (e instanceof EstadisticaFutbol) {
	            EstadisticaFutbol f = (EstadisticaFutbol) e;
	            je.put("tipo", "FUTBOL");
	            je.put("goles", f.getGoles());
	            je.put("asistencias", f.getAsistencias());
	            je.put("amarillas", f.getTarjetasAmarillas());
	            je.put("rojas", f.getTarjetasRojas());
	        } else if (e instanceof EstadisticaBaloncesto) {
	            EstadisticaBaloncesto b = (EstadisticaBaloncesto) e;
	            je.put("tipo", "BALONCESTO");
	            je.put("puntos", b.getPuntos());
	            je.put("rebotes", b.getRebotes());
	        } else if (e instanceof EstadisticaVoleibol) {
	            EstadisticaVoleibol v = (EstadisticaVoleibol) e;
	            je.put("tipo", "VOLEIBOL");
	            je.put("aces", v.getAces());
	            je.put("bloqueos", v.getBloqueos());
	        } else if (e instanceof EstadisticaTenisMesa) {
	            EstadisticaTenisMesa m = (EstadisticaTenisMesa) e;
	            je.put("tipo", "TENIS_MESA");
	            je.put("setsGanados", m.getSetsGanados());
	            je.put("puntosTotales", m.getPuntosTotales());
	        } else {
	            EstadisticaTenisPadel t = (EstadisticaTenisPadel) e;
	            je.put("tipo", "TENIS_PADEL");
	            je.put("setsGanados", t.getSetsGanados());
	            je.put("gamesGanados", t.getGamesGanados());
	            je.put("aces", t.getAces());
	            je.put("erroresNoForzados", t.getErroresNoForzados());
	        }
	        return je;
	    }

	    private Estadistica jsonAEstadistica(JSONObject je, Socio socio, Partido partido) {
	        switch (je.getString("tipo")) {
	        case "FUTBOL":
	            return new EstadisticaFutbol(socio, partido, je.getInt("goles"), je.getInt("asistencias"),
	                    je.getInt("amarillas"), je.getInt("rojas"));
	        case "BALONCESTO":
	            return new EstadisticaBaloncesto(socio, partido, je.getInt("puntos"), je.getInt("rebotes"));
	        case "VOLEIBOL":
	            return new EstadisticaVoleibol(socio, partido, je.getInt("aces"), je.getInt("bloqueos"));
	        case "TENIS_MESA":
	            return new EstadisticaTenisMesa(socio, partido, je.getInt("setsGanados"), je.getInt("puntosTotales"));
	        default:
	            return new EstadisticaTenisPadel(socio, partido, je.getInt("setsGanados"), je.getInt("gamesGanados"),
	                    je.getInt("aces"), je.getInt("erroresNoForzados"));
	        }
	    }

	    // ------------------------------------------------------------------------ sesiones
	    @Override
	    public void guardarSesiones(Collection<SesionEntrenamiento> sesiones) throws Exception {
	        JSONArray arreglo = new JSONArray();
	        for (SesionEntrenamiento s : sesiones) {
	            JSONObject js = new JSONObject();
	            js.put("fecha", s.getFecha().toString());
	            js.put("hora", s.getHora().toString());
	            js.put("duracion", s.getDuracion());
	            js.put("tipo", s.getTipo());
	            js.put("entrenador", s.getEntrenador().getLogin());
	            js.put("instalacion", s.getInstalacion().getId());
	            if (s.getEquipo() != null) {
	                js.put("equipo", s.getEquipo().getNombre());
	            }
	            JSONArray asistencias = new JSONArray();
	            for (Asistencia a : s.getAsistencias()) {
	                JSONObject ja = new JSONObject();
	                ja.put("socio", a.getSocio().getLogin());
	                ja.put("estado", a.getEstado());
	                ja.put("nota", a.getNotaDesempeno());
	                ja.put("comentario", a.getComentario());
	                asistencias.put(ja);
	            }
	            js.put("asistencias", asistencias);
	            arreglo.put(js);
	        }
	        escribir("sesiones.json", arreglo);
	    }

	    @Override
	    public ArrayList<SesionEntrenamiento> cargarSesiones(Map<String, Usuario> usuarios,
	            ArrayList<DeporteConjunto> deportes, ArrayList<DisciplinaIndividual> disciplinas) throws Exception {
	        ArrayList<SesionEntrenamiento> sesiones = new ArrayList<SesionEntrenamiento>();
	        JSONArray arreglo = leer("sesiones.json");
	        for (int i = 0; i < arreglo.length(); i++) {
	            JSONObject js = arreglo.getJSONObject(i);
	            Instalacion instalacion = null;
	            for (DisciplinaIndividual d : disciplinas) {
	                for (Instalacion inst : d.getInstalaciones()) {
	                    if (inst.getId() == js.getInt("instalacion")) {
	                        instalacion = inst;
	                    }
	                }
	            }
	            SesionEntrenamiento sesion = new SesionEntrenamiento(LocalDate.parse(js.getString("fecha")),
	                    LocalTime.parse(js.getString("hora")), js.getInt("duracion"), js.getString("tipo"),
	                    (Entrenador) usuarios.get(js.getString("entrenador")), instalacion);
	            if (js.has("equipo")) {
	                sesion.setEquipo(buscarEquipo(deportes, js.getString("equipo")));
	            }
	            JSONArray asistencias = js.getJSONArray("asistencias");
	            for (int j = 0; j < asistencias.length(); j++) {
	                JSONObject ja = asistencias.getJSONObject(j);
	                sesion.registrarAsistencia((Socio) usuarios.get(ja.getString("socio")), ja.getString("estado"),
	                        ja.getInt("nota"), ja.getString("comentario"));
	            }
	            sesiones.add(sesion);
	        }
	        return sesiones;
	    }

	    // ------------------------------------------------------------- turnos y solicitudes
	    @Override
	    public void guardarTurnos(Collection<Usuario> usuarios) throws Exception {
	        JSONArray arreglo = new JSONArray();
	        for (Usuario u : usuarios) {
	            if (!(u instanceof Empleado)) {
	                continue;
	            }
	            Empleado empleado = (Empleado) u;
	            for (Turno t : empleado.getTurnos()) {
	                JSONObject jt = new JSONObject();
	                jt.put("empleado", empleado.getLogin());
	                jt.put("dia", t.getDia());
	                jt.put("horaInicio", t.getHoraInicio().toString());
	                jt.put("horaFin", t.getHoraFin().toString());
	                JSONArray solicitudes = new JSONArray();
	                for (SolicitudCambioTurno s : empleado.getSolicitudes()) {
	                    if (s.getTurno() != t) {
	                        continue;
	                    }
	                    JSONObject js = new JSONObject();
	                    js.put("fecha", s.getFechaSolicitada().toString());
	                    js.put("horaInicio", s.getHoraInicio().toString());
	                    js.put("horaFin", s.getHoraFin().toString());
	                    js.put("estado", s.getEstado());
	                    for (Usuario otro : usuarios) {
	                        if (otro instanceof Administrador
	                                && ((Administrador) otro).getSolicitudesResueltas().contains(s)) {
	                            js.put("resueltaPor", otro.getLogin());
	                        }
	                    }
	                    solicitudes.put(js);
	                }
	                jt.put("solicitudes", solicitudes);
	                arreglo.put(jt);
	            }
	        }
	        escribir("turnos.json", arreglo);
	    }

	    @Override
	    public void cargarTurnos(Map<String, Usuario> usuarios) throws Exception {
	        JSONArray arreglo = leer("turnos.json");
	        for (int i = 0; i < arreglo.length(); i++) {
	            JSONObject jt = arreglo.getJSONObject(i);
	            Empleado empleado = (Empleado) usuarios.get(jt.getString("empleado"));
	            Turno turno = new Turno(jt.getString("dia"), LocalTime.parse(jt.getString("horaInicio")),
	                    LocalTime.parse(jt.getString("horaFin")));
	            empleado.agregarTurno(turno);
	            JSONArray solicitudes = jt.getJSONArray("solicitudes");
	            for (int j = 0; j < solicitudes.length(); j++) {
	                JSONObject js = solicitudes.getJSONObject(j);
	                SolicitudCambioTurno solicitud = new SolicitudCambioTurno(turno,
	                        LocalDate.parse(js.getString("fecha")), LocalTime.parse(js.getString("horaInicio")),
	                        LocalTime.parse(js.getString("horaFin")));
	                solicitud.setEstado(js.getString("estado"));
	                empleado.agregarSolicitud(solicitud);
	                if (js.has("resueltaPor")) {
	                    ((Administrador) usuarios.get(js.getString("resueltaPor"))).agregarSolicitudResuelta(solicitud);
	                }
	            }
	        }
	    }

	    // ------------------------------------------------------------------- sugerencias
	    @Override
	    public void guardarSugerencias(Collection<Usuario> usuarios) throws Exception {
	        JSONArray arreglo = new JSONArray();
	        for (Usuario u : usuarios) {
	            if (!(u instanceof Empleado)) {
	                continue;
	            }
	            for (Sugerencia s : ((Empleado) u).getSugerencias()) {
	                JSONObject js = new JSONObject();
	                js.put("empleado", u.getLogin());
	                js.put("estado", s.getEstado());
	                js.put("producto", productoAJson(s.getProducto()));
	                for (Usuario otro : usuarios) {
	                    if (otro instanceof Administrador
	                            && ((Administrador) otro).getSugerenciasAprobadas().contains(s)) {
	                        js.put("aprobadaPor", otro.getLogin());
	                    }
	                }
	                arreglo.put(js);
	            }
	        }
	        escribir("sugerencias.json", arreglo);
	    }

	    @Override
	    public void cargarSugerencias(Map<String, Usuario> usuarios) throws Exception {
	        JSONArray arreglo = leer("sugerencias.json");
	        for (int i = 0; i < arreglo.length(); i++) {
	            JSONObject js = arreglo.getJSONObject(i);
	            Sugerencia sugerencia = new Sugerencia(jsonAProducto(js.getJSONObject("producto")));
	            if (js.getString("estado").equals(Sugerencia.APROBADA)) {
	                sugerencia.aprobar();
	            } else if (js.getString("estado").equals(Sugerencia.RECHAZADA)) {
	                sugerencia.rechazar();
	            }
	            ((Empleado) usuarios.get(js.getString("empleado"))).agregarSugerencia(sugerencia);
	            if (js.has("aprobadaPor")) {
	                ((Administrador) usuarios.get(js.getString("aprobadaPor"))).agregarSugerenciaAprobada(sugerencia);
	            }
	        }
	    }

	    // ----------------------------------------------------------------------- busquedas
	    private DeporteConjunto buscarDeporte(ArrayList<DeporteConjunto> deportes, String nombre) {
	        for (DeporteConjunto d : deportes) {
	            if (d.getNombre().equals(nombre)) {
	                return d;
	            }
	        }
	        return null;
	    }

	    private Equipo buscarEquipo(ArrayList<DeporteConjunto> deportes, String nombre) {
	        for (DeporteConjunto d : deportes) {
	            for (Equipo e : d.getEquipos()) {
	                if (e.getNombre().equals(nombre)) {
	                    return e;
	                }
	            }
	        }
	        return null;
	    }

	    private DisciplinaIndividual buscarDisciplina(ArrayList<DisciplinaIndividual> disciplinas, String nombre) {
	        for (DisciplinaIndividual d : disciplinas) {
	            if (d.getNombre().equals(nombre)) {
	                return d;
	            }
	        }
	        return null;
	    }

	    private void escribir(String nombreArchivo, JSONArray arreglo) throws Exception {
	        Files.createDirectories(carpeta.toPath());
	        Files.writeString(new File(carpeta, nombreArchivo).toPath(), arreglo.toString(2));
	    }

	    // Devuelve el contenido del archivo, o un arreglo vacio si todavia no existe.
	    private JSONArray leer(String nombreArchivo) throws Exception {
	        File archivo = new File(carpeta, nombreArchivo);
	        if (!archivo.exists()) {
	            return new JSONArray();
	        }
	        return new JSONArray(Files.readString(archivo.toPath()));
	    }
}
