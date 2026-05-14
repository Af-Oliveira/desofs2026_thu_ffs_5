package pt.isep.desofs.vendnet.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import jakarta.persistence.Entity;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

@AnalyzeClasses(
		packages = "pt.isep.desofs.vendnet",
		importOptions = ImportOption.DoNotIncludeTests.class)
class LayeredArchitectureArchTest {

	@ArchTest
	static final ArchRule application_services_should_be_in_application_service_package =
			classes()
					.that()
					.areAnnotatedWith(Service.class)
					.and()
					.resideInAPackage("..application..")
					.should()
					.resideInAPackage("..application.service..")
					.because(
							"Application service classes should be in application.service package");

	@ArchTest
	static final ArchRule controllers_should_be_in_controller_package =
			classes()
					.that()
					.areAnnotatedWith(RestController.class)
					.or()
					.areAnnotatedWith(Controller.class)
					.should()
					.resideInAPackage("..api.controller..")
					.because("Controller classes should be in api.controller package");

	@ArchTest
	static final ArchRule entities_should_be_in_model_package =
			classes()
					.that()
					.areAnnotatedWith(Entity.class)
					.should()
					.resideInAPackage("..domain.model..")
					.because("Entity classes should be in domain.model package");

	@ArchTest
	static final ArchRule repository_interfaces_should_be_in_domain_repository =
			classes()
					.that()
					.haveSimpleNameEndingWith("Repository")
					.and()
					.areInterfaces()
					.and()
					.resideInAPackage("..domain..")
					.should()
					.resideInAPackage("..domain.repository..")
					.because(
							"Domain repository interfaces should be in domain.repository package");

	@ArchTest
	static final ArchRule service_package_should_not_depend_on_controller_package =
			noClasses()
					.that()
					.resideInAPackage("..application.service..")
					.should()
					.dependOnClassesThat()
					.resideInAPackage("..api.controller..")
					.because("Service layer should not depend on controller layer");

	@ArchTest
	static final ArchRule domain_model_should_not_depend_on_infrastructure =
			noClasses()
					.that()
					.resideInAPackage("..domain.model..")
					.should()
					.dependOnClassesThat()
					.resideInAPackage("..infrastructure..")
					.because("Domain model should not depend on infrastructure");

	@ArchTest
	static final ArchRule domain_model_should_not_depend_on_application =
			noClasses()
					.that()
					.resideInAPackage("..domain.model..")
					.should()
					.dependOnClassesThat()
					.resideInAPackage("..application..")
					.because("Domain model should not depend on application layer");

	@ArchTest
	static final ArchRule domain_repository_should_not_depend_on_infrastructure =
			noClasses()
					.that()
					.resideInAPackage("..domain.repository..")
					.should()
					.dependOnClassesThat()
					.resideInAPackage("..infrastructure..")
					.because("Domain repository interfaces should not depend on infrastructure");
}
