package courses.pluralsight.com.smsya

import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import courses.pluralsight.com.smsya.data.bodies.DeliveryReportResponse
import courses.pluralsight.com.smsya.services.MessageService
import courses.pluralsight.com.smsya.services.ServiceBuilder
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory

class DeliveryReportService : Service() {
    private val binder = MyServiceBinder()

    inner class MyServiceBinder : Binder() {
        fun getService(): DeliveryReportService {
            return this@DeliveryReportService
        }
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        //Calling the api
//        val apiService = Retrofit.Builder()
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//            .create(MessageService::class.java)
        Log.d("DELIVERY_SERVICE", "My service started")
        val dest_addr = intent?.getStringExtra("DEST_ADDR")
        val request_id = intent?.getStringExtra("REQUEST_ID")
        Log.d("DELIVERY_SERVICE", "DEST ADDRESS: ${dest_addr}")
        Log.d("DELIVERY_SERVICE", "REQUEST ID: ${request_id}")

        val messageService : MessageService = ServiceBuilder.buildService(MessageService::class.java)
        messageService.getDeliveryReport("https://dlrapi.beem.africa/public/v1/delivery-reports", dest_addr!!, request_id!!).enqueue(
            object : Callback<DeliveryReportResponse>{
                override fun onResponse(
                    call: Call<DeliveryReportResponse>,
                    response: Response<DeliveryReportResponse>
                ) {
                    if(response.isSuccessful){
                        Log.d("DELIVERY SERVICE", "Delivery report : ${response.toString()}")
                        val body = response.body()
                        val notificationBuilder = NotificationCompat.Builder(this@DeliveryReportService,"1")
                            .setSmallIcon(R.drawable.ic_baseline_info_24)
                            .setContentTitle("SMS YA")
                            .setContentText("The message is ${body!!.status}")
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

                        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                        notificationManager.notify(0, notificationBuilder.build())
                    }
                }

                override fun onFailure(call: Call<DeliveryReportResponse>, t: Throwable) {
                    Log.d("DELIVERY_SERVICE", t.printStackTrace().toString())
                }
            }
        )

        return super.onStartCommand(intent, flags, startId)
    }
}