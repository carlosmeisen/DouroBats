package pt.dourobats.app.core.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.datetime.LocalDateTime
import pt.dourobats.app.core.domain.model.TrainingSession
import pt.dourobats.app.core.domain.repository.TrainingRepository

/**
 * Fake implementation of TrainingRepository for MVP
 * Will be replaced with real implementation later
 */
class FakeTrainingRepository : TrainingRepository {

    private val fakeSessions = listOf(
        TrainingSession(
            id = "1",
            title = "Tuesday Training",
            description = "Regular weekly training session",
            startTime = LocalDateTime(2025, 12, 30, 19, 0),
            endTime = LocalDateTime(2025, 12, 30, 21, 0),
            location = "Pavilhão Municipal",
            attendees = 12
        ),
        TrainingSession(
            id = "2",
            title = "Thursday Training",
            description = "Technical skills focus",
            startTime = LocalDateTime(2026, 1, 2, 19, 0),
            endTime = LocalDateTime(2026, 1, 2, 21, 0),
            location = "Pavilhão Municipal",
            attendees = 15
        )
    )

    override fun getTrainingSessions(): Flow<List<TrainingSession>> {
        return flowOf(fakeSessions)
    }

    override suspend fun getTrainingSessionById(id: String): TrainingSession? {
        return fakeSessions.find { it.id == id }
    }

    override suspend fun saveTrainingSession(session: TrainingSession) {
        // No-op for fake implementation
    }
}
