package courses.pluralsight.com.smsya

import android.Manifest
import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.SystemClock
import android.provider.ContactsContract
import android.provider.ContactsContract.CommonDataKinds.Phone
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import courses.pluralsight.com.smsya.data.RecipientContact
import courses.pluralsight.com.smsya.data.bodies.MessageRequest
import courses.pluralsight.com.smsya.data.bodies.MessageResponse
import courses.pluralsight.com.smsya.data.repositories.ContactsRepository
import courses.pluralsight.com.smsya.databinding.FragmentQuicksmsBinding
import courses.pluralsight.com.smsya.services.MessageService
import courses.pluralsight.com.smsya.services.ServiceBuilder
import courses.pluralsight.com.smsya.viewmodels.MainActivityViewModel
import courses.pluralsight.com.smsya.viewmodels.MainActivityViewModelFactory
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [QuicksmsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class QuicksmsFragment : Fragment(){
    private lateinit var tvwPhoneNumber: TextView

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var scheduleDate : TextView
    private lateinit var sendSmsButton : Button
    private var senderNameValue : String = ""
    private lateinit var editTextPhoneNumber : EditText
    private var templateNameValue : String = ""
    private val contactsRepository : ContactsRepository = ContactsRepository()
    private lateinit var edittext_TextMessage : EditText
    private lateinit var edittext_phonenumberLayout : TextInputLayout

    private val viewModel : MainActivityViewModel by activityViewModels {
        MainActivityViewModelFactory(contactsRepository)
    }

//    private lateinit var _binding : FragmentQuicksmsBinding



    //passwords
    private val username : String = "3f995b9eff643b85"
    private val password : String = "MzVlNGEyMWFjZjE4ZmM3NGQ3ODg0YzdhZjJlZDAyZTVhOWQ1NjAyMjBkNTZiNmNmMDE1OTg4NGVhZjI1NWIyZA=="





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
        return inflater.inflate(R.layout.fragment_quicksms, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapterSIDs = ArrayAdapter<String>(requireActivity(), android.R.layout.simple_spinner_item, viewModel.getSenderNamesForSpinner())
        val adaptersmsTemplates = ArrayAdapter<String>(requireActivity(), android.R.layout.simple_spinner_item, viewModel.getSmsTemplatesForSpinner())
        adapterSIDs.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        adaptersmsTemplates.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        view.findViewById<Spinner>(R.id.spinner_sender_names).adapter = adapterSIDs
        view.findViewById<Spinner>(R.id.spinner_sms_templates).adapter = adaptersmsTemplates
        view.findViewById<Spinner>(R.id.spinner_sender_names).onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                senderNameValue = parent?.getItemAtPosition(position).toString()
                Log.d(TAG, "Selected sender id: ${senderNameValue}")

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                senderNameValue = "INFO"
            }
        }
        view.findViewById<Spinner>(R.id.spinner_sms_templates).onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                templateNameValue = parent?.getItemAtPosition(position).toString()
                Log.d(TAG, "Selected template: ${templateNameValue}")
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                senderNameValue = ""

            }
        }
        edittext_TextMessage = view.findViewById(R.id.edittext_text_message)
        editTextPhoneNumber = view.findViewById(R.id.edittext_phone_number)
        edittext_phonenumberLayout = view.findViewById(R.id.textInputLayoutPhonenumber)
//        val drawablePhone = ContextCompat.getDrawable(requireActivity(), R.drawable.ic_baseline_person_add_24)

//        edittext_phonenumberLayout.setOnTouchListener { v, event ->
//            // Check if the user clicked on the drawable
//            if (event.action == MotionEvent.ACTION_UP && event.rawX >= (edittext_phonenumberLayout.right - drawablePhone?.bounds?.width()!!)) {
//                // Launch an intent to open the phone book
//                val intent = Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI)
//                startActivityForResult(intent, PICK_CONTACT_REQUEST_CODE)
//                return@setOnTouchListener true
//            }
//            false
//        }
//
        val imageViewPhoneNumber : ImageView = view.findViewById(R.id.imageViewSelectPhoneNumber)
        imageViewPhoneNumber.setOnClickListener{
            val intent = Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI)
            startActivityForResult(intent, PICK_CONTACT_REQUEST_CODE)
        }


        // Set the drawable as the end compound drawable of the EditText
