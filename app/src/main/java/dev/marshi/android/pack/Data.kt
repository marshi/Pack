package dev.marshi.android.pack

import android.os.Parcelable
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize

@Parcelize
class Data constructor(
  @Pack val content: String,
  val int: Int
) : Parcelable
