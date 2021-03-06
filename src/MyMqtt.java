import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MyMqtt {
    private String host = "tcp://*.*.*.*:1883";
    private String userName = "*";
    private String passWord = "*";
    private MqttClient mqttClient;
    private String mqttClientID;
    private MemoryPersistence memoryPersistence;
    private MqttConnectOptions mqttConnectOptions;
    public MyMqtt(String clientID) {
        this(clientID, null, true);
        mqttClientID = clientID;
    }

    public MyMqtt(String clientID, MqttCallback callback, boolean cleanSession) {
        try {
            mqttClient = new MqttClient(host, clientID, memoryPersistence = new MemoryPersistence());
            mqttConnectOptions = new MqttConnectOptions();
            mqttConnectOptions.setCleanSession(cleanSession);
            mqttConnectOptions.setUserName(userName);
            mqttConnectOptions.setPassword(passWord.toCharArray());
            mqttConnectOptions.setConnectionTimeout(10);
            mqttConnectOptions.setKeepAliveInterval(20);
            if (callback == null) {
                mqttClient.setCallback(new MqttCallback() {
                    @Override
                    public void connectionLost(Throwable throwable) {
                        System.out.println(mqttClientID + " connectionLost " + throwable);
                        reConnect();
                    }

                    @Override
                    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
                        System.out.println(mqttClientID + " messageArrived: " + mqttMessage.toString());
                    }

                    @Override
                    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
                        System.out.println(mqttClientID + " deliveryComplete " + iMqttDeliveryToken);
                    }
                });
            } else {
                mqttClient.setCallback(callback);
            }
            mqttClient.connect(mqttConnectOptions);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void publishMessage(String topic, String msg) {
        try {
            MqttMessage message = new MqttMessage();
            message.setQos(1);
            message.setRetained(true);
            message.setPayload(msg.getBytes());
            MqttTopic mqttTopic = mqttClient.getTopic(topic);
            MqttDeliveryToken token = mqttTopic.publish(message);
            token.waitForCompletion();
        }
//        catch (MqttPersistenceException e) {
//            e.printStackTrace();
//        }
        catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void subTopic(String[] topicFilters, int[] qos) {
        try {
            mqttClient.subscribe(topicFilters, qos);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void cleanTopic(String[] topicFilters) {
        if (null != mqttClient && !mqttClient.isConnected()) {
            try {
                mqttClient.unsubscribe(topicFilters);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("mqttClient is error");
        }
    }

    public void closeConnect() {
        if(null != memoryPersistence) {
            try {
                memoryPersistence.close();
            } catch (MqttPersistenceException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("memoryPersistence is null");
        }

        if(null != mqttClient) {
            if (mqttClient.isConnected()) {
                try {
                    mqttClient.disconnect();
                    mqttClient.close();
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("mqttClient is not connect");
            }
        } else {
            System.out.println("mqttClient is null");
        }
    }

    public void reConnect() {
        if (null != mqttClient) {
            if (!mqttClient.isConnected()) {
                if (null != mqttConnectOptions) {
                    try {
                        mqttClient.connect(mqttConnectOptions);
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                } else {
                    System.out.println("mqttConnectOptions is null");
                }
            } else {
                System.out.println("mqttClient is connect");
            }
        } else {
            System.out.println("mqttClient is null");
        }
    }
}
