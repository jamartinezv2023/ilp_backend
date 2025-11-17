<#
.SYNOPSIS
  Generador completo de proyecto (backend, frontend, automation) para NopCommerce E2E
.DESCRIPTION
  - Genera proyectos y archivos en UTF-8 sin BOM
  - Parametrizable: BaseUrl, OutputFolder, AuthorName, AuthorEmail, Institution, WorkshopTitle
  - Crea zips listos para descargar
.PARAMETER OutputFolder
  Carpeta destino donde se generará todo.
.PARAMETER BaseUrl
  URL base para las pruebas (ej: https://demo.nopcommerce.com/)
.PARAMETER AuthorName
  Nombre que aparecerá en reportes.
.PARAMETER AuthorEmail
  Email del autor.
.PARAMETER Institution
  Nombre de la institución para incluir en reportes.
.PARAMETER WorkshopTitle
  Título del taller.
#>

param(
  [string]$OutputFolder = "$PWD\nopcommerce_taller",
  [string]$BaseUrl = "https://demo.nopcommerce.com/",
  [string]$AuthorName = "JOSE ALFREDO MARTINEZ VALDES",
  [string]$AuthorEmail = "jose.martinez7@udea.edu.co",
  [string]$Institution = "Universidad de Antioquia - Facultad de Ingeniería",
  [string]$WorkshopTitle = "Taller de Automatización - Calidad de Software",
  [string]$Timestamp = (Get-Date -Format "yyyyMMdd_HHmmss")
)

# ---------------------------------------------
# Helper: Write files UTF-8 no BOM
# ---------------------------------------------
function Write-Utf8NoBom {
  [CmdletBinding()]
  param(
    [Parameter(Mandatory=$true)][string]$Path,
    [Parameter(Mandatory=$true)][string]$Content
  )
  $dir = Split-Path $Path -Parent
  if (-not (Test-Path $dir)) { New-Item -ItemType Directory -Path $dir -Force | Out-Null }
  $utf8NoBom = New-Object System.Text.UTF8Encoding($false)
  [System.IO.File]::WriteAllText($Path, $Content, $utf8NoBom)
}

# ---------------------------------------------
# Prepare folders
# ---------------------------------------------
$base = Resolve-Path -Path $OutputFolder
if (-not (Test-Path $base)) { New-Item -ItemType Directory -Path $base -Force | Out-Null }
$backend = Join-Path $base "backend_springboot"
$frontend = Join-Path $base "frontend_react_ts"
$automation = Join-Path $base "automation_serenity"
$zipsFolder = Join-Path $base "zips"

New-Item -ItemType Directory -Path $backend -Force | Out-Null
New-Item -ItemType Directory -Path $frontend -Force | Out-Null
New-Item -ItemType Directory -Path $automation -Force | Out-Null
New-Item -ItemType Directory -Path $zipsFolder -Force | Out-Null

Write-Host "Generando proyectos en: $base" -ForegroundColor Green
Write-Host "BaseUrl configurada: $BaseUrl" -ForegroundColor Cyan

# ---------------------------------------------
# 1) BACKEND - Spring Boot skeleton
# ---------------------------------------------
$backendPom = @"
<project xmlns=""http://maven.apache.org/POM/4.0.0"" 
         xmlns:xsi=""http://www.w3.org/2001/XMLSchema-instance"" 
         xsi:schemaLocation=""http://maven.apache.org/POM/4.0.0 
                             http://maven.apache.org/xsd/maven-4.0.0.xsd"">
  <modelVersion>4.0.0</modelVersion>
  <groupId>com.taller</groupId>
  <artifactId>backend-results</artifactId>
  <version>1.0.0</version>
  <properties>
    <java.version>11</java.version>
    <spring.boot.version>2.7.14</spring.boot.version>
  </properties>
  <dependencies>
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-web</artifactId>
      <version>\${spring.boot.version}</version>
    </dependency>
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-data-jpa</artifactId>
      <version>\${spring.boot.version}</version>
    </dependency>
    <dependency>
      <groupId>com.h2database</groupId>
      <artifactId>h2</artifactId>
      <scope>runtime</scope>
    </dependency>
    <dependency>
      <groupId>org.apache.poi</groupId>
      <artifactId>poi-ooxml</artifactId>
      <version>5.2.3</version>
    </dependency>
  </dependencies>
  <build>
    <plugins>
      <plugin>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-maven-plugin</artifactId>
      </plugin>
    </plugins>
  </build>
</project>
"@
Write-Utf8NoBom -Path (Join-Path $backend "pom.xml") -Content $backendPom

# Application main
$backendApp = @"
package com.taller.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BackendResultsApplication {
    public static void main(String[] args) {
        SpringApplication.run(BackendResultsApplication.class, args);
    }
}
"@
Write-Utf8NoBom -Path (Join-Path $backend "src\main\java\com\taller\backend\BackendResultsApplication.java") -Content $backendApp

# Entities: TestCase and TestRun
$testCase = @"
package com.taller.backend.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.Instant;

@Entity
public class TestCase {
    @Id private String id;
    private String name;
    private String description;
    private String url;
    private String type;
    private Instant createdAt;
    public TestCase() {}
    public String getId(){return id;} public void setId(String id){this.id=id;}
    public String getName(){return name;} public void setName(String name){this.name=name;}
    public String getDescription(){return description;} public void setDescription(String description){this.description=description;}
    public String getUrl(){return url;} public void setUrl(String url){this.url=url;}
    public String getType(){return type;} public void setType(String type){this.type=type;}
    public Instant getCreatedAt(){return createdAt;} public void setCreatedAt(Instant createdAt){this.createdAt=createdAt;}
}
"@
Write-Utf8NoBom -Path (Join-Path $backend "src\main\java\com\taller\backend\model\TestCase.java") -Content $testCase

$testRun = @"
package com.taller.backend.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.Instant;

@Entity
public class TestRun {
    @Id private String id;
    private String testId;
    private String status;
    private long timeMs;
    private String screenshotPath;
    private Instant createdAt;
    public TestRun() {}
    public String getId(){return id;} public void setId(String id){this.id=id;}
    public String getTestId(){return testId;} public void setTestId(String t){this.testId=t;}
    public String getStatus(){return status;} public void setStatus(String s){this.status=s;}
    public long getTimeMs(){return timeMs;} public void setTimeMs(long t){this.timeMs=t;}
    public String getScreenshotPath(){return screenshotPath;} public void setScreenshotPath(String p){this.screenshotPath=p;}
    public Instant getCreatedAt(){return createdAt;} public void setCreatedAt(Instant c){this.createdAt=c;}
}
"@
Write-Utf8NoBom -Path (Join-Path $backend "src\main\java\com\taller\backend\model\TestRun.java") -Content $testRun

# Repositories
$repoTestCase = @"
package com.taller.backend.repo;
import org.springframework.data.jpa.repository.JpaRepository;
import com.taller.backend.model.TestCase;
public interface TestCaseRepository extends JpaRepository<TestCase, String> {}
"@
Write-Utf8NoBom -Path (Join-Path $backend "src\main\java\com\taller\backend\repo\TestCaseRepository.java") -Content $repoTestCase

$repoTestRun = @"
package com.taller.backend.repo;
import org.springframework.data.jpa.repository.JpaRepository;
import com.taller.backend.model.TestRun;
public interface TestRunRepository extends JpaRepository<TestRun, String> {}
"@
Write-Utf8NoBom -Path (Join-Path $backend "src\main\java\com\taller\backend\repo\TestRunRepository.java") -Content $repoTestRun

# Controller with CRUD, upload screenshot and report endpoints (POI example)
$controller = @"
package com.taller.backend.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import java.nio.file.*;
import java.util.*;
import java.time.Instant;
import java.util.UUID;

import com.taller.backend.model.TestCase;
import com.taller.backend.model.TestRun;
import com.taller.backend.repo.TestCaseRepository;
import com.taller.backend.repo.TestRunRepository;

import org.apache.poi.xwpf.usermodel.*;
import java.io.*;

@RestController
@RequestMapping(""/api"")
public class ApiController {

    private final TestCaseRepository caseRepo;
    private final TestRunRepository runRepo;
    private final Path storage = Paths.get(""uploads"");

    public ApiController(TestCaseRepository c, TestRunRepository r) throws Exception {
        this.caseRepo = c;
        this.runRepo = r;
        Files.createDirectories(storage);
    }

    @GetMapping(""/tests"") public List<TestCase> getTests(){ return caseRepo.findAll(); }

    @PostMapping(""/tests"") public TestCase createTest(@RequestBody TestCase t){
        t.setId(UUID.randomUUID().toString()); t.setCreatedAt(Instant.now()); return caseRepo.save(t);
    }

    @GetMapping(""/runs"") public List<TestRun> getRuns(){ return runRepo.findAll(); }

    @PostMapping(""/runs"") public TestRun createRun(@RequestBody TestRun r){
        r.setId(UUID.randomUUID().toString()); r.setCreatedAt(Instant.now()); return runRepo.save(r);
    }

    @PostMapping(""/uploadScreenshot"")
    public Map<String,String> uploadScreenshot(@RequestParam(""file"") MultipartFile f) throws Exception {
        String name = UUID.randomUUID().toString() + "".png"";
        Path target = storage.resolve(name);
        Files.copy(f.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        Map<String,String> resp = new HashMap<>(); resp.put(""path"", ""/uploads/"" + name);
        return resp;
    }

    @GetMapping(""/uploads/{filename:.+}"")
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) throws Exception {
        Path file = storage.resolve(filename);
        Resource r = new UrlResource(file.toUri());
        return ResponseEntity.ok().body(r);
    }

    // Simple docx report generator (example)
    @PostMapping(""/report/generate-docx"")
    public Map<String,String> generateDocx() throws Exception {
        List<TestRun> runs = runRepo.findAll();
        XWPFDocument doc = new XWPFDocument();
        XWPFParagraph p = doc.createParagraph();
        XWPFRun r = p.createRun();
        r.setText(""Report generated by $($AuthorName)"");
        XWPFParagraph p2 = doc.createParagraph();
        p2.createRun().setText(""Test runs: "" + runs.size());
        // Save to file
        String name = ""report_"" + UUID.randomUUID().toString() + "".docx"";
        Path file = storage.resolve(name);
        try (FileOutputStream out = new FileOutputStream(file.toFile())) {
            doc.write(out);
        }
        Map<String,String> resp = new HashMap<>(); resp.put(""path"", ""/uploads/"" + name);
        return resp;
    }

    // TODO: implement pptx generator as needed (Apache POI XSLF)
}
"@
# Note: we used $AuthorName in generateDocx string above via replacement; do replacement after building string
$controllerFinal = $controller -replace '\$\(\"\$AuthorName\"\)', $AuthorName
# But simpler: just write controller and include AuthorName as literal; replace placeholder:
$controllerFinal = $controller -replace '\$\(\\$AuthorName\\\)', $AuthorName
# If above replacement didn't work, include AuthorName directly by constructing string:
$controllerFinal = $controller -replace 'Report generated by \$\(\$AuthorName\)', "Report generated by $AuthorName"

Write-Utf8NoBom -Path (Join-Path $backend "src\main\java\com\taller\backend\controller\ApiController.java") -Content $controllerFinal

# application.properties
$appProps = @"
spring.datasource.url=jdbc:h2:mem:nopcommerce;DB_CLOSE_DELAY=-1
spring.datasource.driverClassName=org.h2.Driver
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.h2.console.enabled=true
logging.level.org.springframework=INFO
server.port=8090
"@
Write-Utf8NoBom -Path (Join-Path $backend "src\main\resources\application.properties") -Content $appProps

# Mock static pages (fallback)
$mockHome = @"
<!doctype html>
<html><head><meta charset='utf-8'><title>Mock NopCommerce Home</title></head>
<body>
  <h1>Mock NopCommerce - Home</h1>
  <a href='/mock/product'>14.1-inch Laptop</a><br/>
  <a href='/mock/cart'>Shopping cart</a>
</body></html>
"@
Write-Utf8NoBom -Path (Join-Path $backend "src\main\resources\static\mock\index.html") -Content $mockHome

$mockProduct = @"
<!doctype html><html><head><meta charset='utf-8'><title>Mock Product</title></head><body>
<h1>14.1-inch Laptop</h1>
<button id='add-to-cart' class='add-to-cart-button'>Add to cart</button>
<a href='/mock/cart'>Shopping cart</a>
</body></html>
"@
Write-Utf8NoBom -Path (Join-Path $backend "src\main\resources\static\mock\product.html") -Content $mockProduct

$mockCart = @"
<!doctype html><html><head><meta charset='utf-8'><title>Mock Cart</title></head><body>
<h1>Shopping cart</h1>
<a href='/mock/checkout'>Checkout</a>
</body></html>
"@
Write-Utf8NoBom -Path (Join-Path $backend "src\main\resources\static\mock\cart.html") -Content $mockCart

$mockCheckout = @"
<!doctype html><html><head><meta charset='utf-8'><title>Mock Checkout</title></head><body>
<h1>Checkout</h1>
<button id='checkout-guest'>Checkout as Guest</button>
<form id='guest-form' style='display:none;'>
  <input id='BillingNewAddress_FirstName' placeholder='FirstName' />
  <input id='BillingNewAddress_LastName' placeholder='LastName' />
  <input id='BillingNewAddress_Email' placeholder='Email' />
  <input id='BillingNewAddress_CountryId' placeholder='Country' />
  <input id='BillingNewAddress_City' placeholder='City' />
  <input id='BillingNewAddress_Address1' placeholder='Address1' />
  <input id='BillingNewAddress_ZipPostalCode' placeholder='Zip' />
  <input id='BillingNewAddress_PhoneNumber' placeholder='Phone' />
  <label><input id='termsofservice' type='checkbox'/> I accept terms</label>
  <div><label><input type='radio' name='payment' value='Check / Money Order'/> Check / Money Order</label></div>
  <button id='confirm-order' class='checkout-confirm-order-button'>Confirm</button>
</form>
<script>
 document.getElementById('checkout-guest').addEventListener('click', ()=>{ document.getElementById('guest-form').style.display='block'; });
 document.getElementById('confirm-order').addEventListener('click', function(e){ e.preventDefault(); window.location='/mock/confirm'; });
</script>
</body></html>
"@
Write-Utf8NoBom -Path (Join-Path $backend "src\main\resources\static\mock\checkout.html") -Content $mockCheckout

$mockConfirm = @"
<!doctype html><html><head><meta charset='utf-8'><title>Order Confirmed</title></head><body>
<h1>Order completed</h1>
<div class='order-number'>Order number: #12345</div>
</body></html>
"@
Write-Utf8NoBom -Path (Join-Path $backend "src\main\resources\static\mock\confirm.html") -Content $mockConfirm

# Backend README
$backendReadme = @"
Backend Spring Boot (Results + Mock)
Author: $AuthorName <$AuthorEmail>
Institution: $Institution
Workshop: $WorkshopTitle
Generated: $Timestamp

Run:
  cd backend_springboot
  mvn clean package
  mvn spring-boot:run

API:
  GET  /api/tests
  POST /api/tests
  GET  /api/runs
  POST /api/runs
  POST /api/uploadScreenshot (multipart file)
  POST /api/report/generate-docx
Mock UI (fallback):
  http://localhost:8090/mock/
"@
Write-Utf8NoBom -Path (Join-Path $backend "README.txt") -Content $backendReadme

# ---------------------------------------------
# 2) FRONTEND (React + TypeScript - Vite)
# ---------------------------------------------
$pkgJson = @{
  name = "taller-frontend"
  version = "1.0.0"
  private = $true
  scripts = @{
    dev = "vite"
    build = "vite build"
    preview = "vite preview"
  }
  dependencies = @{
    react = "^18.2.0"; "react-dom" = "^18.2.0"; axios = "^1.4.0";
    "@mui/material" = "^5.14.0"; "@mui/icons-material" = "^5.14.0";
    "chart.js" = "^4.4.0"; "react-chartjs-2" = "^5.2.0"; "toastify-js" = "^1.14.0"; "uuid"="^9.0.0"
  }
  devDependencies = @{
    typescript="^4.9.5"; vite="^5.0.0"; "@types/react"="^18.0.28"; "@types/react-dom"="^18.0.11"
  }
}
Write-Utf8NoBom -Path (Join-Path $frontend "package.json") -Content (ConvertTo-Json $pkgJson -Depth 5)

$indexHtml = @"
<!doctype html>
<html>
  <head><meta charset='utf-8'/><meta name='viewport' content='width=device-width, initial-scale=1.0'/><title>$WorkshopTitle - Frontend</title></head>
  <body><div id='root'></div><script type='module' src='/src/main.tsx'></script></body>
</html>
"@
Write-Utf8NoBom -Path (Join-Path $frontend "index.html") -Content $indexHtml

# src/main.tsx
$mainTsx = @"
import React from 'react';
import { createRoot } from 'react-dom/client';
import App from './App';
import './styles.css';
createRoot(document.getElementById('root')!).render(<App />);
"@
Write-Utf8NoBom -Path (Join-Path $frontend "src\main.tsx") -Content $mainTsx

# App.tsx - simple dashboard with BaseUrl selector
$appTsx = @"
import React, { useEffect, useState } from 'react';
import axios from 'axios';
import Toastify from 'toastify-js';
import 'toastify-js/src/toastify.css';

const DEFAULT_API = 'http://localhost:8090/api';

export default function App(){
  const [api, setApi] = useState(DEFAULT_API);
  const [tests, setTests] = useState([]);
  const [runs, setRuns] = useState([]);
  const [name, setName] = useState('');
  const [desc, setDesc] = useState('');
  useEffect(()=>{ fetchTests(); fetchRuns(); },[]);
  async function fetchTests(){ try{ const res = await axios.get(api + '/tests'); setTests(res.data);} catch(e){ console.error(e);} }
  async function fetchRuns(){ try{ const res = await axios.get(api + '/runs'); setRuns(res.data);} catch(e){ console.error(e);} }
  async function addTest(e:any){ e.preventDefault(); try{ await axios.post(api + '/tests', { name, description:desc, url: window['BASE_URL'] || '', type:'e2e' }); Toastify({ text: 'Caso añadido', duration:2500 }).showToast(); setName(''); setDesc(''); fetchTests(); } catch(e){ Toastify({ text: 'Error', duration:2500 }).showToast(); } }
  async function simulateAll(){ for(const t of tests){ const ok = Math.random() > 0.25; const start = Date.now(); await new Promise(r=>setTimeout(r,250)); const time = Date.now() - start; await axios.post(api + '/runs', { testId: t.id, status: ok ? 'PASS' : 'FAIL', timeMs: time }); } fetchRuns(); }
  return (<div style={{padding:20}}>
    <h2>$WorkshopTitle - Dashboard</h2>
    <div style={{marginBottom:10}}>
      <label>Base URL para pruebas: </label>
      <input defaultValue={'$BaseUrl'} id='baseUrl' style={{width:400}} onChange={(e)=>{ window['BASE_URL']=e.target.value; }} />
    </div>
    <form onSubmit={addTest}><input placeholder='Nombre del caso' value={name} onChange={e=>setName(e.target.value)} required/> <br/>
    <textarea placeholder='Descripción' value={desc} onChange={e=>setDesc(e.target.value)}></textarea> <br/>
    <button type='submit'>Añadir</button> <button type='button' onClick={simulateAll}>Simular todo</button></form>
    <h3>Tests</h3><ul>{tests.map((t:any)=><li key={t.id}>{t.name}</li>)}</ul>
    <h3>Runs</h3><ul>{runs.map((r:any)=><li key={r.id}>{r.testId} - {r.status}</li>)}</ul>
  </div>);
}
"@
Write-Utf8NoBom -Path (Join-Path $frontend "src\App.tsx") -Content $appTsx

Write-Utf8NoBom -Path (Join-Path $frontend "src\styles.css") -Content "body{font-family:Arial, Helvetica, sans-serif;margin:0;padding:0;}"

# vite config
$vite = @"
import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  server: { port: 5173 }
})
"@
Write-Utf8NoBom -Path (Join-Path $frontend "vite.config.ts") -Content $vite

