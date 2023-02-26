package com.yz.trelloclone.Utils

object Constants {

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
}