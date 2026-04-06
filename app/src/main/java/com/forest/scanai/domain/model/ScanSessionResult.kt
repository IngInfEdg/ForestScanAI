package com.forest.scanai.domain.model

import android.location.Location
import io.github.sceneview.math.Position

data class ScanSessionResult(
    val volume: Double,
    val length: Double,
    val maxHeight: Double,
    val maxWidth: Double,
    val points: List<Position>,
    val topPoints: List<Position>,
    val trajectory: List<Location>,
    val observerPath: List<Position>,
    val coverage: Float,
    val completeness: CompletenessLevel,
    val confidence: Float,
    val pointsCount: Int,
    val arDistanceWalked: Double,
    val gpsDistanceWalked: Double,
    val gpsPointCount: Int,
    val coveredSectors: Int,
    val totalSectors: Int,
    val missingSectors: List<Int>,
    val guidanceSummary: String,
    val timestamp: Long = System.currentTimeMillis()
)
