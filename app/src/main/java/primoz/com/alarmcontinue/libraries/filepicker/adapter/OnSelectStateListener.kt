package primoz.com.alarmcontinue.libraries.filepicker.adapter

interface OnSelectStateListener<T> {
    fun OnSelectStateChanged(state: Boolean, file: T)
}
