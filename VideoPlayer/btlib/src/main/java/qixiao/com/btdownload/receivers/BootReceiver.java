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

import qixiao.com.btdownload.R;
import qixiao.com.btdownload.services.TorrentTaskService;
import qixiao.com.btdownload.settings.SettingsManager;

/*
 * The receiver for autostart service.
 */

public class BootReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            SettingsManager.initPreferences(context.getApplicationContext());

            SettingsManager pref = new SettingsManager(context.getApplicationContext());

            if (pref.getBoolean(context.getString(R.string.pref_key_autostart), false)) {
                context.startService(new Intent(context, TorrentTaskService.class));
            }
        }
    }
}
