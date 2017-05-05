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

package qixiao.com.btdownload.settings;

import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.text.InputFilter;
import android.text.TextUtils;
import android.widget.Toast;

import com.takisoft.fix.support.v7.preference.EditTextPreference;
import com.takisoft.fix.support.v7.preference.PreferenceFragmentCompat;

import qixiao.com.btdownload.InputFilterMinMax;
import qixiao.com.btdownload.R;
import qixiao.com.btdownload.core.TorrentEngine;

public class LimitationsSettingsFragment extends PreferenceFragmentCompat
        implements
        Preference.OnPreferenceChangeListener
{
    @SuppressWarnings("unused")
    private static final String TAG = LimitationsSettingsFragment.class.getSimpleName();
    private boolean settingsChanged = false;

    public static LimitationsSettingsFragment newInstance()
    {
        LimitationsSettingsFragment fragment = new LimitationsSettingsFragment();

        fragment.setArguments(new Bundle());

        return fragment;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        if (settingsChanged) {
            Toast.makeText(getActivity().getApplicationContext(),
                    R.string.settings_apply_after_reboot,
                    Toast.LENGTH_SHORT)
                    .show();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        SettingsManager pref = new SettingsManager(getActivity().getApplicationContext());

        InputFilter[] speedFilter = new InputFilter[]{ new InputFilterMinMax(0, Integer.MAX_VALUE) };
        InputFilter[] connectionsFilter =
                new InputFilter[]{ new InputFilterMinMax(TorrentEngine.MIN_CONNECTIONS_LIMIT, Integer.MAX_VALUE) };
        InputFilter[] queueingFilter =
                new InputFilter[]{ new InputFilterMinMax(1, 1000) };

        String keyMaxDownloadSpeedLimit = getString(R.string.pref_key_max_download_speed);
        EditTextPreference maxDownloadSpeedLimit = (EditTextPreference) findPreference(keyMaxDownloadSpeedLimit);
        maxDownloadSpeedLimit.setDialogMessage(R.string.speed_limit_dialog);
        String value = Integer.toString(pref.getInt(keyMaxDownloadSpeedLimit, 0) / 1024);
        maxDownloadSpeedLimit.getEditText().setFilters(speedFilter);
        maxDownloadSpeedLimit.setSummary(value);
        maxDownloadSpeedLimit.setText(value);
        bindOnPreferenceChangeListener(maxDownloadSpeedLimit);

        String keyMaxUploadSpeedLimit = getString(R.string.pref_key_max_upload_speed);
        EditTextPreference maxUploadSpeedLimit = (EditTextPreference) findPreference(keyMaxUploadSpeedLimit);
        maxUploadSpeedLimit.setDialogMessage(R.string.speed_limit_dialog);
        value = Integer.toString(pref.getInt(keyMaxUploadSpeedLimit, 0) / 1024);
        maxUploadSpeedLimit.getEditText().setFilters(speedFilter);
        maxUploadSpeedLimit.setSummary(value);
        maxUploadSpeedLimit.setText(value);
        bindOnPreferenceChangeListener(maxUploadSpeedLimit);

        String keyMaxConnections = getString(R.string.pref_key_max_connections);
        EditTextPreference maxConnections = (EditTextPreference) findPreference(keyMaxConnections);
        maxConnections.setDialogMessage(R.string.pref_max_connections_summary);
        value = Integer.toString(pref.getInt(keyMaxConnections, TorrentEngine.MIN_CONNECTIONS_LIMIT));
        maxConnections.getEditText().setFilters(connectionsFilter);
        maxConnections.setSummary(value);
        maxConnections.setText(value);
        bindOnPreferenceChangeListener(maxConnections);

        String keyMaxActiveUploads = getString(R.string.pref_key_max_active_uploads);
        EditTextPreference maxActiveUploads  = (EditTextPreference) findPreference(keyMaxActiveUploads);
        value = Integer.toString(pref.getInt(keyMaxActiveUploads, 1));
        maxActiveUploads.getEditText().setFilters(queueingFilter);
        maxActiveUploads.setSummary(value);
        maxActiveUploads.setText(value);
        bindOnPreferenceChangeListener(maxActiveUploads);

        String keyMaxActiveDownloads = getString(R.string.pref_key_max_active_downloads);
        EditTextPreference maxActiveDownloads  = (EditTextPreference) findPreference(keyMaxActiveDownloads);
        value = Integer.toString(pref.getInt(keyMaxActiveDownloads, 1));
        maxActiveDownloads.getEditText().setFilters(queueingFilter);
        maxActiveDownloads.setSummary(value);
        maxActiveDownloads.setText(value);
        bindOnPreferenceChangeListener(maxActiveDownloads);

        String keyMaxActiveTorrents = getString(R.string.pref_key_max_active_torrents);
        EditTextPreference maxActiveTorrents  = (EditTextPreference) findPreference(keyMaxActiveTorrents);
        value = Integer.toString(pref.getInt(keyMaxActiveTorrents, 1));
        maxActiveTorrents.getEditText().setFilters(queueingFilter);
        maxActiveTorrents.setSummary(value);
        maxActiveTorrents.setText(value);
        bindOnPreferenceChangeListener(maxActiveTorrents);
    }

    @Override
    public void onCreatePreferencesFix(Bundle savedInstanceState, String rootKey)
    {
        setPreferencesFromResource(R.xml.pref_limitations, rootKey);
    }

    private void bindOnPreferenceChangeListener(Preference preference)
    {
        preference.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue)
    {
        SettingsManager pref = new SettingsManager(getActivity().getApplicationContext());

        if (preference.getKey().equals(getString(R.string.pref_key_max_connections))) {
            int value = TorrentEngine.MIN_CONNECTIONS_LIMIT;

            if (!TextUtils.isEmpty((String) newValue)) {
                value = Integer.parseInt((String) newValue);
            }

            pref.put(preference.getKey(), value);
            preference.setSummary(Integer.toString(value));

        } else if (preference.getKey().equals(getString(R.string.pref_key_max_upload_speed)) ||
                preference.getKey().equals(getString(R.string.pref_key_max_download_speed))) {

            int value = 0;
            int summary = 0;

            if (!TextUtils.isEmpty((String) newValue)) {
                summary = Integer.parseInt((String) newValue);
                value = summary * 1024;
            }

            pref.put(preference.getKey(), value);
            preference.setSummary(Integer.toString(summary));

        } else if (preference.getKey().equals(getString(R.string.pref_key_max_active_downloads)) ||
                preference.getKey().equals(getString(R.string.pref_key_max_active_uploads)) ||
                preference.getKey().equals(getString(R.string.pref_key_max_active_torrents))) {

            int value = 1;

            if (!TextUtils.isEmpty((String) newValue)) {
                value = Integer.parseInt((String) newValue);
            }

            pref.put(preference.getKey(), value);
            preference.setSummary(Integer.toString(value));
        }
        settingsChanged = true;

        return true;
    }
}
