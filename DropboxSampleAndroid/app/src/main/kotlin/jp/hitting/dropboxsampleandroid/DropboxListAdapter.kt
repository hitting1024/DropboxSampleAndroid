package jp.hitting.dropboxsampleandroid

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.dropbox.core.v2.files.FileMetadata

class DropboxListAdapter(context: Context, textViewResourceId: Int, private val items: MutableList<FileMetadata>) : ArrayAdapter<FileMetadata>(context, textViewResourceId, items) {

    private val inflater: LayoutInflater

    init {
        this.inflater = LayoutInflater.from(context)
    }

    override fun getCount(): Int {
        return this.items.size
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val holder: ViewHolder
        var view = convertView
        if (view == null) {
            view = this.inflater.inflate(android.R.layout.simple_list_item_2, null)
            holder = ViewHolder()
            holder.text1 = view.findViewById(android.R.id.text1) as TextView
            holder.text2 = view.findViewById(android.R.id.text2) as TextView
            view.tag = holder
        } else {
            holder = view.tag as ViewHolder
        }
        val item = this.items[position]
        holder.text1?.text = item.name
        holder.text2?.text = item.size.toString()
        return view!!
    }

    companion object {
        private class ViewHolder {
            var text1: TextView? = null
            var text2: TextView? = null
        }
    }

}