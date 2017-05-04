package jp.hitting.dropboxsampleandroid

import android.os.AsyncTask
import com.dropbox.core.v2.DbxClientV2
import java.io.File
import java.io.FileOutputStream


class DownloadFileAsyncTask(private val dropboxClient: DbxClientV2, private val callback: Callback) : AsyncTask<String, Void, File>() {

    interface Callback {
        fun onDataDownloaded(result: File)
        fun onError(e: Exception?)
    }

    private var exception: Exception? = null

    /**
     * @param params 0: downloading file path in dropbox, 1: saving path in local
     */
    override fun doInBackground(vararg params: String?): File? {
        val targetPath = params[0]
        val localPath = params[1]

        if (targetPath == null) {
            return null
        }

        val file = File(localPath)

        try {
            FileOutputStream(file).use {
                this.dropboxClient.files()
                        .download(targetPath)
                        .download(it)
            }
            return file
        } catch (e: Exception) {
            this.exception = e
            return null
        }
    }

    override fun onPostExecute(result: File?) {
        super.onPostExecute(result)

        if (this.exception == null && result != null) {
            this.callback.onDataDownloaded(result)
        } else {
            this.callback.onError(this.exception)
        }
    }

}
