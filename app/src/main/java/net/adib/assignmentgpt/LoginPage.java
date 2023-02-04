package net.adib.assignmentgpt;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

public class LoginPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
        findViewById(R.id.btnLogin).setOnClickListener(view->{
            String user=((EditText)findViewById(R.id.txtUserName)).getText().toString();
            String password=((EditText)findViewById(R.id.txtPassword)).getText().toString();
            if (user.contentEquals("AdibRah012")&& password.contentEquals("nakama123")){
                SharedPreferences pref = getSharedPreferences("user", MODE_PRIVATE);
                pref.edit().putString("username", "AdibRah012").commit();
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Invalid Login!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}