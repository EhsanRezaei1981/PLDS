/*
 * Copyright (C) 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package rezaei.mohammad.plds.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import rezaei.mohammad.plds.data.model.local.CheckInResponseEntity
import rezaei.mohammad.plds.data.model.local.Document
import rezaei.mohammad.plds.data.model.response.LoginResponse

@Database(
    entities = [LoginResponse.User::class, Document::class, CheckInResponseEntity::class],
    version = 5
)
@TypeConverters(Converters::class)
abstract class PLDSDatabase : RoomDatabase() {

    abstract fun PLDSDao(): PLDSDao

    companion object {
        @Volatile
        private var instance: PLDSDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also {
                instance = it
            }
        }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                PLDSDatabase::class.java,
                "PLDS.db"
            ).fallbackToDestructiveMigration()
                .build()
    }
}
