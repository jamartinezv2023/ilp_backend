/*
 * settings.gradle.kts
 * ILP Backend – Multi-module configuration
 */

rootProject.name = "ilp-backend"

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

/* Core / Shared */
include("common-libs")
include("commons-service")

/* Seguridad y acceso */
include("auth-service")
include("gateway-service")
include("tenant-service")

/* Dominio educativo */
include("adaptive-education-service")
include("assessment-service")

/* Operación y soporte */
include("notification-service")
include("monitoring-service")
include("report-service")
include("student-service")
include("common-events")


include("discovery-server")

include("bdd-tests")
