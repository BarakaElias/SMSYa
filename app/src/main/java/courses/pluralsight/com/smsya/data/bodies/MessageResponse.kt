package courses.pluralsight.com.smsya.data.bodies

data class MessageResponse(val successful : Boolean, val request_id: Int, val code : Int, val message : String, val valid : Int, val invalid : Int, val duplicates : Int)