package courses.pluralsight.com.smsya.data.bodies

import courses.pluralsight.com.smsya.data.Contact
import courses.pluralsight.com.smsya.data.Pagination

data class ContactGetResponse(val data : List<Contact>, val pagination : Pagination)