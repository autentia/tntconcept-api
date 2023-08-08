package com.autentia.tnt.binnacle.usecases

import com.autentia.tnt.binnacle.config.createAttachmentInfo
import com.autentia.tnt.binnacle.exception.AttachmentNotFoundException
import com.autentia.tnt.binnacle.repositories.AttachmentFileRepository
import com.autentia.tnt.binnacle.repositories.AttachmentInfoRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever
import java.util.*
import java.util.Optional.empty

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AttachmentRetrievalUseCaseTest {

    private val attachmentRepository = mock<AttachmentInfoRepository> ()
    private val attachmentFileRepository = mock<AttachmentFileRepository> ()
    private val attachmentRetrievalUseCase = AttachmentRetrievalUseCase (attachmentRepository, attachmentFileRepository)


    @Test
    fun `retrieve attachment by uuid`() {
        whenever(attachmentRepository.findById(ATTACHMENT_UUID))
            .thenReturn(Optional.of(ATTACHMENT_INFO_ENTITY))
        whenever(attachmentFileRepository.getAttachment(ATTACHMENT_INFO_ENTITY.path, ATTACHMENT_INFO_ENTITY.type, ATTACHMENT_INFO_ENTITY.fileName))
            .thenReturn(IMAGE_RAW)

        val attachment = attachmentRetrievalUseCase.getAttachment(ATTACHMENT_UUID)
        assertEquals(ATTACHMENT_INFO, attachment.info)
        assertTrue(Arrays.equals(IMAGE_RAW, attachment.file))
    }

    @Test
    fun `throws AttachmentNotFoundException when getn an attachment by id that doesnt exists` (){
        whenever(attachmentRepository.findById(ATTACHMENT_UUID))
            .thenReturn(empty())

        assertThrows<AttachmentNotFoundException> {
            attachmentRetrievalUseCase.getAttachment(ATTACHMENT_UUID)
        }
    }



    companion object {
        private val ATTACHMENT_INFO_ENTITY = createAttachmentInfo()
        private val ATTACHMENT_INFO = ATTACHMENT_INFO_ENTITY.toDomain()

        private const val IMAGE_BASE64 =
            "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVQYV2NgYAAAAAMAAWgmWQ0AAAAASUVORK5CYII="
        private val ATTACHMENT_UUID = UUID.randomUUID()
        private val IMAGE_RAW = Base64.getDecoder().decode(IMAGE_BASE64)
    }
}