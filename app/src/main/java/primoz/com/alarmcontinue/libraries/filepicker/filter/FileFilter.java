package primoz.com.alarmcontinue.libraries.filepicker.filter;


import androidx.fragment.app.FragmentActivity;
import primoz.com.alarmcontinue.libraries.filepicker.filter.callback.FileLoaderCallbacks;
import primoz.com.alarmcontinue.libraries.filepicker.filter.callback.FilterResultCallback;
import primoz.com.alarmcontinue.libraries.filepicker.filter.entity.AudioFile;
import primoz.com.alarmcontinue.libraries.filepicker.filter.entity.NormalFile;

import static primoz.com.alarmcontinue.libraries.filepicker.filter.callback.FileLoaderCallbacks.TYPE_AUDIO;
import static primoz.com.alarmcontinue.libraries.filepicker.filter.callback.FileLoaderCallbacks.TYPE_FILE;

/**
 * Created by Vincent Woo
 * Date: 2016/10/11
 * Time: 10:19
 */

public class FileFilter {

    public static void getAudios(FragmentActivity activity, FilterResultCallback<AudioFile> callback) {
        activity.getSupportLoaderManager().initLoader(2, null, new FileLoaderCallbacks(activity, callback, TYPE_AUDIO));
    }

    public static void getFiles(FragmentActivity activity, FilterResultCallback<NormalFile> callback, String[] suffix) {
        activity.getSupportLoaderManager().initLoader(3, null, new FileLoaderCallbacks(activity, callback, TYPE_FILE, suffix));
    }
}
