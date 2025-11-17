Write-Host "==============================================="
Write-Host "  AUTO-FIX ADAPTIVE EDUCATION SERVICE MODULE"
Write-Host "==============================================="

$base = "C:\temp_ilp\inclusive-learning-platform-backend\adaptive-education-service\src\main\java\com\inclusive\adaptiveeducationservice"

# --------------------------
# 1. FIX STUDENT.JAVA
# --------------------------
$studentFile = "$base\entity\model\Student.java"

if (Test-Path $studentFile) {
    Write-Host "[1] Corrigiendo imports en Student.java..."
    (Get-Content $studentFile) `
        -replace "javax.persistence", "jakarta.persistence" |
        Set-Content $studentFile
} else {
    Write-Host "❌ Student.java no encontrado."
}

# --------------------------
# 2. CREAR StudentDTO SI NO EXISTE
# --------------------------
$dtoFolder = "$base\entity\dto"
$dtoFile = "$dtoFolder\StudentDTO.java"

if (!(Test-Path $dtoFolder)) {
    New-Item -ItemType Directory -Path $dtoFolder | Out-Null
}

if (!(Test-Path $dtoFile)) {
    Write-Host "[2] Creando StudentDTO.java..."

@"
package com.inclusive.adaptiveeducationservice.entity.dto;

public class StudentDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
"@ | Set-Content $dtoFile
} else {
    Write-Host "[2] StudentDTO ya existe. OK."
}

# --------------------------
# 3. FIX IMPORTS StudentDTO EN Servicios/Controladores
# --------------------------
Write-Host "[3] Reparando imports en Client, Controller y Service..."

$filesToFix = @(
    "$base\client\StudentClient.java",
    "$base\controller\StudentProxyController.java",
    "$base\service\StudentIntegrationService.java"
)

foreach ($file in $filesToFix) {
    if (Test-Path $file) {
        (Get-Content $file) `
            -replace "com.inclusive.common.dto.StudentDTO", "com.inclusive.adaptiveeducationservice.entity.dto.StudentDTO" |
            Set-Content $file
        Write-Host " → Arreglado: $file"
    } else {
        Write-Host " → Archivo no encontrado: $file"
    }
}

# --------------------------
# 4. FIX DATA INITIALIZER
# --------------------------
$dataInit = "$base\config\DataInitializer.java"

if (Test-Path $dataInit) {
    Write-Host "[4] Corrigiendo imports en DataInitializer..."

    (Get-Content $dataInit) `
        -replace "com.inclusive.adaptiveeducationservice.entity.Student", "com.inclusive.adaptiveeducationservice.entity.model.Student" `
        -replace "com.inclusive.adaptiveeducationservice.repository", "com.inclusive.adaptiveeducationservice.entity.repository" |
        Set-Content $dataInit
} else {
    Write-Host "❌ DataInitializer no encontrado."
}

# --------------------------
# 5. RECONSTRUIR EL MÓDULO
# --------------------------
Write-Host "[5] Ejecutando: mvn clean install -DskipTests"
cd "C:\temp_ilp\inclusive-learning-platform-backend"
mvn clean install -DskipTests

Write-Host "==============================================="
Write-Host "   AUTO-FIX COMPLETADO"
Write-Host "   Revisa si ahora compila correctamente."
Write-Host "==============================================="
