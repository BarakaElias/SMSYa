package courses.pluralsight.com.smsya.data.repositories

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import courses.pluralsight.com.smsya.data.*
import courses.pluralsight.com.smsya.data.bodies.*
import courses.pluralsight.com.smsya.services.ContactsServices
import courses.pluralsight.com.smsya.services.MessageService
import courses.pluralsight.com.smsya.services.ServiceBuilder
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import kotlin.reflect.typeOf

open class ContactsRepository() {
    val allContactLists : List<Contact> = DataManager.allContacts
    val allAddressBooks : List<AddressBook> = DataManager.addressbooks
    val allTemplates : List<SmsTemplate> = DataManager.templates

    val contactsServices : ContactsServices = ServiceBuilder.buildService(ContactsServices::class.java)
    val messageService : MessageService = ServiceBuilder.buildService(MessageService::class.java)

    suspend fun getSenderNames() : List<SenderName>{
        return suspendCoroutine { continuation ->
            messageService.getSenderNames().enqueue(
                object : Callback<SenderNamesGetResponse>{
                    override fun onResponse(
                        call: Call<SenderNamesGetResponse>,
                        response: Response<SenderNamesGetResponse>
                    ) {
                        if(response.isSuccessful){
                            Log.d(TAG, "Sender names call succeeded")
                            continuation.resume(response.body()!!.data) //The response body contains the list of sender names I have available
                        }else{
                            Log.d(TAG, "Sender names request failed: ${response.toString()}")
                        }
                    }

                    override fun onFailure(call: Call<SenderNamesGetResponse>, t: Throwable) {
                        Log.d(TAG, "Adressbook fail: ${t.printStackTrace()}")
                    }
                }
            )
        }
    }

    suspend fun getAddressBooks() : List<AddressBook> {
        return suspendCoroutine { continuation ->
            contactsServices.getAddressBooks("${contactsUrl}address-books").enqueue(
                object : Callback<AddressBookResponse>{
                    override fun onResponse(
                        call: Call<AddressBookResponse>,
                        response: Response<AddressBookResponse>
                    ) {
                        if(response.isSuccessful){
                            Log.d(TAG, "Addressbook call succeeded")
                            continuation.resume(response.body()!!.data!!)
                        }else{
                            Log.d(TAG, "Addressbook resonse failed: ${response.toString()}")
                            continuation.resumeWithException(Exception("API call to addressbooks failed"))
                        }
                    }

                    override fun onFailure(call: Call<AddressBookResponse>, t: Throwable) {
                        Log.d(TAG, "Adressbook fail: ${t.printStackTrace()}")
                        continuation.resumeWithException(Exception("Network Error! Check connection"))

                        // continuation.resumeWithException(t) caused a crash
                    }
                }
            )
        }

    }

    suspend fun getSmsTemplates() : List<SmsTemplate>{
        return suspendCoroutine { continuation ->
            messageService.getSmsTemplates().enqueue(
                object : Callback<SmsTemplateGetResponse>{
                    override fun onResponse(
                        call: Call<SmsTemplateGetResponse>,
                        response: Response<SmsTemplateGetResponse>
                    ) {
                        if(response.isSuccessful){
                            Log.d(TAG, "Sms Templates get call succesful")
                            continuation.resume(response.body()!!.data)
                        }else{
                            Log.d(TAG, "Sms Templates resonse failed: ${response.toString()}")
                            continuation.resumeWithException(Exception("API call to sms templates failed"))
                        }
                    }

                    override fun onFailure(call: Call<SmsTemplateGetResponse>, t: Throwable) {
                        Log.d(TAG, "Sms Template fail: ${t.printStackTrace()}")
                        continuation.resumeWithException(Exception("Network Error! Check connection"))
                    }
                }
            )
        }
    }

