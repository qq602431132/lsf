# lsf
java HttpFileServer NanoHTTPD

运行：

java -jar lsf.jar 1234 /root/

如:

java -jar lsf.jar					#默认以jar所在目录/端口1234 开启服务

java -jar lsf.jar 8989	 &			#只指定端口号,目录为jar包所在目录 后台运行服务

java -jar lsf.jar 8989 /opt/RHGL	#指定端口号/目录,开启服务,参数位置可调换.


**JAVA+nanohttpd实现简易文件服务器 包含上传|下载|删除|新建目录|远程命令功能**
**操作指南**
一、文件上传
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200303205410678.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xpZHVzaGVuZw==,size_16,color_FFFFFF,t_70)

二、新建文件夹
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200303205417393.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xpZHVzaGVuZw==,size_16,color_FFFFFF,t_70)
三、删除文件
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200303205424164.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xpZHVzaGVuZw==,size_16,color_FFFFFF,t_70)
***注:任意会话开启删除功能,其他在线都会开启,数据无价,请及时关闭删除功能.***
四、获取文件MD5
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200303205435835.png)
五、执行远程命令
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200303205440876.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xpZHVzaGVuZw==,size_16,color_FFFFFF,t_70)
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200303205455200.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xpZHVzaGVuZw==,size_16,color_FFFFFF,t_70)执行命令为空时返回首页.
六、下载文件
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200303205504860.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xpZHVzaGVuZw==,size_16,color_FFFFFF,t_70)


