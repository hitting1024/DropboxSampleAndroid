package jp.hitting.dropboxsampleandroid

import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.ListView
import android.widget.Toast
import com.dropbox.core.DbxRequestConfig
import com.dropbox.core.android.Auth
import com.dropbox.core.http.OkHttp3Requestor
import com.dropbox.core.v2.DbxClientV2
import com.dropbox.core.v2.files.FileMetadata
import com.dropbox.core.v2.files.ListFolderResult

class MainActivity : AppCompatActivity() {

    private val list = ArrayList<FileMetadata>()

    private var adapter: DropboxListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.activity_main)

        val listView = this.findViewById(R.id.listView) as ListView
        this.adapter = DropboxListAdapter(this, android.R.layout.simple_list_item_2, this.list)
        listView.adapter = this.adapter!!
    }

    override fun onResume() {
        super.onResume()

        val pref = this.getSharedPreferences(PrefName, MODE_PRIVATE)
        var accessToken = pref.getString(DropboxAccessTokenKey, null)
        if (accessToken == null) {
            accessToken = Auth.getOAuth2Token()
            if (accessToken != null) {
                pref.edit().putString(DropboxAccessTokenKey, accessToken).apply()
                initDropboxClient(accessToken)
            }
        } else {
            initDropboxClient(accessToken)
        }
    }

    private fun login() {
        Auth.startOAuth2Authentication(this, this.getString(R.string.dropbox_app_key))
    }

    private fun logout() {
        dropboxClient = null
        val pref = this.getSharedPreferences(PrefName, MODE_PRIVATE)
        pref.edit().remove(DropboxAccessTokenKey).apply()
    }

    private fun loadData() {
        val client = dropboxClient
        if (client == null) {
            this.login()
            return
        }

        ListFolderAsyncTask(client, object : ListFolderAsyncTask.Callback {

            override fun onDataLoaded(result: ListFolderResult) {
                this@MainActivity.list.clear()
                result.entries.forEach {
                    this@MainActivity.list.add(it as FileMetadata)
                }
                this@MainActivity.adapter?.notifyDataSetChanged()
            }

            override fun onError(e: Exception?) {
                Toast.makeText(this@MainActivity, "Fail to load", Toast.LENGTH_SHORT).show()
            }

        }).execute("")
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        this.menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.load -> this.loadData()
            R.id.logout -> this.logout()
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {

        val PrefName = "DropboxSampleAndroid"
        val DropboxAccessTokenKey = "DropboxAccessTokenKey"

        var dropboxClient: DbxClientV2? = null

        fun initDropboxClient(accessToken: String) {
            if (dropboxClient != null) {
                return
            }
            val config = DbxRequestConfig.newBuilder("DropboxSampleAndroid")
                    .withHttpRequestor(OkHttp3Requestor.INSTANCE)
                    .build()
            dropboxClient = DbxClientV2(config, accessToken)
        }

    }

    private class ListFolderAsyncTask(private val dropboxClient: DbxClientV2, private val callback: Callback) : AsyncTask<String, Void, ListFolderResult>() {

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

}
