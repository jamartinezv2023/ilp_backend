package com.inclusive.tenantservice.controller;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("/home")

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controlador principal de bienvenida del Tenant Service.
 *
 * Parte de la plataforma educativa "Inclusive Learning Platform".
 * Este servicio fomenta entornos de aprendizaje inclusivos,
 * seguros y accesibles, administrando instituciones educativas (tenants)
 * en una arquitectura multi-tenant con aislamiento y configuraciÃƒÂ³n independiente.
 *
 * Autor: JosÃƒÂ© Alfredo MartÃƒÂ­nez ValdÃƒÂ©s
 * Contacto: jose.martinez7@udea.edu.co | +57-3017690703
 * AÃƒÂ±o: 2025
 */
@Controller
public class HomeController {

    @GetMapping("/")
    @ResponseBody
    public String home() {
        return """
        <!DOCTYPE html>
        <html lang="es">
        <head>
            <meta charset="UTF-8">
            <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Inclusive Learning Platform | Tenant Service</title>

            <!-- Bootstrap -->
            <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
            <!-- Material Icons -->
            <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
            <!-- Toastify -->
            <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/toastify-js/src/toastify.min.css">

            <style>
                :root {
                    --color-primary: #3b82f6;
                    --color-secondary: #9333ea;
                    --color-accent: #10b981;
                    --bg-light: #f9fafb;
                    --text-light: #1e293b;
                    --bg-dark: #0f172a;
                    --text-dark: #f8fafc;
                }

                body {
                    font-family: 'Segoe UI', Roboto, sans-serif;
                    margin: 0;
                    height: 100vh;
                    display: flex;
                    flex-direction: column;
                    justify-content: center;
                    align-items: center;
                    text-align: center;
                    transition: background 0.5s, color 0.5s;
                }

                @media (prefers-color-scheme: light) {
                    body {
                        background: linear-gradient(135deg, var(--color-primary), var(--color-accent));
                        color: var(--text-light);
                    }
                    .card {
                        background: rgba(255,255,255,0.85);
                        color: var(--text-light);
                    }
                }

                @media (prefers-color-scheme: dark) {
                    body {
                        background: linear-gradient(135deg, var(--color-primary), var(--color-secondary));
                        color: var(--text-dark);
                    }
                    .card {
                        background: rgba(255,255,255,0.1);
                        color: var(--text-dark);
                    }
                }

                .card {
                    backdrop-filter: blur(10px);
                    border-radius: 1.5rem;
                    padding: 2rem 3rem;
                    max-width: 650px;
                    box-shadow: 0 4px 20px rgba(0,0,0,0.25);
                    animation: fadeIn 1.2s ease-out;
                }

                @keyframes fadeIn {
                    from { opacity: 0; transform: translateY(30px); }
                    to { opacity: 1; transform: translateY(0); }
                }

                h1 {
                    font-weight: 700;
                    margin-bottom: 0.5rem;
                }

                h3 {
                    font-weight: 500;
                    margin-bottom: 1rem;
                }

                p {
                    font-size: 1.1rem;
                    line-height: 1.6;
                }

                button {
                    transition: transform 0.2s ease, box-shadow 0.3s ease;
                }

                button:hover {
                    transform: scale(1.05);
                    box-shadow: 0 0 10px rgba(0, 0, 0, 0.3);
                }

                .footer {
                    position: absolute;
                    bottom: 15px;
                    font-size: 0.9rem;
                    text-align: center;
                    opacity: 0.85;
                    line-height: 1.4;
                }

                .material-icons {
                    vertical-align: middle;
                    margin-right: 5px;
                }

                a {
                    color: var(--color-accent);
                    text-decoration: none;
                }

                a:hover {
                    text-decoration: underline;
                }

                .switch-container {
                    position: absolute;
                    top: 15px;
                    right: 20px;
                }

                .form-check-label {
                    font-size: 0.9rem;
                }
            </style>
        </head>
        <body>
            <div class="switch-container form-check form-switch text-light">
                <input class="form-check-input" type="checkbox" role="switch" id="modeSwitch">
                <label class="form-check-label" for="modeSwitch">Modo claro/oscuro</label>
            </div>

            <div class="card text-center">
                <span class="material-icons" style="font-size:70px;">diversity_3</span>
                <h1>Inclusive Learning Platform</h1>
                <h3>Servicio de gestiÃƒÂ³n de instituciones educativas</h3>
                <p>
                    Este servicio fomenta entornos de aprendizaje inclusivos y accesibles para todas las personas.<br>
                    Administra instituciones dentro de una arquitectura multi-tenant, garantizando independencia, seguridad y equidad.
                </p>
                <button class="btn btn-outline-light" id="toastBtn">
                    <span class="material-icons">emoji_people</span> Mostrar mensaje inclusivo
                </button>
            </div>

            <div class="footer">
                Ã‚Â© 2025 Inclusive Learning Platform<br>
                JosÃƒÂ© Alfredo MartÃƒÂ­nez ValdÃƒÂ©s | <a href="mailto:jose.martinez7@udea.edu.co">jose.martinez7@udea.edu.co</a> | +57-3017690703
            </div>

            <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
            <script src="https://cdn.jsdelivr.net/npm/toastify-js"></script>
            <script>
                document.getElementById('toastBtn').addEventListener('click', function() {
                    Toastify({
                        text: "Ã¢Å“Â¨ Ã‚Â¡Bienvenida, bienvenido o bienvenide a un entorno educativo inclusivo y accesible!",
                        duration: 4500,
                        gravity: "bottom",
                        position: "center",
                        style: { background: "linear-gradient(135deg,#10b981,#3b82f6,#9333ea)" }
                    }).showToast();
                });

                // Interruptor manual de modo claro/oscuro
                const modeSwitch = document.getElementById('modeSwitch');
                modeSwitch.addEventListener('change', function() {
                    if (this.checked) {
                        document.body.style.background = "linear-gradient(135deg,#0f172a,#1e293b)";
                        document.body.style.color = "#f8fafc";
                    } else {
                        document.body.style.background = "linear-gradient(135deg,#3b82f6,#10b981)";
                        document.body.style.color = "#1e293b";
                    }
                });
            </script>
        </body>
        </html>
        """;
    }
}




