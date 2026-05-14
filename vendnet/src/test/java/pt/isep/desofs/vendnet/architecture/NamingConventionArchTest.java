package pt.isep.desofs.vendnet.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import jakarta.persistence.Entity;

@AnalyzeClasses(
		packages = "pt.isep.desofs.vendnet",
		importOptions = ImportOption.DoNotIncludeTests.class)
class NamingConventionArchTest {

	@ArchTest
	static final ArchRule controllers_should_end_with_controller =
			classes()
					.that()
					.resideInAPackage("..api.controller..")
					.and()
					.areNotAnonymousClasses()
					.and()
					.areNotMemberClasses()
					.and()
					.haveSimpleNameNotStartingWith("GlobalException")
					.should()
					.haveSimpleNameEndingWith("Controller")
					.because("Controller classes should be named with Controller suffix");

	@ArchTest
	static final ArchRule services_should_end_with_service =
			classes()
					.that()
					.resideInAPackage("..application.service..")
					.and()
					.areNotAnonymousClasses()
					.and()
					.areNotMemberClasses()
					.should()
					.haveSimpleNameEndingWith("Service")
					.because("Service classes should be named with Service suffix");

	@ArchTest
	static final ArchRule entities_should_be_in_model_subpackage =
			classes()
					.that()
					.areAnnotatedWith(Entity.class)
					.should()
					.resideInAnyPackage(
							"..domain.model.user",
							"..domain.model.machine",
							"..domain.model.product",
							"..domain.model.sale",
							"..domain.model.slot",
							"..domain.model.telemetry",
							"..domain.model.audit")
					.because(
							"Entity classes should be in a dedicated subpackage of domain.model based on bounded context");

	@ArchTest
	static final ArchRule exceptions_should_be_in_exception_package =
			classes()
					.that()
					.haveSimpleNameEndingWith("Exception")
					.should()
					.resideInAPackage("..domain.exception..")
					.because("Custom exceptions should be in domain.exception package");

	@ArchTest
	static final ArchRule dto_should_be_in_dto_package =
			classes()
					.that()
					.haveSimpleNameEndingWith("Request")
					.or()
					.haveSimpleNameEndingWith("Response")
					.and()
					.areNotAssignableFrom(Exception.class)
					.should()
					.resideInAPackage("..api.dto..")
					.because("DTO classes should be in api.dto package");
}
