package primoz.com.alarmcontinue.model

import io.realm.RealmList
import io.realm.RealmObject

open class AlarmList : RealmObject() {
    var alarmList: RealmList<Alarm>? = null
}
