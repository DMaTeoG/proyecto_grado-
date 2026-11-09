package com.avicola.monolith.config;

import com.avicola.monolith.iam.model.Rol;
import com.avicola.monolith.iam.model.Usuario;
import com.avicola.monolith.iam.repository.UsuarioRepository;
import com.avicola.monolith.infraestructura.model.Galpon;
import com.avicola.monolith.infraestructura.model.Granja;
import com.avicola.monolith.infraestructura.repository.GalponRepository;
import com.avicola.monolith.infraestructura.repository.GranjaRepository;
import com.avicola.monolith.lotes.model.Lote;
import com.avicola.monolith.lotes.repository.LoteRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final GranjaRepository granjaRepository;
    private final GalponRepository galponRepository;
    private final LoteRepository loteRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UsuarioRepository usuarioRepository,
                           GranjaRepository granjaRepository,
                           GalponRepository galponRepository,
                           LoteRepository loteRepository,
                           PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.granjaRepository = granjaRepository;
        this.galponRepository = galponRepository;
        this.loteRepository = loteRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // 1. Semilla de Usuarios RBAC
        if (!usuarioRepository.existsByEmail("admin@avicola.com")) {
            Usuario admin = new Usuario(
                    "David Mateo Guerrero (Admin)",
                    "admin@avicola.com",
                    passwordEncoder.encode("admin123"),
                    Rol.ROLE_ADMIN
            );
            usuarioRepository.save(admin);
        }

        if (!usuarioRepository.existsByEmail("galponero@avicola.com")) {
            Usuario galponero = new Usuario(
                    "Operario de Campo",
                    "galponero@avicola.com",
                    passwordEncoder.encode("galponero123"),
                    Rol.ROLE_GALPONERO
            );
            usuarioRepository.save(galponero);
        }

        // 2. Semilla de Granja y Galpón
        if (granjaRepository.count() == 0) {
            Granja granja = granjaRepository.save(new Granja("Granja Avícola El Porvenir", "Vereda La Alianza, Pasto, Nariño"));
            Galpon galpon = galponRepository.save(new Galpon(granja, 1, 12000));

            // 3. Semilla de los 4 Lotes Activos (Esquema de 4 cuadrantes por galpón)
            LocalDate inicio = LocalDate.now().minusDays(21); // Lotes con 21 días de vida
            for (int i = 1; i <= 4; i++) {
                String codigo = "LOTE-G1-Q" + i + "-2026";
                if (loteRepository.findByCodigoLote(codigo).isEmpty()) {
                    Lote lote = new Lote(
                            galpon,
                            codigo,
                            i,
                            "Cobb 500",
                            inicio,
                            3000,
                            new BigDecimal("42.50")
                    );
                    loteRepository.save(lote);
                }
            }
        }
    }
}
