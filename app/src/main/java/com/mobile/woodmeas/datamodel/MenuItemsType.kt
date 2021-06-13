package com.mobile.woodmeas.datamodel

enum class MenuItemsType {
    LOG     { override fun chunkFileName(): String = "log" },
    STACK   { override fun chunkFileName(): String = "stack" },
    PLANK   { override fun chunkFileName(): String = "plank" };

    abstract fun chunkFileName(): String
}