package courses.pluralsight.com.smsya.data

data class AddressBook(val id : String, val addressbook : String, val contacts_count : Int = 0, val created : String = "", val description : String?) {
}