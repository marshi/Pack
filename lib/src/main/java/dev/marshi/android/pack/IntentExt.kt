package dev.marshi.android.pack

import android.content.Context
import android.content.Intent
import android.os.Parcelable
import androidx.core.content.edit
import androidx.lifecycle.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.stringify
import java.io.Serializable
import java.lang.IllegalStateException
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

fun <T> Intent.putPackedExtra(context: Context, key: String, content: T) where T : Parcelable {
  content::class.memberProperties.forEach {
    if (it.findAnnotation<Pack>() != null) {
      it.isAccessible = true
      val value = it.javaGetter?.invoke(content) ?: IllegalStateException()
      it.javaField?.set(content, "")
      val sharedPreferences = context.getSharedPreferences("pref", Context.MODE_PRIVATE)
      sharedPreferences.edit {
        putString(key, value as String) // ほんとはintentのkeyと違うkeyにするべき.
      }
    }
  }
  putExtra(key, content)
}


fun <T, R> Intent.getPackedExtra(
  context: T,
  key: String
): R where T : Context, T : LifecycleOwner, R : Parcelable {
  val sharedPreferences = context.getSharedPreferences("pref", Context.MODE_PRIVATE)
  val observer = object : LifecycleObserver {
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
      sharedPreferences.edit {
        remove(key)
      }
    }
  }
  val value = sharedPreferences.getString(key, null)
  val content = getParcelableExtra<R>(key)
  content::class.memberProperties.forEach {
    if (it.findAnnotation<Pack>() != null) {
      it.isAccessible = true
      it.javaField?.set(content, value)
    }
  }
  context.lifecycle.addObserver(observer)
  return content
}

fun <T> Intent.getPackedExtraString(
  context: T,
  key: String
): String? where T : Context, T : LifecycleOwner {

  val sharedPreferences = context.getSharedPreferences("pref", Context.MODE_PRIVATE)
  val string = sharedPreferences.getString(key, null)
  val observer = object : LifecycleObserver {
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
      sharedPreferences.edit {
        remove(key)
      }
    }
  }
  context.lifecycle.addObserver(observer)
  return string
}
