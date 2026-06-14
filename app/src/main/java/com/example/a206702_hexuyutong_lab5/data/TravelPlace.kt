package com.example.travelmap

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity — represents one row in the `travel_places` table.
 *
 * Each property maps to a column. `id` is the primary key and is auto-generated
 * by Room, so newly created records can simply leave it at its default value.
 */
@Entity(tableName = "travel_places")
data class TravelPlace(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val address: String,
    val date: String,
    // `desc` is a reserved word in SQL, so store it under the column name `description`.
    @ColumnInfo(name = "description")
    val desc: String
)
