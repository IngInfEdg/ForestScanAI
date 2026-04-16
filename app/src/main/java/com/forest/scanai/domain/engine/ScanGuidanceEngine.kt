package com.forest.scanai.domain.engine

import com.forest.scanai.domain.model.CompletenessLevel

class ScanGuidanceEngine {

    fun buildMessage(
        completeness: CompletenessLevel,
        missingSectors: List<Int>,
        observerSamples: Int,
        usefulPoints: Int,
        missingUpperBand: Boolean = false,
        lowTopCoverage: Boolean = false,
        autoCompletionCandidate: Boolean = false
    ): String {
        if (autoCompletionCandidate) {
            return "Medición completa detectada. Ya puedes finalizar; no se observan mejoras relevantes en la nube."
        }
        return when (completeness) {
            CompletenessLevel.INSUFFICIENT -> {
                when {
                    missingUpperBand || lowTopCoverage -> "Inclina el celular hacia la cima de la pila y mantén la corona al centro."
                    observerSamples < 20 -> "Sigue rodeando la pila para registrar mejor el recorrido."
                    usefulPoints < 800 -> "Acércate un poco más y sigue escaneando para capturar más puntos."
                    missingSectors.isNotEmpty() -> "Faltan sectores por cubrir: ${missingSectors.joinToString()}."
                    else -> "Cobertura insuficiente. Sigue recorriendo el perímetro."
                }
            }
            CompletenessLevel.PARTIAL -> when {
                missingUpperBand || lowTopCoverage -> "Aún faltan puntos en la parte más alta de la pila."
                else -> "Cobertura parcial. Aún faltan sectores de la pila por medir."
            }
            CompletenessLevel.ACCEPTABLE ->
                "Medición revisable. Puedes finalizar o dar una vuelta corta para afinar."
            CompletenessLevel.COMPLETE ->
                "Medición completa. Ya puedes finalizar."
        }
    }
}
