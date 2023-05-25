package courses.pluralsight.com.smsya.data.bodies

data class ContactPostRequest(
    val fname : String = "",
    val lname : String = "",
    val mob_no : String,
    val addressbook_id : List<String>,
    val mob_no2 : String = "",
    val title : String = "",
    val gender : String = "",
    val email : String = "",
    val country : String = "",
    val city : String = "",
    val area : String = "",
    val birth_date : String = "",
)