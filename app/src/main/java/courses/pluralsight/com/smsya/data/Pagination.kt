package courses.pluralsight.com.smsya.data

data class Pagination(val totalItems : Int, val currentPages: Int, val pageSize : Int, val totalPages : Int, val startPage : Int, val endPage : Int, val startIndex : Int, val endIndex : Int, val pages: List<Int>)