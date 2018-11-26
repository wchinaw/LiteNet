package litenet.cosmo.com.litenet;

import com.cosmo.litenet.anno.Field
import com.cosmo.litenet.anno.POST
import com.cosmo.litenet.net.BaseObserve
import com.m6park.tt.data.Channel
import java.util.*

interface KService{

    @POST("index.php/Home/Interface/index?")
    fun getMainPage(@Field("class") classx: String, @Field("method") method: String, observer : Observer): BaseObserve<Channel>
}