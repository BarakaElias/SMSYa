package courses.pluralsight.com.smsya.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import courses.pluralsight.com.smsya.data.*
import courses.pluralsight.com.smsya.data.bodies.*
import courses.pluralsight.com.smsya.data.repositories.ContactsRepository
import kotlinx.coroutines.launch

class MainActivityViewModel(private val contactsRepository: ContactsRepository) : ViewModel() {
    val isLoading = MutableLiveData<Boolean>()
    val hasError = MutableLiveData<Boolean>()
    val errorMessage = MutableLiveData<String>()
    val successMessage = MutableLiveData<String>()
    private val _addressBooks = MutableLiveData<List<AddressBook>>()
    val addressBooks : LiveData<List<AddressBook>> get() = _addressBooks
    private val _senderNames = MutableLiveData<List<SenderName>>()
    val senderNames : LiveData<List<SenderName>> get() = _senderNames
    private val _contacts = MutableLiveData<List<Contact>>()
    val contacts : LiveData<List<Contact>> get() = _contacts
    private val _smsTemplates  = MutableLiveData<List<SmsTemplate>>()
    val smsTemplates : LiveData<List<SmsTemplate>> get() = _smsTemplates

    //single objects
    val contact : MutableLiveData<Contact> = MutableLiveData()

    fun setContact(currentContact : Contact){
        contact.postValue(currentContact)
    }


    fun getAddressBooks() {
        isLoading.value = true
        viewModelScope.launch {
            try{
                _addressBooks.value = contactsRepository.getAddressBooks()
                Log.d(TAG, "This should display after the call to addressbooks has finished")
                isLoading.value = false
            }catch (e : java.lang.Exception){
                Log.d(TAG, "Addressbook fetch failed: ${e.message.toString()}")
                hasError.value = true
                errorMessage.value = e.message.toString()
            }

        }
        Log.d(TAG, "Inside getAddressBooks() in viewmodel")
    }

    fun getSmsTemplates(){
        isLoading.value = true
        viewModelScope.launch {
            try{
                _smsTemplates.value = contactsRepository.getSmsTemplates()
                isLoading.value = false
            }catch (e : java.lang.Exception){
                Log.d(TAG, "Sms Templates fetch failed: ${e.message.toString()}")
                hasError.value = true
                errorMessage.value = e.message.toString()
            }
        }
    }

    fun createSmsTemplate(body : SmsTemplatePostRequest){
        isLoading.value = true
        viewModelScope.launch {
            try{
                val new_template : HashMap<String, Any> = contactsRepository.createSmsTemplate(body)
//                if(!new_template.get("id").isNullOrEmpty()){
//                    val template : SmsTemplate = SmsTemplate(id = new_template?.get("id")!!, body.sms_title, message = body.message)
//                    val updatedSmsTemplates =  _smsTemplates.value?.toMutableList()
//                    updatedSmsTemplates?.add(template)
//                    _smsTemplates.value = updatedSmsTemplates!!
//                    isLoading.value = false
//                }
                getSmsTemplates()
                isLoading.value = false
                successMessage.value = "Successfully created Template"
            }catch (e : java.lang.Exception){
                isLoading.value = false
                Log.d(TAG, "Sms Template post failed: ${e.message.toString()}")
                hasError.value = true
                errorMessage.value = e.message.toString()
            }
        }
    }

    fun updateSmsTemplate(templateId : String, body: SmsTemplatePostRequest){
        isLoading.value = true
        viewModelScope.launch {
            try{
                val smsTemplate : HashMap<String, Any> = contactsRepository.updateSmsTemplate(templateId,body)
                if(smsTemplate.get("successful") == true){
                    getSmsTemplates()
                }
                isLoading.value = false
                successMessage.value = "Successfully updated Template"

            }catch (e: Exception){
                isLoading.value = false
                Log.d(TAG, "Sms Template put failed: ${e.message.toString()}")
                hasError.value = true
                errorMessage.value = e.message.toString()
            }
        }
    }

    fun deleteSmsTemplate(templateId : String){
        isLoading.value = true
        viewModelScope.launch {
            try {
                contactsRepository.deleteSmsTemplate(templateId)
                getSmsTemplates()
            }catch(e : Exception){
                isLoading.value = false
                Log.d(TAG, "Sms Template put failed: ${e.message.toString()}")
                hasError.value = true
                errorMessage.value = e.message.toString()
            }
        }
    }

    fun getSmsTemplatesForSpinner() : List<String>{
        val templates : MutableList<String> = mutableListOf()
        smsTemplates.value?.map { template ->
            templates.add(template.sms_title)
        }
        return templates
    }

    fun getAddressbooksForSpinner() : List<String>{
        val addressBooks : MutableList<String> = mutableListOf()
        _addressBooks.value?.map { addr -> addressBooks.add(addr.addressbook) }
        return addressBooks
    }

    fun getSmsTemplate(id : String) : SmsTemplate{
        val results = _smsTemplates.value?.filter { smsTemplate -> smsTemplate.id.equals(id) }
        return results!!.get(0)
    }
     fun getAddressBook(id : String) : AddressBook{
         val results = _addressBooks.value?.filter { addressBook -> addressBook.id.equals(id) }
         return results!!.get(0)
     }
    fun getSenderNames(){
        viewModelScope.launch {
            _senderNames.value = contactsRepository.getSenderNames()
        }
    }

    fun getSenderNamesForSpinner() : List<String>{
        val sender_names : MutableList<String> = mutableListOf()
        senderNames.value?.map { sender_name ->
            sender_names.add(sender_name.senderid)
        }
        return sender_names
    }

