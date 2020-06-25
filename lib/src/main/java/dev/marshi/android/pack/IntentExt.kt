package dev.marshi.android.pack

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Parcelable
import androidx.core.content.edit
import androidx.lifecycle.LifecycleOwner
import com.google.gson.Gson
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

fun Intent.putPackedExtra(context: Context, key: String, content: Any, root: Boolean = true) {
  val memberProperties = content::class.memberProperties
  memberProperties.forEach {
    if (it.findAnnotation<Pack>() != null) {
      it.isAccessible = true
      val value = it.javaGetter?.invoke(content) ?: return@forEach
      val nullValue = nullValue(value)
//      nullValue?.let { v ->
      it.javaField?.set(content, nullValue)
//      }
      if (isPrimitive(value)) {
        val sharedPreferences = context.getSharedPreferences("pref", Context.MODE_PRIVATE)
        sharedPreferences.edit { put("${key}_${it.name}", value) }
        return@forEach
      } else {
        putPackedExtra(context, "${key}_${it.name}", value, false)
        return@forEach
      }
    }
  }
//  if (!root) { // @packかつオブジェクトかつpropertyの@packは退避済.
//    val message = Gson().toJson(content)
//    val sharedPreferences = context.getSharedPreferences("pref", Context.MODE_PRIVATE)
//    sharedPreferences.edit { putString("$key", message) }
//    return
  if(root) {
    when (content) {
      is Serializable -> putExtra(key, content)
      is Parcelable -> putExtra(key, content)
    }
  }
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
    else -> putString(key, Gson().toJson(value))
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

fun <T, Any> Intent.getPackedExtra(
  context: T,
  key: String
): Any? where T : Context, T : LifecycleOwner {
  val sharedPreferences = context.getSharedPreferences("pref", Context.MODE_PRIVATE)
  val content = getSerializableExtra(key) ?: getParcelableExtra<Parcelable>(key) ?: return null
  content::class.memberProperties.forEach {
    val v = it.javaGetter?.invoke(content)
    it.isAccessible = true
    val value = if (it.findAnnotation<Pack>() != null) {
      if (isPrimitive(v)) {
        sharedPreferences.get("${key}_${it.name}", v)
      } else {
        getPackedExtra(context, "${key}_${it.name}")
      }
    } else {
      val message = sharedPreferences.getString("${key}_${it.name}", null)
      Gson().fromJson(message, v!!::class.java)
    }
    it.javaField?.set(content, value)
  }
  context.lifecycle.addObserver(CleanSharedPreferenceObserver(sharedPreferences, key))
  return content as Any?
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
