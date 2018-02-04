package xyz.cybersapien.promptodroid.ui.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.google.firebase.auth.FirebaseAuth
import xyz.cybersapien.promptodroid.R

/**
 * Created by ogcybersapien on 16/1/18.
 */
class PromptsListWidget : AppWidgetProvider() {

    internal fun noUserView(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        val views = RemoteViews(context.packageName, R.layout.prompts_logout_widget)
        appWidgetManager.updateAppWidget(appWidgetIds, views)
    }

    internal fun showListView(context: Context, appWidgetManager: AppWidgetManager,
                              appWidgetId: Int) {
        // Construct the RemoteViews object
        val views = RemoteViews(context.packageName, R.layout.prompts_list_widget)
        val listIntent = Intent(context, PromptsListService::class.java)
        views.setRemoteAdapter(R.id.app_widget_list, listIntent)
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        if (FirebaseAuth.getInstance().currentUser == null) {
            noUserView(context, appWidgetManager, appWidgetIds)
        } else {
            for (id in appWidgetIds) {
                showListView(context, appWidgetManager, id)
            }
        }
    }
}