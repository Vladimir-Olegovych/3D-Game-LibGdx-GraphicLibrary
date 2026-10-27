package com.gigapi.dialogs

abstract class Dialog {

    protected var dialogManager: DialogManager? = null
    private var showed = false

    fun isShowed(): Boolean = showed

    open fun onShow() {}
    open fun onHide() {}

    open fun onPause() {}
    open fun onResume() {}

    open fun onRender(deltaTime: Float) {}
    open fun onResize(width: Int, height: Int) {}

    fun dismiss(){
        if (!showed || dialogManager == null) return
        onHide()
        showed = false
    }

    fun show(){
        if (showed || dialogManager == null) return
        onShow()
        showed = true
    }

    fun attach(dialogManager: DialogManager) {
        this.dialogManager?.removeDialog(this)
        this.dialogManager = dialogManager
        dialogManager.addDialog(this)
    }

    fun unattach() {
        this.dialogManager?.removeDialog(this)
        this.dialogManager = null
    }

}