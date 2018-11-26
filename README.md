# LiteNet
Used to access network for android app(封装了网络接口，便于android app 方便快捷的使用网络模块)。参考了部分Retrofit的代码以封装接口。
让网络接口访问简化为了，只需要定义接口，然后就直接返回需要的类。（包含json转换为class)

使用方法：URL由baseUrl及relativeUrl拼接而成 ,
baseUrl: LiteNet.getInstance().create("http://192.168.3.97",KService::class.java) 设置。 
relativeUrl: 由 @POST("")设置

1.自定义Interface
interface KService{
    @POST("index.php/Home/Interface/index?")
    fun getMainPage(@Field("class") classx: String, @Field("method") method: String, observer : Observer): BaseObserve<Channel>
}
    
2.调用
var service = LiteNet.getInstance().create("http://192.168.3.97",KService::class.java)
service.getMainPage("classValue","methodValue",this)

3.回调
override fun update(o: Observable?, arg: Any?) {
        val observe = o as BaseObserve<*>
        var data = observe.data

        when(data){
            is Channel ->{
                Log.e(TAG,"got Channel :"+data);
            }
        }
    }
    
具体调用代码在MainActivity.kt中。
