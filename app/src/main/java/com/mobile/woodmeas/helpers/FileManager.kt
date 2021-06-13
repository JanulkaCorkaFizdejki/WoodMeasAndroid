package com.mobile.woodmeas.helpers

import com.mobile.woodmeas.datamodel.MenuItemsType
import com.mobile.woodmeas.model.Settings
import java.io.File

object FileManager {
    fun deletePdfPackagesWoodFiles(pathDirectory: String) {
        val directory = File(pathDirectory)
        directory.listFiles()?.forEach { file ->
            if (file.name.contains(MenuItemsType.LOG.chunkFileName())) {
                file.delete()
                return
            }
            if (file.name.contains(MenuItemsType.PLANK.chunkFileName())) {
                file.delete()
                return
            }
            if (file.name.contains(MenuItemsType.STACK.chunkFileName())) {
                file.delete()
                return
            }
        }
    }
}