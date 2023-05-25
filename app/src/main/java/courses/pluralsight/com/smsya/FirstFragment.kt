package courses.pluralsight.com.smsya

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import courses.pluralsight.com.smsya.data.bodies.SmsBalanceResponse
import courses.pluralsight.com.smsya.data.repositories.ContactsRepository
import courses.pluralsight.com.smsya.databinding.FragmentFirstBinding
import courses.pluralsight.com.smsya.services.MessageService
import courses.pluralsight.com.smsya.services.ServiceBuilder
import courses.pluralsight.com.smsya.viewmodels.MainActivityViewModel
import courses.pluralsight.com.smsya.viewmodels.MainActivityViewModelFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null
    private lateinit var btnContact : Button
    private lateinit var btnSMS : Button
    private lateinit var toolBar : Toolbar
    private lateinit var checkBalanceProgressBar: ProgressBar
    private val contactsRepository : ContactsRepository = ContactsRepository()
    private val viewModel : MainActivityViewModel by activityViewModels {
        MainActivityViewModelFactory(contactsRepository)
    }

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //setup toolbar
        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        checkBalanceProgressBar = view.findViewById(R.id.checkBalanceProgressBar)
        checkBalance()
       // view.findViewById<Toolbar>(R.id.defaultToolbar).setupWithNavController(navController, appBarConfiguration)
        viewModel.isLoading.observe(viewLifecycleOwner){ isLoading ->
            if(isLoading){
                checkBalanceProgressBar.visibility = View.VISIBLE
                _binding?.txtSmsCreditBalance?.visibility = View.GONE
            }else{
                checkBalanceProgressBar.visibility = View.GONE
                _binding?.txtSmsCreditBalance?.visibility = View.VISIBLE
            }
        }



        btnContact = view.findViewById(R.id.btn_contacts)
        btnSMS = view.findViewById(R.id.btn_sms)

        btnContact.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_contactListsFragment)
        }
        btnSMS.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SMSFragment)
        }

        val btnSN = view.findViewById<Button>(R.id.btn_sender_names)
        btnSN.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_senderNamesFragment)
        }
    }

    private fun checkBalance(){
        Log.d(TAG, "Check balance() called")
        val checkBalanceCall : MessageService = ServiceBuilder.buildService(MessageService::class.java)
        checkBalanceCall.checkBalance().enqueue(
            object : Callback<SmsBalanceResponse>{
                override fun onResponse(
                    call: Call<SmsBalanceResponse>,
                    response: Response<SmsBalanceResponse>
                ) {
                    Log.d(TAG, "Check balance success")
                    if(response.isSuccessful){
                        val body = response.body()
                        _binding?.txtSmsCreditBalance?.text = body?.data?.get("credit_balance").toString()
                    }
                }

                override fun onFailure(call: Call<SmsBalanceResponse>, t: Throwable) {
                    Log.d(TAG, "Check balance fail")

                }
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object{
        private val TAG : String = "FIRST_FRAGMENT"
    }
}