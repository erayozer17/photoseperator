package photosep.example.eray.photosep;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class my_widget_class extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

            final int count = appWidgetIds.length;

            for (int i = 0; i < count; i++) {

                int widgetId = appWidgetIds[i];

                RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

                Intent intent = new Intent(context, widget_gelen_activity.class);

                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

                remoteViews.setOnClickPendingIntent(R.id.actionButton, pendingIntent);

                appWidgetManager.updateAppWidget(widgetId, remoteViews);
            }
    }
}



