package dev.marshi.android.pack

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity2 : AppCompatActivity() {

  companion object {
    const val EXTRA = "EXTRA"

    fun creteIntent(context: Context) =
      Intent(context, MainActivity2::class.java).apply {
        putPackedExtra(context, EXTRA, "a".repeat(1000000))
      }

  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main2)
    val packedExtra = intent.getPackedExtra(this, EXTRA)
    println(packedExtra)
  }
}