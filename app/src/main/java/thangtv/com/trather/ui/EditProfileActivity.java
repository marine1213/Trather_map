package thangtv.com.trather.ui;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.Toast;

import com.parse.GetDataCallback;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import thangtv.com.trather.R;

public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener {

    private EditText editName;
    private EditText editDateOfBirth;
    private EditText editPhone;
    private EditText editIDCardNumber;
    private EditText editDescription;
    private Spinner editGender;
    private ImageView editAvatar;
    private Button buttonChangeAvatar;
    private ProgressDialog progressDialog;

    private static final int REQUEST_CAMERA = 0;
    private static final int SELECT_FILE = 1;
    private static final int PIC_CROP = 2;

    private Uri picUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        setupToolbar();

        //Connect views
        editName = (EditText) findViewById(R.id.edit_name);
        editDateOfBirth = (EditText) findViewById(R.id.edit_date_of_birth);
        editPhone = (EditText) findViewById(R.id.edit_phone_number);
        editIDCardNumber = (EditText) findViewById(R.id.edit_id_card_number);
        editDescription = (EditText) findViewById(R.id.edit_description);
        editAvatar = (ImageView) findViewById(R.id.edit_avatar);
        editGender = (Spinner) findViewById(R.id.edit_gender);
        buttonChangeAvatar = (Button) findViewById(R.id.button_change_avatar);

        //Set up button change avatar
        buttonChangeAvatar.setOnClickListener(this);

        //set up date of birth input field
        editDateOfBirth.setFocusable(false);
        editDateOfBirth.setClickable(true);
        editDateOfBirth.setOnClickListener(this);

        //Determine whether the current user is an anonymous user
        if (!ParseAnonymousUtils.isLinked(ParseUser.getCurrentUser())) {

            //If current user is not anonymous user
            //Get current user data from Parse.com
            ParseUser currentUser = ParseUser.getCurrentUser();
            if (currentUser != null) {

                //get user information
                editName.setText(currentUser.getString("name"));
                editDateOfBirth.setText(currentUser.getString("date_of_birth"));
                if (currentUser.getString("gender").equals("Male")) {
                    editGender.setSelection(1);
                } else {
                    editGender.setSelection(2);
                }
                editPhone.setText(currentUser.getString("phone"));
                editDescription.setText(currentUser.getString("description"));
                editIDCardNumber.setText(currentUser.getString("id_card_number"));

                //get avatar
                ParseFile file = currentUser.getParseFile("avatar");
                file.getDataInBackground(new GetDataCallback() {
                    @Override
                    public void done(byte[] bytes, ParseException e) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        editAvatar.setImageBitmap(bitmap);
                    }
                });
            }
        }

        //Set up gender spinner
        List<String> list = new ArrayList<String>();
        list.add("Male");
        list.add("Female");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        editGender.setAdapter(dataAdapter);

        //Set up progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(this.getResources().getString(R.string.wait));
        progressDialog.setCanceledOnTouchOutside(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_change_avatar:
                selectImage();
                break;
            case R.id.edit_date_of_birth:
                showDatePicker();
                break;
        }
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(R.string.edit_your_profile);
            }
        }
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_white_24dp));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void selectImage() {

        PopupMenu popupMenu = new PopupMenu(EditProfileActivity.this, buttonChangeAvatar);
        popupMenu.getMenuInflater().inflate(R.menu.popup_menu_select_image, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.take_picture:

                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                        //Set picUri points to new file where we save avatar
                        picUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "avatar_" + String.valueOf(System.currentTimeMillis()) + ".jpg"));
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, picUri);
                        try {
                            intent.putExtra("return-data", true);
                            startActivityForResult(intent, REQUEST_CAMERA);
                        } catch (ActivityNotFoundException e) {
                            Toast.makeText(EditProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                        break;
                    case R.id.choose_from_gallery:

                        Intent intent1 = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        intent1.setType("image/*");
                        startActivityForResult(Intent.createChooser(intent1, "select file"), SELECT_FILE);
                        break;
                    case R.id.remove_picture:
                        editAvatar.setImageResource(R.drawable.user_no_avatar);
                        /*bitmapAvatar = ((BitmapDrawable) editAvatar.getDrawable()).getBitmap();*/
                        break;
                }
                return true;

            }
        });
        popupMenu.show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_FILE) {
                picUri = data.getData();
                try {
                    Intent cropIntent = new Intent("com.android.camera.action.CROP");
                    cropIntent.setDataAndType(picUri, "image/*");
                    cropIntent.putExtra("crop", "true");
                    cropIntent.putExtra("aspectX", 1);
                    cropIntent.putExtra("aspectY", 1);
                    cropIntent.putExtra("outputX", 480);
                    cropIntent.putExtra("outputY", 480);
                    cropIntent.putExtra("return-data", true);
                    startActivityForResult(cropIntent, PIC_CROP);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(EditProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
            if (requestCode == REQUEST_CAMERA) {
                try {
                    Intent cropIntent = new Intent("com.android.camera.action.CROP");
                    cropIntent.setDataAndType(picUri, "image/*");
                    cropIntent.putExtra("crop", "true");
                    cropIntent.putExtra("aspectX", 1);
                    cropIntent.putExtra("aspectY", 1);
                    cropIntent.putExtra("outputX", 480);
                    cropIntent.putExtra("outputY", 480);
                    cropIntent.putExtra("return-data", true);
                    startActivityForResult(cropIntent, PIC_CROP);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(EditProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
            if (requestCode == PIC_CROP) {
                Bundle extras = data.getExtras();
                Bitmap bitmap = extras.getParcelable("data");
                editAvatar.setImageBitmap(bitmap);
            }

        }
    }

    private void signUpUser() {

        //convert bitmapAvatar to byte[]
        Bitmap bitmap = ((BitmapDrawable) editAvatar.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        //make parse file of avatar and save it
        final ParseFile avatar = new ParseFile("avatar.png", byteArray);
        avatar.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Toast.makeText(EditProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                } else {


                    //get intent
                    final Intent intent = getIntent();

                    //new user
                    ParseUser user = new ParseUser();

                    //set user mail and password
                    user.setUsername(intent.getStringExtra("email"));
                    user.setEmail(intent.getStringExtra("email"));
                    user.setPassword((intent.getStringExtra("password")));

                    //set other information
                    user.put("name", editName.getText().toString().trim());
                    user.put("gender", editGender.getSelectedItem().toString());
                    user.put("date_of_birth", editDateOfBirth.getText().toString());
                    user.put("phone", editPhone.getText().toString().trim());
                    user.put("id_card_number", editIDCardNumber.getText().toString().trim());
                    user.put("description", editDescription.getText().toString().trim());
                    user.put("avatar", avatar);

                    //Sign up user
                    user.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(ParseException e) {
                            progressDialog.hide();
                            if (e == null) {
                                Toast.makeText(EditProfileActivity.this, getString(R.string.sign_up_completed), Toast.LENGTH_SHORT).show();

                                //Send user to MapViewActivity
                                Intent intent1 = new Intent(EditProfileActivity.this, MapViewActivity.class);
                                startActivity(intent1);
                                finish();
                            } else {
                                Toast.makeText(EditProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

    }

    private void updateUser() {

        //convert bitmapAvatar to byte[]
        Bitmap bitmap = ((BitmapDrawable) editAvatar.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        //make parse file of avatar and save it
        final ParseFile avatar = new ParseFile("avatar.png", byteArray);
        avatar.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Toast.makeText(EditProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                } else {

                    //get intent
                    Intent intent = getIntent();

                    //new user
                    final ParseUser user = ParseUser.getCurrentUser();
                    //set other information

                    user.put("name", editName.getText().toString().trim());
                    user.put("gender", editGender.getSelectedItem().toString());
                    user.put("date_of_birth", editDateOfBirth.getText().toString());
                    user.put("phone", editPhone.getText().toString().trim());
                    user.put("id_card_number", editIDCardNumber.getText().toString().trim());
                    user.put("description", editDescription.getText().toString().trim());
                    user.put("avatar", avatar);

                    //Sign up user
                    user.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            progressDialog.hide();
                            if (e == null) {
                                Toast.makeText(EditProfileActivity.this, "Update completed", Toast.LENGTH_SHORT).show();

                                finish();
                            } else {
                                Toast.makeText(EditProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }

        });

    }

    private void showDatePicker() {
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );


        dpd.show(getFragmentManager(), this.getResources().getString(R.string.date_of_birth));
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        editDateOfBirth.setText(dayOfMonth + "/" + monthOfYear + "/" + year);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_edit_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()) {
            case R.id.done:
                if (editName.getText().toString().trim().equals("")) {
                    Toast.makeText(EditProfileActivity.this, getString(R.string.please_enter_your_name), Toast.LENGTH_SHORT).show();
                    editName.requestFocus();
                    return false;
                }

                if (editPhone.getText().toString().equals("")) {
                    Toast.makeText(EditProfileActivity.this, getString(R.string.please_enter_your_phone_number), Toast.LENGTH_SHORT).show();
                    editPhone.requestFocus();
                    return false;
                }

                if (editIDCardNumber.getText().toString().trim().equals("")) {
                    Toast.makeText(EditProfileActivity.this, getString(R.string.please_enter_your_id_card_number), Toast.LENGTH_SHORT).show();
                    editIDCardNumber.requestFocus();
                    return false;
                }

                if (editDescription.getText().toString().trim().equals("")) {
                    Toast.makeText(EditProfileActivity.this, getString(R.string.you_must_tell_something_about_yourself), Toast.LENGTH_SHORT).show();
                    editDescription.requestFocus();
                    return false;
                }

                progressDialog.show();

                //Check if we should signUpUser or updateUser and then send user to MapViewActivity

                if (ParseAnonymousUtils.isLinked(ParseUser.getCurrentUser())) {
                    signUpUser();
                } else {
                    //If current user is not anonymous user
                    //Get current user data from Parse.com
                    ParseUser currentUser = ParseUser.getCurrentUser();
                    if (currentUser != null) {
                        //Send logged in user to MapViewActivity.class
                        updateUser();

                    } else {
                        //Send user to LoginSignupActivity.class
                        signUpUser();

                    }
                }

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
