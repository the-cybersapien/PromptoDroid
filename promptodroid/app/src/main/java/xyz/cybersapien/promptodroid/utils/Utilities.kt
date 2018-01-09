package xyz.cybersapien.promptodroid.utils

import android.content.Context
import android.net.ConnectivityManager
import xyz.cybersapien.promptodroid.R
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by ogcybersapien on 31/12/17.
 */

fun isInternetConnected(context: Context): Boolean {
    val cManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
    val info = cManager?.activeNetworkInfo
    return info != null && info.isConnected
}

fun getFormattedDate(date: Long): String {
    val dateFormat = SimpleDateFormat.getDateInstance()
    return dateFormat.format(Date(date))
}

fun getWordCount(context: Context, detailText: String): String {
    val words = detailText.split(" ").size
    return context.getString(R.string.words_string, words)
}
