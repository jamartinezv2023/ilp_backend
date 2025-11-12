// ðŸ“‚ src/main/java/com/inclusive/authservice/exception/ResourceNotFoundException.java
package com.inclusive.authservice.exception;

/**
 * ExcepciÃ³n personalizada para indicar que un recurso no fue encontrado.
 *
 * Se utiliza en los servicios o controladores para manejar casos donde
 * una entidad no existe en la base de datos. Al ser una RuntimeException,
 * no es obligatorio capturarla, pero puede manejarse globalmente con
 * @ControllerAdvice.
 *
 * Ejemplo de uso:
 * throw new ResourceNotFoundException("Usuario con ID " + id + " no encontrado");
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {

        super(message);
    }
}






