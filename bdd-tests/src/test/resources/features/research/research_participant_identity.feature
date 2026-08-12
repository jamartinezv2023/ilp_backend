# language: es

@research
@pseudonymization
Característica: Identidad de investigación de un participante

  Para utilizar observaciones de participantes en el estudio
  quien realiza actividades de investigación
  necesita separar la identidad de investigación de los datos usados durante el reclutamiento

  @happy
  @identity_assignment
  Escenario: Asignar una identidad de investigación a un participante
    Dado que un participante cuenta con consentimiento vigente para el estudio
    Y el participante no tiene una identidad de investigación asignada
    Cuando quien realiza la investigación incorpora al participante al conjunto de datos del estudio
    Entonces el participante queda asociado con una identidad de investigación
    Y la identidad de investigación no contiene el código utilizado durante el reclutamiento

  @happy
  @identity_stability
  Escenario: Conservar la identidad de investigación de un participante
    Dado que un participante cuenta con consentimiento vigente para el estudio
    Y el participante tiene una identidad de investigación asignada
    Cuando quien realiza la investigación incorpora nuevamente al participante al conjunto de datos del estudio
    Entonces el participante conserva la identidad de investigación previamente asignada

  @validation
  @consent_required
  Escenario: Impedir la asignación sin consentimiento vigente
    Dado que una persona no cuenta con consentimiento vigente para el estudio
    Cuando quien realiza la investigación intenta incorporarla al conjunto de datos del estudio
    Entonces la persona no queda asociada con una identidad de investigación

  @exceptional
  Escenario: Impedir nuevas observaciones después del retiro del consentimiento
    Dado que un participante tiene una identidad de investigación asignada
    Y el participante ha retirado su consentimiento para el estudio
    Cuando quien realiza la investigación intenta registrar una nueva observación del participante
    Entonces la observación no queda incorporada al conjunto de datos del estudio

  @security
  Escenario: Utilizar la identidad de investigación en una observación científica
    Dado que un participante cuenta con consentimiento vigente para el estudio
    Y el participante tiene una identidad de investigación asignada
    Cuando quien realiza la investigación registra una observación científica del participante
    Entonces la observación queda asociada con la identidad de investigación del participante
    Y la observación no contiene el código utilizado durante el reclutamiento
