package com.transcode.smartcity101p2

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import com.facebook.*
import com.transcode.smartcity101p2.fragment.FragmentHelper
import com.transcode.smartcity101p2.fragment.MainMenuFragment
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.orhanobut.hawk.Hawk
import com.transcode.smartcity101p2.fragment.SelectPlaceFragment
import com.transcode.smartcity101p2.model.Const
import android.util.Base64
import android.util.Log
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import android.R.attr.data
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount


class LoginActivity : CoreActivity() {

    private var mCallbackManager: CallbackManager? = null
    val LOGIN_MODE = "login_mode"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        FragmentHelper.replaceWithoutAddingToBackStack(supportFragmentManager, MainMenuFragment.newInstance(), R.id.content_frame)

        val bundle = intent.extras

        bundle?.let {
            if (it.containsKey(Const.KEY_NEED_LOGIN)) {
                Hawk.put(Const.KEY_NEED_LOGIN, it.getBoolean(Const.KEY_NEED_LOGIN))
            }
        }

        try {
            val info = packageManager.getPackageInfo(
                    "com.transcode.smartcity101p2",
                    PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT))
            }
        } catch (e: PackageManager.NameNotFoundException) {

        } catch (e: NoSuchAlgorithmException) {

        }


        mCallbackManager = CallbackManager.Factory.create()
        LoginManager.getInstance().registerCallback(mCallbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                Hawk.put(LOGIN_MODE, "1")
                // App code
                val fragment = FragmentHelper.getCurrentFragment(supportFragmentManager)
                if (fragment is MainMenuFragment) {
                    fragment.loginWithFacebook()
                }
            }

            override fun onCancel() {
                // App code
            }

            override fun onError(exception: FacebookException) {
                // App code
                exception.printStackTrace()
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == Const.RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            supportFragmentManager?.let {
                val currentFragment = FragmentHelper.getCurrentFragment(it, R.id.content_frame)
                if (currentFragment is MainMenuFragment) {
                    currentFragment.handleSignInGResult(task)
                }
            }
        } else {
            mCallbackManager?.onActivityResult(requestCode, resultCode, data)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        FragmentHelper.getCurrentFragment(supportFragmentManager)?.let {
            if (it is SelectPlaceFragment) {
                it.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        }
    }
}
