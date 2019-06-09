package primoz.com.alarmcontinue.model

import io.realm.RealmList
import io.realm.RealmObject

open class SongList : RealmObject() {
    var songList: RealmList<Song>? = null
}
