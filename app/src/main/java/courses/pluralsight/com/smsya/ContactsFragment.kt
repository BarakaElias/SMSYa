package courses.pluralsight.com.smsya

import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import androidx.navigation.NavController

import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder

import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.progressindicator.CircularProgressIndicator
import courses.pluralsight.com.smsya.adapters.ContactsRecyclerAdapter
import courses.pluralsight.com.smsya.data.AddressBook
import courses.pluralsight.com.smsya.data.Contact
import courses.pluralsight.com.smsya.data.repositories.ContactsRepository
import courses.pluralsight.com.smsya.viewmodels.MainActivityViewModel
import courses.pluralsight.com.smsya.viewmodels.MainActivityViewModelFactory


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ContactsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ContactsFragment : Fragment() {
    private lateinit var addressBook: AddressBook
    private lateinit var navController: NavController
    private val contactsRepository : ContactsRepository = ContactsRepository()

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var addressBookId : String? = null
    private lateinit var filterContactsEditText : EditText
    private lateinit var contactsRecyclerView : RecyclerView
    private lateinit var contactsInAddressBook : List<Contact>
    private lateinit var tvwEmpty : TextView
    private lateinit var loaderContacts : CircularProgressIndicator
    private lateinit var tvw_contact_count : TextView
    private lateinit var tvw_addressBookName : TextView
    private lateinit var tvw_addressBookDesc : TextView
    private lateinit var txv_addressbook_date_created : TextView
    private lateinit var contacts : LiveData<Contact>

    private val viewModel : MainActivityViewModel by activityViewModels {
        MainActivityViewModelFactory(contactsRepository)
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)

            addressBookId = it.getString(ARG_ADDRESSBOOK_ID)
        }


    }


    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onresume has been called")
        contactsRecyclerView.adapter?.notifyDataSetChanged()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true)
        viewModel
        return inflater.inflate(R.layout.fragment_contacts, container, false)
    }






    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addressBook = viewModel.getAddressBook(addressBookId!!)
        viewModel.getContactsInAddressBook(addressBookId!!)


        navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)
//
//        view.findViewById<Toolbar>(R.id.defaultToolbar).setupWithNavController(navController, appBarConfiguration)
//        view.findViewById<CollapsingToolbarLayout>(R.id.collapsingToolbar)?.title = addressBook.addressbook
        tvwEmpty = view.findViewById(R.id.tvw_empty_text)
        loaderContacts = view.findViewById(R.id.loader_contacts)
        tvw_addressBookName = view.findViewById(R.id.tvw_addressbookName)
        tvw_addressBookDesc = view.findViewById(R.id.tvw_addressbookDesc)
        tvw_contact_count = view.findViewById(R.id.tvw_contact_count)
        txv_addressbook_date_created = view.findViewById(R.id.txv_addressbook_date_created)

        tvw_addressBookName.setText(addressBook.addressbook)
        tvw_addressBookDesc.setText(addressBook.description)
        txv_addressbook_date_created.setText(addressBook.created)

        viewModel.isLoading.observe(viewLifecycleOwner){
            if(it){
                loaderContacts.visibility = View.VISIBLE
                tvwEmpty.visibility = View.GONE
                contactsRecyclerView.visibility = View.GONE
            }else{
                loaderContacts.visibility = View.GONE
                Log.d(TAG, "ADdressbook length: ${viewModel.contacts.value?.size}")
                if(viewModel.contacts.value?.isEmpty() == true){
                    Log.d(TAG, "No contacts in this addressbook")
                    tvwEmpty.visibility = View.VISIBLE
                    contactsRecyclerView.visibility = View.GONE
                }else{
                    Log.d(TAG, "There are contacts in this addressbook")

                    tvwEmpty.visibility = View.GONE
                    contactsRecyclerView.visibility = View.VISIBLE
                }
            }
        }


//        view.findViewById<FloatingActionButton>(R.id.fabContacts).setOnClickListener {
//            val bundle = Bundle()
//            bundle.putInt("ADDRESS_BOOK_Id", addressBookId!!.toInt())
////            navController!!.navigate(R.id.action_contactsFragment_to_addContact, bundle)
////            navController.navigate(R.id.action_contactsFragment_to_scrollingFragment)
//        }

        contactsRecyclerView = view.findViewById(R.id.recyclerViewContacts)
//        filterContactsEditText = view.findViewById(R.id.filterContactsEditText)
        contactsRecyclerView.adapter = ContactsRecyclerAdapter(requireActivity(), viewModel, emptyList(), addressBookId.toString())
        contactsRecyclerView.layoutManager = LinearLayoutManager(activity)


        viewModel.errorMessage.observe(viewLifecycleOwner){
            errorMessage ->
            Toast.makeText(requireActivity(), errorMessage, Toast.LENGTH_LONG).show()
        }

        viewModel.contacts.observe(viewLifecycleOwner){ items ->
            Log.d(TAG, items.toString())
            contactsInAddressBook = items
            contactsRecyclerView.adapter = ContactsRecyclerAdapter(requireActivity(),viewModel, items, addressBookId.toString())
            tvw_contact_count.text = items.size.toString()
        }

//        if(!contactsInAddressBook.isEmpty()){
//            tvwEmpty.visibility = View.GONE
//        }else{
//            contactsRecyclerView.visibility = View.GONE
//        }
    }





    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.addressbooks_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_addressbook_add -> {
                val bundle = Bundle()
                bundle.putString(ARG_ADDRESSBOOK_ID, addressBookId!!)
                navController!!.navigate(R.id.action_contactsFragment_to_addContact, bundle)
            }
            R.id.menu_addressbook_edit -> {
                val bundle = Bundle()
                Log.d(TAG, "Edit addressbook parameter in contacts: ${addressBookId}")
                bundle.putString(ARG_ADDRESSBOOK_ID, addressBookId!!)
                navController!!.navigate(R.id.action_contactsFragment_to_editAddressbookFragment, bundle)
            }
            R.id.menu_addressbook_delete -> {
                MaterialAlertDialogBuilder(requireActivity()).setTitle(getString(R.string.txt_delete_addressbook)).setMessage("${getString(R.string.txt_are_you_sure_delete)} ${addressBook?.addressbook}")
                    .setPositiveButton(getString(R.string.txt_delete), DialogInterface.OnClickListener { dialog, which -> doSomething() })
                    .setNegativeButton(getString(R.string.txt_cancel), DialogInterface.OnClickListener { dialog, which -> false })
                    .show()
            }
        }
        return true
    }

    private fun doSomething() {
        Log.d(TAG, "Deleting addressbook: ${addressBook.addressbook}")
        viewModel.deleteAddressBook(addressBookId!!)

        navController.navigateUp()
        Toast.makeText(requireActivity(), "${addressBook.addressbook} was deleted!",Toast.LENGTH_LONG)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ContactsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ContactsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }


            private val ARG_ADDRESSBOOK_ID : String = "addressBookId"
        private val TAG : String = "CONTACTS_FRAGMENT"

    }
}