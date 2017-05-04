package jp.hitting.dropboxsampleandroid

import android.os.AsyncTask
import com.dropbox.core.v2.DbxClientV2
import com.dropbox.core.v2.files.ListFolderResult

class ListFolderAsyncTask(private val dropboxClient: DbxClientV2, private val callback: Callback) : AsyncTask<String, Void, ListFolderResult>() {

    interface Callback {
        fun onDataLoaded(result: ListFolderResult)
        fun onError(e: Exception?)
    }

    private var exception: Exception? = null

    /**
     * @param params 0: path in dropbox
     */
    override fun doInBackground(vararg params: String?): ListFolderResult? {
        try {
            return this.dropboxClient.files().listFolder(params[0])
        } catch (e: Exception) {
            this.exception = e
        }
        return null
    }

    override fun onPostExecute(result: ListFolderResult?) {
        super.onPostExecute(result)

        if (this.exception == null && result != null) {
            this.callback.onDataLoaded(result)
        } else {
            this.callback.onError(this.exception)
        }
    }

}
