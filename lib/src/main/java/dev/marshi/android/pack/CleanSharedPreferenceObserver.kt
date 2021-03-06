package dev.marshi.android.pack

import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent

class CleanSharedPreferenceObserver(
    private val sharedPreferences: SharedPreferences
) : LifecycleObserver {
  @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
  fun onDestroy() {
    sharedPreferences.edit {
      clear()
    }
  }
}
