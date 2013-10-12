package com.chanqueo.flashlight;

import com.chanqueo.flashlight.R;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.util.Log;
import android.widget.RemoteViews;

/// Implements an AppWidget provider.
public class WidgetProvider extends AppWidgetProvider
{
    /// Tag used for logs.
    private static final String TAG = "com.chanqueo.flashlight";

    /// Intent action raised when widget is clicked.
    private static final String ACTION_CLICKED = "com.chanqueo.flashlight.APPWIDGET_CLICKED";

    /// Camera handle.
    private static Camera camera = null;

    /// Indicates flashlight is on.
    /// @return true is flashlight is on, false otherwise.
    private static boolean flashligtIsOn()
    {
        return camera != null;
    }

    /// Turns on flashlight.
    private static void turnOnFlashlight()
    {
        try {
            camera = Camera.open();
            Parameters params = camera.getParameters();
            params.setFlashMode(Parameters.FLASH_MODE_TORCH);
            camera.setParameters(params);
            //camera.startPreview();
        } catch (Exception exception) {
            Log.d(TAG, "Exception turning-on flashlight " + exception.getMessage());
        }
    }

    /// Turns off flashlight.
    private static void turnOffFlashlight()
    {
        try {
            Parameters params = camera.getParameters();
            params.setFlashMode(Parameters.FLASH_MODE_OFF);
            camera.setParameters(params);
            //camera.stopPreview();
            camera.release();
            camera = null;
        } catch (Exception exception) {
            Log.d(TAG, "Exception turning-off flashlight " + exception.getMessage());
        }
    }

    /// Updates specified widget.
    /// @param  context           The Context in which this receiver is running.
    /// @param  appWidgetManager  Manager used to update widgets.
    /// @param  appWidgetId       Widget for which an update is needed.
    private static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId)
    {
        Log.d(TAG, "updateAppWidget " + appWidgetId);

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget);

        Intent intent = new Intent(context, WidgetProvider.class);
        intent.setAction(ACTION_CLICKED);

        PendingIntent pendingIntent =
            PendingIntent.getBroadcast(context, 0, intent, 0);

        remoteViews.setOnClickPendingIntent(R.id.button, pendingIntent);

        if (WidgetProvider.flashligtIsOn())
            remoteViews.setTextViewText(R.id.button, Parameters.FLASH_MODE_TORCH);
        else
            remoteViews.setTextViewText(R.id.button, Parameters.FLASH_MODE_OFF);

        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
    }

    /// Updates all the widgets.
    /// @param  context  The Context in which this receiver is running.
    private static void updateAllAppWidgets(Context context)
    {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

        int[] appWidgetIds =
            appWidgetManager.getAppWidgetIds(new ComponentName(context, WidgetProvider.class));

        for (int i = 0; i < appWidgetIds.length; i++)
            WidgetProvider.updateAppWidget(context, appWidgetManager, appWidgetIds[i]);
    }

    /// Called when the first widget for this provider is instantiated.
    /// @param  context  The Context in which this receiver is running.
    @Override
    public void onEnabled(Context context)
    {
        Log.d(TAG, "onEnabled");

        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(new ComponentName(context, WidgetProvider.class),
                                      PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                                      PackageManager.DONT_KILL_APP);
    }

    /// Called when widgets are asked to be updated.
    /// @param  context           The Context in which this receiver is running.
    /// @param  appWidgetManager  Manager used to update widgets.
    /// @param  appWidgetIds      Widgets for which an update is needed.
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds)
    {
        Log.d(TAG, "onUpdate");

        for (int i = 0; i < appWidgetIds.length; i++)
            WidgetProvider.updateAppWidget(context, appWidgetManager, appWidgetIds[i]);
    }

    /// Dispatches calls to the various other methods on AppWidgetProvider.
    /// @param  context  The Context in which this receiver is running.
    /// @param  intent   The Intent being received.
    @Override
    public void onReceive(Context context, Intent intent)
    {
        Log.d(TAG, "onReceive " + intent);

        String action = intent.getAction();

        if (action.equals(ACTION_CLICKED)) {
            if (WidgetProvider.flashligtIsOn())
                WidgetProvider.turnOffFlashlight();
            else
                WidgetProvider.turnOnFlashlight();
            WidgetProvider.updateAllAppWidgets(context);
        }

        super.onReceive(context, intent);
    }

    /// Called when the last widget instance for this provider is deleted.
    /// @param  context  The Context in which this receiver is running.
    @Override
    public void onDisabled (Context context)
    {
        Log.d(TAG, "onDisabled");

        if (WidgetProvider.flashligtIsOn())
            WidgetProvider.turnOffFlashlight();

        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(new ComponentName(context, WidgetProvider.class),
                                      PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                                      PackageManager.DONT_KILL_APP);
    }
}
