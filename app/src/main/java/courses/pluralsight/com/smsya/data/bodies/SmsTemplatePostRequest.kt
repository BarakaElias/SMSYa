package courses.pluralsight.com.smsya.data.bodies

import courses.pluralsight.com.smsya.data.Pagination
import courses.pluralsight.com.smsya.data.SmsTemplate

data class SmsTemplatePostRequest(val message : String, val sms_title : String)