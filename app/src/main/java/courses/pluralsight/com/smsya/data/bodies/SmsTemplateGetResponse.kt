package courses.pluralsight.com.smsya.data.bodies

import courses.pluralsight.com.smsya.data.Pagination
import courses.pluralsight.com.smsya.data.SmsTemplate

data class SmsTemplateGetResponse(val data : List<SmsTemplate>, val pagination: Pagination)