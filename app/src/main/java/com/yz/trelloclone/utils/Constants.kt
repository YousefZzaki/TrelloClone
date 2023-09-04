package com.yz.trelloclone.Utils

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.yz.trelloclone.activities.BaseActivity

object Constants : BaseActivity(){

    const val USERS = "Users"

    const val BOARDS = "Boards"

    const val DOCUMENT_ID = "documentId"
    const val ASSIGNED_TO = "assignedTo"
    const val TASK_LIST = "taskList"

    const val ID = "id"
    const val IMAGE = "image"
    const val NAME = "name"
    const val EMAIL = "email"
    const val MOBILE = "mobile"

    const val BOARD_DETAILS = "board_details"

    const val TASK_POSITION = "task_position"
    const val CARD_POSITION = "card_position"

    const val BOARD_MEMBERS_LIST = "board_members_list"

    const val SELECTED = "selected"
    const val UN_SELECTED = "un_selected"

    const val USER_ASSIGNED_NOTIFICATION_ID = 0

    const val PROJECT_PREFS = "project_prefs"

    const val FCM_TOKEN = "fcmToken"

    const val IS_TOKEN_UPDATED = "token_is_updated"

    const val FCM_BASE_URL = "https://fcm.googleapis.com/fcm/send"
    const val FCM_AUTHORIZATION = "authorization"
    const val FCM_KEY = "key"
    const val FCM_KEY_TITLE = "title"
    const val FCM_KEY_MESSAGE = "message"
    const val FCM_KEY_DATA = "data"
    const val FCM_KEY_TO = "to"



    fun getColorList(): ArrayList<String>{
        val colorList = ArrayList<String>()
        colorList.add("#FF1E88E5")
        colorList.add("#FF3949AB")
        colorList.add("#FFE53935")
        colorList.add("#FF00695C")
        colorList.add("#FF76FF03")
        colorList.add("#FF00B0FF")
        colorList.add("#FF011343")

        return colorList
    }

     suspend fun getBitmap(imageUrl: String): Bitmap {

        val loading = ImageLoader(applicationContext)
        val imageRequest = ImageRequest.Builder(applicationContext)
            .data(imageUrl)
            .build()

        val result = (loading.execute(imageRequest) as SuccessResult).drawable
        return (result as BitmapDrawable).bitmap
    }
}