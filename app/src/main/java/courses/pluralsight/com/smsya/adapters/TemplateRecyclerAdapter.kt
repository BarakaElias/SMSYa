package courses.pluralsight.com.smsya.adapters

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import courses.pluralsight.com.smsya.R
import courses.pluralsight.com.smsya.data.SmsTemplate

class TemplateRecyclerAdapter(private val context: Context, private val templates : List<SmsTemplate>) : RecyclerView.Adapter<TemplateRecyclerAdapter.ViewHolder>() {
     private val layoutInflater = LayoutInflater.from(context)

    class ViewHolder(itemView : View?) : RecyclerView.ViewHolder(itemView!!){
        val templateTitle = itemView?.findViewById<TextView>(R.id.tvw_template_title)
        val templateMessage = itemView?.findViewById<TextView>(R.id.tvw_template_message)
        var navController : NavController? = null

        var templateId : String? = null
        init {
            itemView?.setOnClickListener {
                navController = Navigation.findNavController(itemView)
                val bundle = Bundle()
                bundle.putString(ARG_TEMPLATE_ID, templateId!!)

                navController!!.navigate(R.id.action_smsTemplatesFragment_to_templateFragment, bundle)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = layoutInflater.inflate(R.layout.item_templates_list, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val template = templates[position]
        holder.templateTitle?.text = template.sms_title
        holder.templateMessage?.text = template.message
        holder.templateId = template.id
    }

    override fun getItemCount(): Int {
        return templates.size
    }

    companion object{
        private val ARG_TEMPLATE_ID : String = "TEMPLATE_ID"
    }
}