package com.tasnimulhasan.ui

import android.app.Activity
import com.tasnimulhasan.common.extfun.clickWithDebounce
import com.tasnimulhasan.common.extfun.showViewAlertDialog
import com.tasnimulhasan.ui.databinding.DialogWarningMessageBinding
import com.tasnimulhasan.designsystem.R as Res

fun Activity.showWarningDialog(title : String, message : String, positiveBtnCallback : ()-> Unit, negativeButtonCallback : ()-> Unit){
    val dialogBinding = DialogWarningMessageBinding.inflate(layoutInflater)

    dialogBinding.titleTv.text = title
    dialogBinding.messageTv.text = message

    val dialog = this.showViewAlertDialog(
        dialogBinding.root,
        null,
        Res.style.AlertDialogTransparentBg,
        true
    )

    dialogBinding.positiveBtnTv.clickWithDebounce {
        positiveBtnCallback.invoke()
        dialog.dismiss()
    }

    dialogBinding.negativeBtnTv.clickWithDebounce {
        negativeButtonCallback.invoke()
        dialog.dismiss()
    }
}