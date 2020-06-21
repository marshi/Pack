package dev.marshi.android.pack

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.serialization.json.Json

class MainActivity2 : AppCompatActivity() {

  companion object {
    const val EXTRA = "EXTRA"

    fun creteIntent(context: Context) =
      Intent(context, MainActivity2::class.java).apply {
        val content = Data("a".repeat(1000000), 10)
        putPackedExtra(context, EXTRA, content)
      }

  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main2)
    val packedExtra = intent.getPackedExtra<AppCompatActivity, Data>(this, EXTRA)
    println(packedExtra)
  }
}