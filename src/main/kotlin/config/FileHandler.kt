package com.example.config

import com.example.Constants
import java.io.File
import java.io.InputStream
import java.util.UUID

object FileHandler {
    private const val PATH = "uploads"
    private const val BASE_URL = Constants.PICTURE_URL
    private const val MAX_FILE_SIZE : Long = 5*1024*1024
    private val allowedExtensions: Set<String> = setOf("jpg", "jpeg", "png", "pdf")

    init {
        val dir = File(PATH)
        if (!dir.exists()){
            dir.mkdirs()
        }
    }

    fun uploadSingleFile(stream : InputStream, fileName : String) : String{
        val tempBytes = stream.readBytes()
        validateFile(fileName, tempBytes)
        val sanitizedName = fileName.sanitizeFileName()
        val uniqueName = ensureUniqueName(File(PATH, sanitizedName))
        val targetFile = File(PATH, uniqueName)
        targetFile.writeBytes(tempBytes)
        return "$BASE_URL/$uniqueName"
    }

    fun uploadMultipleFile(files : List<Pair<InputStream, String>>): List<String>{
        val uploadUrls = mutableListOf<String>()
        try{
            files.forEach { (stream, name) ->
                val url = uploadSingleFile(stream, name)
                uploadUrls.add(url)
            }
            return uploadUrls
        }catch (e : Exception){
            uploadUrls.forEach { deleteFileByUrl(it) }
            throw e
        }
    }
    fun deleteFileByUrl(fileUrl: String): Boolean {
        if (!fileUrl.startsWith(BASE_URL)) return false
        val name = fileUrl.removePrefix("$BASE_URL/").sanitizeFileName()
        val file = File(PATH, name)
        return file.exists() && file.delete()
    }

    private fun validateFile(fileName: String, bytes: ByteArray) {
        val ext = fileName.substringAfterLast('.', "").lowercase()
        if (ext !in allowedExtensions) {
            throw IllegalArgumentException("Unsupported file extension: .$ext")
        }
        if (bytes.size > MAX_FILE_SIZE) {
            throw IllegalArgumentException("File $fileName exceeds size limit of ${MAX_FILE_SIZE / 1024 / 1024} MB")
        }
    }

    private fun ensureUniqueName(file: File): String {
        if (!file.exists()) return file.name
        val name = file.nameWithoutExtension
        val ext = file.extension
        return "$name-${UUID.randomUUID()}.$ext"
    }

    private fun String.sanitizeFileName(): String {
        return this.replace(Regex("[^a-zA-Z0-9_.-]"), "_")
    }

}