    suspend fun createSmsTemplate(body : SmsTemplatePostRequest) : HashMap<String, Any>{
        return suspendCoroutine { continuation ->
            messageService.createSmsTemplate(body).enqueue(
                object : Callback<SmsTemplatePostResponse>{
                    override fun onResponse(
                        call: Call<SmsTemplatePostResponse>,
                        response: Response<SmsTemplatePostResponse>
                    ) {
                        if(response.isSuccessful){
                            Log.d(TAG, "Sms Templates post call successful")
                            continuation.resume(response.body()!!.data)
                        }else{
                            Log.d(TAG, "SMS Template post responose failed: ${response.toString()}")
                        }
                    }

                    override fun onFailure(call: Call<SmsTemplatePostResponse>, t: Throwable) {
                        Log.d(TAG, "Sms Template post fail: ${t.printStackTrace()}")
                        continuation.resumeWithException(Exception("Network Error! Check connection"))
                    }
                }
            )
        }
    }

    suspend fun updateSmsTemplate(templateId : String, body: SmsTemplatePostRequest) : HashMap<String, Any>{
        return suspendCoroutine { continuation ->
            messageService.updateSmsTemplate("https://apisms.beem.africa/public/v1/sms-templates/${templateId}", body).enqueue(
                object : Callback<SmsTemplatePostResponse>{
                    override fun onResponse(
                        call: Call<SmsTemplatePostResponse>,
                        response: Response<SmsTemplatePostResponse>
                    ) {
                        if(response.isSuccessful){
                            val body = response.body()
                            continuation.resume(body!!.data!!)
                        }else{
                            Log.d(TAG, "Update sms template faiiled succesfully")
                            continuation.resumeWithException(Exception("Failed succesfully"))
                        }
                    }

                    override fun onFailure(call: Call<SmsTemplatePostResponse>, t: Throwable) {
                        Log.d(TAG, "Sms Template update fail: ${t.printStackTrace()}")
                        continuation.resumeWithException(Exception("Network Error! Check connection"))
                    }
                }
            )
        }
    }

    suspend fun deleteSmsTemplate(templateId : String) : HashMap<String, Any>{
        return suspendCoroutine { continuation ->
            messageService.deleteSmsTemplate("https://apisms.beem.africa/public/v1/sms-templates/${templateId}").enqueue(
                object : Callback<SmsTemplatePostResponse>{
                    override fun onResponse(
                        call: Call<SmsTemplatePostResponse>,
                        response: Response<SmsTemplatePostResponse>
                    ) {
                        if(response.isSuccessful){
                            val body = response.body()
                            continuation.resume(body?.data!!)
                        }else{
                            Log.d(TAG, "Deleting sms template faiiled succesfully")
                            continuation.resumeWithException(Exception("Failed succesfully"))
                        }
                    }

                    override fun onFailure(call: Call<SmsTemplatePostResponse>, t: Throwable) {
                        Log.d(TAG, "Sms Template delete fail: ${t.printStackTrace()}")
                        continuation.resumeWithException(Exception("Network Error! Check connection"))
                    }
                }
            )
        }
    }

    suspend fun getContactsInAddressBook(id: String) : List<Contact>{
      return suspendCoroutine { continuation ->
          contactsServices.getContacts("${contactsUrl}contacts",id).enqueue(
              object : Callback<ContactGetResponse>{
                  override fun onResponse(
                      call: Call<ContactGetResponse>,
                      response: Response<ContactGetResponse>
                  ) {
                      if(response.isSuccessful){
                          val body = response.body()
                          Log.d(TAG, "Get contacts from address book succeeded")
                          continuation.resume(body!!.data!!)
                      }else{
                          Log.d(TAG, "Get contacts from address book failed")
                          continuation.resumeWithException(Exception("Failed fetching contacts"))
                      }
                  }

                  override fun onFailure(call: Call<ContactGetResponse>, t: Throwable) {
                      Log.d(TAG, "Get contacts from address book failed ${t.printStackTrace()}")
                      continuation.resumeWithException(Exception("Network Error! Check connection"))
                  }

              }
          )
      }
    }


