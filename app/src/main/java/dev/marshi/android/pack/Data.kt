package dev.marshi.android.pack

import android.graphics.Bitmap
import android.os.Parcelable
import android.widget.ImageView
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

@Parcelize
data class Data(
  @Pack val int: Int,
  @Pack val content: String,
  @Pack val long: Long,
  @Pack val subData: SubData,
  val bitmap: Bitmap,
  val non: String
) : Parcelable, Serializable


@Parcelize
data class SubData(
  val str: String,
  val sub: SubDataParcelable
) : Parcelable, Serializable

@Parcelize
data class SubDataParcelable(
  @Pack val str: String
) : Parcelable, Serializable
