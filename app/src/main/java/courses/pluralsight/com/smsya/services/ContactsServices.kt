package courses.pluralsight.com.smsya.services

import courses.pluralsight.com.smsya.data.AddressBook
import courses.pluralsight.com.smsya.data.Contact
import courses.pluralsight.com.smsya.data.bodies.*
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ContactsServices {
    @GET
    fun getAddressBooks(@Url url : String) : Call<AddressBookResponse>

    @POST
    fun addAddressBook(@Url url : String, @Body newAddressBook : AddressBookPostRequest) : Call<AddressBookPostResponse>

    @PUT
    fun editAddressBook(@Url url : String, @Body updatedAddressBook: AddressBookPostRequest) : Call<AddressBookPostResponse>

    @DELETE
    fun deleteAddressBook(@Url url : String) : Call<AddressBookPostResponse>

    @GET
    fun getContacts(@Url url : String, @Query("addressbook_id") addressbook_id: String) : Call<ContactGetResponse>

    @POST
    fun addContact(@Url url : String, @Body contact: ContactPostRequest) : Call<ContactPostResponse>

    @DELETE
    fun deleteContact(@Url url : String) : Call<ContactDeleteResponse>

    @PUT
    fun editContact(@Url url : String, @Body body: ContactPostRequest) : Call<GenericDataResponse>


}