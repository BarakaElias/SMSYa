package courses.pluralsight.com.smsya.data.bodies

import courses.pluralsight.com.smsya.data.AddressBook
import courses.pluralsight.com.smsya.data.Pagination

data class AddressBookResponse(val data : List<AddressBook>, val pagination: Pagination)