# ===================================================================
#   FIX ADAPTIVE STUDENT FILES - RECONSTRUCCIÓN AUTOMÁTICA
# ===================================================================

Write-Host "======================================" -ForegroundColor Cyan
Write-Host "  ARREGLANDO ARCHIVOS DEL MÓDULO     " -ForegroundColor Cyan
Write-Host "======================================" -ForegroundColor Cyan

# Rutas base
$base = "C:\temp_ilp\inclusive-learning-platform-backend\adaptive-education-service\src\main\java\com\inclusive\adaptiveeducationservice"

$entityPath   = Join-Path $base "entity\model\Student.java"
$dtoPath      = Join-Path $base "entity\dto\StudentDTO.java"
$mapperPath   = Join-Path $base "entity\model\StudentMapper.java"
$repoPath     = Join-Path $base "entity\repository\StudentRepository.java"

# ================================================================
# 1. RECONSTRUIR ENTITY Student.java
# ================================================================

$studentEntityContent = @"
package com.inclusive.adaptiveeducationservice.entity.model;

import jakarta.persistence.*;

@Entity
@Table(name = "students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String grade;

    public Student() {}

    public Student(Long id, String name, String grade) {
        this.id = id;
        this.name = name;
        this.grade = grade;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }
}
"@

Set-Content $entityPath $studentEntityContent -Encoding UTF8
Write-Host " Student.java reconstruido" -ForegroundColor Green

# ================================================================
# 2. RECONSTRUIR DTO StudentDTO.java
# ================================================================

$studentDTOContent = @"
package com.inclusive.adaptiveeducationservice.entity.dto;

public class StudentDTO {

    private Long id;
    private String name;
    private String grade;

    public StudentDTO() {}

    public StudentDTO(Long id, String name, String grade) {
        this.id = id;
        this.name = name;
        this.grade = grade;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }
}
"@

Set-Content $dtoPath $studentDTOContent -Encoding UTF8
Write-Host " StudentDTO.java reconstruido" -ForegroundColor Green

# ================================================================
# 3. RECONSTRUIR StudentMapper
# ================================================================

$studentMapperContent = @"
package com.inclusive.adaptiveeducationservice.entity.model;

import com.inclusive.adaptiveeducationservice.entity.dto.StudentDTO;

public class StudentMapper {

    public static StudentDTO toDTO(Student student) {
        return new StudentDTO(
            student.getId(),
            student.getName(),
            student.getGrade()
        );
    }

    public static Student toEntity(StudentDTO dto) {
        return new Student(
            dto.getId(),
            dto.getName(),
            dto.getGrade()
        );
    }
}
"@

Set-Content $mapperPath $studentMapperContent -Encoding UTF8
Write-Host " StudentMapper.java reconstruido" -ForegroundColor Green

# ================================================================
# 4. RECONSTRUIR StudentRepository
# ================================================================

$studentRepoContent = @"
package com.inclusive.adaptiveeducationservice.entity.repository;

import com.inclusive.adaptiveeducationservice.entity.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
}
"@

Set-Content $repoPath $studentRepoContent -Encoding UTF8
Write-Host " StudentRepository.java reconstruido" -ForegroundColor Green

Write-Host "======================================" -ForegroundColor Cyan
Write-Host " ARREGLO FINALIZADO - AHORA COMPILA    " -ForegroundColor Green
Write-Host "======================================" -ForegroundColor Cyan
