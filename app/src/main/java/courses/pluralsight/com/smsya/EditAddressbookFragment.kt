package courses.pluralsight.com.smsya

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
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.progressindicator.CircularProgressIndicator
import courses.pluralsight.com.smsya.data.AddressBook
import courses.pluralsight.com.smsya.data.bodies.AddressBookPostRequest
import courses.pluralsight.com.smsya.data.repositories.ContactsRepository
import courses.pluralsight.com.smsya.viewmodels.MainActivityViewModel
import courses.pluralsight.com.smsya.viewmodels.MainActivityViewModelFactory

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [EditAddressbookFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EditAddressbookFragment : BottomSheetDialogFragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var addressBookId : String? = null
    private lateinit var fragmentTitle : TextView
    private lateinit var addressBook: AddressBook
    private lateinit var addressBookTitle : EditText
    private lateinit var addressBookDesc : EditText
    private lateinit var loaderEditAddressbook : CircularProgressIndicator
    private lateinit var constraintLayout: ConstraintLayout
    private lateinit var fragmentButton : Button
    private val contactsRepository : ContactsRepository = ContactsRepository()
    private val viewModel : MainActivityViewModel by activityViewModels {
        MainActivityViewModelFactory(contactsRepository)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)

            addressBookId = it.getString(ARG_ADDRESSBOOK_ID)
            Log.d(TAG, "Addressbook ID: ${addressBookId}")
        }
    Log.d(TAG, "iNSIDE EDIT FRAGMENT")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_addressbook, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentTitle = view.findViewById(R.id.tvw_ea_title)
        fragmentButton = view.findViewById(R.id.btn_edit_addressbook_save)
        addressBookTitle = view.findViewById(R.id.edittext_edit_addressbook_name)
        addressBookDesc = view.findViewById(R.id.edittext_edit_addressbook_desc)
        loaderEditAddressbook = view.findViewById(R.id.loader_edit_addressbook)
        constraintLayout = view.findViewById(R.id.bottom_sheet)
        viewModel.isLoading.observe(viewLifecycleOwner){
            if(it){
                loaderEditAddressbook.visibility = View.VISIBLE
                constraintLayout.visibility = View.GONE
            }else{
                loaderEditAddressbook.visibility = View.GONE
                constraintLayout.visibility = View.VISIBLE
            }
        }

        Log.d(TAG, "Adrresbook param: ${addressBookId}")

        viewModel.successMessage.observe(viewLifecycleOwner){
            message -> Toast.makeText(requireActivity(), message, Toast.LENGTH_LONG).show()
        }

        if(addressBookId.isNullOrEmpty()){
            fragmentTitle.text = getString(R.string.txt_create_addressbook)
            fragmentButton.text = getString(R.string.txt_create_addressbook)
        }else{
            addressBook = viewModel.getAddressBook(addressBookId!!)
            Log.d(TAG, "Addressbook: ${addressBook.toString()}")
            addressBookTitle.setText(addressBook.addressbook)
            addressBookDesc.setText(addressBook.description)
        }

        viewModel.errorMessage.observe(viewLifecycleOwner){
                errorMessage ->
                Toast.makeText(requireActivity(), errorMessage, Toast.LENGTH_LONG).show()
    }
        fragmentButton.setOnClickListener {
            if(addressBookId.isNullOrEmpty()){
                //Create addressbook
                val res : Boolean = viewModel.addAddressbook(AddressBookPostRequest(addressBookTitle.text.toString(), addressBookDesc.text.toString()))
                Log.d(TAG, "Addressbook add: Value of res is $res")
                if(res){
                    findNavController().navigateUp()
                }
            }else{
                //Update addressbook
                val res : Boolean = viewModel.editAddressbook(addressBookId!!, AddressBookPostRequest(addressBookTitle.text.toString(), addressBookDesc.text.toString()))
                if(res){
                    findNavController().navigateUp()
                }
            }
        }

    }

    companion object {
       private const val ARG_ADDRESSBOOK_ID : String = "addressBookId"
        private const val TAG : String = "EDIT_ADDRESSBOOK_FRAGMENT"
    }
}