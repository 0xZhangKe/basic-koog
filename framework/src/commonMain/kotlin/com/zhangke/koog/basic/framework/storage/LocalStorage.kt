package com.zhangke.koog.basic.framework.storage

import com.russhwolf.settings.Settings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LocalStorage(private val settings: Settings) {

    suspend fun keys(): Set<String> = withContext(Dispatchers.IO) {
        settings.keys
    }

    suspend fun size(): Int = withContext(Dispatchers.IO) {
        settings.size
    }

    suspend fun clear() = withContext(Dispatchers.IO) {
        settings.clear()
    }

    suspend fun remove(key: String) = withContext(Dispatchers.IO) {
        settings.remove(key)
    }

    suspend fun hasKey(key: String): Boolean = withContext(Dispatchers.IO) {
        settings.hasKey(key)
    }

    suspend fun putInt(key: String, value: Int) = withContext(Dispatchers.IO) {
        settings.putInt(key, value)
    }

    suspend fun getInt(key: String, defaultValue: Int): Int = withContext(Dispatchers.IO) {
        settings.getInt(key, defaultValue)
    }

    suspend fun getIntOrNull(key: String): Int? = withContext(Dispatchers.IO) {
        settings.getIntOrNull(key)
    }

    suspend fun putLong(key: String, value: Long) = withContext(Dispatchers.IO) {
        settings.putLong(key, value)
    }

    suspend fun getLong(key: String, defaultValue: Long): Long = withContext(Dispatchers.IO) {
        settings.getLong(key, defaultValue)
    }

    suspend fun getLongOrNull(key: String): Long? = withContext(Dispatchers.IO) {
        settings.getLongOrNull(key)
    }

    suspend fun putString(key: String, value: String) = withContext(Dispatchers.IO) {
        settings.putString(key, value)
    }

    suspend fun getString(key: String, defaultValue: String): String = withContext(Dispatchers.IO) {
        settings.getString(key, defaultValue)
    }

    suspend fun getStringOrNull(key: String): String? = withContext(Dispatchers.IO) {
        settings.getStringOrNull(key)
    }

    suspend fun putFloat(key: String, value: Float) = withContext(Dispatchers.IO) {
        settings.putFloat(key, value)
    }

    suspend fun getFloat(key: String, defaultValue: Float): Float = withContext(Dispatchers.IO) {
        settings.getFloat(key, defaultValue)
    }

    suspend fun getFloatOrNull(key: String): Float? = withContext(Dispatchers.IO) {
        settings.getFloatOrNull(key)
    }

    suspend fun putDouble(key: String, value: Double) = withContext(Dispatchers.IO) {
        settings.putDouble(key, value)
    }

    suspend fun getDouble(key: String, defaultValue: Double): Double = withContext(Dispatchers.IO) {
        settings.getDouble(key, defaultValue)
    }

    suspend fun getDoubleOrNull(key: String): Double? = withContext(Dispatchers.IO) {
        settings.getDoubleOrNull(key)
    }

    suspend fun putBoolean(key: String, value: Boolean) = withContext(Dispatchers.IO) {
        settings.putBoolean(key, value)
    }

    suspend fun getBoolean(key: String, defaultValue: Boolean): Boolean = withContext(Dispatchers.IO) {
        settings.getBoolean(key, defaultValue)
    }

    suspend fun getBooleanOrNull(key: String): Boolean? = withContext(Dispatchers.IO) {
        settings.getBooleanOrNull(key)
    }
}
