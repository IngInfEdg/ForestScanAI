package com.forest.scanai.domain.engine

import com.forest.scanai.domain.model.CompletenessLevel

class ScanGuidanceEngine {

    fun buildMessage(
        completeness: CompletenessLevel,
        missingSectors: List<Int>,
        observerSamples: Int,
        usefulPoints: Int
    ): String {
        return when (completeness) {
            CompletenessLevel.INSUFFICIENT -> {
                when {
                    observerSamples < 20 -> "Sigue rodeando la pila para registrar mejor el recorrido."
                    usefulPoints < 800 -> "Acércate un poco más y sigue escaneando para capturar más puntos."
                    missingSectors.isNotEmpty() -> "Faltan sectores por cubrir: ${missingSectors.joinToString()}."
                    else -> "Cobertura insuficiente. Sigue recorriendo el perímetro."
                }
            }
            CompletenessLevel.PARTIAL ->
                "Cobertura parcial. Aún faltan sectores de la pila por medir."
            CompletenessLevel.ACCEPTABLE ->
                "Cobertura aceptable. Puedes seguir escaneando para mejorar precisión."
            CompletenessLevel.COMPLETE ->
                "Medición completa. Ya puedes finalizar."
        }
    }
}
