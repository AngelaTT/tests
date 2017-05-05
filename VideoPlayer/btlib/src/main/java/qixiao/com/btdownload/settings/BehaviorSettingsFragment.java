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

import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.preference.Preference;

import com.takisoft.fix.support.v7.preference.PreferenceFragmentCompat;
import com.takisoft.fix.support.v7.preference.SwitchPreferenceCompat;

import qixiao.com.btdownload.R;
import qixiao.com.btdownload.core.utils.Utils;
import qixiao.com.btdownload.receivers.BootReceiver;

public class BehaviorSettingsFragment extends PreferenceFragmentCompat
        implements
        Preference.OnPreferenceChangeListener
{
    @SuppressWarnings("unused")
    private static final String TAG = BehaviorSettingsFragment.class.getSimpleName();

    public static BehaviorSettingsFragment newInstance()
    {
        BehaviorSettingsFragment fragment = new BehaviorSettingsFragment();

        fragment.setArguments(new Bundle());

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        final SettingsManager pref = new SettingsManager(getActivity().getApplicationContext());

        String keyAutostart = getString(R.string.pref_key_autostart);
        SwitchPreferenceCompat autostart = (SwitchPreferenceCompat) findPreference(keyAutostart);
        autostart.setChecked(pref.getBoolean(keyAutostart, false));
        bindOnPreferenceChangeListener(autostart);

        String keyShutdownComplete = getString(R.string.pref_key_shutdown_downloads_complete);
        SwitchPreferenceCompat shutdownComplete = (SwitchPreferenceCompat) findPreference(keyShutdownComplete);
        shutdownComplete.setChecked(pref.getBoolean(keyShutdownComplete, false));
        bindOnPreferenceChangeListener(shutdownComplete);

        String keyCpuSleep = getString(R.string.pref_key_cpu_do_not_sleep);
        SwitchPreferenceCompat cpuSleep = (SwitchPreferenceCompat) findPreference(keyCpuSleep);
        cpuSleep.setChecked(pref.getBoolean(keyCpuSleep, false));
        bindOnPreferenceChangeListener(cpuSleep);

        String keyOnlyCharging = getString(R.string.pref_key_download_and_upload_only_when_charging);
        SwitchPreferenceCompat onlyCharging = (SwitchPreferenceCompat) findPreference(keyOnlyCharging);
        onlyCharging.setChecked(pref.getBoolean(keyOnlyCharging, false));
        bindOnPreferenceChangeListener(onlyCharging);

        String keyBatteryControl = getString(R.string.pref_key_battery_control);
        SwitchPreferenceCompat batteryControl = (SwitchPreferenceCompat) findPreference(keyBatteryControl);
        batteryControl.setSummary(String.format(getString(R.string.pref_battery_control_summary),
                Utils.getDefaultBatteryLowLevel()));
        batteryControl.setChecked(pref.getBoolean(keyBatteryControl, false));
        bindOnPreferenceChangeListener(batteryControl);
    }

    @Override
    public void onCreatePreferencesFix(Bundle savedInstanceState, String rootKey)
    {
        setPreferencesFromResource(R.xml.pref_behavior, rootKey);
    }

    private void bindOnPreferenceChangeListener(Preference preference)
    {
        preference.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue)
    {
        SettingsManager pref = new SettingsManager(getActivity().getApplicationContext());

        if (preference instanceof SwitchPreferenceCompat) {
            pref.put(preference.getKey(), (boolean) newValue);

            if (preference.getKey().equals(getString(R.string.pref_key_autostart))) {
                int flag = ((boolean) newValue ?
                        PackageManager.COMPONENT_ENABLED_STATE_ENABLED :
                        PackageManager.COMPONENT_ENABLED_STATE_DISABLED);
                ComponentName bootReceiver = new ComponentName(getActivity(), BootReceiver.class);

                getActivity().getPackageManager()
                        .setComponentEnabledSetting(bootReceiver, flag, PackageManager.DONT_KILL_APP);
            }
        }

        return true;
    }
}
