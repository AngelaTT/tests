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

package qixiao.com.btdownload.core.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.util.Log;

import org.apache.commons.io.FileUtils;
import qixiao.com.btdownload.core.Torrent;
import qixiao.com.btdownload.core.utils.TorrentUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * The class provides a single access to the torrents repository.
 */

public class TorrentStorage
{
    @SuppressWarnings("unused")
    private static final String TAG = TorrentStorage.class.getSimpleName();

    private String[] allColumns = {
            DatabaseHelper.COLUMN_ID,
            DatabaseHelper.COLUMN_TORRENT_ID,
            DatabaseHelper.COLUMN_NAME,
            DatabaseHelper.COLUMN_PATH_TO_TORRENT,
            DatabaseHelper.COLUMN_PATH_TO_DOWNLOAD,
            DatabaseHelper.COLUMN_FILE_PRIORITIES,
            DatabaseHelper.COLUMN_IS_SEQUENTIAL,
            DatabaseHelper.COLUMN_IS_FINISHED,
            DatabaseHelper.COLUMN_IS_PAUSED
    };

    private Context context;

    public class Model
    {
        public static final String DATA_TORRENT_FILE_NAME = "torrent";
        public static final String DATA_TORRENT_RESUME_FILE_NAME = "fastresume";
        public static final String DATA_TORRENT_SESSION_FILE = "session";
        public static final String FILE_LIST_SEPARATOR = ",";
    }

    public TorrentStorage(Context context)
    {
        this.context = context;
    }

    public boolean add(Torrent torrent, String pathToTorrent, boolean deleteTorrentFile) throws Throwable
    {
        if (pathToTorrent == null) {
            return false;
        }

        String newPath = TorrentUtils.copyTorrent(
                context,
                torrent.getId(),
                pathToTorrent);

        if (newPath == null) {
            return false;
        }

        if (deleteTorrentFile) {
            try {
                FileUtils.forceDelete(new File(torrent.getTorrentFilePath()));

            } catch (IOException e) {
                Log.w(TAG, "Could not delete torrent file: ", e);
            }
        }

        torrent.setTorrentFilePath(newPath);

        return insert(torrent) >= 0;
    }

    private long insert(Torrent torrent)
    {
        ContentValues values = new ContentValues();

        values.put(DatabaseHelper.COLUMN_TORRENT_ID, torrent.getId());
        values.put(DatabaseHelper.COLUMN_NAME, torrent.getName());
        values.put(DatabaseHelper.COLUMN_PATH_TO_TORRENT, torrent.getTorrentFilePath());
        values.put(DatabaseHelper.COLUMN_PATH_TO_DOWNLOAD, torrent.getDownloadPath());
        values.put(DatabaseHelper.COLUMN_FILE_PRIORITIES, integerListToString(torrent.getFilePriorities()));
        values.put(DatabaseHelper.COLUMN_IS_SEQUENTIAL, (torrent.isSequentialDownload() ? 1 : 0));
        values.put(DatabaseHelper.COLUMN_IS_FINISHED, (torrent.isFinished() ? 1 : 0));
        values.put(DatabaseHelper.COLUMN_IS_PAUSED, (torrent.isPaused() ? 1 : 0));

        return ConnectionManager.getDatabase(context).insert(DatabaseHelper.TORRENTS_TABLE, null, values);
    }

    public void replace(Torrent torrent, String pathToTorrent, boolean deleteTorrentFile) throws Throwable
    {
        if (pathToTorrent == null) {
            return;
        }

        String newPath = TorrentUtils.copyTorrent(
                context,
                torrent.getId(),
                pathToTorrent);

        if (newPath == null) {
            return;
        }

        if (deleteTorrentFile) {
            try {
                FileUtils.forceDelete(new File(torrent.getTorrentFilePath()));

            } catch (IOException e) {
                Log.w(TAG, "Could not delete torrent file: ", e);
            }
        }

        torrent.setTorrentFilePath(newPath);

        update(torrent);
    }

    public void update(Torrent torrent)
    {
        ContentValues values = new ContentValues();

        values.put(DatabaseHelper.COLUMN_TORRENT_ID, torrent.getId());
        values.put(DatabaseHelper.COLUMN_NAME, torrent.getName());
        values.put(DatabaseHelper.COLUMN_PATH_TO_TORRENT, torrent.getTorrentFilePath());
        values.put(DatabaseHelper.COLUMN_PATH_TO_DOWNLOAD, torrent.getDownloadPath());
        values.put(DatabaseHelper.COLUMN_FILE_PRIORITIES, integerListToString(torrent.getFilePriorities()));
        values.put(DatabaseHelper.COLUMN_IS_SEQUENTIAL, (torrent.isSequentialDownload() ? 1 : 0));
        values.put(DatabaseHelper.COLUMN_IS_FINISHED, (torrent.isFinished() ? 1 : 0));
        values.put(DatabaseHelper.COLUMN_IS_PAUSED, (torrent.isPaused() ? 1 : 0));

        ConnectionManager.getDatabase(context).update(DatabaseHelper.TORRENTS_TABLE,
                values,
                DatabaseHelper.COLUMN_TORRENT_ID + " = '" + torrent.getId() + "' ",
                null);
    }

    public void delete(Torrent torrent)
    {
        ConnectionManager.getDatabase(context).delete(DatabaseHelper.TORRENTS_TABLE,
                DatabaseHelper.COLUMN_TORRENT_ID + " = '" + torrent.getId() + "' ",
                null);

        if (!TorrentUtils.removeTorrentDataDir(context, torrent.getId())) {
            Log.e(TAG, "Can't delete torrent " + torrent);
        }
    }

