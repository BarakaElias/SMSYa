package courses.pluralsight.com.smsya.adapters

import android.content.Context
import android.os.Bundle
import android.text.Layout
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import courses.pluralsight.com.smsya.R
import courses.pluralsight.com.smsya.data.Contact
import courses.pluralsight.com.smsya.viewmodels.MainActivityViewModel

class ContactsRecyclerAdapter(private val context: Context, private val viewModel : MainActivityViewModel,  private val contacts : List<Contact>, private val addressBookId : String) : RecyclerView.Adapter<ContactsRecyclerAdapter.ViewHolder>() {
    private val layoutInflater = LayoutInflater.from(context)



    inner class ViewHolder(itemView : View?) : RecyclerView.ViewHolder(itemView!!){
        val textContactName = itemView?.findViewById<TextView>(R.id.txt_contact_name)
        val textContactNumber = itemView?.findViewById<TextView>(R.id.txt_contact_number)
        var navController : NavController? = null
        var contactId : String? = null

        init {
            itemView?.setOnClickListener {
                navController = Navigation.findNavController(itemView)
                val bundle = Bundle()
                bundle.putString(ARG_CONTACT_ID, contactId!!)
                Log.d(TAG, "Addressbook id: ${addressBookId}")
                bundle.putString(ARG_ADDRESSBOOK_ID, addressBookId)

                val selectedContact : Contact? = contacts.find { contact -> contact.id.equals(contactId) }
                if(selectedContact !== null){
                    viewModel.setContact(selectedContact)
                }

                navController!!.navigate(R.id.action_contactsFragment_to_contactFragment, bundle)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = layoutInflater.inflate(R.layout.item_contacts_list, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val contact = contacts[position]
        holder.textContactName?.text = contact.fname
        holder.textContactNumber?.text = contact.mob_no
        holder.contactId = contact.id
    }

    override fun getItemCount(): Int {
        return contacts.size
    }

    companion object{
        private const val ARG_CONTACT_ID : String = "CONTACT_ID"
        private const val ARG_ADDRESSBOOK_ID : String = "ADDRESSBOOK_ID"
        private const val TAG : String = "CONTACTS_RECYCLER_ADAPTER"
    }
}