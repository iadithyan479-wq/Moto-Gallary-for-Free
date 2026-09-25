package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.AlbumItem
import com.example.data.model.FaceCluster
import com.example.data.model.PhotoItem
import kotlinx.coroutines.flow.Flow

@Dao
interface PhotoDao {

    @Query("SELECT * FROM photos WHERE isVaulted = 0 ORDER BY timestamp DESC")
    fun getPublicPhotos(): Flow<List<PhotoItem>>

    @Query("SELECT * FROM photos WHERE isVaulted = 1 ORDER BY timestamp DESC")
    fun getVaultPhotos(): Flow<List<PhotoItem>>

    @Query("SELECT * FROM photos WHERE isFavorite = 1 AND isVaulted = 0 ORDER BY timestamp DESC")
    fun getFavoritePhotos(): Flow<List<PhotoItem>>

    @Query("SELECT * FROM photos WHERE isVaulted = 0 AND faceNames LIKE '%' || :personName || '%' ORDER BY timestamp DESC")
    fun getPhotosForPerson(personName: String): Flow<List<PhotoItem>>

    @Query("SELECT * FROM photos WHERE isVaulted = 0 AND (title LIKE '%' || :query || '%' OR tags LIKE '%' || :query || '%' OR location LIKE '%' || :query || '%' OR faceNames LIKE '%' || :query || '%')")
    fun searchPhotos(query: String): Flow<List<PhotoItem>>

    @Query("SELECT * FROM face_clusters ORDER BY photoCount DESC")
    fun getAllFaceClusters(): Flow<List<FaceCluster>>

    @Query("SELECT * FROM albums ORDER BY photoCount DESC")
    fun getAllAlbums(): Flow<List<AlbumItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhotos(photos: List<PhotoItem>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhoto(photo: PhotoItem)

    @Update
    suspend fun updatePhoto(photo: PhotoItem)

    @Query("UPDATE photos SET isVaulted = :isVaulted WHERE id = :photoId")
    suspend fun setPhotoVaulted(photoId: String, isVaulted: Boolean)

    @Query("UPDATE photos SET isFavorite = :isFavorite WHERE id = :photoId")
    suspend fun setPhotoFavorite(photoId: String, isFavorite: Boolean)

    @Query("UPDATE photos SET isCloudSynced = 1, cloudSyncStatus = 'SYNCED' WHERE id = :photoId")
    suspend fun markCloudSynced(photoId: String)

    @Query("DELETE FROM photos WHERE id = :photoId")
    suspend fun deletePhoto(photoId: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFaceClusters(clusters: List<FaceCluster>)

    @Query("UPDATE face_clusters SET personName = :newName WHERE id = :clusterId")
    suspend fun renameFaceCluster(clusterId: String, newName: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlbums(albums: List<AlbumItem>)

    @Query("SELECT COUNT(*) FROM photos WHERE isVaulted = 0")
    suspend fun getPhotoCount(): Int
}
