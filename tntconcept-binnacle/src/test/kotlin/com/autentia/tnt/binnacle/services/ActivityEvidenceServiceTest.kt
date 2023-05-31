package com.autentia.tnt.binnacle.services

import com.autentia.tnt.AppProperties
import com.autentia.tnt.binnacle.entities.dto.EvidenceDTO
import com.autentia.tnt.binnacle.exception.InvalidEvidenceMimeTypeException
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.io.File
import java.time.LocalDate
import java.time.ZoneId
import java.util.*

@MicronautTest
internal class ActivityEvidenceServiceTest {
    private val appProperties = AppProperties().apply {
        files.evidencesPath = "/tmp"
        files.supportedMimeTypes = mapOf(
            Pair("application/pdf", "pdf"),
            Pair("image/jpg", "jpg"),
            Pair("image/jpeg", "jpeg"),
            Pair("image/png", "png"),
            Pair("image/gif", "gif")
        )
    }
    private val date = Date.from(LocalDate.parse("2022-04-08").atStartOfDay(ZoneId.systemDefault()).toInstant())
    private val activityEvidenceService = ActivityEvidenceService(appProperties)

    @Nested
    inner class StoreImage {
        @ParameterizedTest
        @ValueSource(
            strings = [
                "application/pdf",
                "image/png",
                "image/jpg",
                "image/jpeg",
                "image/gif"
            ]
        )
        fun `should create a new file with the decoded value of the image`(mimeType: String) {
            val evidence = EvidenceDTO.from("data:$mimeType;base64,SGVsbG8gV29ybGQh")
            activityEvidenceService.storeActivityEvidence(2L, evidence, date)

            val expectedExtension = getExtensionForMimeType(mimeType)
            val expectedEvidenceFilename = "/tmp/2022/4/2.$expectedExtension"
            val file = File(expectedEvidenceFilename)
            val content = File(expectedEvidenceFilename).readText()
            assertThat(content).isEqualTo("Hello World!")

            file.delete()
        }

        private fun getExtensionForMimeType(mimeType: String): String {
            return appProperties.files.supportedMimeTypes[mimeType] ?: throw InvalidEvidenceMimeTypeException(mimeType)
        }
    }

    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @Nested
    inner class DeleteImage {
        @ParameterizedTest
        @ValueSource(
            strings = [
                "jpg",
                "jpeg",
                "pdf",
                "png",
                "gif"
            ]
        )
        fun `should delete evidence with any valid file type`(fileExtension: String) {
            val file = File("/tmp/2022/4/2.$fileExtension")
            file.createNewFile()

            val result = activityEvidenceService.deleteActivityEvidence(2L, date)

            assertTrue(result)
            assertFalse(file.exists())
        }

        @Test
        fun `should return false when the file couldn't be deleted`() {
            val result = activityEvidenceService.deleteActivityEvidence(2L, date)

            assertFalse(result)
        }
    }

    @Nested
    inner class RetrievalImage {
        @ParameterizedTest
        @ValueSource(
            strings = [
                "jpg",
                "jpeg",
                "pdf",
                "png",
                "gif"
            ]
        )
        fun `should return the stored image of the activity in base 64`(fileExtension: String) {
            val file = File("/tmp/2022/4/2.$fileExtension")
            file.writeText("Hello World!")

            val result = activityEvidenceService.getActivityEvidenceAsBase64String(2L, date)

            assertThat(result).isEqualTo("SGVsbG8gV29ybGQh")

            file.delete()
        }
    }
}
