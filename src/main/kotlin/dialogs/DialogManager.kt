package com.gigapi.dialogs

class DialogManager {

    private val dialogs = HashSet<Dialog>()

    fun addDialog(dialog: Dialog){
        if (dialogs.contains(dialog)) return
        dialogs.add(dialog)
    }

    fun removeDialog(dialog: Dialog){
        dialogs.remove(dialog)
    }

    fun clear(){
        HashSet(dialogs).forEach { it.dismiss() }
        dialogs.clear()
    }

    fun onDestroy() {
        HashSet(dialogs).forEach { it.dismiss() }
        dialogs.clear()
    }

    fun onPause() {
        dialogs.forEach { it.onPause() }
    }

    fun onResume() {
        dialogs.forEach { it.onResume() }
    }

    fun onRender(deltaTime: Float) {
        dialogs.forEach { it.onRender(deltaTime) }
    }

    fun onResize(width: Int, height: Int) {
        dialogs.forEach { it.onResize(width, height) }
    }

}