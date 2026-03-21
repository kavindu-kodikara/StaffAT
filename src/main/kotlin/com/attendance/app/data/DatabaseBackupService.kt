package com.attendance.app.data

import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object DatabaseBackupService {
    private val dbFile = File("attendance.db")

    fun exportDatabase(destination: File): Boolean {
        return try {
            if (dbFile.exists()) {
                Files.copy(dbFile.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun importDatabase(source: File): Boolean {
        return try {
            if (source.exists() && source.length() > 0) {
                // Ensure atomic overwrite
                Files.copy(source.toPath(), dbFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun getDefaultExportFileName(): String {
        val dateString = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm"))
        return "attendance_backup_$dateString.db"
    }
}
