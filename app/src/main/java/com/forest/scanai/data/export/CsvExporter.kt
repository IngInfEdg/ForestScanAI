package com.forest.scanai.data.export

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import com.forest.scanai.domain.model.ScanSessionResult
import com.forest.scanai.domain.model.ScanUiState
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CsvExporter(private val context: Context) {
    fun buildSuggestedFileName(now: Date = Date()): String {
        val ts = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(now)
        return "ForestScan_$ts.csv"
    }

    fun saveReport(
        uiState: ScanUiState,
        result: ScanSessionResult?,
        appVersionDisplay: String,
        lat: Double,
        lon: Double
    ): String? {
        val ts = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = buildSuggestedFileName()

        return try {
            val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)
            FileWriter(file).use { writer ->
                writer.append(
                        "Timestamp,AppVersion,Lat,Lon,Distance,StereoVol,NetVol,StereoVolFinal,NetVolFinal,VolumeGeometricBase,VolumeGeometricCorrected,VolumeStereoTemporalSmoothed,StereoObservationFactor,EdgeRecoveryFactor,TemporalAlpha,TemporalBoundedRatio,VolumeNetEstimate,VolumeRouteARefinedRaw,VolumeRouteARefinedCorrected,VolumeRouteARefinedStereo,VolumeRouteARefinedNet,VolumeRouteBDetectedRaw,VolumeRouteBDetectedCorrected,VolumeRouteBDetectedStereo,VolumeRouteBDetectedNet,RouteAInputCount,RouteBInputCount,RouteASource,RouteBSource,RouteBMinusRouteA,RouteBVsRouteARatio,ABTestEnabled,TotalPoints,RawPoints,AcceptedPoints,GroundPoints,NonGroundPoints,PilePoints,DetectionPilePointsCount,RefinedPilePointsCount,Clusters,SelectedCluster,DetectionQuality,DetectionConfidence,DetectionReasons,DominanceScore,HeightScore,CompactnessScore,ObserverConsistencyScore,GroundSeparationScore,EstimatedHeight,BoundingBox,BoundingBoxFinal,MaxHeight,P95Height,MeanHeight,VerticalCoverageScore,VerticalWeakBands,VerticalReasons,TopCoverageScore,TopPointCount,TopBandDensity,TopCoverageState,TopCoverageTrend,TopCoverageTemporalStability,TrajectoryQualityScore,VolumeStabilityScore,VolumeIsStable,VolumeVariationRatio,VolumeIqrRatio,VolumeMadRatio,VolumeDriftRatio,RecentUsefulPointsGrowthRatio,RecentVolumeDeltaRatio,ScaleValidationScore,ReferenceExpectedM,ReferenceObservedM,ReferenceRelativeError,ReferenceStatus,ReferenceNotes,ReferenceObservationSource,AutoCompletionCandidate,VisualCount,SessionCount,SegmentedCount,VolumeInputCount,ReviewCount,SourceOfVolumePoints,SourceOfVolumePointsOfficial,FallbackReasons\n"
                )

                val debug = result?.detectionDebugInfo.orEmpty()
                val fallbackReasons = result?.pileDetectionReasons?.joinToString(" | ") ?: ""
                val boundingBox = debug["bounding_box"] ?: ""
                val stereoVolForExport = result?.volume ?: uiState.stereoVolume
                val netVolForExport = result?.netVolumeEstimate ?: uiState.netVolume

                writer.append(
                        listOf(
                        ts,
                        appVersionDisplay,
                        lat.toString(),
                        lon.toString(),
                        uiState.distance.toString(),
                        stereoVolForExport.toString(),
                        netVolForExport.toString(),
                        debug["volume_stereo_final"] ?: stereoVolForExport.toString(),
                        netVolForExport.toString(),
                        debug["volume_geometric_raw"] ?: "",
                        debug["volume_geometric_corrected"] ?: "",
                        debug["volume_stereo_smoothed"] ?: "",
                        debug["volume_stereo_observation_factor"] ?: "",
                        debug["volume_edge_recovery_factor"] ?: "",
                        debug["volume_temporal_alpha"] ?: "",
                        debug["volume_temporal_bounded_ratio_vs_corrected"] ?: "",
                        debug["volume_net_estimate"] ?: "",
                        result?.volumeRouteARefinedRaw?.toString() ?: "",
                        result?.volumeRouteARefinedCorrected?.toString() ?: "",
                        result?.volumeRouteARefinedStereo?.toString() ?: "",
                        result?.volumeRouteARefinedNet?.toString() ?: "",
                        result?.volumeRouteBDetectedRaw?.toString() ?: "",
                        result?.volumeRouteBDetectedCorrected?.toString() ?: "",
                        result?.volumeRouteBDetectedStereo?.toString() ?: "",
                        result?.volumeRouteBDetectedNet?.toString() ?: "",
                        result?.routeAInputCount?.toString() ?: "",
                        result?.routeBInputCount?.toString() ?: "",
                        result?.routeASource ?: "",
                        result?.routeBSource ?: "",
                        debug["route_b_minus_route_a"] ?: "",
                        debug["route_b_vs_route_a_ratio"] ?: "",
                        debug["ab_test_enabled"] ?: "",
                        result?.pointsCount?.toString() ?: "",
                        debug["raw_points"] ?: "",
                        debug["accepted_points"] ?: "",
                        debug["ground_points"] ?: "",
                        debug["non_ground_points"] ?: "",
                        debug["pile_points"] ?: "",
                        debug["detection_pile_points_count"] ?: "",
                        debug["refined_pile_points_count"] ?: "",
                        debug["clusters"] ?: "",
                        debug["selected_cluster"] ?: "",
                        result?.pileDetectionQuality ?: "",
                        result?.pileDetectionConfidence?.toString() ?: "",
                        debug["detection_reasons"] ?: "",
                        debug["dominance_score"] ?: "",
                        debug["height_score"] ?: "",
                        debug["compactness_score"] ?: "",
                        debug["observer_consistency_score"] ?: "",
                        debug["ground_separation_score"] ?: "",
                        debug["estimated_height"] ?: "",
                        boundingBox,
                        debug["bounding_box_final"] ?: "",
                        debug["max_height"] ?: "",
                        debug["p95_height"] ?: "",
                        debug["mean_height"] ?: "",
                        debug["vertical_coverage_score"] ?: "",
                        debug["vertical_weak_bands"] ?: "",
                        debug["vertical_reasons"] ?: "",
                        debug["top_coverage_score"] ?: "",
                        debug["top_point_count"] ?: "",
                        debug["top_band_density"] ?: "",
                        debug["top_coverage_state"] ?: "",
                        debug["top_coverage_trend"] ?: "",
                        debug["top_coverage_temporal_stability"] ?: "",
                        debug["trajectory_quality_score"] ?: "",
                        debug["volume_stability_score"] ?: "",
                        debug["volume_is_stable"] ?: "",
                        debug["volume_variation_ratio"] ?: "",
                        debug["volume_iqr_ratio"] ?: "",
                        debug["volume_mad_ratio"] ?: "",
                        debug["volume_drift_ratio"] ?: "",
                        debug["recent_useful_points_growth_ratio"] ?: "",
                        debug["recent_volume_delta_ratio"] ?: "",
                        debug["scale_validation_score"] ?: "",
                        debug["reference_expected_m"] ?: "",
                        debug["reference_observed_m"] ?: "",
                        debug["reference_relative_error"] ?: "",
                        debug["reference_status"] ?: "",
                        debug["reference_notes"] ?: "",
                            debug["reference_observation_source"] ?: "",
                            debug["auto_completion_candidate"] ?: "",
                            result?.visualCount?.toString() ?: "",
                            result?.sessionCount?.toString() ?: "",
                            result?.segmentedCount?.toString() ?: "",
                            result?.volumeInputCount?.toString() ?: "",
                            result?.reviewCount?.toString() ?: "",
                            result?.sourceOfVolumePoints ?: "",
                            debug["source_of_volume_points_official"] ?: "",
                            fallbackReasons
                        ).joinToString(",") { csvSafe(it) } + "\n"
                    )
            }
            file.absolutePath
        } catch (e: Exception) {
            Log.e("CsvExporter", "Error saving CSV: ${e.message}")
            null
        }
    }

    fun saveReportToUri(
        destinationUri: Uri,
        uiState: ScanUiState,
        result: ScanSessionResult?,
        appVersionDisplay: String,
        lat: Double,
        lon: Double
    ): Boolean {
        val ts = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        return try {
            context.contentResolver.openOutputStream(destinationUri, "wt")?.bufferedWriter()?.use { writer ->
                writer.append(
                        "Timestamp,AppVersion,Lat,Lon,Distance,StereoVol,NetVol,StereoVolFinal,NetVolFinal,VolumeGeometricBase,VolumeGeometricCorrected,VolumeStereoTemporalSmoothed,StereoObservationFactor,EdgeRecoveryFactor,TemporalAlpha,TemporalBoundedRatio,VolumeNetEstimate,VolumeRouteARefinedRaw,VolumeRouteARefinedCorrected,VolumeRouteARefinedStereo,VolumeRouteARefinedNet,VolumeRouteBDetectedRaw,VolumeRouteBDetectedCorrected,VolumeRouteBDetectedStereo,VolumeRouteBDetectedNet,RouteAInputCount,RouteBInputCount,RouteASource,RouteBSource,RouteBMinusRouteA,RouteBVsRouteARatio,ABTestEnabled,TotalPoints,RawPoints,AcceptedPoints,GroundPoints,NonGroundPoints,PilePoints,DetectionPilePointsCount,RefinedPilePointsCount,Clusters,SelectedCluster,DetectionQuality,DetectionConfidence,DetectionReasons,DominanceScore,HeightScore,CompactnessScore,ObserverConsistencyScore,GroundSeparationScore,EstimatedHeight,BoundingBox,BoundingBoxFinal,MaxHeight,P95Height,MeanHeight,VerticalCoverageScore,VerticalWeakBands,VerticalReasons,TopCoverageScore,TopPointCount,TopBandDensity,TopCoverageState,TopCoverageTrend,TopCoverageTemporalStability,TrajectoryQualityScore,VolumeStabilityScore,VolumeIsStable,VolumeVariationRatio,VolumeIqrRatio,VolumeMadRatio,VolumeDriftRatio,RecentUsefulPointsGrowthRatio,RecentVolumeDeltaRatio,ScaleValidationScore,ReferenceExpectedM,ReferenceObservedM,ReferenceRelativeError,ReferenceStatus,ReferenceNotes,ReferenceObservationSource,AutoCompletionCandidate,VisualCount,SessionCount,SegmentedCount,VolumeInputCount,ReviewCount,SourceOfVolumePoints,SourceOfVolumePointsOfficial,FallbackReasons\n"
                )

                val debug = result?.detectionDebugInfo.orEmpty()
                val fallbackReasons = result?.pileDetectionReasons?.joinToString(" | ") ?: ""
                val boundingBox = debug["bounding_box"] ?: ""
                val stereoVolForExport = result?.volume ?: uiState.stereoVolume
                val netVolForExport = result?.netVolumeEstimate ?: uiState.netVolume

                writer.append(
                        listOf(
                        ts,
                        appVersionDisplay,
                        lat.toString(),
                        lon.toString(),
                        uiState.distance.toString(),
                        stereoVolForExport.toString(),
                        netVolForExport.toString(),
                        debug["volume_stereo_final"] ?: stereoVolForExport.toString(),
                        netVolForExport.toString(),
                        debug["volume_geometric_raw"] ?: "",
                        debug["volume_geometric_corrected"] ?: "",
                        debug["volume_stereo_smoothed"] ?: "",
                        debug["volume_stereo_observation_factor"] ?: "",
                        debug["volume_edge_recovery_factor"] ?: "",
                        debug["volume_temporal_alpha"] ?: "",
                        debug["volume_temporal_bounded_ratio_vs_corrected"] ?: "",
                        debug["volume_net_estimate"] ?: "",
                        result?.volumeRouteARefinedRaw?.toString() ?: "",
                        result?.volumeRouteARefinedCorrected?.toString() ?: "",
                        result?.volumeRouteARefinedStereo?.toString() ?: "",
                        result?.volumeRouteARefinedNet?.toString() ?: "",
                        result?.volumeRouteBDetectedRaw?.toString() ?: "",
                        result?.volumeRouteBDetectedCorrected?.toString() ?: "",
                        result?.volumeRouteBDetectedStereo?.toString() ?: "",
                        result?.volumeRouteBDetectedNet?.toString() ?: "",
                        result?.routeAInputCount?.toString() ?: "",
                        result?.routeBInputCount?.toString() ?: "",
                        result?.routeASource ?: "",
                        result?.routeBSource ?: "",
                        debug["route_b_minus_route_a"] ?: "",
                        debug["route_b_vs_route_a_ratio"] ?: "",
                        debug["ab_test_enabled"] ?: "",
                        result?.pointsCount?.toString() ?: "",
                        debug["raw_points"] ?: "",
                        debug["accepted_points"] ?: "",
                        debug["ground_points"] ?: "",
                        debug["non_ground_points"] ?: "",
                        debug["pile_points"] ?: "",
                        debug["detection_pile_points_count"] ?: "",
                        debug["refined_pile_points_count"] ?: "",
                        debug["clusters"] ?: "",
                        debug["selected_cluster"] ?: "",
                        result?.pileDetectionQuality ?: "",
                        result?.pileDetectionConfidence?.toString() ?: "",
                        debug["detection_reasons"] ?: "",
                        debug["dominance_score"] ?: "",
                        debug["height_score"] ?: "",
                        debug["compactness_score"] ?: "",
                        debug["observer_consistency_score"] ?: "",
                        debug["ground_separation_score"] ?: "",
                        debug["estimated_height"] ?: "",
                        boundingBox,
                        debug["bounding_box_final"] ?: "",
                        debug["max_height"] ?: "",
                        debug["p95_height"] ?: "",
                        debug["mean_height"] ?: "",
                        debug["vertical_coverage_score"] ?: "",
                        debug["vertical_weak_bands"] ?: "",
                        debug["vertical_reasons"] ?: "",
                        debug["top_coverage_score"] ?: "",
                        debug["top_point_count"] ?: "",
                        debug["top_band_density"] ?: "",
                        debug["top_coverage_state"] ?: "",
                        debug["top_coverage_trend"] ?: "",
                        debug["top_coverage_temporal_stability"] ?: "",
                        debug["trajectory_quality_score"] ?: "",
                        debug["volume_stability_score"] ?: "",
                        debug["volume_is_stable"] ?: "",
                        debug["volume_variation_ratio"] ?: "",
                        debug["volume_iqr_ratio"] ?: "",
                        debug["volume_mad_ratio"] ?: "",
                        debug["volume_drift_ratio"] ?: "",
                        debug["recent_useful_points_growth_ratio"] ?: "",
                        debug["recent_volume_delta_ratio"] ?: "",
                        debug["scale_validation_score"] ?: "",
                        debug["reference_expected_m"] ?: "",
                        debug["reference_observed_m"] ?: "",
                        debug["reference_relative_error"] ?: "",
                        debug["reference_status"] ?: "",
                        debug["reference_notes"] ?: "",
                            debug["reference_observation_source"] ?: "",
                            debug["auto_completion_candidate"] ?: "",
                            result?.visualCount?.toString() ?: "",
                            result?.sessionCount?.toString() ?: "",
                            result?.segmentedCount?.toString() ?: "",
                            result?.volumeInputCount?.toString() ?: "",
                            result?.reviewCount?.toString() ?: "",
                            result?.sourceOfVolumePoints ?: "",
                            debug["source_of_volume_points_official"] ?: "",
                            fallbackReasons
                        ).joinToString(",") { csvSafe(it) } + "\n"
                    )
            } != null
        } catch (e: Exception) {
            Log.e("CsvExporter", "Error saving CSV to Uri: ${e.message}")
            false
        }
    }

    private fun csvSafe(value: String): String {
        val escaped = value.replace("\"", "\"\"")
        return if (escaped.contains(',') || escaped.contains('\n')) "\"$escaped\"" else escaped
    }
}
