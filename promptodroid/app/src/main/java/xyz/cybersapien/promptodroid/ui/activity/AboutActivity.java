package xyz.cybersapien.promptodroid.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.vansuita.materialabout.builder.AboutBuilder;
import com.vansuita.materialabout.views.AboutView;

import butterknife.BindView;
import butterknife.ButterKnife;
import xyz.cybersapien.promptodroid.R;
import xyz.cybersapien.promptodroid.data.GetVersionTask;

public class AboutActivity extends AppCompatActivity implements GetVersionTask.OnFinishListener {

    @BindView(R.id.about_card_container)
    FrameLayout view;
    @BindView(R.id.version_text_view)
    TextView versionTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);
        AboutView aboutView = AboutBuilder.with(this)
                .setPhoto(R.mipmap.profile_picture)
                .setCover(R.mipmap.profile_cover)
                .setName(R.string.dev_name)
                .setSubTitle(R.string.username)
                .setBrief(R.string.bio)
                .setAppIcon(R.mipmap.ic_launcher_round)
                .setAppName(R.string.app_name)
                .addGitHubLink("the-cybersapien") // Don't need to store fixed data in strings
                .addFacebookLink("aditya.cybersapien")
                .addTwitterLink("the_cybersapien")
                .addEmailLink("aditya@cybersapien.xyz")
                .addWebsiteLink("www.cybersapien.xyz")
                .addFiveStarsAction()
                .setVersionNameAsAppSubTitle()
                .addShareAction(R.string.app_name)
                .setWrapScrollView(true)
                .setLinksAnimated(true)
                .setShowAsCard(true)
                .build();
        view.addView(aboutView);

        GetVersionTask versionTask = new GetVersionTask(this);
        versionTask.execute();
    }

    @Override
    public void onVersionFinish(String version) {
        if (version == null) {
            versionTextView.setText(R.string.error_occured);
        } else {
            versionTextView.setText(getString(R.string.latest_version_template, version));
        }
    }

}
