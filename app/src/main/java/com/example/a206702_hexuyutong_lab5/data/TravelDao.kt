package com.example.travelmap

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * DAO (Data Access Object) — defines the database operations.
 *
 * Read queries return a [Flow] so the UI is notified automatically whenever the
 * underlying table changes. Write operations are `suspend` functions so they run
 * off the main thread.
 */
@Dao
interface TravelDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(place: TravelPlace)

    @Update
    suspend fun update(place: TravelPlace)

    @Delete
    suspend fun delete(place: TravelPlace)

    @Query("SELECT * FROM travel_places ORDER BY id DESC")
    fun getAll(): Flow<List<TravelPlace>>

    @Query("SELECT * FROM travel_places WHERE id = :id")
    fun getById(id: Int): Flow<TravelPlace?>
}
