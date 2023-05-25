package courses.pluralsight.com.smsya.adapters

import android.content.Context
import android.os.Bundle
import android.text.Layout.Directions
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import courses.pluralsight.com.smsya.ContactListsFragment
import courses.pluralsight.com.smsya.R
import courses.pluralsight.com.smsya.data.AddressBook
import courses.pluralsight.com.smsya.data.repositories.ContactsRepository

class AddressBookRecyclerAdapter(private val context: Context, private val addressBooks : List<AddressBook>) : RecyclerView.Adapter<AddressBookRecyclerAdapter.ViewHolder>() {
//    val contactsRepository = ContactsRepository(con)
    private val layoutInflater = LayoutInflater.from(context)


    inner class ViewHolder(itemView : View?) : RecyclerView.ViewHolder(itemView!!) {
        val textAddressBookTitle = itemView?.findViewById<TextView>(R.id.txt_addressbook)
        val textAddressBookDescription = itemView?.findViewById<TextView>(R.id.txt_addressbook_description)
        var navController : NavController? = null
        var addressBookId : String? = null


        init {
            itemView?.setOnClickListener {
                navController = Navigation.findNavController(itemView)
                val bundle = Bundle()
                Log.d("ADDRESS_BOOK_RECYCLER_ADAPTER", "Value of addrebook id: ${addressBookId.toString()}")
                bundle.putString(ARG_ADDRESSBOOK_ID,addressBookId.toString()!!)
                navController!!.navigate(R.id.action_contactListsFragment_to_contactsFragment, bundle)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = layoutInflater.inflate(R.layout.item_addressbook_list, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val addressBook = addressBooks[position]
        holder.textAddressBookTitle?.text = addressBook.addressbook
        holder.textAddressBookDescription?.text = addressBook.description
        holder.addressBookId = addressBook.id
    }

    override fun getItemCount(): Int {
        return addressBooks.size
    }

    companion object{
        private val ARG_ADDRESSBOOK_ID : String = "addressBookId"
    }
}