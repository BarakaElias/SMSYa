package courses.pluralsight.com.smsya.data.bodies
import courses.pluralsight.com.smsya.data.RecipientContact

data class MessageRequest(val source_addr : String, val schedule_time : String = "",val encoding : String, val message : String,  val recipients : List<RecipientContact>)