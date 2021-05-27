package com.mobile.woodmeas

interface AppActivityManager {
    fun loadView()
    fun removeItem(item: Int)
    fun goToActivity() {}
    fun goToActivity(id: Int) {}
}