    public void delete(String id)
    {
        ConnectionManager.getDatabase(context).delete(DatabaseHelper.TORRENTS_TABLE,
                DatabaseHelper.COLUMN_TORRENT_ID + " = '" + id + "' ",
                null);

        if (!TorrentUtils.removeTorrentDataDir(context, id)) {
            Log.e(TAG, "Can't delete torrent " + id);
        }
    }

    public Torrent getTorrentByID(String id)
    {
        Cursor cursor = ConnectionManager.getDatabase(context).query(DatabaseHelper.TORRENTS_TABLE,
                allColumns,
                DatabaseHelper.COLUMN_TORRENT_ID + " = '" + id + "' ",
                null,
                null,
                null,
                null);

        Torrent torrent = null;

        ColumnIndexCache indexCache = new ColumnIndexCache();

        if (cursor.moveToNext()) {
            torrent = cursorToTorrent(cursor, indexCache);
        }

        cursor.close();
        indexCache.clear();

        return torrent;
    }

    public List<Torrent> getAll()
    {
        List<Torrent> torrents = new ArrayList<Torrent>();

                Cursor cursor = ConnectionManager.getDatabase(context).query(DatabaseHelper.TORRENTS_TABLE,
                allColumns,
                null,
                null,
                null,
                null,
                null);

        ColumnIndexCache indexCache = new ColumnIndexCache();

        while (cursor.moveToNext()) {
            torrents.add(cursorToTorrent(cursor, indexCache));
        }

        cursor.close();
        indexCache.clear();

        return torrents;
    }

    /*
     * Returns map with id as key.
     */

    public Map<String, Torrent> getAllAsMap()
    {
        Map<String, Torrent> torrents = new HashMap<String, Torrent>();

        Cursor cursor = ConnectionManager.getDatabase(context).query(DatabaseHelper.TORRENTS_TABLE,
                allColumns,
                null,
                null,
                null,
                null,
                null);

        ColumnIndexCache indexCache = new ColumnIndexCache();

        while (cursor.moveToNext()) {
            Torrent torrent = cursorToTorrent(cursor, indexCache);
            torrents.put(torrent.getId(), torrent);
        }

        cursor.close();
        indexCache.clear();

        return torrents;
    }

    public boolean exists(Torrent torrent)
    {
        Cursor cursor = ConnectionManager.getDatabase(context).query(DatabaseHelper.TORRENTS_TABLE,
                allColumns,
                DatabaseHelper.COLUMN_TORRENT_ID + " = '" + torrent.getId() + "' ",
                null,
                null,
                null,
                null);

        if (cursor.moveToNext()) {
            cursor.close();

            return true;
        }

        cursor.close();

        return false;
    }

    public boolean exists(String id)
    {
        Cursor cursor = ConnectionManager.getDatabase(context).query(DatabaseHelper.TORRENTS_TABLE,
                allColumns,
                DatabaseHelper.COLUMN_TORRENT_ID + " = '" + id + "' ",
                null,
                null,
                null,
                null);

        if (cursor.moveToNext()) {
            cursor.close();

            return true;
        }

        cursor.close();

        return false;
    }

    private Torrent cursorToTorrent(Cursor cursor, ColumnIndexCache indexCache)
    {
        String id = cursor.getString(
                indexCache.getColumnIndex(cursor, DatabaseHelper.COLUMN_TORRENT_ID));

        String name = cursor.getString(
                indexCache.getColumnIndex(cursor, DatabaseHelper.COLUMN_NAME));

        String pathToTorrent = cursor.getString(
                indexCache.getColumnIndex(cursor, DatabaseHelper.COLUMN_PATH_TO_TORRENT));

        String pathToDownload = cursor.getString(
                indexCache.getColumnIndex(cursor, DatabaseHelper.COLUMN_PATH_TO_DOWNLOAD));

        String priorities = cursor.getString(
                indexCache.getColumnIndex(cursor, DatabaseHelper.COLUMN_FILE_PRIORITIES));
        Collection<Integer> filePriorities = integerListFromString(priorities);

        boolean isSequentialDownload = cursor.getInt(
                indexCache.getColumnIndex(cursor, DatabaseHelper.COLUMN_IS_SEQUENTIAL)) > 0;

        boolean isFinished = cursor.getInt(
                indexCache.getColumnIndex(cursor, DatabaseHelper.COLUMN_IS_FINISHED)) > 0;

        boolean isPaused = cursor.getInt(
                indexCache.getColumnIndex(cursor, DatabaseHelper.COLUMN_IS_PAUSED)) > 0;

        Torrent torrent =
                new Torrent(
                        id, pathToTorrent,
                        name, filePriorities,
                        pathToDownload);

        torrent.setSequentialDownload(isSequentialDownload);
        torrent.setFinished(isFinished);
        torrent.setPaused(isPaused);

        return torrent;
    }

    @NonNull
    private String integerListToString(Collection<Integer> indexes)
    {
        return TextUtils.join(Model.FILE_LIST_SEPARATOR, indexes);
    }

    private Collection<Integer> integerListFromString(String s)
    {
        List<String> numbers = Arrays.asList(s.split(Model.FILE_LIST_SEPARATOR));

        Collection<Integer> list = new ArrayList<>();

        for (String number : numbers) {
            list.add(Integer.valueOf(number));
        }

        return list;
    }

    /*
     * Using a cache to speed up data retrieval from Cursors.
     */

    private class ColumnIndexCache
    {
        private ArrayMap<String, Integer> map = new ArrayMap<>();

        public int getColumnIndex(Cursor cursor, String columnName)
        {
            if (!map.containsKey(columnName)) {
                map.put(columnName, cursor.getColumnIndex(columnName));
            }

            return map.get(columnName);
        }

        public void clear()
        {
            map.clear();
        }
    }
}
