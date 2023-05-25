package courses.pluralsight.com.smsya

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import courses.pluralsight.com.smsya.adapters.AddressBookRecyclerAdapter
import courses.pluralsight.com.smsya.data.AddressBook
import courses.pluralsight.com.smsya.data.bodies.AddressBookResponse
import courses.pluralsight.com.smsya.data.repositories.ContactsRepository
import courses.pluralsight.com.smsya.services.ContactsServices
import courses.pluralsight.com.smsya.services.ServiceBuilder
import courses.pluralsight.com.smsya.viewmodels.MainActivityViewModel
import courses.pluralsight.com.smsya.viewmodels.MainActivityViewModelFactory
import retrofit2.Call
import retrofit2.Response
import java.util.*
import javax.security.auth.callback.Callback

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ContactListsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ContactListsFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var addressBookRecyclerView: RecyclerView
    private lateinit var swipeRefreshLayout : SwipeRefreshLayout
    private val contactsRepository = ContactsRepository()
    private lateinit var addressBooksProgressBar : ProgressBar
    private lateinit var navController : NavController



    private val viewModel: MainActivityViewModel by activityViewModels {
        MainActivityViewModelFactory(contactsRepository)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    navController = findNavController()



    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.contact_lists_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_contact_list_add -> {
                showAddAddressBookBottomSheet()
                return true
            }
        }
        return true
    }

    private fun showAddAddressBookBottomSheet() {
        Log.d(TAG, "Action pressed")
        navController.navigate(R.id.action_contactListsFragment_to_editAddressbookFragment)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_contact_lists, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addressBooksProgressBar = view.findViewById(R.id.addressBooksProgressBar)
        //setup toolbar
        val appBarConfiguration = AppBarConfiguration(navController.graph)
//        view.findViewById<Toolbar>(R.id.defaultToolbar).setupWithNavController(navController, appBarConfiguration)

//        val contactsRepository = ContactsRepository(requireActivity().application)
        viewModel.isLoading.observe(viewLifecycleOwner, androidx.lifecycle.Observer { isLoading ->
            if(isLoading){
                addressBooksProgressBar.visibility = View.VISIBLE
            }else{
                addressBooksProgressBar.visibility = View.INVISIBLE
            }
        })
        addressBookRecyclerView = view.findViewById(R.id.contactListsRecyclerView)
        swipeRefreshLayout = view.findViewById(R.id.contactListSwipeRefreshLayout)

        addressBookRecyclerView.layoutManager = LinearLayoutManager(activity)
        var addressBookRecyclerViewAdapter = AddressBookRecyclerAdapter(requireActivity().applicationContext, emptyList())
        addressBookRecyclerView.adapter = addressBookRecyclerViewAdapter
        viewModel.addressBooks.observe(viewLifecycleOwner){ items ->
            addressBookRecyclerViewAdapter = AddressBookRecyclerAdapter(requireActivity().applicationContext, items)
            addressBookRecyclerView.adapter = addressBookRecyclerViewAdapter
        }
        swipeRefreshLayout.setOnRefreshListener(this)

    }




    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ContactListsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ContactListsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

        private val TAG : String = "CONTACT_LIST_FRAGMENT"
    }

    override fun onRefresh() {
        addressBookRecyclerView.adapter?.notifyDataSetChanged()
        swipeRefreshLayout.isRefreshing = false
    }
}