    suspend fun addContact(contact : ContactPostRequest) : AddContactSuccess{
        return suspendCoroutine { continuation ->
            contactsServices.addContact("${contactsUrl}contacts",contact).enqueue(
                object : Callback<ContactPostResponse>{
                    override fun onResponse(
                        call: Call<ContactPostResponse>,
                        response: Response<ContactPostResponse>
                    ) {
                        if(response.isSuccessful){
                            val body = response.body()
                            Log.d(TAG, "Contact added successfully: ${response.toString()}")
                            continuation.resume(body!!.data!!)
                        }else{
                            Log.d(TAG, "Add contact response failed: ${response.errorBody()?.string()}")
                            val error_body = JSONObject(response.errorBody()?.string())

                            if(error_body !== null){
                                if(error_body.get("data") !== null){
                                    val data  = error_body.getJSONObject("data")
                                    Log.d(TAG, "Errobody json object: ${data.toString()}")


                                    continuation.resumeWithException(Exception(error_body.getString("message")))

                                }
                                continuation.resumeWithException(Exception(error_body.getString("message")))

                            }

                        }
                    }

                    override fun onFailure(call: Call<ContactPostResponse>, t: Throwable) {
                        Log.d(TAG, "Get contacts from address book failed ${t.printStackTrace()}")
                        continuation.resumeWithException(Exception("Network Error! Check connection"))
                    }
                }
            )
        }
    }

    suspend fun editContact(contactId : String, contact : ContactPostRequest) : GenericDataBodyResponse{
        return suspendCoroutine { continuation ->
            contactsServices.editContact("${contactsUrl}contacts/${contactId}", contact).enqueue(
                object : Callback<GenericDataResponse>{
                    override fun onResponse(
                        call: Call<GenericDataResponse>,
                        response: Response<GenericDataResponse>
                    ) {
                        if(response.isSuccessful){
                            val body = response.body()
                            Log.d(TAG, "Contact edited succesfully: ${response.toString()}")
                            continuation.resume(body!!.data!!)
                        }else{
                            Log.d(TAG, "Edit contact response failed: ${response.errorBody()?.string()}")
                            val error_body = response.errorBody()
                            continuation.resumeWithException(Exception("Error editing contact"))
                        }
                    }

                    override fun onFailure(call: Call<GenericDataResponse>, t: Throwable) {
                        Log.d(TAG, "Get contacts from address book failed ${t.printStackTrace()}")
                        continuation.resumeWithException(Exception("Network Error: Could not edit contact"))
                    }
                }
            )
        }
    }

    suspend fun addAddressbook(addressBook : AddressBookPostRequest) : HashMap<String, String>{
        return suspendCoroutine { continuation ->
            contactsServices.addAddressBook("${contactsUrl}address-books", addressBook).enqueue(
                object : Callback<AddressBookPostResponse>{
                    override fun onResponse(
                        call: Call<AddressBookPostResponse>,
                        response: Response<AddressBookPostResponse>
                    ) {
                        if(response.isSuccessful){
                            val body = response.body()
                            continuation.resume(body!!.data!!)
                        }else{
                            Log.d(TAG, "Add addressbook failed succesfully")
                            val error_body = JSONObject(response.errorBody()?.string())
                            val data = error_body.getJSONObject("data")
                            continuation.resumeWithException(Exception(data.getString("message")))
                        }
                    }

                    override fun onFailure(call: Call<AddressBookPostResponse>, t: Throwable) {
                        Log.d(TAG, "Creating addressbok failed ${t.printStackTrace()}")
                        continuation.resumeWithException(Exception("Network Error! Could not create Addresbook"))
                    }
                }
            )
        }
    }

