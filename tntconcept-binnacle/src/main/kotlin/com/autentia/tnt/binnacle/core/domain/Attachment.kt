package com.autentia.tnt.binnacle.core.domain

data class Attachment (
    val info: AttachmentInfo,
    val file: ByteArray,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Attachment

        if (info != other.info) return false
        return file.contentEquals(other.file)
    }

    override fun hashCode(): Int {
        var result = info.hashCode()
        result = 31 * result + file.contentHashCode()
        return result
    }

}