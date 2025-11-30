@Override
public AuthTokens login(String email, String rawPassword) {

    User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("Credenciales inválidas"));

    // Validar contraseña encriptada
    if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
        throw new IllegalArgumentException("Credenciales inválidas");
    }

    // Generar Access + Refresh
    return jwtTokenService.generateTokens(user);
}
