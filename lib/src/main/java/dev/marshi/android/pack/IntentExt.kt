package dev.marshi.android.pack

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Parcelable
import androidx.core.content.edit
import androidx.lifecycle.LifecycleOwner
import java.io.Serializable
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.jvm.javaField
import kotlin.reflect.jvm.javaGetter

fun Intent.putPackedExtra(context: Context, key: String, content: String) {
  val sharedPreferences = context.getSharedPreferences("pref", Context.MODE_PRIVATE)
  sharedPreferences.edit {
    putString(key, content)
  }
}

fun <T> Intent.putPackedExtra(context: Context, key: String, content: T) where T : Serializable {
  content::class.memberProperties.forEach {
    if (it.findAnnotation<Pack>() != null) {
      it.isAccessible = true
      val value = it.javaGetter?.invoke(content) ?: return@forEach
      if (Serializable::class.isInstance(value) && !isPrimitive(value)) {
        putPackedExtra(context, "${key}_${it.name}", value as Serializable)
        return@forEach
      }
      val nullValue = nullValue(value)
      nullValue?.let { v ->
        it.javaField?.set(content, v)
      }
      val sharedPreferences = context.getSharedPreferences("pref", Context.MODE_PRIVATE)
      sharedPreferences.edit {
        put("${key}_${it.name}", value)
      }
    }
  }
  putExtra(key, content)
}

fun isPrimitive(value: Any?) =
  String::class.isInstance(value) ||
      Int::class.isInstance(value) ||
      Float::class.isInstance(value) ||
      Double::class.isInstance(value) ||
      Boolean::class.isInstance(value) ||
      Long::class.isInstance(value)


fun nullValue(value: Any) = when {
  String::class.isInstance(value) -> ""
  Int::class.isInstance(value) -> 0
  Float::class.isInstance(value) -> 0f
  Double::class.isInstance(value) -> 0.0
  Boolean::class.isInstance(value) -> false
  Long::class.isInstance(value) -> 0L
  else -> null
}

fun SharedPreferences.Editor.put(key: String, value: Any) {
  when {
    String::class.isInstance(value) -> putString(key, value as String)
    Int::class.isInstance(value) -> putInt(key, value as Int)
    Float::class.isInstance(value) -> putFloat(key, value as Float)
    Double::class.isInstance(value) -> putFloat(key, value as Float)
    Boolean::class.isInstance(value) -> putBoolean(key, value as Boolean)
    Long::class.isInstance(value) -> putLong(key, value as Long)
    else -> {
    }
  }
}

fun SharedPreferences.get(key: String, value: Any?) = when {
  String::class.isInstance(value) -> getString(key, "")
  Int::class.isInstance(value) -> getInt(key, 0)
  Float::class.isInstance(value) -> getFloat(key, 0f)
  Double::class.isInstance(value) -> getFloat(key, 0f)
  Boolean::class.isInstance(value) -> getBoolean(key, false)
  Long::class.isInstance(value) -> getLong(key, 0L)
  else -> null
}

fun <T, R : Serializable> Intent.getPackedExtra(
  context: T,
  key: String
): R where T : Context, T : LifecycleOwner {
  val sharedPreferences = context.getSharedPreferences("pref", Context.MODE_PRIVATE)
  val content = getSerializableExtra(key)
  content::class.memberProperties.forEach {
    if (it.findAnnotation<Pack>() != null) {
      val v = it.javaGetter?.invoke(content)
      it.isAccessible = true
      val value = if (Serializable::class.isInstance(v) && !isPrimitive(v)) {
        getPackedExtra(context, "${key}_${it.name}")
      } else {
        sharedPreferences.get("${key}_${it.name}", v)
      }
      it.javaField?.set(content, value)
    }
  }
  context.lifecycle.addObserver(CleanSharedPreferenceObserver(sharedPreferences, key))
  return content as R
}

fun <T> Intent.getPackedExtraString(
  context: T,
  key: String
): String? where T : Context, T : LifecycleOwner {
  val sharedPreferences = context.getSharedPreferences("pref", Context.MODE_PRIVATE)
  val string = sharedPreferences.getString(key, null)
  context.lifecycle.addObserver(CleanSharedPreferenceObserver(sharedPreferences, key))
  return string
}
