package courses.pluralsight.com.smsya

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import courses.pluralsight.com.smsya.data.Contact
import courses.pluralsight.com.smsya.data.bodies.ContactDeleteRequest
import courses.pluralsight.com.smsya.data.repositories.ContactsRepository
import courses.pluralsight.com.smsya.viewmodels.MainActivityViewModel
import courses.pluralsight.com.smsya.viewmodels.MainActivityViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class ContactFragment : Fragment() {
    private lateinit var contactName : TextView
    private lateinit var contactPhone : TextView
    private lateinit var contactCity : TextView
    private lateinit var contactEmail : TextView
    var contactId : String? = null
    var addressBookId : String? = null
    private val contactsRepository : ContactsRepository = ContactsRepository()
    var contact : Contact? = null
    lateinit var navController : NavController

    private val viewModel : MainActivityViewModel by activityViewModels {
        MainActivityViewModelFactory(contactsRepository)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let{
            contactId = it.getString(ARG_CONTACT_ID)
            addressBookId = it.getString(ARG_ADDRESSBOOK_ID)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
//        contact = viewModel.getContact(contactId!!)
        return inflater.inflate(R.layout.fragment_contact, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)



        contactName = view.findViewById(R.id.tvw_contact_name)
        contactPhone = view.findViewById(R.id.tvw_contact_phone)
        contactCity = view.findViewById(R.id.tvw_contact_city)
        contactEmail = view.findViewById(R.id.tvw_contact_email)

        viewModel.contact.observe(viewLifecycleOwner){
                contact ->
            contactName.text = "${contact?.lname}, ${contact?.fname}"
            contactPhone.text = contact?.mob_no
            contactCity.text = contact?.city
            contactEmail.text = contact?.email
        }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.contacts_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_contact_edit -> {
                editContact()
                return true
            }
            R.id.menu_contact_delete -> {
                deleteContact()
                return true
            }
        }
        return true
    }

    private fun editContact() {
        //open dialog to edit contact
        val bundle = Bundle()
        bundle.putString(ARG_CONTACT_ID,contactId!!)
        bundle.putString(ARG_ADDRESSBOOK_ID, addressBookId)
        Log.d(TAG, "Addressbook id: ${addressBookId}, Contact id: ${contactId}")
        navController.navigate(R.id.action_contactFragment_to_addContact, bundle)
    }

    private fun deleteContact() {
        MaterialAlertDialogBuilder(requireActivity()).setTitle(getString(R.string.txt_delete_contact)).setMessage("${getString(R.string.txt_are_you_sure_delete)} ${contact?.fname}?")
            .setPositiveButton(getString(R.string.txt_delete)) { dialog, which -> handleDelete() }
            .setNegativeButton(getString(R.string.txt_cancel)) { dialog, which -> false }
            .show()

    }

    private fun handleDelete() {
        Log.d(TAG, "Deleting contact with id: ${contactId}")
        viewModel.deleteContact(ContactDeleteRequest(addressbook_id = listOf(addressBookId!!), contacts_id = listOf(contactId!!)))
//        navController.navigateUp()
    }


    companion object{
        private const val ARG_CONTACT_ID : String = "CONTACT_ID"
        private const val TAG : String = "CONTACT_FRAGMENT"
        private const val ARG_ADDRESSBOOK_ID : String = "ADDRESSBOOK_ID"

    }

}