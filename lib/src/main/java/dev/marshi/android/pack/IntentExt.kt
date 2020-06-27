package dev.marshi.android.pack

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.os.Parcelable
import androidx.core.content.edit
import androidx.lifecycle.LifecycleOwner
import com.google.gson.Gson
import java.io.Serializable
import java.lang.ClassCastException
import java.lang.IllegalArgumentException
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.jvm.javaField
import kotlin.reflect.jvm.javaGetter

fun Intent.putPackedExtra(context: Context, key: String, content: Any) {
  val memberProperties = content::class.memberProperties
  val allNotPackProperties = memberProperties.all { it.findAnnotation<Pack>() == null }
  memberProperties.forEach { prop ->
    if (allNotPackProperties || prop.findAnnotation<Pack>() != null) {
      prop.isAccessible = true
      val value = prop.javaGetter?.invoke(content) ?: return@forEach
      if (value !is Serializable) {
        return@forEach
      }
      val nullValue = nullValue(value)
      nullValue?.let { v ->
        prop.javaField?.set(content, nullValue)
      }
      if (isPrimitive(value)) {
        val sharedPreferences = context.getSharedPreferences("pref", Context.MODE_PRIVATE)
        sharedPreferences.edit { put("${key}_${prop.name}", value) }
        return@forEach
      } else {
        putPackedExtra(context, "${key}_${prop.name}", value)
        return@forEach
      }
    }
  }
  when (content) {
    is Serializable -> putExtra(key, content)
    is Parcelable -> putExtra(key, content)
    else -> throw IllegalArgumentException()
  }
}

fun <T, Any> Intent.getPackedExtra(
  context: T,
  key: String
): Any? where T : Context, T : LifecycleOwner {
  val sharedPreferences = context.getSharedPreferences("pref", Context.MODE_PRIVATE)
  val content = getSerializableExtra(key) ?: getParcelableExtra<Parcelable>(key) ?: return null

  content::class.memberProperties.forEach { prop ->
    val v = prop.javaGetter?.invoke(content)
    val klass = prop.javaGetter?.returnType?.kotlin ?: return@forEach
    prop.isAccessible = true
    val value = if (isPrimitive(v)) {
      sharedPreferences.get("${key}_${prop.name}", v)
    } else {
      getPackedExtra(context, "${key}_${prop.name}")
    }
    value?.let {
      prop.javaField?.set(content, it)
    }
  }
  context.lifecycle.addObserver(CleanSharedPreferenceObserver(sharedPreferences, key))
  return content as Any?
}

private fun isPrimitive(value: Any?) =
  String::class.isInstance(value) ||
      Int::class.isInstance(value) ||
      Float::class.isInstance(value) ||
      Double::class.isInstance(value) ||
      Boolean::class.isInstance(value) ||
      Long::class.isInstance(value)


private fun nullValue(value: Any) = when {
  String::class.isInstance(value) -> ""
  Int::class.isInstance(value) -> 0
  Float::class.isInstance(value) -> 0f
  Double::class.isInstance(value) -> 0.0
  Boolean::class.isInstance(value) -> false
  Long::class.isInstance(value) -> 0L
  else -> null
}

private fun SharedPreferences.Editor.put(key: String, value: Any) {
  when {
    String::class.isInstance(value) -> putString(key, value as String)
    Int::class.isInstance(value) -> putInt(key, value as Int)
    Float::class.isInstance(value) -> putFloat(key, value as Float)
    Double::class.isInstance(value) -> putFloat(key, value as Float)
    Boolean::class.isInstance(value) -> putBoolean(key, value as Boolean)
    Long::class.isInstance(value) -> putLong(key, value as Long)
    else -> putString(key, Gson().toJson(value))
  }
}

private fun SharedPreferences.get(key: String, value: Any?) = when {
  String::class.isInstance(value) -> getString(key, "")
  Int::class.isInstance(value) -> getInt(key, 0)
  Float::class.isInstance(value) -> getFloat(key, 0f)
  Double::class.isInstance(value) -> getFloat(key, 0f)
  Boolean::class.isInstance(value) -> getBoolean(key, false)
  Long::class.isInstance(value) -> getLong(key, 0L)
  else -> null
}