    fun getContactsInAddressBook(id: String) {
        isLoading.value = true
        viewModelScope.launch {
            try{
                _contacts.value = contactsRepository.getContactsInAddressBook(id)
            }catch (e: Exception){
                Log.d(TAG, e.message!!)
            }
            isLoading.value = false
        }
    }

    fun getContact(id: String) : Contact{
        val res = _contacts.value?.filter { contact -> contact.id.equals(id) }
        return res!!.get(0)
    }

    fun deleteContact(body: ContactDeleteRequest){
        Log.d(TAG, "Deleting contact")
        viewModelScope.launch {
            try{
                isLoading.value = true
                val response = contactsRepository.deleteContact(body)
                if(response.get("status").equals("true")){
                    val updatedConntacts = _contacts.value?.toMutableList()
                    val deletedContact = updatedConntacts?.filter { contact -> contact.id.equals(body.contacts_id) }?.get(0)
                    updatedConntacts?.remove(deletedContact!!)
                    _contacts.value = updatedConntacts!!
                }
            }catch (e: java.lang.Exception){
                Log.d(TAG, e.printStackTrace().toString())
            }
        }
    }

    fun addContactToAddressBook(contact : ContactPostRequest){
        isLoading.value = true
        viewModelScope.launch {
            try{
                val response : AddContactSuccess = contactsRepository.addContact(contact)
                Log.d(TAG,"Response is ${response.toString()}")
                if(response.status.equals("true")){
                    val new_contact : Contact = Contact(fname = contact.fname, lname = contact.lname, mob_no = contact.mob_no)
                    val updatedList = _contacts.value?.toMutableList()
                    updatedList?.add(new_contact)
                    _contacts.value = updatedList!!
                    isLoading.value = false
                    successMessage.value = "Successfully Added contact"
                }
            }catch (e: java.lang.Exception){
                Log.d(TAG, "Add contact error: ${e.message}")
                errorMessage.value = e.message
            }
            isLoading.value = false
        }
    }

    fun editContactInAddressBook(addressBookId: String, contactId : String,contact : ContactPostRequest){
        isLoading.value = true
        viewModelScope.launch {
            try {
                val response : GenericDataBodyResponse = contactsRepository.editContact(contactId , contact)
                if(response.status.equals("true")){
                    getContactsInAddressBook(addressBookId)
                    isLoading.value = false
                    successMessage.value = "Successfully updated contact"
                }
            }catch (e: Exception){
                Log.d(TAG, "Edit contact error : ${e.message}")
                errorMessage.value = e.message
            }
        }
    }

    fun addAddressbook(addressBook : AddressBookPostRequest) : Boolean{
        isLoading.value = true
        var success : Boolean = false
        viewModelScope.launch {
            try{
                val response : HashMap<String, String> = contactsRepository.addAddressbook(addressBook)
                Log.d(TAG, "Add addressbook response: ${response.toString()}")
                if(response?.get("status").equals("true")){
                    val updatedAddressbookList = _addressBooks.value?.toMutableList()
                    updatedAddressbookList?.add(AddressBook(id = response.get("id")!!, addressbook = addressBook.addressbook, description = addressBook.description))
                    _addressBooks.value = updatedAddressbookList!!
                    isLoading.value = false
                    successMessage.value = "Successfully Created Addressbook"
                    success = true
                }

            }catch (e : Exception){
                Log.d(TAG, "Add addressbook error: ${e.message}")
                errorMessage.value = e.message
            }
            isLoading.value = false
        }
        return success
    }

    fun editAddressbook(addressBookId : String, addressBook: AddressBookPostRequest) : Boolean{
        isLoading.value = true
        var success : Boolean = true
        viewModelScope.launch {
            try {
                val response : HashMap<String, String> = contactsRepository.editAddressbook(addressBookId, addressBook)
                Log.d(TAG, "Update addressbook response: ${response.toString()}")
                if(response?.get("status").equals("true")){
//                    val updatedAddressbookList = _addressBooks.value?.toMutableList()
//                    updatedAddressbookList?.add(AddressBook(id = response.get("id")!!, addressbook = addressBook.addressbook, description = addressBook.description))
//                    _addressBooks.value = updatedAddressbookList!!
                    isLoading.value = false
                    successMessage.value = "Successfully updated addressbook"
                    success = true
                }
            }catch(e: Exception){
                Log.d(TAG, "Add addressbook error: ${e.message}")
                errorMessage.value = e.message
            }
            isLoading.value = false
        }
        return success
    }

    fun deleteAddressBook(id: String){
        isLoading.value = true
        viewModelScope.launch {
            try{
                val response :  HashMap<String, String> = contactsRepository.deleteAddressBook(id)
                if(response.get("status").equals("true")){
                    val deleted_addressbook : AddressBook? = _addressBooks.value?.find { addressBook -> addressBook.id.equals(id) }
                    val updated_addressBooks = _addressBooks.value?.toMutableList()
                    updated_addressBooks?.remove(deleted_addressbook)
                    _addressBooks.value = updated_addressBooks!!
                    isLoading.value = false
                }
            }catch(e : java.lang.Exception){
                Log.d(TAG, "Delete addressbook ${e.message}")
                errorMessage.value = e.message
                isLoading.value = false
            }
        }
    }



    companion object{
        private val TAG : String = "MAIN ACTIVITY VIEW MODEL"
    }
}
class MainActivityViewModelFactory(private val contactsRepository: ContactsRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainActivityViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainActivityViewModel(ContactsRepository()) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")

    }
}