    suspend fun editAddressbook(addressBookId : String, addressBook: AddressBookPostRequest) : HashMap<String, String>{
        return suspendCoroutine { continuation ->
            contactsServices.editAddressBook("${contactsUrl}address-books/${addressBookId}", addressBook).enqueue(
                object : Callback<AddressBookPostResponse>{
                    override fun onResponse(
                        call: Call<AddressBookPostResponse>,
                        response: Response<AddressBookPostResponse>
                    ) {
                        if(response.isSuccessful){
                            val body = response.body()
                            continuation.resume(body!!.data!!)
                        }else{
                            Log.d(TAG, "Edit addressbook failed succesfully")
                            val error_body = response.errorBody()
                            continuation.resumeWithException(Exception("Failed updating addressbook"))
                        }
                    }

                    override fun onFailure(call: Call<AddressBookPostResponse>, t: Throwable) {
                        Log.d(TAG, "Creating addressbok failed ${t.printStackTrace()}")
                        continuation.resumeWithException(Exception("Network Error! Check connection"))
                    }
                }
            )
        }
    }

    suspend fun deleteAddressBook(addressbookId : String) : HashMap<String, String>{
        return suspendCoroutine { continuation ->
            contactsServices.deleteAddressBook("${contactsUrl}address-books/${addressbookId}").enqueue(
                object : Callback<AddressBookPostResponse>{
                    override fun onResponse(
                        call: Call<AddressBookPostResponse>,
                        response: Response<AddressBookPostResponse>
                    ) {
                        if(response.isSuccessful){
                            val body = response.body()
                            continuation.resume(body!!.data!!)
                        }else{
                            Log.d(TAG, "Deleting addressbook failed succesfully")
                            val error_body = response.errorBody()
                            continuation.resumeWithException(Exception("Failed deleting addressbook"))
                        }
                    }

                    override fun onFailure(call: Call<AddressBookPostResponse>, t: Throwable) {
                        Log.d(TAG, "Deleting addressbok failed ${t.printStackTrace()}")
                        continuation.resumeWithException(Exception("Network Error! Check connection"))
                    }
                }
            )
        }
    }

    suspend fun deleteContact(body : ContactDeleteRequest) : HashMap<String, String>{
        return suspendCoroutine { continuation ->
            contactsServices.deleteContact("${contactsUrl}contacts").enqueue(
                object : Callback<ContactDeleteResponse>{
                    override fun onResponse(
                        call: Call<ContactDeleteResponse>,
                        response: Response<ContactDeleteResponse>
                    ) {
                        if(response.isSuccessful){
                            val body = response.body()
                            Log.d(TAG, "Contact deleted successfully: ${response.toString()}")
                            continuation.resume(body!!.data!!)
                        }else{
                            Log.d(TAG, "Delete contact response failed")
                        }
                    }

                    override fun onFailure(call: Call<ContactDeleteResponse>, t: Throwable) {
                        Log.d(TAG, "Delete contacts from address book failed ${t.printStackTrace()}")
                        continuation.resumeWithException(Exception("Network Error! Could not delete contact"))
                    }
                }
            )
        }
    }

    fun deleteTemplate(template : SmsTemplate){
        DataManager.templates.remove(template)
    }



    fun getContact(contactId: String) : Contact{
        val res = DataManager.allContacts.filter { contact -> contact.id.equals(contactId)}
        return res[0]
    }

    fun updateContact(updated_contact : Contact){
        val old_contact = DataManager.allContacts.find { contact -> contact.id == updated_contact.id }
        val old_contact_index = DataManager.allContacts.indexOf(old_contact)
        DataManager.allContacts.set(old_contact_index, updated_contact)
    }




    fun addAddressBook(addressBook: AddressBook){
        DataManager.addressbooks.add(addressBook)
        Log.d(TAG, "Added addressbook: ${addressBook.addressbook}")
    }




    companion object{
        private val TAG : String = "CONTACTS_REPOSITORY"
        private val contactsUrl : String = "https://apicontacts.beem.africa/public/v1/"

    }
}