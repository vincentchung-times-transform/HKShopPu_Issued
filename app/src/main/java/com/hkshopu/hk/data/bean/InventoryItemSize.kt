package com.HKSHOPU.hk.data.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class InventoryItemSize (val size_name: String, val price: Int, val quantity: Int) : Parcelable