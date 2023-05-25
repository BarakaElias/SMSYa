package courses.pluralsight.com.smsya.data.bodies

import courses.pluralsight.com.smsya.data.Pagination
import courses.pluralsight.com.smsya.data.SenderName

data class SenderNamesGetResponse(val data : List<SenderName>, val pagination: Pagination)