# Auth Service — Module Contract (Golden Module)

> **Module:** `auth-service`  
> **Type:** Spring Boot (Gradle multi-module)  
> **Purpose:** Autenticación, autorización (roles/permisos), emisión/validación JWT, gestión de cuentas y roles.

---

## 0) Non-negotiables (reglas de oro)
- ✅ `./gradlew :auth-service:clean :auth-service:test` **debe pasar siempre**.
- ✅ Nada de `@RestController` dentro de `src/test/java` (prohibido).
- ✅ Un solo `@SpringBootApplication` por módulo (en `auth-service`).
- ✅ `SecurityFilterChain` definido una vez por perfil (`prod`, `test`), sin colisiones de beans.
- ✅ Configuración vía `application.yml` + `@ConfigurationProperties` (sin lógica de negocio en configs).
- ✅ Logs sin secretos (nunca imprimir tokens, keys o claims sensibles).

---

## 1) Scope (qué hace / qué NO hace)

### 1.1 Hace
- Autenticación basada en JWT (Resource Server).
- Emisión y validación de JWT (según perfil).
- Gestión de cuentas de usuario.
- Gestión de roles y permisos.
- Asociación usuario–rol y rol–permiso.
- Protección de endpoints mediante roles y authorities.
- Manejo consistente de errores de seguridad y dominio.

### 1.2 No hace (fuera de alcance)
- Gestión de dominio educativo (estudiantes, cursos, evaluaciones).
- Lógica de tenants de negocio (solo soporte de autoridad si aplica).
- UI, SSR o frontend.
- Persistencia de dominios ajenos a autenticación/autorización.

---

## 2) API Contract (endpoints reales del módulo)
> **Regla:** solo se documentan endpoints expuestos fuera del servicio.

### 2.1 Endpoints públicos (sin JWT)
> *Actualmente no hay endpoints públicos explícitos de login en este módulo.*

- `GET /actuator/health` (si está habilitado por perfil)

---

### 2.2 Endpoints protegidos (JWT requerido)

#### Test / verificación
- `GET /api/user/test`
- `GET /api/admin/test`

---

### 2.3 Endpoints de gestión de cuentas (JWT requerido)

#### User Accounts
- `GET    /api/user-accounts`
- `GET    /api/user-accounts/{id}`
- `POST   /api/user-accounts`
- `PUT    /api/user-accounts/{id}`
- `DELETE /api/user-accounts/{id}`

---

### 2.4 Endpoints de autorización (JWT requerido)

#### Roles
- `GET    /api/auth/roles`
- `GET    /api/auth/roles/{roleId}/permissions`
- `POST   /api/auth/roles`
- `DELETE /api/auth/roles/{roleId}`

#### Permissions
- `GET  /api/auth/permissions`
- `POST /api/auth/permissions`

#### Role–Permission
- `POST   /api/auth/role-permissions/roles/{roleId}/permissions/{permissionId}`
- `DELETE /api/auth/role-permissions/roles/{roleId}/permissions/{permissionId}`

#### User–Role
- `POST   /api/auth/user-roles/users/{userId}/roles/{roleId}`
- `DELETE /api/auth/user-roles/users/{userId}/roles/{roleId}`

---

## 3) Security Contract

### 3.1 Roles y Authorities
- Roles:
  - `ROLE_ADMIN`
  - `ROLE_USER`
- Authorities derivadas:
  - `ROLE_*`
  - `SCOPE_*` (si aplica)
  - `TENANT_<id>` (si aplica)

---

### 3.2 Reglas de autorización

| Endpoint Pattern | Método | Requiere |
|---|---|---|
| `/api/user/**` | * | JWT válido |
| `/api/auth/**` | * | `ROLE_ADMIN` |
| `/api/admin/**` | * | `ROLE_ADMIN` |
| `/actuator/**` | * | según perfil |

---

### 3.3 JWT Claims esperados
- `sub` — identificador del principal
- `roles` — lista de roles
- `scope` / `scp` — scopes (si aplica)
- `tenant` — identificador de tenant (opcional)

---

### 3.4 Componentes de seguridad
- `JwtAuthConverter`
- `JwtAuthoritiesExtractor`
- `JwtProperties` (`@ConfigurationProperties(prefix="jwt")`)
- `SecurityConfig` (`SecurityFilterChain`)

> **Regla:** configuraciones de test (`SmokeTestSecurityConfig`) deben ser aisladas y nunca colisionar con beans productivos.

---

## 4) Persistence Contract

### 4.1 Base de datos
- Motor: PostgreSQL
- Migraciones: (pendiente de estandarizar)
- Esquema sugerido: `auth`

---

### 4.2 Entidades
- `UserAccount`
- `Role`
- `Permission`
- `UserRole`
- `RolePermission`

---

## 5) Configuration Contract

### 5.1 Properties obligatorias
- `jwt.access-token-secret`
- `jwt.access-token-expiration-ms`
- `spring.datasource.url`
- `spring.datasource.username`
- `spring.datasource.password`

---

### 5.2 Perfiles
- `default` — desarrollo local
- `test` — pruebas automatizadas
- `prod` — producción

---

## 6) Observability Contract
- Logging estructurado (sin secretos).
- Spring Actuator:
  - `health`
  - `info`
- Métricas/tracing: pendiente de estandarización global.

---

## 7) Test Contract

### 7.1 Unit Tests
- `JwtAuthoritiesExtractorTest`
- `JwtAuthConverterTest`

---

### 7.2 Slice Tests
- `@WebMvcTest` solo con controllers reales.
- Sin duplicación de beans de seguridad.

---

### 7.3 Smoke / Security Tests
Debe validar:
- 401 sin JWT.
- 200 con JWT válido y rol correcto.
- 403 con JWT válido pero rol incorrecto.

**Clase estándar:** `SecuritySmokeTest`

---

## 8) Build Contract (Gradle)
- Tasks obligatorias:
  - `clean`
  - `test`
  - `bootJar`
- Calidad (siguiente fase):
  - Spotless / Checkstyle
  - Coverage mínimo
  - SAST

---

## 9) Release Contract
- Imagen Docker: `ilp/auth-service:<tag>`
- Versionado: SemVer
- Kubernetes / Helm: `k8s/auth-service/*`
- IaC: `infra/*`

---

## 10) Definition of Done — Golden Module
- ✅ `:auth-service:test` verde local y CI.
- ✅ Contrato alineado con código real.
- ✅ Seguridad estable (sin colisiones).
- ✅ Controllers limpios y únicos.
- ✅ Build Docker funcional.
- ✅ Pipeline ejecuta tests automáticamente.

---

## 11) Ownership
- Owner: __________________
- Reviewer: _______________
- Fecha: __________________
