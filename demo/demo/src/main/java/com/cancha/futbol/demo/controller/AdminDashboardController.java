package com.cancha.futbol.demo.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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
            @RequestParam(required = false) String search,
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
        model.addAttribute("search", search);

        if (entity != null && entity.equals("matriculas")) {
            if (search != null && !search.isBlank()) {
                if (estado != null) {
                    model.addAttribute("matriculas", matriculaRepo.searchByEstado(estado, search.trim()));
                } else {
                    model.addAttribute("matriculas", matriculaRepo.search(search.trim()));
                }
            } else if (estado != null) {
                model.addAttribute("matriculas", matriculaRepo.findByEstado(estado));
            } else {
                model.addAttribute("matriculas", matriculaRepo.findAll());
            }
        } else {
            model.addAttribute("matriculas", matriculaRepo.findAll());
        }
        return "dashboard";
    }

    @GetMapping("/reportes-avanzados")
    public String advancedReports(@RequestParam(required = false, defaultValue = "matriculas") String reportEntity,
                                  @RequestParam(required = false) LocalDate startDate,
                                  @RequestParam(required = false) LocalDate endDate,
                                  Model model) {
        model.addAttribute("alumnoCount", alumnoRepo.count());
        model.addAttribute("categoriaCount", categoriaRepo.count());
        model.addAttribute("matriculaCount", matriculaRepo.count());
        model.addAttribute("pagoCount", pagoRepo.count());
        model.addAttribute("alumnos", alumnoRepo.findAll());
        model.addAttribute("categorias", categoriaRepo.findAll());
        model.addAttribute("pagos", pagoRepo.findAll());
        model.addAttribute("entity", "reportes");
        model.addAttribute("reportEntity", reportEntity);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("estadoOptions", EstadoMatricula.values());
        model.addAttribute("selectedEstado", null);

        switch (reportEntity) {
            case "alumnos" -> model.addAttribute("reportAlumnos", getAlumnosForRange(startDate, endDate));
            case "categorias" -> model.addAttribute("reportCategorias", categoriaRepo.findAll());
            case "pagos" -> model.addAttribute("reportPagos", getPagosForRange(startDate, endDate));
            default -> model.addAttribute("reportMatriculas", getMatriculasForRange(startDate, endDate));
        }
        return "dashboard";
    }

    @GetMapping("/report")
    public void downloadReport(@RequestParam(required = false, defaultValue = "dashboard") String entity,
                               @RequestParam(required = false, defaultValue = "pdf") String format,
                               @RequestParam(required = false) LocalDate startDate,
                               @RequestParam(required = false) LocalDate endDate,
                               HttpServletResponse response) throws IOException {
        String suffix = "";
        if (startDate != null) {
            suffix += "_" + startDate;
        }
        if (endDate != null) {
            suffix += "_" + endDate;
        }
        String extension = "pdf".equalsIgnoreCase(format) ? "pdf" : "csv";
        String filename = "reporte_" + entity + suffix + "." + extension;

        if ("pdf".equalsIgnoreCase(format)) {
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
            response.getOutputStream().write(generatePdfReport(entity, startDate, endDate));
        } else {
            response.setContentType("text/csv; charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
            try (PrintWriter writer = response.getWriter()) {
                switch (entity) {
                    case "alumnos" -> writeAlumnosCsv(writer, getAlumnosForRange(startDate, endDate));
                    case "categorias" -> writeCategoriasCsv(writer, categoriaRepo.findAll());
                    case "matriculas" -> writeMatriculasCsv(writer, getMatriculasForRange(startDate, endDate));
                    case "pagos" -> writePagosCsv(writer, getPagosForRange(startDate, endDate));
                    default -> writeDashboardSummaryCsv(writer);
                }
            }
        }
    }

    private void writeDashboardSummaryCsv(PrintWriter writer) {
        writer.println("Entidad,Total");
        writer.printf("Alumnos,%d\n", alumnoRepo.count());
        writer.printf("Categorías,%d\n", categoriaRepo.count());
        writer.printf("Matrículas,%d\n", matriculaRepo.count());
        writer.printf("Pagos,%d\n", pagoRepo.count());
    }

    private void writeAlumnosCsv(PrintWriter writer, List<com.cancha.futbol.demo.entity.Alumno> alumnos) {
        writer.println("ID,Nombre completo,DNI,Correo tutor,Nombre tutor,Teléfono tutor,Fecha nacimiento");
        for (var alumno : alumnos) {
            writer.printf("%d,%s,%s,%s,%s,%s,%s\n",
                    alumno.getIdAlumno(),
                    escapeCsv(alumno.getNombreCompleto()),
                    escapeCsv(alumno.getDni()),
                    escapeCsv(alumno.getCorreoTutor()),
                    escapeCsv(alumno.getNombreTutor()),
                    escapeCsv(alumno.getTelefonoTutor()),
                    alumno.getFechaNacimiento() != null ? alumno.getFechaNacimiento() : "");
        }
    }

    private void writeCategoriasCsv(PrintWriter writer, List<Categoria> categorias) {
        writer.println("ID,Nombre,Cupos totales,Cupos disponibles,Monto matrícula,Edad mínima,Edad máxima");
        for (var categoria : categorias) {
            writer.printf("%d,%s,%d,%d,%.2f,%d,%d\n",
                    categoria.getIdCategoria(),
                    escapeCsv(categoria.getNombreCategoria()),
                    categoria.getCuposTotales(),
                    categoria.getCuposDisponibles(),
                    categoria.getMontoMatricula(),
                    categoria.getEdadMinima(),
                    categoria.getEdadMaxima());
        }
    }

    private void writeMatriculasCsv(PrintWriter writer, List<com.cancha.futbol.demo.entity.Matricula> matriculas) {
        writer.println("ID,Alumno,Categoría,Estado,Fecha matrícula,Reserva hasta");
        for (var matricula : matriculas) {
            writer.printf("%d,%s,%s,%s,%s,%s\n",
                    matricula.getIdMatricula(),
                    escapeCsv(matricula.getAlumno() != null ? matricula.getAlumno().getNombreCompleto() : ""),
                    escapeCsv(matricula.getCategoria() != null ? matricula.getCategoria().getNombreCategoria() : ""),
                    matricula.getEstado() != null ? matricula.getEstado().name() : "",
                    matricula.getFechaMatricula() != null ? matricula.getFechaMatricula() : "",
                    matricula.getReservationExpiry() != null ? matricula.getReservationExpiry() : "");
        }
    }

    private void writePagosCsv(PrintWriter writer, List<Pago> pagos) {
        writer.println("ID,Matrícula,Alumno,Monto,Método pago,Estado pago,Fecha pago");
        for (var pago : pagos) {
            writer.printf("%d,%s,%s,%.2f,%s,%s,%s\n",
                    pago.getIdPago(),
                    pago.getMatricula() != null ? String.valueOf(pago.getMatricula().getIdMatricula()) : "",
                    pago.getMatricula() != null && pago.getMatricula().getAlumno() != null ? escapeCsv(pago.getMatricula().getAlumno().getNombreCompleto()) : "",
                    pago.getMonto() != null ? pago.getMonto() : 0.0,
                    escapeCsv(pago.getMetodoPago()),
                    pago.getEstadoPago() != null ? pago.getEstadoPago().name() : "",
                    pago.getFechaPago() != null ? pago.getFechaPago() : "");
        }
    }

    private List<com.cancha.futbol.demo.entity.Alumno> getAlumnosForRange(LocalDate startDate, LocalDate endDate) {
        if (startDate != null && endDate != null) {
            return alumnoRepo.findByFechaNacimientoBetween(startDate, endDate);
        }
        return alumnoRepo.findAll();
    }

    private List<Matricula> getMatriculasForRange(LocalDate startDate, LocalDate endDate) {
        if (startDate != null && endDate != null) {
            return matriculaRepo.findByFechaMatriculaBetween(startDate.atStartOfDay(), endDate.atTime(23, 59, 59));
        }
        return matriculaRepo.findAll();
    }

    private List<Pago> getPagosForRange(LocalDate startDate, LocalDate endDate) {
        if (startDate != null && endDate != null) {
            return pagoRepo.findByFechaPagoBetween(startDate.atStartOfDay(), endDate.atTime(23, 59, 59));
        }
        return pagoRepo.findAll();
    }

    private byte[] generatePdfReport(String entity, LocalDate startDate, LocalDate endDate) throws IOException {
        List<String> lines = new ArrayList<>();
        String title = switch (entity) {
            case "alumnos" -> "Alumnos";
            case "categorias" -> "Categorías";
            case "matriculas" -> "Matrículas";
            case "pagos" -> "Pagos";
            default -> "Resumen";
        };
        lines.add("Reporte avanzado - " + title);
        if (startDate != null || endDate != null) {
            String range = "Rango de fechas: ";
            if (startDate != null) {
                range += startDate;
            }
            if (endDate != null) {
                range += " hasta " + endDate;
            }
            lines.add(range);
        }
        lines.add("");

        switch (entity) {
            case "alumnos" -> {
                lines.add("ID | Nombre completo | DNI | Correo tutor | Tutor | Fecha nacimiento");
                for (var alumno : getAlumnosForRange(startDate, endDate)) {
                    lines.add(String.format("%d | %s | %s | %s | %s | %s",
                            alumno.getIdAlumno(),
                            safe(alumno.getNombreCompleto()),
                            safe(alumno.getDni()),
                            safe(alumno.getCorreoTutor()),
                            safe(alumno.getNombreTutor()),
                            alumno.getFechaNacimiento() != null ? alumno.getFechaNacimiento() : ""));
                }
            }
            case "categorias" -> {
                lines.add("ID | Nombre | Cupos totales | Cupos disponibles | Monto | Edad mínima | Edad máxima");
                for (var categoria : categoriaRepo.findAll()) {
                    lines.add(String.format("%d | %s | %d | %d | %.2f | %d | %d",
                            categoria.getIdCategoria(),
                            safe(categoria.getNombreCategoria()),
                            categoria.getCuposTotales(),
                            categoria.getCuposDisponibles(),
                            categoria.getMontoMatricula(),
                            categoria.getEdadMinima(),
                            categoria.getEdadMaxima()));
                }
            }
            case "pagos" -> {
                lines.add("ID | Matrícula | Alumno | Monto | Método pago | Estado | Fecha pago");
                for (var pago : getPagosForRange(startDate, endDate)) {
                    String alumnoNombre = pago.getMatricula() != null && pago.getMatricula().getAlumno() != null ? safe(pago.getMatricula().getAlumno().getNombreCompleto()) : "";
                    lines.add(String.format("%d | %s | %s | %.2f | %s | %s | %s",
                            pago.getIdPago(),
                            pago.getMatricula() != null ? String.valueOf(pago.getMatricula().getIdMatricula()) : "",
                            alumnoNombre,
                            pago.getMonto() != null ? pago.getMonto() : 0.0,
                            safe(pago.getMetodoPago()),
                            pago.getEstadoPago() != null ? pago.getEstadoPago().name() : "",
                            pago.getFechaPago() != null ? pago.getFechaPago().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : ""));
                }
            }
            default -> {
                lines.add("ID | Alumno | Categoría | Estado | Fecha matrícula");
                for (var matricula : getMatriculasForRange(startDate, endDate)) {
                    lines.add(String.format("%d | %s | %s | %s | %s",
                            matricula.getIdMatricula(),
                            matricula.getAlumno() != null ? safe(matricula.getAlumno().getNombreCompleto()) : "",
                            matricula.getCategoria() != null ? safe(matricula.getCategoria().getNombreCategoria()) : "",
                            matricula.getEstado() != null ? matricula.getEstado().name() : "",
                            matricula.getFechaMatricula() != null ? matricula.getFechaMatricula().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : ""));
                }
            }
        }
        return renderLinesAsPdf(lines);
    }

    private byte[] renderLinesAsPdf(List<String> lines) throws IOException {
        try (PDDocument document = new PDDocument(); ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PDPage page = new PDPage();
            document.addPage(page);
            PDPageContentStream content = new PDPageContentStream(document, page);
            content.setFont(PDType1Font.HELVETICA, 10);
            float y = page.getMediaBox().getHeight() - 50;
            content.beginText();
            content.newLineAtOffset(50, y);
            for (String line : lines) {
                if (y < 70) {
                    content.endText();
                    content.close();
                    page = new PDPage();
                    document.addPage(page);
                    content = new PDPageContentStream(document, page);
                    content.setFont(PDType1Font.HELVETICA, 10);
                    y = page.getMediaBox().getHeight() - 50;
                    content.beginText();
                    content.newLineAtOffset(50, y);
                }
                content.showText(line);
                content.newLineAtOffset(0, -14);
                y -= 14;
            }
            content.endText();
            content.close();
            document.save(outputStream);
            return outputStream.toByteArray();
        }
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        return '"' + value.replace("\"", "\"\"") + '"';
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

    @GetMapping("/registro/{id}/edit")
    public String editRegistro(@PathVariable Long id, Model model) {
        Matricula matricula = matriculaService.getById(id);
        model.addAttribute("entity", "registro");
        model.addAttribute("matricula", matricula);
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
    public String processRegistro(@Valid @ModelAttribute Matricula matricula,
            BindingResult bindingResult,
            @RequestParam(required = false) String metodoPago,
            @RequestParam(required = false) Double montoPago,
            Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("entity", "registro");
            model.addAttribute("categorias", categoriaRepo.findAll());
            model.addAttribute("selectedEstado", null);
            model.addAttribute("estadoOptions", EstadoMatricula.values());
            model.addAttribute("showRegisterStep", 0);
            model.addAttribute("registroError", "Corrige los campos marcados en el formulario.");
            model.addAttribute("formErrors", bindingResult.getAllErrors());
            return "dashboard";
        }

        if (matricula.getIdMatricula() != null) {
            matriculaService.update(matricula.getIdMatricula(), matricula);
            return "redirect:/dashboard?entity=matriculas";
        }

        double montoEstimado = 0.0;
        if (matricula.getCategoria() != null && matricula.getCategoria().getIdCategoria() != null) {
            Categoria categoria = categoriaRepo.findById(matricula.getCategoria().getIdCategoria()).orElse(null);
            if (categoria != null && categoria.getMontoMatricula() != null) {
                montoEstimado = categoria.getMontoMatricula();
            }
        }

        if (matricula.getCategoria() == null || matricula.getCategoria().getIdCategoria() == null) {
            bindingResult.rejectValue("categoria.idCategoria", "NotNull", "Debes seleccionar una categoría.");
        } else {
            Categoria categoriaSeleccionada = categoriaRepo.findById(matricula.getCategoria().getIdCategoria()).orElse(null);
            if (categoriaSeleccionada == null) {
                bindingResult.rejectValue("categoria.idCategoria", "NotFound", "La categoría seleccionada no existe.");
            } else if (categoriaSeleccionada.getCuposDisponibles() == null || categoriaSeleccionada.getCuposDisponibles() <= 0) {
                bindingResult.rejectValue("categoria.idCategoria", "NoCupos", "La categoría seleccionada no tiene cupos disponibles. Elige otra categoría.");
            }
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("entity", "registro");
            model.addAttribute("categorias", categoriaRepo.findAll());
            model.addAttribute("selectedEstado", null);
            model.addAttribute("estadoOptions", EstadoMatricula.values());
            model.addAttribute("showRegisterStep", 1);
            model.addAttribute("registroError", "Corrige los campos marcados en el formulario.");
            model.addAttribute("formErrors", bindingResult.getAllErrors());
            return "dashboard";
        }

        if (montoPago == null || montoPago < montoEstimado) {
            model.addAttribute("entity", "registro");
            model.addAttribute("categorias", categoriaRepo.findAll());
            model.addAttribute("selectedEstado", null);
            model.addAttribute("estadoOptions", EstadoMatricula.values());
            model.addAttribute("montoError", "El monto ingresado debe ser igual o mayor al monto estimado.");
            model.addAttribute("showRegisterStep", 1);
            return "dashboard";
        }

        Matricula created = matriculaService.create(matricula);
        if (metodoPago != null && !metodoPago.isBlank() && montoPago != null) {
            Pago pago = new Pago();
            pago.setMatricula(created);
            pago.setMonto(montoPago);
            pago.setMetodoPago(metodoPago);
            double vuelto = Math.max(0, montoPago - (created.getCategoria() != null && created.getCategoria().getMontoMatricula() != null
                    ? created.getCategoria().getMontoMatricula() : 0.0));
            pago.setVuelto(vuelto);
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
