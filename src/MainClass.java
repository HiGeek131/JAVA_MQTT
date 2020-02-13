public class MainClass {
    public static void main(String[] args) {
        MyMqtt myMqtt = new MyMqtt("testServer1");
        myMqtt.publishMessage("test", "HelloWord");
    }
}
