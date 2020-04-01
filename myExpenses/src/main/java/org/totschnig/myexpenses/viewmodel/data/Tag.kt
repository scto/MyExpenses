package org.totschnig.myexpenses.viewmodel.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Tag(val id: Long, val label: String, var selected: Boolean): Parcelable {
    override fun toString() = label
}