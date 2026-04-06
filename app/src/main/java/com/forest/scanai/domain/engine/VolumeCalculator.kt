package com.forest.scanai.domain.engine

import com.forest.scanai.core.ScanParams
import com.forest.scanai.domain.model.ScanResult
import io.github.sceneview.math.Position

class VolumeCalculator(private val params: ScanParams) {
    
    private var lastCalculatedVolume = 0.0

    fun calculate(points: List<Position>): ScanResult {
        // Umbral mínimo de puntos aumentado para evitar cálculos erráticos al inicio
        if (points.size < 500) return ScanResult(0.0, emptyList())
        
        val minX = points.minOf { it.x }
        val maxX = points.maxOf { it.x }
        val length = maxX - minX
        
        // Si la pila es muy pequeña, no calculamos para evitar ruido
        if (length < 0.5) return ScanResult(0.0, emptyList())

        val sliceWidth = params.sliceWidth
        val numSlices = (length / sliceWidth).toInt()
        val sliceAreas = mutableListOf<Double>()
        val rawTopPoints = mutableListOf<Position>()

        for (i in 0 until numSlices) {
            val startX = minX + i * sliceWidth
            val pointsInSlice = points.filter { it.x in startX..(startX + sliceWidth) }
            
            if (pointsInSlice.size >= 15) {
                val sortedY = pointsInSlice.map { it.y }.sorted()
                
                // Base de la pila: usamos el percentil 15 para evitar huecos en el terreno
                val groundLevel = sortedY[sortedY.size / 7]
                
                // Cima de la pila: usamos el percentil 90 para ignorar ramas sueltas (ruido)
                val topLevel = sortedY[(sortedY.size * 0.90).toInt()]
                
                val topPoint = pointsInSlice.filter { it.y >= topLevel }.maxByOrNull { it.y } ?: pointsInSlice.maxBy { it.y }
                
                val height = (topLevel - groundLevel).toDouble()
                
                if (height > params.groundMargin) {
                    rawTopPoints.add(topPoint)
                    
                    // Profundidad de la pila en este slice
                    val zMin = pointsInSlice.minOf { it.z }
                    val zMax = pointsInSlice.maxOf { it.z }
                    val depth = (zMax - zMin).toDouble().coerceAtMost(params.maxPileDepth)
                    
                    // Cálculo de área de la sección (Aproximación trapezoidal simple)
                    sliceAreas.add(height * depth)
                } else {
                    sliceAreas.add(0.0)
                    rawTopPoints.add(Position(startX + sliceWidth / 2, groundLevel, 0f))
                }
            } else {
                sliceAreas.add(0.0)
                rawTopPoints.add(Position(startX + sliceWidth / 2, 0f, 0f))
            }
        }

        // Suavizado del contorno (Línea roja) para que no sea tan "nerviosa"
        val smoothedTopPoints = rawTopPoints.mapIndexed { index, pos ->
            if (index > 2 && index < rawTopPoints.size - 3) {
                val avgY = (rawTopPoints[index - 3].y + rawTopPoints[index - 2].y + 
                            rawTopPoints[index - 1].y + pos.y + 
                            rawTopPoints[index + 1].y + rawTopPoints[index + 2].y + 
                            rawTopPoints[index + 3].y) / 7f
                Position(pos.x, avgY, pos.z)
            } else pos
        }

        var rawVolume = 0.0
        for (i in 0 until sliceAreas.size - 1) {
            rawVolume += ((sliceAreas[i] + sliceAreas[i + 1]) / 2.0) * sliceWidth
        }

        // Aplicamos un filtro de media móvil simple (EMA) al volumen total 
        // para evitar los saltos bruscos que viste en las fotos (ej. de 38 a 132)
        val finalVolume = if (lastCalculatedVolume == 0.0) rawVolume 
                          else lastCalculatedVolume + 0.05 * (rawVolume - lastCalculatedVolume)
        
        lastCalculatedVolume = finalVolume
        
        return ScanResult(finalVolume, smoothedTopPoints)
    }
}
