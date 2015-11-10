package thangtv.com.trather.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

import thangtv.com.trather.R;

public class LoginSignupActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText editMail;
    private EditText editPass;
    private Button buttonLogin;
    private Button buttonSignup;
    private String email;
    private String password;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_signup);
        editMail = (EditText) findViewById(R.id.edit_mail);
        editPass = (EditText) findViewById(R.id.edit_pass);
        buttonLogin = (Button) findViewById(R.id.button_login);
        buttonSignup = (Button) findViewById(R.id.button_signup);
        buttonLogin.setOnClickListener(this);
        buttonSignup.setOnClickListener(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(this.getResources().getString(R.string.wait));
    }

    @Override
    public void onClick(View v) {
        email = editMail.getText().toString().trim();
        password = editPass.getText().toString().trim();
        switch (v.getId()) {
            case R.id.button_login:
                login();
                break;
            case R.id.button_signup:
                signup();
                break;
        }
    }

    private void login() {
        //Email validating
        if (!isValidEmail(email)) {
            Toast.makeText(LoginSignupActivity.this, getString(R.string.please_enter_email), Toast.LENGTH_SHORT).show();
            editMail.requestFocus();
            return;
        }

        //Password validating
        if (password.equals("")) {
            Toast.makeText(LoginSignupActivity.this, getString(R.string.please_enter_password), Toast.LENGTH_SHORT).show();
            editPass.requestFocus();
            return;
        }

        //Send data to Parse for verification
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);
        ParseUser.logInInBackground(email, password, new LogInCallback() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                progressDialog.hide();
                if (parseUser != null) {
                    //If user exits and authenticated, send user to MapViewActivity
                    Toast.makeText(getApplicationContext(), getString(R.string.successfully_logged_in), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), MapViewActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginSignupActivity.this, getString(R.string.wrong_email_or_password), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void signup() {

        //Email validating
        if (!isValidEmail(email)) {
            Toast.makeText(LoginSignupActivity.this, getString(R.string.please_enter_email), Toast.LENGTH_SHORT).show();
            editMail.requestFocus();
            return;
        }

        //Password validating
        if (password.equals("")) {
            Toast.makeText(LoginSignupActivity.this, getString(R.string.please_enter_password), Toast.LENGTH_SHORT).show();
            editPass.requestFocus();
            return;
        }
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);
        //Check if user exits or not
        ParseQuery<ParseObject> query = ParseQuery.getQuery("_User");
        query.whereEqualTo("email", email);
        query.findInBackground(new FindCallback<ParseObject>() {

            @Override
            public void done(List<ParseObject> list, ParseException e) {
                progressDialog.hide();
                if (e == null) {
                    if (list.size() > 0) {
                        Toast.makeText(LoginSignupActivity.this, getString(R.string.email_already_exits), Toast.LENGTH_SHORT).show();
                    } else {
                        //Send user to EditProfileActivity
                        Intent intent = new Intent(LoginSignupActivity.this, EditProfileActivity.class);
                        intent.putExtra("email", email);
                        intent.putExtra("password", password);
                        startActivity(intent);
                    }
                } else {
                    Toast.makeText(LoginSignupActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private boolean isValidEmail(CharSequence target) {
        if(TextUtils.isEmpty(target)) {
            return false;
        } else {
            return Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }
}
