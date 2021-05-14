package com.mobile.woodmeas.helpers

import com.mobile.woodmeas.model.Settings
import java.io.File

object FileManager {
    fun deletePdfPackagesWoodFiles(pathDirectory: String) {
        val directory = File(pathDirectory)
        directory.listFiles()?.forEach { file ->
            if (file.name.contains(Settings.PDF_FILE_NAME_PREFIX)) {
                file.delete()
            }
        }
    }
}