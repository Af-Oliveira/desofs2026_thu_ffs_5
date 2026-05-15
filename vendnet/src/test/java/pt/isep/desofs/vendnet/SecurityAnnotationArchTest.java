package pt.isep.desofs.vendnet;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;

import jakarta.annotation.security.RolesAllowed;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;

@AnalyzeClasses(
        packages = "pt.isep.desofs.vendnet",
        importOptions = ImportOption.DoNotIncludeTests.class
)
class SecurityAnnotationArchTest {

    @ArchTest
    static final ArchRule all_public_controller_methods_must_have_security_annotation =
            methods()
                    .that().areDeclaredInClassesThat().haveSimpleNameEndingWith("Controller")
                    .and().arePublic()
                    .and().areNotStatic()
                    .should().beAnnotatedWith(PreAuthorize.class)
                    .orShould().beAnnotatedWith(Secured.class)
                    .orShould().beAnnotatedWith(RolesAllowed.class)
                    .because("Every public controller method must declare its access policy via " +
                             "@PreAuthorize, @Secured, or @RolesAllowed");
}
