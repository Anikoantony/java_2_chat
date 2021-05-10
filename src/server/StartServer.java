package server;

import java.sql.*;
import java.util.Arrays;

import static jdk.nashorn.internal.objects.Global.print;

public class StartServer {
    private static Object monitor = new Object();

    public static void main(String[] args) {

       // bd();

        new Thread(new Runnable() {
            @Override
            public void run() {

            new Server();
            }
        }).start();

        new Thread(()->{
           //new ServerTest();
        }).start();



    /*   String[] arr=("I love Java   ".split(" ", 1));
       String[] arr2=("I love Java   ".split(" ", 4));
        System.out.println(Arrays.toString(arr));
        System.out.println(Arrays.toString(arr2));*/
    }


}