# tsconfig
$ts = @{
  compilerOptions = @{
    target = "ESNext"
    useDefineForClassFields = $true
    lib = @("DOM","ESNext")
    jsx = "react-jsx"
    module = "ESNext"
    moduleResolution = "Node"
    strict = $true
    esModuleInterop = $true
    skipLibCheck = $true
    forceConsistentCasingInFileNames = $true
  }
}
Write-Utf8NoBom -Path (Join-Path $frontend "tsconfig.json") -Content (ConvertTo-Json $ts -Depth 10)

# frontend README
$frontendReadme = @"
Frontend React + TypeScript (Vite)
Author: $AuthorName <$AuthorEmail>
Institution: $Institution
Workshop: $WorkshopTitle
Default BaseUrl: $BaseUrl

Run:
  cd frontend_react_ts
  npm install
  npm run dev

Notes:
 - To change the Base URL for tests, edit the input on the top of the dashboard or set window.BASE_URL
"@
Write-Utf8NoBom -Path (Join-Path $frontend "README.txt") -Content $frontendReadme

# ---------------------------------------------
# 3) AUTOMATION - Serenity Screenplay skeleton
# ---------------------------------------------
$autoPom = @"
<project xmlns='http://maven.apache.org/POM/4.0.0' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'
 xsi:schemaLocation='http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd'>
  <modelVersion>4.0.0</modelVersion>
  <groupId>com.taller</groupId>
  <artifactId>automation-nopcommerce</artifactId>
  <version>1.0.0</version>
  <properties>
    <serenity.version>3.7.0</serenity.version>
    <cucumber.version>7.14.0</cucumber.version>
    <maven.compiler.source>11</maven.compiler.source>
    <maven.compiler.target>11</maven.compiler.target>
  </properties>
  <dependencies>
    <dependency><groupId>net.serenity-bdd</groupId><artifactId>serenity-core</artifactId><version>\${serenity.version}</version></dependency>
    <dependency><groupId>net.serenity-bdd</groupId><artifactId>serenity-screenplay</artifactId><version>\${serenity.version}</version></dependency>
    <dependency><groupId>net.serenity-bdd</groupId><artifactId>serenity-screenplay-webdriver</artifactId><version>\${serenity.version}</version></dependency>
    <dependency><groupId>io.cucumber</groupId><artifactId>cucumber-java</artifactId><version>\${cucumber.version}</version></dependency>
    <dependency><groupId>io.cucumber</groupId><artifactId>cucumber-junit</artifactId><version>\${cucumber.version}</version><scope>test</scope></dependency>
    <dependency><groupId>org.seleniumhq.selenium</groupId><artifactId>selenium-java</artifactId><version>4.8.0</version></dependency>
  </dependencies>
