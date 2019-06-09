package primoz.com.alarmcontinue.libraries.filepicker.adapter

interface OnSelectViewListener {
    fun OnSelectStateChanged(isChecked: Boolean, position: Int): Boolean
}
