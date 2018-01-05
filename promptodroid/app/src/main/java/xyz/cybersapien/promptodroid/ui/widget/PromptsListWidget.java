package xyz.cybersapien.promptodroid.ui.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.google.firebase.auth.FirebaseAuth;

import xyz.cybersapien.promptodroid.R;

/**
 * Implementation of App Widget functionality.
 */
public class PromptsListWidget extends AppWidgetProvider {

    static void showListView(Context context, AppWidgetManager appWidgetManager,
                             int appWidgetId) {
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.prompts_list_widget);
        Intent listIntent = new Intent(context, PromptsListService.class);
        views.setRemoteAdapter(R.id.app_widget_list, listIntent);
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    private static void noUserView(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.prompts_logout_widget);
        appWidgetManager.updateAppWidget(appWidgetIds, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            noUserView(context, appWidgetManager, appWidgetIds);
        } else {
            for (int appWidgetId : appWidgetIds) {
                showListView(context, appWidgetManager, appWidgetId);
            }
        }
    }

    @Override
    public void onEnabled(Context context) {

    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

