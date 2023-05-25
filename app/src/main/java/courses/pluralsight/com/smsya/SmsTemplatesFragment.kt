package courses.pluralsight.com.smsya

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import courses.pluralsight.com.smsya.adapters.TemplateRecyclerAdapter
import courses.pluralsight.com.smsya.data.repositories.ContactsRepository
import courses.pluralsight.com.smsya.viewmodels.MainActivityViewModel
import courses.pluralsight.com.smsya.viewmodels.MainActivityViewModelFactory

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SmsTemplatesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SmsTemplatesFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var templatesRecyclerView : RecyclerView
    private val contactsRepository: ContactsRepository = ContactsRepository()
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
        return inflater.inflate(R.layout.fragment_sms_templates, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        templatesRecyclerView = view.findViewById(R.id.recyclerViewTemplates)
        templatesRecyclerView.layoutManager = LinearLayoutManager(activity)
        templatesRecyclerView.adapter = TemplateRecyclerAdapter(requireActivity().applicationContext, contactsRepository.allTemplates)

        viewModel.smsTemplates.observe(viewLifecycleOwner){
            items ->
            val tempAdapter = TemplateRecyclerAdapter(requireActivity().applicationContext, items)
            Log.d(TAG, "Template adapter: ${tempAdapter.itemCount.toString()}")
            templatesRecyclerView.adapter = tempAdapter
        }

        Log.d(TAG, "Templates: ${viewModel.smsTemplates.value}")



        view.findViewById<FloatingActionButton>(R.id.fab_add_template).setOnClickListener {
            findNavController().navigate(R.id.action_smsTemplatesFragment_to_templateFragment)
        }
    }

    companion object {
        private const val TAG : String = "SMS_TEMPLATES_FRAGMENT"
    }
}