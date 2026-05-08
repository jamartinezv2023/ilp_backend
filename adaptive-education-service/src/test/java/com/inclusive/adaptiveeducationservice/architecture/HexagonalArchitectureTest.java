package com.inclusive.adaptiveeducationservice.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.Test;

class HexagonalArchitectureTest {

    private static final String BASE_PACKAGE = "com.inclusive.adaptiveeducationservice";

    private final JavaClasses importedClasses = new ClassFileImporter()
            .importPackages(BASE_PACKAGE);

    @Test
    void domainShouldNotDependOnApiOrInfrastructure() {
        classes()
                .that()
                .resideInAPackage(BASE_PACKAGE + ".domain..")
                .should()
                .onlyDependOnClassesThat()
                .resideOutsideOfPackages(
                        BASE_PACKAGE + ".api..",
                        BASE_PACKAGE + ".infrastructure.."
                )
                .check(importedClasses);
    }

    @Test
    void applicationShouldNotDependOnApi() {
        classes()
                .that()
                .resideInAPackage(BASE_PACKAGE + ".application..")
                .should()
                .onlyDependOnClassesThat()
                .resideOutsideOfPackage(BASE_PACKAGE + ".api..")
                .check(importedClasses);
    }

    @Test
    void controllersShouldStayInApiLayer() {
        classes()
                .that()
                .haveSimpleNameEndingWith("Controller")
                .should()
                .resideInAnyPackage(
                        BASE_PACKAGE + ".api.."
                )
                .check(importedClasses);
    }
}