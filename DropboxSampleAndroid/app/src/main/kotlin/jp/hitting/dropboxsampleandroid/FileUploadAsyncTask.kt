package jp.hitting.dropboxsampleandroid

import android.os.AsyncTask
import com.dropbox.core.v2.DbxClientV2
import com.dropbox.core.v2.files.FileMetadata
import com.dropbox.core.v2.files.WriteMode
import java.io.FileInputStream

class FileUploadAsyncTask(private val dropboxClient: DbxClientV2, private val callback: Callback) : AsyncTask<String, Void, FileMetadata>() {

    interface Callback {
        fun onDataUploaded(result: FileMetadata)
        fun onError(e: Exception?)
    }

    private var exception: Exception? = null

    /**
     * @param params 0: uploading file path in local, 1: path in dropbox
     */
    override fun doInBackground(vararg params: String?): FileMetadata? {
        val localPath = params[0]
        val targetPath = params[1]

        if (localPath == null) {
            return null
        }

        FileInputStream(localPath).use {
            try {
                return this.dropboxClient.files().uploadBuilder(targetPath)
                        .withMode(WriteMode.OVERWRITE)
                        .uploadAndFinish(it)
            } catch (e: Exception) {
                this.exception = e
                return null
            }
        }
    }

    override fun onPostExecute(result: FileMetadata?) {
        super.onPostExecute(result)

        if (this.exception == null && result != null) {
            this.callback.onDataUploaded(result)
        } else {
            this.callback.onError(this.exception)
        }
    }

}
