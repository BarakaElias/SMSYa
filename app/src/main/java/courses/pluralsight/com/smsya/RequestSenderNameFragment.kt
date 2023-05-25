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
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import courses.pluralsight.com.smsya.data.SenderName
import courses.pluralsight.com.smsya.data.bodies.SenderNamePostRequest
import courses.pluralsight.com.smsya.data.bodies.SenderNamePostResponse
import courses.pluralsight.com.smsya.services.MessageService
import courses.pluralsight.com.smsya.services.ServiceBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [RequestSenderNameFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RequestSenderNameFragment : BottomSheetDialogFragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    val messageService = ServiceBuilder.buildService(MessageService::class.java)


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
        return inflater.inflate(R.layout.fragment_request_sender_name, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.btn_request_sn).setOnClickListener {
            val sn : String = view.findViewById<EditText>(R.id.edititext_sender_name).text.toString()
            requestSenderName()
        }

    }

    private fun requestSenderName() {
        messageService.requestSenderName(SenderNamePostRequest(senderid = "", sample_content = "")).enqueue(
            object : Callback<SenderNamePostResponse>{
                override fun onResponse(
                    call: Call<SenderNamePostResponse>,
                    response: Response<SenderNamePostResponse>
                ) {
                    Log.d(TAG, "Request sender name succeeded!")
                    findNavController().navigateUp()
                    Toast.makeText(requireActivity(), "Requesting sender name succesful", Toast.LENGTH_LONG).show()
                }

                override fun onFailure(call: Call<SenderNamePostResponse>, t: Throwable) {
                    Log.d(TAG, "Request sender name failed!")
                }
            }
        )

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment RequestSenderNameFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RequestSenderNameFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

        private val TAG : String = "REQUEST_SENDER_NAME_FRAGEMENT"
    }
}