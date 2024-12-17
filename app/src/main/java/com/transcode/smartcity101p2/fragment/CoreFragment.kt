package com.transcode.smartcity101p2.fragment

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.inputmethod.InputMethodManager

open class CoreFragment : Fragment() {
    var isAttach = false

    fun refresh() {
        fragmentManager?.beginTransaction()?.detach(this)?.attach(this)?.commit()
    }

    fun hideSoftKeyboard() {
        activity?.let {
            try {
                val inputMethodManager = it.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(it.currentFocus?.windowToken, 0)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun isGrantedPermission(): Boolean {
        activity?.let {
            return (ContextCompat.checkSelfPermission(it, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(it, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        } ?: kotlin.run {
            return false
        }
    }

    fun isGrantedStorePermission(): Boolean {
        activity?.let {
            return (ContextCompat.checkSelfPermission(it, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(it, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
        } ?: kotlin.run {
            return false
        }
    }

    fun replaceWithoutAddingToBackStack(fragment: CoreFragment, layoutRes: Int) {
        fragmentManager?.let {
            FragmentHelper.replaceWithoutAddingToBackStack(it, fragment, layoutRes)
        }
    }

    fun replaceFragment(fragment: CoreFragment, layoutRes: Int) {
        fragmentManager?.let {
            FragmentHelper.replace(it, fragment, layoutRes)
        }
    }

    fun addFragment(fragment: CoreFragment, layoutRes: Int) {
        fragmentManager?.let {
            FragmentHelper.add(it, fragment, layoutRes)
        }
    }

    override fun onAttach(context: Context?) {
        isAttach = true
        super.onAttach(context)
    }

    fun isFragmentReady(): Boolean {
        return isAttach
    }
}