//        editTextPhoneNumber.setCompoundDrawablesWithIntrinsicBounds(null, null, drawablePhone, null)



        scheduleDate = view.findViewById(R.id.edittext_schedule_date)
        val currentTime = Calendar.getInstance().time
        val currentDate = DateFormat.getDateInstance(DateFormat.FULL).format(currentTime)
        sendSmsButton = view.findViewById<Button>(R.id.btn_send_quicksms)



        sendSmsButton.setOnClickListener {
            Log.d(TAG, "Sending sms")
//            if(formValidation(_binding.tvwPhoneNumber.text.toString(), senderNameValue, _binding.edittextTextMessage.text.toString())){
//                sendSMS()
//            }else{
//                Toast.makeText(requireActivity(), "Please fill the form correctly",Toast.LENGTH_LONG).show()
//            }
            sendSMS()
        }



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



    }

    // Handle the result of the phone book selection
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_CONTACT_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val contactUri = data.data
            val projection = arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER)

            // Get the selected phone number from the phone book
            contactUri?.let {
                val cursor = requireActivity().contentResolver.query(it, projection, null, null, null)
                cursor?.let {
                    if (it.moveToFirst()) {
                        val phoneNumberIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                        if(phoneNumberIndex >= 0){
                            val phoneNumber = it.getString(phoneNumberIndex)
                            val formattedPhonenumber = formatPhoneNumber(phoneNumber)
                            editTextPhoneNumber.setText(formattedPhonenumber)

                        }

                        // Populate the EditText with the selected phone number
                    }
                    it.close()
                }
            }
        }
    }

    private fun formatPhoneNumber(phoneNumber: String): String {
        val phone = phoneNumber.replace("\\s".toRegex(), "")
        if(phone.startsWith("255")){
            return phone
        }
        if(phone.startsWith("+")){
            return phone.substring(1)
        }
        if(phone.startsWith("0")){
            return "255${phone.substring(1)}"
        }
        return phone
    }


    private fun formValidation(phoneNumber : String, senderName: String, textMessage:String) : Boolean {
        return phoneNumber.isNotEmpty() && senderName.isNotEmpty() && textMessage.isNotEmpty()
    }

    private fun sendSMS(){
        val sendSmsCall : MessageService = ServiceBuilder.buildService(MessageService::class.java)
        val messageRequest : MessageRequest = MessageRequest(
            source_addr = senderNameValue,
            schedule_time = scheduleDate.text.toString(),
            message = edittext_TextMessage.text.toString(),
            encoding = "0",
            recipients = listOf(RecipientContact(1,editTextPhoneNumber.text.toString()))
        )

        sendSmsCall.sendMessage("https://apisms.beem.africa/v1/send",messageRequest).enqueue(
            object : Callback<MessageResponse>{
                override fun onResponse(
                    call: Call<MessageResponse>,
                    response: Response<MessageResponse>
                ) {
                    if(response.isSuccessful){
                        val body : MessageResponse? = response.body()

                        Toast.makeText(requireActivity(), body?.message, Toast.LENGTH_LONG).show()
                        Log.d(TAG, "Message sent succesfully: ${body.toString()}")

                        val alarmManager = requireActivity().getSystemService(ALARM_SERVICE) as AlarmManager
                        val intent = Intent(requireActivity(), DeliveryReportService::class.java)
                        intent.putExtra("DEST_ADDR", editTextPhoneNumber.text.toString())
                        intent.putExtra("REQUEST_ID", body?.request_id.toString())
                        val pendingIntent = PendingIntent.getService(requireActivity(), 0,intent, PendingIntent.FLAG_UPDATE_CURRENT)

                        val delay = 1 * 60 * 1000 //5 minutes in milliseconds
                        val triggerTime = SystemClock.elapsedRealtime() + delay
                        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerTime, pendingIntent)


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

    companion object {

        private const val PICK_CONTACT_REQUEST_CODE : Int = 1
        private const val REQUEST_READ_CONTACTS_PERMISSION = 0
        private const val TAG : String = "QUICK_SMS_FRAGMENT"
    }
}