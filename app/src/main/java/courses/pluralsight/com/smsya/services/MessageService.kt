package courses.pluralsight.com.smsya.services

import courses.pluralsight.com.smsya.data.SmsTemplate
import courses.pluralsight.com.smsya.data.bodies.*
import retrofit2.Call
import retrofit2.http.*

interface MessageService {
    @POST @Headers("Content-Type: application/json")
    fun sendMessage(
        @Url url :String,
        @Body messageRequest : MessageRequest
    ) : Call<MessageResponse>

    @GET("vendors/balance")
    fun checkBalance() : Call<SmsBalanceResponse>

    @GET("sender-names")
    fun getSenderNames() : Call<SenderNamesGetResponse>

    @POST("sender-names")
    fun requestSenderName(@Body body : SenderNamePostRequest) : Call<SenderNamePostResponse>


    @GET("sms-templates")
    fun getSmsTemplates() : Call<SmsTemplateGetResponse>

    @POST("sms-templates")
    fun createSmsTemplate(@Body body : SmsTemplatePostRequest) : Call<SmsTemplatePostResponse>

    @PUT
    fun updateSmsTemplate(@Url url : String, @Body body : SmsTemplatePostRequest) : Call<SmsTemplatePostResponse>

    @DELETE
    fun deleteSmsTemplate(@Url url : String) : Call<SmsTemplatePostResponse>

    @GET
    fun getDeliveryReport(@Url url : String, @Query("dest_addr") dest_addr : String, @Query("request_id") request_id : String) : Call<DeliveryReportResponse>
}