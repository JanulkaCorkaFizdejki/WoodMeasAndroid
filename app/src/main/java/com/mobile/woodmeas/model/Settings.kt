package com.mobile.woodmeas.model

object Settings {
    object VolumeCalculatorView {
        var  barkOn: Boolean = false
        var  currentTree: Int = 0
        var  woodPackageFromSelectIndex: Int = -1
    }

    object PackagesSelect {
        var id: Int = 0
    }

    object IntentsPutValues {
        const val WOOD_PACKAGE_ID = "wood_package_id"
    }

    const val PDF_FILE_NAME_PREFIX = "wood_meas_package_log"
}