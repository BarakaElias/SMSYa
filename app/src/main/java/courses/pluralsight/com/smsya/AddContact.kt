package courses.pluralsight.com.smsya

import android.app.Dialog
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import courses.pluralsight.com.smsya.data.Contact
import courses.pluralsight.com.smsya.data.bodies.ContactPostRequest
import courses.pluralsight.com.smsya.data.repositories.ContactsRepository
import courses.pluralsight.com.smsya.viewmodels.MainActivityViewModel
import courses.pluralsight.com.smsya.viewmodels.MainActivityViewModelFactory

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AddContact.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddContact : BottomSheetDialogFragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private val contactsRepository: ContactsRepository =  ContactsRepository()
    private lateinit var edittextAddFirstName : EditText
    private lateinit var edittextAddLastName : EditText
    private lateinit var edittextAddPhone : EditText
    private lateinit var edittextAddCity : EditText
    private lateinit var edittextAddEmail : EditText
    private lateinit var tvwAddContact : TextView
    private var contact : Contact? = null
    private var addressBookId : String? = null
    private var contactId : String? = null
    private val viewModel : MainActivityViewModel by activityViewModels {
        MainActivityViewModelFactory(contactsRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
            addressBookId = it.getString(ARG_ADDRESSBOOK_ID)
            contactId = it.getString(ARG_CONTACT_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_contact, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val layout = view.findViewById<CoordinatorLayout>(R.id.bottom_sheet_container)
        layout.minimumHeight = Resources.getSystem().displayMetrics.heightPixels

        edittextAddFirstName = view.findViewById(R.id.edittext_add_first_name)
        edittextAddLastName = view.findViewById(R.id.edittext_add_last_name)
        edittextAddPhone = view.findViewById(R.id.edittext_add_phone)
        edittextAddEmail = view.findViewById(R.id.edittext_add_email)
        edittextAddCity = view.findViewById(R.id.edittext_add_city)
        tvwAddContact = view.findViewById(R.id.txt_add_contact)

        if(!contactId.isNullOrEmpty()){
            contact = viewModel.getContact(contactId!!)

            edittextAddFirstName.setText(contact?.fname)
            edittextAddLastName.setText(contact?.lname)
            edittextAddPhone.setText(contact?.mob_no)
            edittextAddEmail.setText(contact?.email)
            edittextAddCity.setText(contact?.city)
            tvwAddContact.text = getString(R.string.txt_edit_contact)
            view.findViewById<Button>(R.id.btn_add_contact).text = getString(R.string.txt_update_contact)
        }

        viewModel.errorMessage.observe(viewLifecycleOwner){
            Log.d(TAG, "eRROR MESSAGE FROM VIEW MODEL: ${it}")
            if(!it.isNullOrEmpty()){
                Toast.makeText(requireActivity().applicationContext, it, Toast.LENGTH_LONG).show()
            }
        }



        view.findViewById<Button>(R.id.btn_add_contact).setOnClickListener {
           Log.d(TAG,"Adding contact: ${edittextAddPhone.text.toString()}")
            val navController = findNavController()

            if(!contactId.isNullOrEmpty()){
                viewModel.editContactInAddressBook(
                    addressBookId!!,
                    contactId!!,
                    ContactPostRequest(
                        fname = edittextAddFirstName.text.toString(),
                        lname = edittextAddLastName.text.toString(),
                        mob_no = edittextAddPhone.text.toString(),
                        mob_no2 = "",
                        email = edittextAddEmail.text.toString(),
                        addressbook_id = listOf(addressBookId!!),
                        city = edittextAddCity.text.toString()
                    )
                )
                viewModel.setContact(
                    Contact(
                        fname = edittextAddFirstName.text.toString(),
                        lname = edittextAddLastName.text.toString(),
                        mob_no = edittextAddPhone.text.toString(),
                        mob_no2 = "",
                        email = edittextAddEmail.text.toString(),
                        city = edittextAddCity.text.toString()
                    )
                )
            }else{
                viewModel.addContactToAddressBook(
                    ContactPostRequest(
                        fname = edittextAddFirstName.text.toString(),
                        lname = edittextAddLastName.text.toString(),
                        mob_no = edittextAddPhone.text.toString(),
                        mob_no2 = "",
                        email = edittextAddEmail.text.toString(),
                        addressbook_id = listOf(addressBookId!!),
                        city = edittextAddCity.text.toString()
                    )
                )
            }
            navController.navigateUp()
        }
    }


    companion object {
        private const val ARG_ADDRESSBOOK_ID : String = "addressBookId"
        private const val ARG_CONTACT_ID : String = "CONTACT_ID"
        private const val TAG : String = "ADD_CONTACT_BOTTOM_SHEET_FRAGMENT"
    }
}