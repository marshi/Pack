package dev.marshi.android.pack

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import java.text.DateFormat

class MainActivity2 : AppCompatActivity() {

  companion object {
    const val EXTRA = "EXTRA"

    fun creteIntent(context: Context) =
      Intent(context, MainActivity2::class.java).apply {
        val content = Data(
          1,
          "a".repeat(10),
          1000L,
          SubData(
            null,
            SubDataParcelable(
              "1".repeat(1)
            )
          ),
          DateFormat.getDateInstance(),
          "non"
        )
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
