package com.example.push_notification;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private String accessToken;
    private String dv2_token = "e95RhZkrTwyTz2X-xps1oX:APA91bFj524lnebUi4j_0BMgtCInE99H8tRPtuMb3KcJEMFfBd6p-2nIt7TRAVbv2-Ab8QsFsILP8dzw7Oj_2FYpthvjZDK4XDHo5AGzdqMkQSIJsSxiVh8se17L4Dra-d-aTVA2Roqg";
    Button btn_submit;
    EditText edt_content;
    TextView txv_user;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getTokenFromFirebase();
        mapping();


        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendNotification();
            }
        });

    }

    public void mapping() {
        btn_submit = findViewById(R.id.submit_content_input);
        edt_content = findViewById(R.id.content_input);
        txv_user = findViewById(R.id.user_token);
    }

    private void getTokenFromFirebase() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();
                        if (accessToken == null) {
                            accessToken = token;
                            txv_user.setText(hideSensitiveInfo(accessToken));
                        }

                        // Log and toast
                        Log.i("[check]", "Here is Token: " + token);
                    }
                });
    }

    private void sendNotification() {
        // Lấy nội dung từ EditText
        String notificationContent = edt_content.getText().toString().trim();

        // Kiểm tra xem EditText có dữ liệu hay không
        if (notificationContent.isEmpty()) {
            // Nếu không có dữ liệu, không gửi thông báo
            return;
        }

        // Kiểm tra xem token đã được lấy từ Firebase chưa
        if (accessToken == null || accessToken.isEmpty()) {
            Log.e("[error]", "Token is null or empty");
            return;
        }

        // Xây dựng payload cho thông báo
        // Trong ví dụ này, chúng ta chỉ gửi dữ liệu (Data Message), không gửi Push Notification
        // Nếu muốn hiển thị thông báo, bạn có thể thêm phần "notification" vào payload

        Log.i("[check]", "My Token: " + accessToken);

        String payload = "{\"data\": {\"content\": \"" + notificationContent + "\"}, \"to\": \"" + dv2_token + "\"}";

        // Gửi thông báo bằng cách sử dụng AsyncTask tĩnh
        new NotificationTask().execute(payload);
    }

    private String hideSensitiveInfo(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }

        // Số lượng dấu * bạn muốn thay thế
        int numberOfStars = 10;

        // Tạo một chuỗi gồm numberOfStars ký tự "*"
        String stars = new String(new char[numberOfStars]).replace('\0', '*');

        // Lấy độ dài của chuỗi và chỉ giữ lại 10 ký tự đầu tiên
        int length = input.length();
        int visibleLength = Math.min(length, numberOfStars);

        // Lấy 10 ký tự đầu tiên và thêm dấu "*"
        String visiblePart = input.substring(0, visibleLength) + stars;

        return visiblePart;
    }

    private static class NotificationTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            try {
                // Gửi request đến FCM
                URL url = new URL("https://fcm.googleapis.com/fcm/send");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Authorization", "key=AAAAHhe_Pp8:APA91bHJdG5WRqEyUEMLAP6qNT1WSvXUJAn62IM7N2xoA2q1ZmRkrwbdBeXqt-tyo4SGNWZUu50boatgoE-HdC-CyWKETZvHx8KrNzh0Mg7q0jm0hApDfp0PO6EQxxOr8Y1YlWtPreK6"); // Đặt YOUR_FCM_SERVER_KEY

                // Bật gửi và nhận dữ liệu
                connection.setDoOutput(true);
                connection.getOutputStream().write(params[0].getBytes(StandardCharsets.UTF_8));

                // Đọc phản hồi từ FCM (nếu cần)
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                StringBuilder response = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                // Đóng kết nối
                connection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

    }
}