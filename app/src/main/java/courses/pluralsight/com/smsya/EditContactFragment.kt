package courses.pluralsight.com.smsya

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import courses.pluralsight.com.smsya.adapters.ContactsRecyclerAdapter
import courses.pluralsight.com.smsya.data.Contact
import courses.pluralsight.com.smsya.data.repositories.ContactsRepository

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [EditContactFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EditContactFragment : BottomSheetDialogFragment() {


    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var contactsRepository: ContactsRepository
    private lateinit var old_contact : Contact
    private lateinit var contactName : EditText
    private lateinit var contactPhone : EditText
    private lateinit var contactCity : EditText
    private var contactId : String? = null
    private lateinit var navController : NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)

            contactId = it.getString(ARG_CONTACT_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_contact, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        contactsRepository = ContactsRepository()
        navController = findNavController()

        old_contact = contactsRepository.getContact(contactId!!)

        contactName = view.findViewById<EditText>(R.id.edittext_edit_name)
        contactPhone = view.findViewById<EditText>(R.id.edittext_edit_phone)
        contactCity = view.findViewById(R.id.edittext_edit_city)

        if(contactId != null){
            contactName.setText(old_contact.fname)
            contactPhone.setText(old_contact.mob_no)
            contactCity.setText(old_contact.city)
        }

        view.findViewById<Button>(R.id.btn_save_contact).setOnClickListener {
            Log.d("EDIT_CONTACT_FRAGMENT", contactId!!)
            if(contactId != null) {
//                contactsRepository.updateContact(
//                    Contact(
//                        contactId!!,
//                        contactName.text.toString(),
//                        contactPhone.text.toString(),
//                        old_contact.id,
//                        contactCity.text.toString()
//                    )
//                )

                navController.navigateUp()

                Toast.makeText(requireActivity(), "Contact Updated", Toast.LENGTH_LONG).show()
            }
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment EditContactFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            EditContactFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }


        private val ARG_CONTACT_ID : String = "CONTACT_ID"

    }
}