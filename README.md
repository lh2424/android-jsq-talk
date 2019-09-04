# android-jsq-talk

一,基本核心API说明

1启动即时通讯

不管是否登录就直接调用,如果已经登录的,就把id保存,下次直接启动,未登录默认传-1
TalkCoreClass.getInstance(MainActivity.this).starSocketServer("这个写登录的id编号,类型int");

2.登录

登录过后把登录id保存起来,以便下次启动使用,登录id结合具体APP业务登录来处理
TalkCoreClass.getInstance(MainActivity.this).loginAccount("这个写登录的id编号,类型int");

3.接收

发送string的内容是什么,接收到的string内容就是一模一样
TalkCoreClass.getInstance(MainActivity.this).receviedNetWorkCallbackInter = new TalkCoreClass.NetWorkCallbackInterface() {
            @Override
            public void showCallback(String result) {
                System.out.print("收到的内容:"+result);
            }
        };

4.发送

reciveId就是对方的登录编码, JsonText是任意字串内容,本例子是个json字串,可以扩展各种功能
TalkCoreClass.sendContextData(reciveId,JsonText,Context);

5,退出登录

抹除退出登录的数据,保存的缓存数据根据各自的需求来处理
TalkCoreClass.getInstance(Context).exitAccount();


二,demo逻辑和结构大致说明

知道了基本核心的API,其实就已经可以开发各种即时通讯的功能了,我们不参与广大开发者的各种业务和逻辑上的开发,只是推广学习的作用,于是临时花费了半天的时间开发了个有点粗糙的demo,实话说Android和iOS客户端的demo不是非常适合傻瓜式的移植,只是学习参考价值

重要的界面和类如下,总共就12个文件,我相信大致阅读就可以知道,关键是是以下3个

1,MainActivity是聊天首页界面,列表保存的是聊天主列表

当收到消息的时候判断首页列表的是和谁聊天,相应的保存,(注意:)如果是主动发送的时候,不能显示自己的头像和昵称,只能显示自己说的话;
登录,退出登录,声音控制开关,弹出消息框等代码功能请仔细查阅读代码,相信不是很难,毕竟代码量很小

2,TalkToActivity是聊天单独具体的界面,列表保存的是聊天详情列表

当收到消息后,如果是当前聊天的id,就添加保存数据,并刷新显示,发送的也是,具体的UI和数据我相信已经烂大街的原理,不用多说
划下拉多展示一定数量的数据,demo仅供参考
发送的时候是个json字串,里面可以包含很多信息,可以扩展很多功能,接收到的时候也可以相应的处理,比如红包,分享,等等


3,SaveTalkDataClass是保存数据的关键类

先说下用SharedPreferences保存复杂数据的思路,做个抛砖引玉,定一个关键字,比如十六进制的id:0C24FFEE等,获取这个id的值,里面保存一个数据是表示array的长度假设是:12
那就循环	for(i=0 i<12 i++)取"0C24FFEE"+"showTalkText"+i,"0C24FFEE"+"talkId"+i......这些关键字的各种值,表示这个聊天列表数组的各种不同的数据;
存储数据的其他思路和更好的方式我想有一定基础的开发者应该有办法做得更好,并不一定按照demo的思路来保存


三,配置和并发等等的讨论,首先服务器带宽先保留考虑

核心的服务器功能是我们的首推.我们服务器的核心主程有15年以上的实际系统开发经验,百万级别的海关码头系统,和交通局系统,项目由于签署了保密协议等不能明说.高并发和数据及时性的超强处理是比较顶尖的.

1.很多标榜2C4G,10万连接并发等等,1000/s并发量,只消耗3k一个用户来计算等等,如果经过实测实现了,我只能说向它虚心学习.

2.我们严格的针对实际的测试情况,2C4G内存的配置大概可以跑1500个在线,并且发送信息超高的活跃客户,无伤无卡顿,可以在4C64G的情况下,35000个高并发连接,因为当时模拟不到如此高的客户端数量后放弃没有测到峰值

3.其实市面上能够做到多少高并发活跃的项目基本也没几家,有过这样的数量级的和测试过的才真正懂,数量级达不到多大的,无所谓选择了

成熟商用的在线产品下载:http://www.xuanjiwenhua.com/xjwh_download.jsp
交流和学习联系方式
QQ:2894379918
WX:xuanjiwenhua666888
