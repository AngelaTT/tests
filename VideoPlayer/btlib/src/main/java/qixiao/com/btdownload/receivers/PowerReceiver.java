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

import qixiao.com.btdownload.services.TorrentTaskService;

/*
 * The receiver for power monitoring.
 */

public class PowerReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        switch (intent.getAction()) {
            case Intent.ACTION_BATTERY_LOW:
            case Intent.ACTION_BATTERY_OKAY:
            case Intent.ACTION_POWER_CONNECTED:
            case Intent.ACTION_POWER_DISCONNECTED:
                Intent serviceIntent = new Intent(context.getApplicationContext(), TorrentTaskService.class);
                serviceIntent.setAction(intent.getAction());

                context.startService(serviceIntent);
                break;
        }
    }
}
