package courses.pluralsight.com.smsya.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import courses.pluralsight.com.smsya.R
import courses.pluralsight.com.smsya.data.SenderName

class SenderNameRecyclerAdapter(private val context: Context, private val senderNames : List<SenderName>) : RecyclerView.Adapter<SenderNameRecyclerAdapter.ViewHolder> (){
    private val layoutInflater = LayoutInflater.from(context)


    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView!!){
        var textSenderName = itemView?.findViewById<TextView>(R.id.txt_sender_name)
        var textStatus = itemView?.findViewById<TextView>(R.id.txt_sender_name_status)
        var textCreated = itemView?.findViewById<TextView>(R.id.txt_sender_name_created)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = layoutInflater.inflate(R.layout.item_sendernames_list, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val senderName = senderNames[position]
        holder.textSenderName?.text = senderName.senderid
        holder.textStatus?.text = senderName.status
        holder.textCreated?.text = senderName.created
    }

    override fun getItemCount(): Int {
       return senderNames.size
    }


}