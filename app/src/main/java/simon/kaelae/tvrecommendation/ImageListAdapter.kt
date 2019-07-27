package simon.kaelae.tvrecommendation

import android.app.PendingIntent.getActivity
import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso

internal class ImageListAdapter internal constructor(context: Context, private val resource: Int, private val itemList: Array<String>?) : ArrayAdapter<ImageListAdapter.ItemHolder>(context, resource) {
    internal var cardImageUrl = arrayOf(
        Uri.parse("http://i.imgur.com/xTtwwzS.jpg"),
        Uri.parse("http://i.imgur.com/ucm7n4h.jpg"),
        Uri.parse("http://i.imgur.com/dydKNhm.jpg"),
        Uri.parse("http://i.imgur.com/a2yFl08.jpg"),
        Uri.parse("http://i.imgur.com/a7rFbnX.png"),
        Uri.parse("http://i.imgur.com/ManpvmP.png"),
        Uri.parse("http://i.imgur.com/wnl8bSg.jpg"),
        Uri.parse("http://i.imgur.com/1pvaI8X.jpg"),
        Uri.parse("https://i.imgur.com/oKNRcsb.jpg")


    )
    override fun getCount(): Int {
        return if (this.itemList != null) this.itemList.size else 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView

        val holder: ItemHolder
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(resource, null)
            holder = ItemHolder()
            holder.name = convertView!!.findViewById(R.id.textView)
            holder.icon = convertView.findViewById(R.id.icon)

            Picasso.with(context).load(cardImageUrl[position]).into(holder.icon);

            convertView.tag = holder
        } else {
            holder = convertView.tag as ItemHolder
        }

        holder.name!!.text = this.itemList!![position]
        Picasso.with(context).load(cardImageUrl[position]).into(holder.icon);

        return convertView
    }

    internal class ItemHolder {
        var name: TextView? = null
        var icon: ImageView? = null
    }
}
