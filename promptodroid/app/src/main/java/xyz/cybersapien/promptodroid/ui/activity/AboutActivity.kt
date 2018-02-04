package xyz.cybersapien.promptodroid.ui.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.vansuita.materialabout.builder.AboutBuilder
import kotlinx.android.synthetic.main.activity_about.*
import xyz.cybersapien.promptodroid.R
import xyz.cybersapien.promptodroid.data.GetVersionTask

/**
 * Created by ogcybersapien on 21/1/18.
 */
class AboutActivity : AppCompatActivity(), GetVersionTask.OnFinishListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        val aboutView = AboutBuilder.with(this)
                .setPhoto(R.mipmap.profile_picture)
                .setCover(R.mipmap.profile_cover)
                .setName(R.string.dev_name)
                .setSubTitle(R.string.username)
                .setBrief(R.string.bio)
                .setAppIcon(R.mipmap.ic_launcher_round)
                .setAppName(R.string.app_name)
                .addGitHubLink("the-cybersapien")
                .addFacebookLink("aditya.cybersapien")
                .addTwitterLink("the_cybersapien")
                .addEmailLink("aditya@cybersapien.xyz")
                .addWebsiteLink("www.cybersapien.xyz")
                .addFiveStarsAction().setVersionNameAsAppSubTitle()
                .addShareAction(R.string.app_name)
                .setWrapScrollView(true)
                .setLinksAnimated(true)
                .setShowAsCard(false)
                .build()
        about_card_container.addView(aboutView)

        val versionTask = GetVersionTask(this)
        versionTask.execute()
    }

    override fun onVersionTaskFinish(version: String?) {
        if (version != null) {
            version_text_view.text = getString(R.string.latest_version_template, version)
        } else {
            version_text_view.setText(R.string.error_occured)
        }
    }
}