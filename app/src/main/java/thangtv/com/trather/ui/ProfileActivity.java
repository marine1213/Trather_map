package thangtv.com.trather.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import thangtv.com.trather.R;

public class ProfileActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbar;
    private ImageView header;
    private TextView name;
    private TextView dateOfBirth;
    private TextView gender;
    private TextView idCardNumber;
    private TextView phone;
    private TextView email;
    private TextView description;
    private FloatingActionButton fab;

    private ParseUser user;

    @Override
    protected void onResume() {
        super.onResume();

        //set up header image
        ParseFile file = user.getParseFile("avatar");
        file.getDataInBackground(new GetDataCallback() {
            @Override
            public void done(byte[] bytes, ParseException e) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                header.setImageBitmap(bitmap);
            }
        });

        //set up information
        name.setText(user.getString("name"));
        dateOfBirth.setText(user.getString("date_of_birth"));
        gender.setText(user.getString("gender"));
        idCardNumber.setText(user.getString("id_card_number"));
        phone.setText(user.getString("phone"));
        email.setText(user.getEmail());
        description.setText(user.getString("description"));

        //set up collapsing toolbar
        collapsingToolbar.setTitle(user.getString("name"));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);



        //connect views
        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        header = (ImageView) findViewById(R.id.header);
        name = (TextView) findViewById(R.id.name);
        gender = (TextView) findViewById(R.id.gender);
        dateOfBirth = (TextView) findViewById(R.id.date_of_birth);
        idCardNumber = (TextView) findViewById(R.id.id_card_number);
        phone = (TextView) findViewById(R.id.phone);
        email = (TextView) findViewById(R.id.email);
        description = (TextView) findViewById(R.id.description);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        toolbar = (Toolbar) findViewById(R.id.anim_toolbar);

        //set up toolbar
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_white_24dp));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        //Check user
        final Intent intent = getIntent();
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (intent.getStringExtra("user").equals(currentUser.getUsername())) {
            user = currentUser;
            fab.setImageResource(R.drawable.ic_mode_edit_white_36dp);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent1 = new Intent(ProfileActivity.this, EditProfileActivity.class);
                    startActivity(intent1);
                }
            });

        } else {
            ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("_User");
            query.whereEqualTo("username", intent.getStringExtra("user"));
            try {
                user = (ParseUser) query.find().get(0);
            } catch (Exception e) {
                user = null;
                Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        }

    }

}
