package litenet.cosmo.com.litenet

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.cosmo.litenet.anno.LiteNet
import com.cosmo.litenet.net.BaseObserve
import com.m6park.tt.data.Channel
import java.util.*

class MainActivity : AppCompatActivity() , Observer {
    val TAG = "MainActivity"

    override fun update(o: Observable?, arg: Any?) {
        val observe = o as BaseObserve<*>
        var data = observe.data

        when(data){
            is Channel ->{
                Log.e(TAG,"got Channel :"+data);
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var service = LiteNet.getInstance().create("http://192.168.3.97",KService::class.java)
        service.getMainPage("classValue","methodValue",this)
    }
}
