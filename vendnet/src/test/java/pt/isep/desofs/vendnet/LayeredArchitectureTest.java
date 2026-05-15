package pt.isep.desofs.vendnet;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

class LayeredArchitectureTest {

    private static JavaClasses importedClasses;

    @BeforeAll
    static void setup() {
        importedClasses = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages("pt.isep.desofs.vendnet");
    }

    @Test
    void domain_layer_does_not_depend_on_application_infrastructure_or_api() {
        noClasses()
                .that().resideInAPackage("pt.isep.desofs.vendnet.domain..")
                .should().dependOnClassesThat().resideInAPackage("pt.isep.desofs.vendnet.application..")
                .orShould().dependOnClassesThat().resideInAPackage("pt.isep.desofs.vendnet.infrastructure..")
                .orShould().dependOnClassesThat().resideInAPackage("pt.isep.desofs.vendnet.api..")
                .because("Domain layer must remain pure and not depend on outer layers (DDD)")
                .check(importedClasses);
    }

    @Test
    void api_and_application_layers_only_depend_on_repository_interfaces_not_jpa_implementations() {
        noClasses()
                .that().resideInAPackage("pt.isep.desofs.vendnet.api..")
                .or().resideInAPackage("pt.isep.desofs.vendnet.application..")
                .should().dependOnClassesThat().resideInAPackage("pt.isep.desofs.vendnet.infrastructure.persistence..")
                .because("API and Application layers must depend only on repository interfaces, not JPA implementations")
                .check(importedClasses);
    }

    @Test
    void controllers_do_not_call_repositories_directly() {
        noClasses()
                .that().resideInAPackage("pt.isep.desofs.vendnet.api.controller..")
                .should().dependOnClassesThat().resideInAPackage("pt.isep.desofs.vendnet.domain.repository..")
                .because("Controllers must not call repositories directly; they must go through application services")
                .check(importedClasses);
    }

    @Test
    void every_public_controller_method_has_PreAuthorize_annotation() {
        methods()
                .that().areDeclaredInClassesThat().resideInAPackage("pt.isep.desofs.vendnet.api.controller..")
                .and().arePublic()
                .and().areNotStatic()
                .and().areNotAnnotatedWith(org.springframework.web.bind.annotation.ExceptionHandler.class)
                .and().areDeclaredInClassesThat().areNotAnnotatedWith(org.springframework.web.bind.annotation.RestControllerAdvice.class)
                .should().beAnnotatedWith(PreAuthorize.class)
                .orShould().beAnnotatedWith(org.springframework.security.access.annotation.Secured.class)
                .orShould().beAnnotatedWith(jakarta.annotation.security.RolesAllowed.class)
                .because("Every public controller method must declare its access policy via @PreAuthorize, @Secured, or @RolesAllowed (SR-06)")
                .check(importedClasses);
    }

    @Test
    void EnableMethodSecurity_is_present_in_security_configuration() {
        boolean hasEnableMethodSecurity = importedClasses.stream()
                .filter(c -> c.isAnnotatedWith(EnableMethodSecurity.class))
                .anyMatch(c -> c.getPackageName().startsWith("pt.isep.desofs.vendnet.config"));
        if (!hasEnableMethodSecurity) {
            throw new AssertionError(
                "@EnableMethodSecurity must be present in security configuration class for method-level RBAC (SR-06)");
        }
    }

    @Test
    void controllers_annotated_with_RestController_reside_in_api_controller_package() {
        classes()
                .that().areAnnotatedWith(RestController.class)
                .should().resideInAPackage("pt.isep.desofs.vendnet.api.controller..")
                .because("All REST controllers must reside in the api.controller package")
                .check(importedClasses);
    }

    @Test
    void services_reside_in_application_service_or_infrastructure_packages() {
        boolean allServicesInCorrectPackages = importedClasses.stream()
                .filter(c -> c.isAnnotatedWith(Service.class))
                .allMatch(c ->
                    c.getPackageName().startsWith("pt.isep.desofs.vendnet.application.service") ||
                    c.getPackageName().startsWith("pt.isep.desofs.vendnet.infrastructure"));
        if (!allServicesInCorrectPackages) {
            throw new AssertionError(
                "@Service classes must be in application.service or infrastructure packages (DDD layered architecture)");
        }
    }

    @Test
    void repositories_annotated_with_Repository_reside_in_infrastructure_persistence_package() {
        classes()
                .that().areAnnotatedWith(Repository.class)
                .should().resideInAPackage("pt.isep.desofs.vendnet.infrastructure.persistence..")
                .because("All JPA repository implementations must reside in the infrastructure.persistence package")
                .check(importedClasses);
    }
}
