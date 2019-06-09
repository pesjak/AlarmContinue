/*
 * Copyright 2016 Realm Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package primoz.com.alarmcontinue.model

import io.realm.Realm
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.concurrent.atomic.AtomicInteger

open class Song : RealmObject() {

    @PrimaryKey
    var id: Int = 0
    var secondsPlayed: Int? = null
    var name: String? = null
    var path: String? = null
    var size: Long = 0   //byte
    var bucketId: String? = null  //Directory ID
    var bucketName: String? = null  //Directory Name

    companion object {
        const val FIELD_ID = "id"
        private val INTEGER_COUNTER = AtomicInteger(0)

        fun createSong(
            realm: Realm,
            secondsPlayed: Int,
            name: String,
            path: String,
            size: Long,
            bucketId: String,
            bucketName: String
        ): Song {
            // val parent = realm.where(SongList::class.java).findFirst()
            // val songList = parent!!.songList
            val song = realm.createObject(Song::class.java, increment())
            song.secondsPlayed = secondsPlayed
            song.name = name
            song.path = path
            song.size = size
            song.bucketId = bucketId
            song.bucketName = bucketName
            // songList?.add(song)
            return song
        }

        internal fun delete(realm: Realm, id: Int) {
            val alarm = realm.where(Song::class.java).equalTo(FIELD_ID, id).findFirst()
            alarm?.deleteFromRealm()
        }

        private fun increment(): Int {
            return INTEGER_COUNTER.getAndIncrement()
        }
    }
}