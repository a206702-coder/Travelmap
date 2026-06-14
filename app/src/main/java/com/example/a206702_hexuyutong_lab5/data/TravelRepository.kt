package com.example.travelmap

import kotlinx.coroutines.flow.Flow

/**
 * Repository — the single layer the ViewModel talks to.
 *
 * It hides the DAO from the rest of the app, so the data source could later be
 * swapped (e.g. a network API) without touching the ViewModel or UI.
 */
class TravelRepository(private val travelDao: TravelDao) {

    val allPlaces: Flow<List<TravelPlace>> = travelDao.getAll()

    fun getPlace(id: Int): Flow<TravelPlace?> = travelDao.getById(id)

    suspend fun insert(place: TravelPlace) = travelDao.insert(place)

    suspend fun update(place: TravelPlace) = travelDao.update(place)

    suspend fun delete(place: TravelPlace) = travelDao.delete(place)
}
