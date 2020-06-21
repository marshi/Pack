package dev.marshi.android.pack

import android.content.Context
import android.content.Intent
import android.os.Parcelable
import androidx.core.content.edit
import androidx.lifecycle.*
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaGetter

fun Intent.putPackedExtra(context: Context, key: String, content: String) {
  val sharedPreferences = context.getSharedPreferences("pref", Context.MODE_PRIVATE)
  sharedPreferences.edit {
    putString(key, content)
  }
}

fun <T : Parcelable> Intent.putPackedExtra(context: Context, key: String, content: T) {
  content::class.memberProperties.forEach {
    println(it.name)
    if (it.findAnnotation<Pack>() != null) {
      it.javaGetter?.invoke(content)
    }
  }
  val sharedPreferences = context.getSharedPreferences("pref", Context.MODE_PRIVATE)
  sharedPreferences.edit {
    putExtra(key, content)
  }
}

fun <T> Intent.getPackedExtra(
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
