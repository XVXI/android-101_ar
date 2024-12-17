package com.transcode.smartcity101p2.fragment

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import com.transcode.smartcity101p2.R

const val NO_ANIMATION = 0
const val SLIDE_RIGHT_TO_LEFT = 1

class FragmentHelper {
    companion object {
        fun remove(manager: FragmentManager?, fragment: Fragment) {
            val transaction = manager?.beginTransaction()
            transaction?.remove(fragment)
            transaction?.commit()
        }

        fun replace(manager: FragmentManager, fragment: Fragment, replaceLayout: Int) {
            val transaction = manager.beginTransaction()
            transaction.replace(replaceLayout, fragment)
            transaction.addToBackStack(fragment.javaClass.simpleName)
            transaction.commitAllowingStateLoss()
        }

        fun add(manager: FragmentManager, fragment: Fragment, replaceLayout: Int) {
            val transaction = manager.beginTransaction()
            transaction.add(replaceLayout, fragment)
            transaction.addToBackStack(fragment.javaClass.simpleName)
            transaction.commitAllowingStateLoss()
        }

        fun replaceWithoutAddingToBackStack(manager: FragmentManager, fragment: Fragment, replaceLayout: Int) {
            val transaction = manager.beginTransaction()
            transaction.replace(replaceLayout, fragment)
            transaction.commitAllowingStateLoss()
        }

        fun replaceWithAnimation(manager: FragmentManager,
                                 fragment: Fragment,
                                 replaceLayout: Int, animationType: Int) {
            val transaction = manager.beginTransaction()
            when (animationType) {
                NO_ANIMATION -> {
                }
                SLIDE_RIGHT_TO_LEFT -> transaction.setCustomAnimations(R.anim.activity_open_translate, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_translate)
                else -> transaction.setCustomAnimations(R.anim.activity_open_scale, R.anim.activity_close_scale, R.anim.activity_open_scale, R.anim.activity_close_scale)
            }
            transaction.replace(replaceLayout, fragment)
            transaction.addToBackStack(fragment.javaClass.simpleName)
            transaction.commitAllowingStateLoss()

        }

        fun popToFragment(fm: FragmentManager?, fragmentName: String) {
            fm?.let {
                val backStackCount = fm.backStackEntryCount
                if (backStackCount > 0) {
                    val current = fm.getBackStackEntryAt(backStackCount - 1)
                    if (current.name == null || current.name != fragmentName) {
                        // if current fragment is not the specified fragment, find it in back stack and pop to it
                        for (i in 0 until backStackCount) {
                            val entry = fm.getBackStackEntryAt(i)
                            if (entry.name != null && entry.name == fragmentName) {
                                fm.popBackStack(entry.id, 0)
                                break
                            }
                        }
                    }
                }
            }
        }

        fun getCurrentFragment(fm: FragmentManager?): Fragment? {
            return getCurrentFragment(fm, R.id.content_frame)
        }

        fun getCurrentFragment(fm: FragmentManager?, contentView: Int): Fragment? {
            fm?.let {
                return it.findFragmentById(contentView)
            }
            return null
        }

        fun popToRootFragment(fm: FragmentManager?) {
            fm?.let {
                val backStackCount = fm.backStackEntryCount
                if (backStackCount > 0) {
                    for (i in 0 until backStackCount) {
                        it.popBackStack()
                    }
                }
                for (f in it.fragments) {
                    if (f !is FragmentForPager && f !is NewsFragment && f !is QueueFragment && f !is AccountFragment && f !is HomeFragment) {
                        remove(it, f)
                    }
                }
            }
        }
    }
}