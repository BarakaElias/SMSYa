package courses.pluralsight.com.smsya

import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import courses.pluralsight.com.smsya.data.SmsTemplate
import courses.pluralsight.com.smsya.data.bodies.SmsTemplatePostRequest
import courses.pluralsight.com.smsya.data.repositories.ContactsRepository
import courses.pluralsight.com.smsya.viewmodels.MainActivityViewModel
import courses.pluralsight.com.smsya.viewmodels.MainActivityViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [TemplateFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TemplateFragment : Fragment() {
    private var template: SmsTemplate? = null
    private val contactsRepository: ContactsRepository = ContactsRepository()
    private lateinit var btnSaveTemplate : Button
    private val viewModel : MainActivityViewModel by activityViewModels {
        MainActivityViewModelFactory(contactsRepository)
    }



    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var templaetId : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)

            templaetId = it.getString(ARG_TEMPLATE_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true)

        return inflater.inflate(R.layout.fragment_template, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val editTitle = view.findViewById<EditText>(R.id.edittext_template_title)
        val editMessage = view.findViewById<EditText>(R.id.edittext_template_message)

        btnSaveTemplate = view.findViewById(R.id.btn_save_template)
        btnSaveTemplate.setOnClickListener {
            if(templaetId !== null){
                //Update template
                viewModel.updateSmsTemplate(templaetId!!, SmsTemplatePostRequest(editMessage.text.toString(), editTitle.text.toString()))
            }else{
                //Create templaet
                viewModel.createSmsTemplate(SmsTemplatePostRequest(message = editMessage.text.toString(), sms_title = editTitle.text.toString()))
            }
        }

        if(templaetId !== null){
            template = viewModel.getSmsTemplate(templaetId!!)

            editTitle.setText(template?.sms_title)
            editMessage.setText(template?.message)

        }


        view.findViewById<Button>(R.id.btn_save_template).setOnClickListener {
            val title : String = editTitle.text.toString()
            val message : String = editMessage.text.toString()
            saveTemplate(title, message)
        }

    }

    private fun saveTemplate(title: String, message: String) {
        viewModel.createSmsTemplate(SmsTemplatePostRequest(sms_title = title, message = message))
        Toast.makeText(requireActivity(), "Saving template",Toast.LENGTH_LONG).show()
        findNavController().navigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_sms_template, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_delete_template -> {
                MaterialAlertDialogBuilder(requireActivity()).setTitle("Are you sure?").setMessage("Are you sure you want to delete this template?")
                    .setPositiveButton("DELETE", DialogInterface.OnClickListener { dialog, which -> deleteTemplate() })
                    .setNegativeButton("CANCEL", DialogInterface.OnClickListener { dialog, which -> false })
                    .show()
            }
        }
        return true
    }

    private fun deleteTemplate() {
        if(template !== null){
            viewModel.deleteSmsTemplate(templaetId!!)
            findNavController().navigateUp()
            Toast.makeText(requireActivity(), "Deleting Template", Toast.LENGTH_LONG).show()
        }
        Toast.makeText(requireActivity(), "No Template", Toast.LENGTH_LONG).show()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment TemplateFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            TemplateFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

        private val ARG_TEMPLATE_ID : String = "TEMPLATE_ID"

    }
}