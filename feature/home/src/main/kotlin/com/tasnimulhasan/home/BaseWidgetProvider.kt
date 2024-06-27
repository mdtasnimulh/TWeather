package com.tasnimulhasan.home

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews

class BaseWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        appWidgetIds.forEach { appWidgetId ->
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context, 0, Intent(context, BaseWidgetProvider::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val views: RemoteViews = RemoteViews(context.packageName, R.layout.widget_layout).apply {
            setOnClickPendingIntent(R.id.widgetTitle, pendingIntent)
        }

        views.setTextViewText(R.id.widgetTitle, "TH Widget")
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }
}