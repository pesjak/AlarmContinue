package primoz.com.alarmcontinue.model

import io.realm.RealmObject

open class RealmString : RealmObject {
    var value: String? = null

    constructor()

    constructor(value: String) {
        this.value = value
    }
}