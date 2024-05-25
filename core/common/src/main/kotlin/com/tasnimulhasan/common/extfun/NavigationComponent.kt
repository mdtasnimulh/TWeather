package com.tasnimulhasan.common.extfun

import android.net.Uri
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.tasnimulhasan.designsystem.R as Res

fun Fragment.popBack() {
    findNavController().popBackStack()
}

fun <T> Fragment.navigationBackStackResultLiveData(key: String = "key"): MutableLiveData<T>? {
    viewLifecycleOwner.lifecycle.addObserver(LifecycleEventObserver { _, event ->
        if (event == Lifecycle.Event.ON_DESTROY) {
            findNavController().previousBackStackEntry?.savedStateHandle?.remove<T>(key)
        }
    })
    return findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData(key)
}

fun <T> Fragment.setNavigationBackStackResult(key: String = "key", result: T) {
    findNavController().previousBackStackEntry?.savedStateHandle?.set(key, result)
}

fun Fragment.navigateDestination(direction: NavDirections) {
    val currentNode = (
            if (findNavController().currentBackStack.value.isEmpty())
                findNavController().graph
            else
                findNavController().currentBackStack.value.last().destination
            )
    val navAction = currentNode.getAction(direction.actionId)
    findNavController().navigate(
        direction.actionId,
        direction.arguments,
        navAction?.navOptions?.let {
            navOptions {
                anim {
                    enter = Res.anim.slide_in_right
                    exit = Res.anim.slide_out_left
                    popEnter = Res.anim.slide_in_left
                    popExit = Res.anim.slide_out_right
                }
                popUpTo(it.popUpToId) {
                    inclusive = it.isPopUpToInclusive()
                }
            }
        }
    )
}

fun Fragment.navigateToDestination(
    uri: Uri,
    popupToId: Int? = null,
    popupToInclusive: Boolean = false
) {
    findNavController().navigate(
        NavDeepLinkRequest.Builder.fromUri(uri).build(),
        navOptions = navOptions {
            anim {
                enter = Res.anim.slide_in_right
                exit = Res.anim.slide_out_left
                popEnter = Res.anim.slide_in_left
                popExit = Res.anim.slide_out_right
            }
            popupToId?.let {
                popUpTo(popupToId) {
                    inclusive = popupToInclusive
                }
            }
        }
    )
}