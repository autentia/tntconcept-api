package com.autentia.tnt.binnacle.repositories

import com.autentia.tnt.binnacle.entities.AttachmentInfo
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.time.LocalDateTime
import java.util.*

@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AttachmentInfoDaoIT {

    @Inject
    private lateinit var attachmentInfoDao: AttachmentInfoDao

    @Test
    fun `should save an attachment and find it by id`() {
        val savedAttachment = attachmentInfoDao.save(createAttachmentInfoEntity())

        val result = attachmentInfoDao.findById(savedAttachment.id)

        assertEquals(savedAttachment, result.get())
    }

    @Test
    fun `should find an attachment by id and user id`() {
        val savedAttachment = attachmentInfoDao.save(createAttachmentInfoEntity())

        val result = attachmentInfoDao.findByIdAndUserId(savedAttachment.id, savedAttachment.userId)

        assertEquals(savedAttachment, result.get())
    }

    @Test
    fun `should change isTemporary to correct value`() {
        attachmentInfoDao.updateIsTemporary(attachmentId, false)

        val result = attachmentInfoDao.findById(attachmentId).get()

        assertFalse(result.isTemporary)
    }

    @Test
    fun `should find all temporary attachments`() {
        val expectedSize = 1

        val temporaryAttachments = attachmentInfoDao.findByIsTemporaryTrue()

        assertEquals(expectedSize, temporaryAttachments.size)
        assertEquals(attachmentId, temporaryAttachments.get(0).id)
    }

    @Test
    fun `should delete all temporary attachments from a list`() {
        val savedAttachment = attachmentInfoDao.save(createAttachmentInfoEntity())

        attachmentInfoDao.delete(listOf(savedAttachment.id))

        assertThat(attachmentInfoDao.findById(attachmentId).isEmpty)
    }

    private fun createAttachmentInfoEntity() = AttachmentInfo(
            id = UUID.randomUUID(),
            userId = 1L,
            path = "/",
            fileName = "Evidence001",
            mimeType = "application/png",
            uploadDate = LocalDateTime.now().withSecond(0).withNano(0),
            isTemporary = false
    )

    companion object {
        private val attachmentId = UUID.fromString("4d3cbe3f-369f-11ee-99c2-0242ac180003")
    }

}