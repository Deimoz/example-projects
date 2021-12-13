package com.example.fakeapi

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity
data class PostClass(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "body") val body: String,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "user_id") val userId: Int
)