</project>
"@
Write-Utf8NoBom -Path (Join-Path $automation "pom.xml") -Content $autoPom

# serenity.properties
$serProps = @"
webdriver.driver = chrome
serenity.take.screenshots = FOR_EACH_ACTION
serenity.outputDirectory = target/site/serenity
"@
Write-Utf8NoBom -Path (Join-Path $automation "src\test\resources\serenity.properties") -Content $serProps

# Feature file with BaseUrl parameter in Background
$feature = @"
Feature: Compra como invitado en NopCommerce Demo

  Background:
    Given que el navegador está en ""$BaseUrl""

  Scenario: Ruta feliz - seleccionar producto, añadir al carrito y completar checkout como invitado
    When selecciona el producto ""14.1-inch Laptop""
    And lo añade al carrito
    And va al carrito y procede al checkout
    And elige ""Checkout as Guest""
    And completa la dirección de envío con datos válidos
    And acepta los términos y condiciones
    And selecciona el método de pago ""Check / Money Order""
    And confirma la orden
    Then debe ver la página de confirmación de la orden con número de pedido
"@
Write-Utf8NoBom -Path (Join-Path $automation "src\test\resources\features\compra_guest.feature") -Content $feature

# AppUI.java
$appUI = @"
package com.taller.automation.ui;
import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;
public class AppUI {
    public static Target productLink(String title){ return Target.the(""product "" + title).located(By.linkText(title)); }
    public static final Target addToCartButton = Target.the(""add to cart"").located(By.xpath(""//button[contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'add to cart')]""));
    public static final Target shoppingCartLink = Target.the(""shopping cart"").located(By.cssSelector(""a[href*='/cart']""));
    public static final Target checkoutButton = Target.the(""checkout"").located(By.cssSelector(""button.checkout-button, a[href*='/checkout']""));
    public static final Target continueAsGuestButton = Target.the(""checkout as guest"").located(By.cssSelector(""input[value='Checkout as Guest'], button.checkout-as-guest-button""));
    public static final Target termsCheckbox = Target.the(""terms"").located(By.id(""termsofservice""));
    public static final Target firstName = Target.the(""first name"").located(By.id(""BillingNewAddress_FirstName""));
    public static final Target lastName = Target.the(""last name"").located(By.id(""BillingNewAddress_LastName""));
    public static final Target email = Target.the(""email"").located(By.id(""BillingNewAddress_Email""));
    public static final Target country = Target.the(""country"").located(By.id(""BillingNewAddress_CountryId""));
    public static final Target city = Target.the(""city"").located(By.id(""BillingNewAddress_City""));
    public static final Target address1 = Target.the(""address1"").located(By.id(""BillingNewAddress_Address1""));
    public static final Target postalCode = Target.the(""zip"").located(By.id(""BillingNewAddress_ZipPostalCode""));
    public static final Target phoneNumber = Target.the(""phone"").located(By.id(""BillingNewAddress_PhoneNumber""));
    public static Target paymentMethodByName(String name){ return Target.the(""payment ""+name).located(By.xpath(""//*[contains(text(),'"" + name + ""')]/ancestor::label//input[@type='radio']"")); }
    public static final Target confirmOrderButton = Target.the(""confirm order"").located(By.cssSelector(""button.checkout-confirm-order-button""));
    public static final Target orderConfirmation = Target.the(""order confirmation"").located(By.cssSelector(""div.order-number, div.section.order-completed""));
}
"@
Write-Utf8NoBom -Path (Join-Path $automation "src\test\java\com\taller\automation\ui\AppUI.java") -Content $appUI

# ClickOn and EnterText interactions
$clickOn = @"
package com.taller.automation.interactions;
import net.serenitybdd.screenplay.Interaction;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.actions.Click;
import net.serenitybdd.screenplay.targets.Target;
public class ClickOn implements Interaction {
    private Target target;
    public ClickOn(Target t){ this.target = t; }
    public static ClickOn on(Target t){ return new ClickOn(t); }
    @Override public <T extends Actor> void performAs(T actor){ actor.attemptsTo(Click.on(target)); }
}
"@
Write-Utf8NoBom -Path (Join-Path $automation "src\test\java\com\taller\automation\interactions\ClickOn.java") -Content $clickOn

$enterText = @"
package com.taller.automation.interactions;
import net.serenitybdd.screenplay.Interaction;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.actions.Enter;
import net.serenitybdd.screenplay.targets.Target;
public class EnterText implements Interaction {
    private Target target; private String text;
    public EnterText(Target t, String text){ this.target=t; this.text=text; }
    public static EnterText into(Target t, String text){ return new EnterText(t, text); }
    @Override public <T extends Actor> void performAs(T actor){ actor.attemptsTo(Enter.theValue(text).into(target)); }
}
"@
Write-Utf8NoBom -Path (Join-Path $automation "src\test\java\com\taller\automation\interactions\EnterText.java") -Content $enterText

# Tasks skeleton (SelectProduct, AddToCart, GoToCart, ProceedToCheckout, CheckoutAsGuest, FillAddress, AcceptTerms, SelectPayment, ConfirmOrder)
$tasks = @{
 "SelectProduct" = @"
package com.taller.automation.tasks;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.Actor;
import static net.serenitybdd.screenplay.Tasks.instrumented;
import com.taller.automation.ui.AppUI;
import net.serenitybdd.screenplay.actions.Click;
public class SelectProduct implements Task {
    private String title;
    public SelectProduct(String title){ this.title = title; }
    public static SelectProduct called(String title){ return instrumented(SelectProduct.class, title); }
    @Override public <T extends Actor> void performAs(T actor){ actor.attemptsTo(Click.on(AppUI.productLink(title))); }
}
"@
 "AddToCart" = @"
package com.taller.automation.tasks;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.Actor;
import static net.serenitybdd.screenplay.Tasks.instrumented;
import com.taller.automation.ui.AppUI;
import net.serenitybdd.screenplay.actions.Click;
public class AddToCart implements Task {
    public static AddToCart item(){ return instrumented(AddToCart.class); }
    @Override public <T extends Actor> void performAs(T actor){ actor.attemptsTo(Click.on(AppUI.addToCartButton)); }
}
"@
 "GoToCart" = @"
package com.taller.automation.tasks;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.Actor;
import static net.serenitybdd.screenplay.Tasks.instrumented;
import com.taller.automation.ui.AppUI;
import net.serenitybdd.screenplay.actions.Click;
public class GoToCart implements Task { public static GoToCart open(){ return instrumented(GoToCart.class); } @Override public <T extends Actor> void performAs(T actor){ actor.attemptsTo(Click.on(AppUI.shoppingCartLink)); } }
"@
 "ProceedToCheckout" = @"
package com.taller.automation.tasks;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.Actor;
import static net.serenitybdd.screenplay.Tasks.instrumented;
import com.taller.automation.ui.AppUI;
import net.serenitybdd.screenplay.actions.Click;
public class ProceedToCheckout implements Task {
    public static ProceedToCheckout now(){ return instrumented(ProceedToCheckout.class); }
    @Override public <T extends Actor> void performAs(T actor){ actor.attemptsTo(Click.on(AppUI.checkoutButton)); }
}
"@
 "CheckoutAsGuest" = @"
package com.taller.automation.tasks;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.Actor;
import static net.serenitybdd.screenplay.Tasks.instrumented;
import com.taller.automation.ui.AppUI;
import net.serenitybdd.screenplay.actions.Click;
public class CheckoutAsGuest implements Task { public static CheckoutAsGuest choose(){ return instrumented(CheckoutAsGuest.class); } @Override public <T extends Actor> void performAs(T actor){ actor.attemptsTo(Click.on(AppUI.continueAsGuestButton)); } }
"@
 "FillAddress" = @"
package com.taller.automation.tasks;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.Actor;
import static net.serenitybdd.screenplay.Tasks.instrumented;
import com.taller.automation.ui.AppUI;
import com.taller.automation.interactions.EnterText;
public class FillAddress implements Task {
    private String first,last,email,country,city,address,zip,phone;
    public FillAddress(String first,String last,String email,String country,String city,String address,String zip,String phone){
        this.first=first;this.last=last;this.email=email;this.country=country;this.city=city;this.address=address;this.zip=zip;this.phone=phone;
    }
    public static FillAddress with(String first,String last,String email,String country,String city,String address,String zip,String phone){
        return instrumented(FillAddress.class, first,last,email,country,city,address,zip,phone);
    }
    @Override public <T extends Actor> void performAs(T actor){
        actor.attemptsTo(
            EnterText.into(AppUI.firstName, first),
            EnterText.into(AppUI.lastName, last),
            EnterText.into(AppUI.email, email),
            EnterText.into(AppUI.country, country),
            EnterText.into(AppUI.city, city),
            EnterText.into(AppUI.address1, address),
            EnterText.into(AppUI.postalCode, zip),
            EnterText.into(AppUI.phoneNumber, phone)
        );
    }
}
"@
 "AcceptTerms" = @"
package com.taller.automation.tasks;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.Actor;
import static net.serenitybdd.screenplay.Tasks.instrumented;
import com.taller.automation.ui.AppUI;
import net.serenitybdd.screenplay.actions.Click;
public class AcceptTerms implements Task { public static AcceptTerms now(){ return instrumented(AcceptTerms.class); } @Override public <T extends Actor> void performAs(T actor){ actor.attemptsTo(Click.on(AppUI.termsCheckbox)); } }
"@
 "SelectPayment" = @"
package com.taller.automation.tasks;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.Actor;
import static net.serenitybdd.screenplay.Tasks.instrumented;
import com.taller.automation.ui.AppUI;
import net.serenitybdd.screenplay.actions.Click;
public class SelectPayment implements Task {
    private String method;
    public SelectPayment(String method){ this.method=method; }
    public static SelectPayment named(String method){ return instrumented(SelectPayment.class, method); }
    @Override public <T extends Actor> void performAs(T actor){ actor.attemptsTo(Click.on(AppUI.paymentMethodByName(method))); }
}
"@
 "ConfirmOrder" = @"
package com.taller.automation.tasks;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.Actor;
import static net.serenitybdd.screenplay.Tasks.instrumented;
import com.taller.automation.ui.AppUI;
import net.serenitybdd.screenplay.actions.Click;
public class ConfirmOrder implements Task { public static ConfirmOrder now(){ return instrumented(ConfirmOrder.class); } @Override public <T extends Actor> void performAs(T actor){ actor.attemptsTo(Click.on(AppUI.confirmOrderButton)); } }
"@
}

foreach ($k in $tasks.Keys) {
  $path = Join-Path $automation ("src\test\java\com\taller\automation\tasks\" + $k + ".java")
  Write-Utf8NoBom -Path $path -Content $tasks[$k]
}

# Question: OrderConfirmation
$orderQuestion = @"
package com.taller.automation.questions;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.questions.Text;
import com.taller.automation.ui.AppUI;
public class OrderConfirmation implements Question<String> {
    public static OrderConfirmation number(){ return new OrderConfirmation(); }
    @Override public String answeredBy(Actor actor){ return Text.of(AppUI.orderConfirmation).viewedBy(actor).asString(); }
}
"@
Write-Utf8NoBom -Path (Join-Path $automation "src\test\java\com\taller\automation\questions\OrderConfirmation.java") -Content $orderQuestion

# Steps
$steps = @"
package com.taller.automation.steps;
import io.cucumber.java.en.*;
import net.serenitybdd.screenplay.actors.OnStage;
import net.serenitybdd.screenplay.actors.OnlineCast;
import net.serenitybdd.screenplay.actions.Open;
import com.taller.automation.tasks.*;
import com.taller.automation.questions.OrderConfirmation;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

public class StepDefinitions {
    @Given(""que el navegador está en {string}"")
    public void openHome(String url){ OnStage.setTheStage(new OnlineCast()); OnStage.theActorCalled(""usuario"").wasAbleTo(Open.url(url)); }

    @When(""selecciona el producto {string}"")
    public void selectProduct(String prod){ OnStage.theActorInTheSpotlight().attemptsTo(SelectProduct.called(prod)); }

    @When(""lo añade al carrito"")
    public void addToCart(){ OnStage.theActorInTheSpotlight().attemptsTo(AddToCart.item()); }

    @When(""va al carrito y procede al checkout"")
    public void goToCart(){ OnStage.theActorInTheSpotlight().attemptsTo(GoToCart.open(), ProceedToCheckout.now()); }

    @When(""elige {string}"")
    public void chooseGuest(String s){ OnStage.theActorInTheSpotlight().attemptsTo(CheckoutAsGuest.choose()); }

    @When(""completa la dirección de envío con datos válidos"")
    public void fillAddress(){ OnStage.theActorInTheSpotlight().attemptsTo(FillAddress.with(
            ""John"",""Doe"",""john.doe+"" + System.currentTimeMillis() + ""@example.com"",""United States"",""New York"",""123 Main St"",""10001"",""5551234567""
        )); }

    @When(""acepta los términos y condiciones"")
    public void acceptTerms(){ OnStage.theActorInTheSpotlight().attemptsTo(AcceptTerms.now()); }

    @When(""selecciona el método de pago {string}"")
    public void selectPayment(String m){ OnStage.theActorInTheSpotlight().attemptsTo(SelectPayment.named(m)); }

    @When(""confirma la orden"")
    public void confirmOrder(){ OnStage.theActorInTheSpotlight().attemptsTo(ConfirmOrder.now()); }

    @Then(""debe ver la página de confirmación de la orden con número de pedido"")
    public void verifyConfirmation(){ String txt = OnStage.theActorInTheSpotlight().asksFor(OrderConfirmation.number()); assertThat(txt.toLowerCase(), containsString(""order"")); }
}
"@
Write-Utf8NoBom -Path (Join-Path $automation "src\test\java\com\taller\automation\steps\StepDefinitions.java") -Content $steps

# Runner
$runner = @"
package com.taller.automation.runner;
import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(features=""src/test/resources/features"", glue=""com.taller.automation.steps"")
public class TestRunner {}
"@
Write-Utf8NoBom -Path (Join-Path $automation "src\test\java\com\taller\automation\runner\TestRunner.java") -Content $runner

Write-Utf8NoBom -Path (Join-Path $automation "README.txt") -Content "Automation (Serenity) project generated. Run with: mvn clean verify"

# ---------------------------------------------
# 4) Helper run scripts
# ---------------------------------------------
$runBackend = @"
# Run backend (requires Maven & Java)
cd `"$($backend)`"
mvn clean package
mvn spring-boot:run
"@
Write-Utf8NoBom -Path (Join-Path $base "run_backend.ps1") -Content $runBackend

$runFrontend = @"
# Run frontend (requires Node & npm)
cd `"$($frontend)`"
npm install
npm run dev
"@
Write-Utf8NoBom -Path (Join-Path $base "run_frontend.ps1") -Content $runFrontend

$runAutomation = @"
# Run automation (requires Maven & Java & Chrome)
cd `"$($automation)`"
mvn clean verify
"@
Write-Utf8NoBom -Path (Join-Path $base "run_automation.ps1") -Content $runAutomation

# ---------------------------------------------
# 5) Write top-level README
# ---------------------------------------------
$globalReadme = @"
TALLER - Proyecto generado
Author: $AuthorName <$AuthorEmail>
Institution: $Institution
Workshop: $WorkshopTitle
Generated: $Timestamp

Contenido:
 - backend_springboot/    Backend Spring Boot (API + Mock + report generator)
 - frontend_react_ts/     Frontend React + TypeScript (Vite) - Dashboard
 - automation_serenity/   Automation Serenity Screenplay + Cucumber

Ejecutar:
 - Backend:
    cd backend_springboot
    mvn clean package
    mvn spring-boot:run
 - Frontend:
    cd frontend_react_ts
    npm install
    npm run dev
 - Automation:
    cd automation_serenity
    mvn clean verify

Cambiar Base URL para pruebas:
 - Edita el archivo src/test/resources/features/compra_guest.feature en automation_serenity y modifica Background -> Given que el navegador está en "<URL>"
 - O desde el frontend, en la caja 'Base URL para pruebas' escribe la URL y úsala al ejecutar.

Zips en la carpeta: $zipsFolder
"@
Write-Utf8NoBom -Path (Join-Path $base "README.txt") -Content $globalReadme

# ---------------------------------------------
# 6) Zip packaging
# ---------------------------------------------
function Zip-Folder($src, $dest) {
    if (Test-Path $dest) { Remove-Item $dest -Force }
    Add-Type -AssemblyName System.IO.Compression.FileSystem
    [IO.Compression.ZipFile]::CreateFromDirectory($src, $dest)
}

$autoZip = Join-Path $zipsFolder "automation_serenity_nopcommerce_$Timestamp.zip"
$backendZip = Join-Path $zipsFolder "backend_results_nopcommerce_$Timestamp.zip"
$frontendZip = Join-Path $zipsFolder "frontend_react_ts_$Timestamp.zip"
$completeZip = Join-Path $zipsFolder "proyecto_nopcommerce_full_$Timestamp.zip"

Write-Host "Creando zip: Automation..."
Zip-Folder -src $automation -dest $autoZip
Write-Host "Creando zip: Backend..."
Zip-Folder -src $backend -dest $backendZip
Write-Host "Creando zip: Frontend..."
Zip-Folder -src $frontend -dest $frontendZip

# Combined zip (all)
if (Test-Path $completeZip) { Remove-Item $completeZip -Force }
Add-Type -AssemblyName System.IO.Compression.FileSystem
[IO.Compression.ZipFile]::CreateFromDirectory($base, $completeZip)

Write-Host "Zips generados en: $zipsFolder" -ForegroundColor Cyan
Write-Host " - $autoZip"
Write-Host " - $backendZip"
Write-Host " - $frontendZip"
Write-Host " - $completeZip"

Write-Host "Proceso completado. Revisa README dentro de cada carpeta para instrucciones de build." -ForegroundColor Green
