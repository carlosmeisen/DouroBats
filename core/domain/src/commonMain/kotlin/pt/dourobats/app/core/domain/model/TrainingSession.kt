package pt.dourobats.app.core.domain.model

import kotlinx.datetime.LocalDateTime

/**
 * Domain model representing a training session
 */
data class TrainingSession(
    val id: String,
    val title: String,
    val description: String,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val location: String,
    val attendees: Int = 0
)
