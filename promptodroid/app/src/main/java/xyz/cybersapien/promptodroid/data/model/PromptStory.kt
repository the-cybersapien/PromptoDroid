package xyz.cybersapien.promptodroid.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class PromptStory(
        var storyTitle: String = "",
        var storyDetail: String = "",
        var id: String? = null,
        var date: Long = Date().time,
        var userId: String? = null
) : Parcelable {

    constructor(storyTitle: String, storyDetail: String) : this() {
        this.storyTitle = storyTitle
        this.storyDetail = storyDetail
        this.date = Date().time
    }
}
