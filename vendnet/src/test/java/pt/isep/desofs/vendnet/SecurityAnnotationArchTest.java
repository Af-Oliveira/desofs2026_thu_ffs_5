package pt.isep.desofs.vendnet;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;

@AnalyzeClasses(
		packages = "pt.isep.desofs.vendnet",
		importOptions = ImportOption.DoNotIncludeTests.class)
class SecurityAnnotationArchTest {

	// Arrange — ArchUnit scans classes in pt.isep.desofs.vendnet (excluding tests)
	// Act & Assert — the rule verifies every public controller method carries a security annotation
	@ArchTest
	static final ArchRule all_public_controller_methods_must_have_security_annotation =
			methods()
					.that()
					.areDeclaredInClassesThat()
					.haveSimpleNameEndingWith("Controller")
					.and()
					.arePublic()
					.and()
					.areNotStatic()
					.should()
					.beAnnotatedWith(PreAuthorize.class)
					.orShould()
					.beAnnotatedWith(Secured.class)
					.orShould()
					.beAnnotatedWith(RolesAllowed.class)
					.because(
							"Every public controller method must declare its access policy via "
									+ "@PreAuthorize, @Secured, or @RolesAllowed");
}
