package dev.marshi.android.pack

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class Data constructor(
  @Pack val content: String,
  val int: Int
) : Parcelable
