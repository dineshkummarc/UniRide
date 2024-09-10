package com.drdisagree.uniride.data.utils

object Constant {

    // Google Sign In
    const val WEB_CLIENT_ID =
        "228240041465-ul73jd983ueobdu081ub29aa3b7q2unn.apps.googleusercontent.com"

    // Firebase Collections
    const val ADMIN_COLLECTION = "admin_list"
    const val STUDENT_COLLECTION = "student_list"
    const val DRIVER_COLLECTION = "driver_list"
    const val WHICH_USER_COLLECTION = "which_user_list"
    const val BUS_COLLECTION = "bus_list"
    const val BUS_CATEGORY_COLLECTION = "bus_category_list"
    const val ROUTE_COLLECTION = "route_list"
    const val SCHEDULE_COLLECTION = "schedule_list"
    const val PLACE_COLLECTION = "place_list"
    const val DRIVER_DOCUMENT_COLLECTION = "driver_documents_list"
    const val ANNOUNCEMENT_COLLECTION = "announcement_list"

    // Allowed Student Email Suffix
    val STUDENT_MAIL_SUFFIX = listOf(
        "@diu.edu.bd",
        "@daffodilvarsity.edu.bd"
    )

    // Driver Documents
    const val DRIVING_LICENSE_FRONT = "driving_license_front"
    const val DRIVING_LICENSE_BACK = "driving_license_back"
    const val NID_CARD_FRONT = "nid_card_front"
    const val NID_CARD_BACK = "nid_card_back"
}