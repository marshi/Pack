package dev.marshi.android.pack

import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import kotlinx.serialization.ImplicitReflectionSerializer

class MainActivity : AppCompatActivity() {
  @OptIn(ImplicitReflectionSerializer::class)
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    val createBitmap = Bitmap.createBitmap(10, 10, Bitmap.Config.ALPHA_8)
    val message = Gson().toJson(createBitmap)
    println(message)
    val java = Bitmap::class.java
    val bitmap = Gson().fromJson(message, java)
    println(bitmap)
    findViewById<Button>(R.id.button).setOnClickListener {
      startActivity(MainActivity2.creteIntent(this))
    }
  }
}
