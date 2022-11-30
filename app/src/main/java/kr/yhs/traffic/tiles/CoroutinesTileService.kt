/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package kr.yhs.traffic.tiles

import android.content.SharedPreferences
import androidx.wear.tiles.RequestBuilders.ResourcesRequest
import androidx.wear.tiles.RequestBuilders.TileRequest
import androidx.wear.tiles.ResourceBuilders.Resources
import androidx.wear.tiles.TileBuilders.Tile
import androidx.wear.tiles.TileService
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.guava.future
import kr.yhs.traffic.BaseEncryptedSharedPreference


abstract class CoroutinesTileService : TileService() {
    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)
    private lateinit var sharedPreferences: BaseEncryptedSharedPreference

    fun getPreferences(filename: String): SharedPreferences =
        sharedPreferences.getPreferences(filename)

    override fun onCreate() {
        super.onCreate()
        sharedPreferences = BaseEncryptedSharedPreference(this.baseContext)
        sharedPreferences.masterKeyBuild()
    }

    final override fun onTileRequest(
        requestParams: TileRequest
    ): ListenableFuture<Tile> = serviceScope.future {
        tileRequest(requestParams)
    }

    abstract suspend fun tileRequest(requestParams: TileRequest): Tile

    final override fun onResourcesRequest(requestParams: ResourcesRequest):
        ListenableFuture<Resources> = serviceScope.future {
        resourcesRequest(requestParams)
    }

    abstract suspend fun resourcesRequest(requestParams: ResourcesRequest): Resources

    override fun onDestroy() {
        super.onDestroy()
        serviceJob.cancel()
    }
}
