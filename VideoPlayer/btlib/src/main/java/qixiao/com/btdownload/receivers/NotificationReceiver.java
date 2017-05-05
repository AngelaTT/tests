/*
 * Copyright (C) 2016 Yaroslav Pronin <proninyaroslav@mail.ru>
 *
 * This file is part of LibreTorrent.
 *
 * LibreTorrent is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * LibreTorrent is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with LibreTorrent.  If not, see <http://www.gnu.org/licenses/>.
 */

package qixiao.com.btdownload.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import qixiao.com.btdownload.MainActivity;
import qixiao.com.btdownload.services.TorrentTaskService;

/*
 * The receiver for actions of foreground notification, added by service.
 */

public class NotificationReceiver extends BroadcastReceiver
{
    public static final String NOTIFY_ACTION_SHUTDOWN_APP = "NotificationReceiver.NOTIFY_ACTION_SHUTDOWN_APP";
    public static final String NOTIFY_ACTION_ADD_TORRENT = "NotificationReceiver.NOTIFY_ACTION_ADD_TORRENT";

    @Override
    public void onReceive(Context context, Intent intent)
    {
        switch (intent.getAction()) {
            /* Send action to the already running service */
            case NOTIFY_ACTION_SHUTDOWN_APP:
                Intent serviceIntent = new Intent(context.getApplicationContext(), TorrentTaskService.class);
                serviceIntent.setAction(NOTIFY_ACTION_SHUTDOWN_APP);

                context.startService(serviceIntent);
                break;
            case NOTIFY_ACTION_ADD_TORRENT:
                Intent mainIntent = new Intent(context.getApplicationContext(), MainActivity.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                mainIntent.setAction(NOTIFY_ACTION_ADD_TORRENT);

                context.startActivity(mainIntent);
                break;
        }
    }
}
