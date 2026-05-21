package com.cancha.futbol.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.cancha.futbol.demo.entity.Categoria;
import com.cancha.futbol.demo.entity.EstadoMatricula;
import com.cancha.futbol.demo.entity.EstadoPago;
import com.cancha.futbol.demo.entity.Matricula;
import com.cancha.futbol.demo.entity.Pago;
import com.cancha.futbol.demo.repository.AlumnoRepository;
import com.cancha.futbol.demo.repository.CategoriaRepository;
import com.cancha.futbol.demo.repository.MatriculaRepository;
import com.cancha.futbol.demo.repository.PagoRepository;
import com.cancha.futbol.demo.service.MatriculaService;
import com.cancha.futbol.demo.service.PagoService;

@Controller
@RequestMapping("/dashboard")
public class AdminDashboardController {
    private final AlumnoRepository alumnoRepo;
    private final CategoriaRepository categoriaRepo;
    private final MatriculaRepository matriculaRepo;
    private final PagoRepository pagoRepo;
    private final MatriculaService matriculaService;
    private final PagoService pagoService;

    public AdminDashboardController(AlumnoRepository alumnoRepo,
            CategoriaRepository categoriaRepo,
            MatriculaRepository matriculaRepo,
            PagoRepository pagoRepo,
            MatriculaService matriculaService,
            PagoService pagoService) {
        this.alumnoRepo = alumnoRepo;
        this.categoriaRepo = categoriaRepo;
        this.matriculaRepo = matriculaRepo;
        this.pagoRepo = pagoRepo;
        this.matriculaService = matriculaService;
        this.pagoService = pagoService;
    }

    @GetMapping
    public String dashboard(@RequestParam(required = false) String entity,
            @RequestParam(required = false) EstadoMatricula estado,
            Model model) {
        model.addAttribute("alumnoCount", alumnoRepo.count());
        model.addAttribute("categoriaCount", categoriaRepo.count());
        model.addAttribute("matriculaCount", matriculaRepo.count());
        model.addAttribute("pagoCount", pagoRepo.count());
        model.addAttribute("alumnos", alumnoRepo.findAll());
        model.addAttribute("categorias", categoriaRepo.findAll());
        model.addAttribute("pagos", pagoRepo.findAll());
        model.addAttribute("entity", entity == null ? "dashboard" : entity);
        model.addAttribute("estadoOptions", EstadoMatricula.values());
        model.addAttribute("selectedEstado", estado);

        if (entity != null && entity.equals("matriculas")) {
            if (estado != null) {
                model.addAttribute("matriculas", matriculaRepo.findByEstado(estado));
            } else {
                model.addAttribute("matriculas", matriculaRepo.findAll());
            }
        } else {
            model.addAttribute("matriculas", matriculaRepo.findAll());
        }
        return "dashboard";
    }

    @ModelAttribute("newCategoria")
    public Categoria newCategoria() {
        return new Categoria();
    }

    @GetMapping("/registro")
    public String showRegistroForm(Model model) {
        model.addAttribute("entity", "registro");
        model.addAttribute("matricula", new Matricula());
        model.addAttribute("categorias", categoriaRepo.findAll());
        model.addAttribute("selectedEstado", null);
        model.addAttribute("estadoOptions", EstadoMatricula.values());
        return "dashboard";
    }

    @PostMapping("/categorias")
    public String processCategoria(@ModelAttribute Categoria categoria) {
        if (categoria.getCuposDisponibles() == null) {
            categoria.setCuposDisponibles(categoria.getCuposTotales());
        }
        categoriaRepo.save(categoria);
        return "redirect:/dashboard?entity=categorias";
    }

    @PostMapping("/registro")
    public String processRegistro(@ModelAttribute Matricula matricula,
            @RequestParam(required = false) String numeroOperacion) {
        Matricula created = matriculaService.create(matricula);
        if (numeroOperacion != null && !numeroOperacion.isBlank()) {
            Pago pago = new Pago();
            pago.setMatricula(created);
            pago.setMonto(created.getCategoria().getMontoMatricula());
            pago.setNumeroOperacion(numeroOperacion);
            pago.setEstadoPago(EstadoPago.APROBADO);
            pagoService.create(pago);
        }
        return "redirect:/dashboard/registro/print/" + created.getIdMatricula();
    }

    @GetMapping("/registro/print/{id}")
    public String showPrintView(@PathVariable Long id, Model model) {
        Matricula matricula = matriculaRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Matrícula no encontrada: " + id));
        model.addAttribute("matricula", matricula);
        // try to find a payment for this matricula (may be null)
        Pago pago = pagoRepo.findAll().stream().filter(p -> p.getMatricula() != null && p.getMatricula().getIdMatricula() != null && p.getMatricula().getIdMatricula().equals(id)).findFirst().orElse(null);
        model.addAttribute("pago", pago);
        return "registro_print";
    }

    @PostMapping("/matriculas/{id}/confirm")
    public String confirmMatricula(@PathVariable Long id) {
        matriculaService.confirm(id);
        return "redirect:/dashboard?entity=matriculas";
    }

    @PostMapping("/matriculas/{id}/cancel")
    public String cancelMatricula(@PathVariable Long id) {
        matriculaService.cancel(id);
        return "redirect:/dashboard?entity=matriculas";
    }
}
