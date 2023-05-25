package courses.pluralsight.com.smsya

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.activityViewModels
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import courses.pluralsight.com.smsya.data.AddressBook
import courses.pluralsight.com.smsya.data.RecipientContact
import courses.pluralsight.com.smsya.data.bodies.MessageRequest
import courses.pluralsight.com.smsya.data.bodies.MessageResponse
import courses.pluralsight.com.smsya.data.repositories.ContactsRepository
import courses.pluralsight.com.smsya.services.MessageService
import courses.pluralsight.com.smsya.services.ServiceBuilder
import courses.pluralsight.com.smsya.viewmodels.MainActivityViewModel
import courses.pluralsight.com.smsya.viewmodels.MainActivityViewModelFactory
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CampaignFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CampaignFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var senderNameValue : String = "INFO"
    private var addressBookName : String = ""
    private var param2: String? = null
    private lateinit var message : EditText
    private lateinit var scheduleDate : TextView
    private val contactsRepository : ContactsRepository = ContactsRepository()
    private val viewModel : MainActivityViewModel by activityViewModels {
        MainActivityViewModelFactory(contactsRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_campaign, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //getting view
        val spinner : Spinner = view.findViewById(R.id.spinner_sms_addressbook)
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                addressBookName = parent?.getItemAtPosition(position).toString()
                val addressBook : AddressBook? = viewModel.addressBooks.value?.find { addressBk -> addressBk.addressbook.equals(addressBookName) }
                viewModel.getContactsInAddressBook(addressBook!!.id)

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                addressBookName = "INFO"
            }
        }
        val senderNameSpinner : Spinner = view.findViewById(R.id.spinner_campaign_sender_name)
        message = view.findViewById(R.id.edittext_campaign_text_message)
        scheduleDate = view.findViewById(R.id.edittext_campaign_schedule_date)
        val btnSendCampaign : Button = view.findViewById(R.id.btn_send_campaign)

        //setting up spinner
        val adapterAddressbook = ArrayAdapter<String>(requireActivity(), android.R.layout.simple_spinner_item, viewModel.getAddressbooksForSpinner())
        adapterAddressbook.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapterAddressbook

        

        val adapterSenderNames = ArrayAdapter<String>(requireActivity(), android.R.layout.simple_spinner_dropdown_item, viewModel.getSenderNamesForSpinner())
        adapterSenderNames.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        senderNameSpinner.adapter = adapterSenderNames
        senderNameSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                senderNameValue = parent?.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                senderNameValue = "INFO"
            }
        }

        //setting up schedule
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Schedule date")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()

        val timePicker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setHour(12)
            .setMinute(10)
            .setTitleText("Select Time")
            .build()

        datePicker.addOnPositiveButtonClickListener {
            val date = Date(it)
            val format = SimpleDateFormat("yyy-MM-dd")
            scheduleDate.setText(format.format(date))
            timePicker.show(childFragmentManager,"TIMETAG")
        }

        timePicker.addOnPositiveButtonClickListener {
            val date = scheduleDate.text.toString()
            scheduleDate.setText("${date} ${timePicker.hour}:${timePicker.minute}")
        }
        scheduleDate.setOnClickListener {
            datePicker?.show(childFragmentManager,"TAG")
        }





        btnSendCampaign.setOnClickListener {

            sendCampaign()
        }
    }



    private fun sendCampaign() {
        val sendSmsCall : MessageService = ServiceBuilder.buildService(MessageService::class.java)
        //find the addressbook
        val addressBook : AddressBook? = viewModel.addressBooks.value?.find { addressBk -> addressBk.addressbook.equals(addressBookName) }
        Log.d(TAG, "Addressbook found: ${addressBook.toString()}")
//        viewModel.getContactsInAddressBook(addressBook!!.id)
        if(!viewModel.contacts.value.isNullOrEmpty()){
            val contacts = viewModel.contacts.value
            Log.d(TAG, "Contacts of addressbooks: ${contacts.toString()}")
            var i = 0
            val recipients : MutableList<RecipientContact> = mutableListOf()
            for(contact in contacts!!){
                recipients.add(RecipientContact(i++, contact.mob_no))
            }
            val messageRequest : MessageRequest = MessageRequest(
                source_addr = senderNameValue,
                schedule_time =  scheduleDate.text.toString(),
                message = message.text.toString(),
                encoding = "0",
                recipients = recipients
            )
            sendSmsCall.sendMessage("https://apisms.beem.africa/v1/send",messageRequest).enqueue(
                object : Callback<MessageResponse> {
                    override fun onResponse(
                        call: Call<MessageResponse>,
                        response: Response<MessageResponse>
                    ) {
                        if(response.isSuccessful){
                            val body : MessageResponse? = response.body()

                            Toast.makeText(requireActivity(), body?.message, Toast.LENGTH_LONG).show()
                            Log.d(TAG, "Message sent succesfully: ${body.toString()}")
                        }else{
                            val error_body = response.errorBody()
                            val jsonBody = JSONObject(error_body?.string())
                            Toast.makeText(requireActivity(), jsonBody.getString("message"), Toast.LENGTH_LONG).show()
                            Log.d(TAG, "Failed response: ${error_body?.string()}")
                        }
                    }

                    override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                        Log.d(TAG + "ERROR", "DOES NOT WORK")
                    }

                }
            )
        }

    }

    companion object {
        private const val TAG : String = "CAMPAIGN_FRAGMENT"
    }
}