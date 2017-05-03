package jp.hitting.dropboxsampleandroid

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.dropbox.core.DbxRequestConfig
import com.dropbox.core.android.Auth
import com.dropbox.core.http.OkHttp3Requestor
import com.dropbox.core.v2.DbxClientV2

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()

        if (dropboxClient == null) {
            val accessToken = Auth.getOAuth2Token()
            if (accessToken != null) {
                initDropboxClient(accessToken)
            }
        }
    }

    private fun login() {
        Auth.startOAuth2Authentication(this, this.getString(R.string.dropbox_app_key))
    }

    companion object {

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

}
