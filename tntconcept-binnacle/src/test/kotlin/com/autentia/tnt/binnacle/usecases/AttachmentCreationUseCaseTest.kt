package com.autentia.tnt.binnacle.usecases

import com.autentia.tnt.AppProperties
import com.autentia.tnt.binnacle.config.createAttachmentInfoWithFilenameAndMimetype
import com.autentia.tnt.binnacle.converters.AttachmentInfoConverter
import com.autentia.tnt.binnacle.exception.AttachmentMimeTypeNotSupportedException
import com.autentia.tnt.binnacle.repositories.AttachmentFileRepository
import com.autentia.tnt.binnacle.repositories.AttachmentInfoRepository
import io.micronaut.security.authentication.ClientAuthentication
import io.micronaut.security.utils.DefaultSecurityService
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.mockito.Mockito.*
import org.mockito.kotlin.whenever
import java.io.File
import java.util.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AttachmentCreationUseCaseTest {
    private val securityService = mock<DefaultSecurityService>()
    private val attachmentFileRepository = mock<AttachmentFileRepository>()
    private val attachmentInfoRepository = mock<AttachmentInfoRepository>()
    private val attachmentInfoConverter = AttachmentInfoConverter()
    private val appProperties = AppProperties().apply {
        files.evidencesPath = "src/test/resources/attachments_test/evidences"
        files.supportedMimeTypes = mapOf(
            Pair("image/png", "png"),
        )
    }
    private val attachmentCreationUseCase =
        AttachmentCreationUseCase(
            securityService,
            attachmentFileRepository,
            attachmentInfoRepository,
            attachmentInfoConverter,
            appProperties
        )

    @BeforeEach
    fun setUp() {
        whenever(securityService.authentication).thenReturn(Optional.of(authenticationUser))
    }

    @Test
    fun `store attachment info when attachment is valid`() {

        whenever(attachmentInfoRepository.save(SUPPORTED_ATTACHMENT_INFO_ENTITY.copy(id = null))).thenReturn(
            SUPPORTED_ATTACHMENT_INFO_ENTITY
        )

        val savedAttachment = attachmentCreationUseCase.storeAttachment(
            IMAGE_BYTEARRAY,
            SUPPORTED_ATTACHMENT_INFO_ENTITY.fileName,
            SUPPORTED_ATTACHMENT_INFO_ENTITY.mimeType
        )

        assertNotNull(savedAttachment.id)
        assertTrue(savedAttachment.isTemporary)
        assertEquals(SUPPORTED_ATTACHMENT_INFO_ENTITY.fileName, savedAttachment.fileName)
    }

    @Test
    fun `throws AttachmentMimeTypeNotSupportedException when mimetype is not valid`() {
        assertThrows<AttachmentMimeTypeNotSupportedException> {
            attachmentCreationUseCase.storeAttachment(
                IMAGE_BYTEARRAY,
                UNSUPPORTED_INFO_ENTITY.fileName,
                UNSUPPORTED_INFO_ENTITY.mimeType
            )
        }
    }

    companion object {
        val IMAGE_BYTEARRAY =
            File("src/test/resources/attachments_test/evidences/7a5a56cf-03c3-42fb-8c1a-91b4cbf6b42b.jpeg").readBytes()
        private val SUPPORTED_ATTACHMENT_INFO_ENTITY =
            createAttachmentInfoWithFilenameAndMimetype(mimeType = "image/png")
        private val UNSUPPORTED_INFO_ENTITY = createAttachmentInfoWithFilenameAndMimetype(mimeType = "application/json")

        private const val userId = 1L
        private val authenticationUser =
            ClientAuthentication(userId.toString(), mapOf("roles" to listOf("user")))

    }
}