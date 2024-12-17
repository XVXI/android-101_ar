package com.transcode.smartcity101p2.firebase

import com.google.firebase.messaging.FirebaseMessaging
import com.orhanobut.hawk.Hawk
import com.transcode.smartcity101p2.BuildConfig
import com.transcode.smartcity101p2.model.Const
import com.transcode.smartcity101p2.model.LoginResponse

class FirebaseMessagingManager {

    private val mFirebaseMessaging: FirebaseMessaging = FirebaseMessaging.getInstance()

    //    init {
//        val login = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
//        val city_id_text = if (login.authority_info?.CityId.isNullOrEmpty()) {
//            ""
//        } else {
//            "_" + login.authority_info?.CityId
//        }
//        mFirebaseMessaging.subscribeToTopic(KEY_FIREBASE_TOPIC + city_id_text)
//        mFirebaseMessaging.subscribeToTopic(KEY_FIREBASE_TOPIC_NEWS + city_id_text)
//    }

    fun build(): FirebaseMessaging {
        return mFirebaseMessaging
    }

    fun subscribeTopics() {

        val login = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
        val city_id_text = if (login.authority_info?.CityId.isNullOrEmpty()) {
            ""
        } else {
            "_" + login.authority_info?.CityId
        }

        mFirebaseMessaging.unsubscribeFromTopic("DEV_$KEY_FIREBASE_TOPIC$city_id_text")
        mFirebaseMessaging.unsubscribeFromTopic("QA_$KEY_FIREBASE_TOPIC$city_id_text")
        mFirebaseMessaging.unsubscribeFromTopic("PROD_$KEY_FIREBASE_TOPIC$city_id_text")
        mFirebaseMessaging.unsubscribeFromTopic("DEV_$KEY_FIREBASE_TOPIC_NEWS$city_id_text")
        mFirebaseMessaging.unsubscribeFromTopic("QA_$KEY_FIREBASE_TOPIC_NEWS$city_id_text")
        mFirebaseMessaging.unsubscribeFromTopic("PROD_$KEY_FIREBASE_TOPIC_NEWS$city_id_text")

        mFirebaseMessaging.subscribeToTopic(KEY_FIREBASE_STAGE + KEY_FIREBASE_TOPIC + city_id_text)
        mFirebaseMessaging.subscribeToTopic(KEY_FIREBASE_STAGE + KEY_FIREBASE_TOPIC_NEWS + city_id_text)

    }

    companion object {
        const val KEY_FIREBASE_TOPIC = "SCCitizen"
        const val KEY_FIREBASE_TOPIC_NEWS = "News"
        const val KEY_FIREBASE_STAGE = BuildConfig.FIREBASE_STAGE
    }
}