# dubbo


### dubbo写入端配置
参考[DubboSinkDescriptor](../../alchemy-web/src/main/java/com/dfire/platform/alchemy/descriptor/DubboSinkDescriptor.java)，目前只支持固定参数调用，调用的接口方法参数类型必须是（String, Map）,如com.dfire.test.Example.add(String uniqueName, Map<String,Object> param)

```yaml
unique-name: test   #任务的唯一标识
application-name: test-app # 需要注册的应用名
interface-name: com.dfire.test.Example #调用的接口名称
method-name: echo #调用的方法名
version: 1.0.0   #调用接口的版本号
registry-addr: zookeeper://localhost:2181  #注册中心地址


```

