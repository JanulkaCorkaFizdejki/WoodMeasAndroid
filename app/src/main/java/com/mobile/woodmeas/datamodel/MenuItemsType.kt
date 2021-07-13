package com.mobile.woodmeas.datamodel

enum class MenuItemsType {
    LOG     { override fun chunkFileName(): String = "log_bundle" },
    STACK   { override fun chunkFileName(): String = "stack_bundle" },
    PLANK   { override fun chunkFileName(): String = "plank_bundle" };

    abstract fun chunkFileName(): String
}