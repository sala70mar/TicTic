package com.qboxus.tictic.activitesfragments.profile;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;

import com.qboxus.tictic.apiclasses.FileUploader;
import com.qboxus.tictic.databinding.ActivityEditProfileBinding;
import com.qboxus.tictic.interfaces.FragmentCallBack;
import com.qboxus.tictic.models.OptionSelectionModel;
import com.qboxus.tictic.simpleclasses.AppCompatLocaleActivity;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.qboxus.tictic.apiclasses.ApiLinks;
import com.qboxus.tictic.trimmodule.TrimType;
import com.qboxus.tictic.trimmodule.TrimVideo;
import com.qboxus.tictic.trimmodule.TrimmerUtils;
import com.volley.plus.VPackages.VolleyRequest;
import com.qboxus.tictic.Constants;
import com.volley.plus.interfaces.APICallBack;
import com.volley.plus.interfaces.Callback;
import com.qboxus.tictic.interfaces.KeyboardHeightObserver;
import com.qboxus.tictic.models.UserModel;
import com.qboxus.tictic.R;
import com.qboxus.tictic.simpleclasses.DataParsing;
import com.qboxus.tictic.simpleclasses.Functions;
import com.qboxus.tictic.simpleclasses.KeyboardHeightProvider;
import com.qboxus.tictic.simpleclasses.PermissionUtils;
import com.qboxus.tictic.simpleclasses.Variables;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EditProfileA extends AppCompatLocaleActivity {


    ActivityEditProfileBinding binding;
    //for Permission taken
    PermissionUtils takePermissionUtils;

    ArrayList<OptionSelectionModel> optionalList=new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Functions.setLocale(Functions.getSharedPreference(this).getString(Variables.APP_LANGUAGE_CODE,Variables.DEFAULT_LANGUAGE_CODE), this, getClass(),false);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_edit_profile);

        initControl();
        actionControl();
    }

    private void actionControl() {
        binding.ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePermissionUtils=new PermissionUtils(EditProfileA.this,mPermissionImageResult);
                if (takePermissionUtils.isStorageCameraPermissionGranted()) {
                    openBottomSheetforImage();
                }
                else
                {
                    takePermissionUtils.showStorageCameraPermissionDailog(getString(R.string.we_need_storage_and_camera_permission_for_upload_profile_pic));
                }
            }
        });

        binding.ivProfileVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePermissionUtils=new PermissionUtils(EditProfileA.this,mPermissionVideoResult);
                if (takePermissionUtils.isStoragePermissionGranted()) {
                    openBottomSheetforGif();
                }
                else
                {
                    takePermissionUtils.showStoragePermissionDailog(getString(R.string.we_need_storage_permission_for_upload_profile_video));
                }
            }
        });
        binding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        binding.tvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkValidation()) {
                    callApiForEditProfile();
                }
            }
        });

        setKeyboardListener();


        // add the input filter to eidt text of username
        InputFilter[] username_filters = new InputFilter[1];
        username_filters[0] = new InputFilter.LengthFilter(Constants.USERNAME_CHAR_LIMIT);
        binding.etUsername.setFilters(username_filters);
        binding.etUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.tvUsernameCount.setText(binding.etUsername.getText().length() + "/" + Constants.USERNAME_CHAR_LIMIT);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        // add the input filter to edittext of userbio
        InputFilter[] filters = new InputFilter[1];
        filters[0] = new InputFilter.LengthFilter(Constants.BIO_CHAR_LIMIT);
        binding.etUserBio.setFilters(filters);
        binding.etUserBio.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.tvBioCount.setText(binding.etUserBio.getText().length() + "/" + Constants.BIO_CHAR_LIMIT);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void openBottomSheetforImage() {
        optionalList.clear();
        optionalList.add(new OptionSelectionModel("1",binding.getRoot().getContext().getString(R.string.take_photo)));
        optionalList.add(new OptionSelectionModel("2",binding.getRoot().getContext().getString(R.string.select_from_gallery)));
        optionalList.add(new OptionSelectionModel("3",binding.getRoot().getContext().getString(R.string.view_photo)));
        final OptionSelectionSheetF fragment = new OptionSelectionSheetF(optionalList,new FragmentCallBack() {
            @Override
            public void onResponce(Bundle bundle) {
                if (bundle.getBoolean("isShow",false))
                {
                    OptionSelectionModel item=optionalList.get(bundle.getInt("position",0));
                    if (item.getId().equals("1"))
                    {
                        openCameraIntent();
                    }
                    else
                    if (item.getId().equals("2"))
                    {
                        openGalleryIntent();
                    }
                    else
                    if (item.getId().equals("3"))
                    {
                        openProfileFullview(false);
                    }
                }
            }
        });
        fragment.show(getSupportFragmentManager(), "OptionSelectionSheetF");
    }


    private void openBottomSheetforGif() {
        optionalList.clear();
        optionalList.add(new OptionSelectionModel("1",binding.getRoot().getContext().getString(R.string.change_video)));
        optionalList.add(new OptionSelectionModel("2",binding.getRoot().getContext().getString(R.string.remove_video)));
        optionalList.add(new OptionSelectionModel("3",binding.getRoot().getContext().getString(R.string.watch_video)));
        final OptionSelectionSheetF fragment = new OptionSelectionSheetF(optionalList,new FragmentCallBack() {
            @Override
            public void onResponce(Bundle bundle) {
                if (bundle.getBoolean("isShow",false))
                {
                    OptionSelectionModel item=optionalList.get(bundle.getInt("position",0));
                    if (item.getId().equals("1"))
                    {
                        pickVideoFromGallery();
                    }
                    else
                    if (item.getId().equals("2"))
                    {
                        updateEmptyProfile();
                    }
                    else
                    if (item.getId().equals("3"))
                    {
                        openProfileFullview(true);
                    }
                }
            }
        });
        fragment.show(getSupportFragmentManager(), "OptionSelectionSheetF");
    }

    private void openGalleryIntent() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        resultCallbackForGallery.launch(intent);
    }

    private void openProfileFullview(boolean isGif) {
        String mediaUrl="";
        if (isGif)
        {
            mediaUrl= Functions.getSharedPreference(binding.getRoot().getContext()).getString(Variables.U_GIF, "");
        }
        else
        {
            mediaUrl= Functions.getSharedPreference(binding.getRoot().getContext()).getString(Variables.U_PIC, "");
        }

        Intent intent=new Intent(binding.getRoot().getContext(), SeeFullImageA.class);
        intent.putExtra("image_url", mediaUrl);
        intent.putExtra("isGif",isGif);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }


    private void initControl() {

        setupScreenData();
        callApiForUserDetails();
    }

    private void setupScreenData() {
        binding.etUsername.setText(Functions.getSharedPreference(binding.getRoot().getContext()).getString(Variables.U_NAME, ""));
        binding.etFirstname.setText(Functions.getSharedPreference(binding.getRoot().getContext()).getString(Variables.F_NAME, ""));
        binding.etLastname.setText(Functions.getSharedPreference(binding.getRoot().getContext()).getString(Variables.L_NAME, ""));


        String pic = Functions.getSharedPreference(binding.getRoot().getContext()).getString(Variables.U_PIC, "");
        binding.ivProfile.setController(Functions.frescoImageLoad(pic,R.drawable.ic_user_icon,binding.ivProfile,false));

        String videoGif = Functions.getSharedPreference(binding.getRoot().getContext()).getString(Variables.U_GIF, "");
        binding.ivProfileVideo.setController(Functions.frescoImageLoad(videoGif,R.drawable.ic_user_icon,binding.ivProfileVideo,true));


        String gender = Functions.getSharedPreference(binding.getRoot().getContext()).getString(Variables.GENDER, "");
        if (gender != null && gender.equalsIgnoreCase("male")) {
            binding.rbMale.setChecked(true);
        } else if (gender != null && gender.equalsIgnoreCase("female")) {
            binding.rbFemale.setChecked(true);
        }


        binding.etWebsite.setText(Functions.getSharedPreference(binding.getRoot().getContext()).getString(Variables.U_LINK, ""));
        binding.etUserBio.setText(Functions.getSharedPreference(binding.getRoot().getContext()).getString(Variables.U_BIO, ""));

        showTextLimit();
    }



    // open the intent for get the video from gallery
    public void pickVideoFromGallery() {
        File fileTrim=new File( Functions.getAppFolder(binding.getRoot().getContext())+Variables.gallery_trimed_video);
        File fileFilter=new File( Functions.getAppFolder(binding.getRoot().getContext())+ Variables.output_filter_file);
        Functions.clearFilesCacheBeforeOperation(fileTrim,fileFilter);

        Constants.RECORDING_DURATION=6*1000;
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        takeOrSelectVideoResultLauncher.launch(Intent.createChooser(intent, "Select Video"));
    }

    // start trimming activity
    ActivityResultLauncher<Intent> takeOrSelectVideoResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK ) {
                        Intent data = result.getData();
                        if (TrimmerUtils.getDuration(EditProfileA.this,data.getData())<Constants.MIN_TRIM_TIME){
                            Toast.makeText(EditProfileA.this,binding.getRoot().getContext().getString(R.string.video_must_be_larger_then_second),Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (data.getData() != null) {
                            openTrimActivity(String.valueOf(data.getData()));
                        }
                    }
                }
            });



    private void openTrimActivity(String data) {
        TrimVideo.activity(data)
                .setTrimType(TrimType.MIN_MAX_DURATION)
                .setMinToMax(Constants.MIN_TRIM_TIME, (Constants.RECORDING_DURATION/1000))
                .setMinDuration(Constants.MAX_TRIM_TIME)
                .setTitle("")//seconds
                .setMaxTimeCheck(Constants.RECORDING_DURATION)
                .start(this,videoTrimResultLauncher);
    }

    ActivityResultLauncher<Intent> videoTrimResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Uri uri = Uri.parse(TrimVideo.getTrimmedVideoPath(result.getData(),Variables.gallery_trimed_video));
                        String filepath = String.valueOf(uri);
                        uploadProfileVideo(filepath);
                    } else
                        Log.d(Constants.tag,"videoTrimResultLauncher data is null");
                }
            });

    private void uploadProfileVideo(String filepath) {
        Functions.showLoader(EditProfileA.this,false,false);
        String userId=Functions.getSharedPreference(binding.getRoot().getContext()).getString(Variables.U_ID, "");
        FileUploader fileUploader = new FileUploader(new File(filepath),getApplicationContext(),userId);
        fileUploader.SetCallBack(new FileUploader.FileUploaderCallback() {
            @Override
            public void onError() {
                //send error broadcast
                Functions.cancelLoader();

            }

            @Override
            public void onFinish(String responses) {
                Functions.cancelLoader();
                Functions.printLog(Constants.tag, responses);
                try {
                    JSONObject jsonObject = new JSONObject(responses);
                    int code = jsonObject.optInt("code",0);
                    JSONObject msg=jsonObject.getJSONObject("msg");
                    if (code==200) {
                        UserModel userDetailModel= DataParsing.getUserDataModel(msg.optJSONObject("User"));
                        SharedPreferences.Editor editor = Functions.getSharedPreference(binding.getRoot().getContext()).edit();
                        editor.putString(Variables.U_GIF,userDetailModel.getProfileGif());
                        editor.commit();
                        isActivityCallback=true;
                        setupScreenData();
                    }

                } catch (Exception e) {
                    Functions.printLog(Constants.tag, "Exception: "+e);
                }
            }

            @Override
            public void onProgressUpdate(int currentpercent, int totalpercent,String msg) {
                //send progress broadcast
                if (currentpercent>0)
                {

                }
            }
        });


    }


    //intialize the keyboard listener
    int priviousHeight = 0;
    private void setKeyboardListener() {

        KeyboardHeightProvider keyboardHeightProvider = new KeyboardHeightProvider(EditProfileA.this);
        keyboardHeightProvider.setKeyboardHeightObserver(new KeyboardHeightObserver() {
            @Override
            public void onKeyboardHeightChanged(int height, int orientation) {
                Functions.printLog(Constants.tag, "" + height);
                if (height < 0) {
                    priviousHeight = Math.abs(height);
                }

                LinearLayout main_layout = findViewById(R.id.main_layout);

                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(main_layout.getWidth(), main_layout.getHeight());
                params.bottomMargin = height + priviousHeight;
                main_layout.setLayoutParams(params);
            }
        });

    }



    private ActivityResultLauncher<String[]> mPermissionImageResult = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onActivityResult(Map<String, Boolean> result) {

                    boolean allPermissionClear=true;
                    List<String> blockPermissionCheck=new ArrayList<>();
                    for (String key : result.keySet())
                    {
                        if (!(result.get(key)))
                        {
                            allPermissionClear=false;
                            blockPermissionCheck.add(Functions.getPermissionStatus(EditProfileA.this,key));
                        }
                    }
                    if (blockPermissionCheck.contains("blocked"))
                    {
                        Functions.showPermissionSetting(EditProfileA.this,getString(R.string.we_need_storage_and_camera_permission_for_upload_profile_pic));
                    }
                    else
                    if (allPermissionClear)
                    {
                        openBottomSheetforImage();
                    }

                }
            });



    private ActivityResultLauncher<String[]> mPermissionVideoResult = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onActivityResult(Map<String, Boolean> result) {

                    boolean allPermissionClear=true;
                    List<String> blockPermissionCheck=new ArrayList<>();
                    for (String key : result.keySet())
                    {
                        if (!(result.get(key)))
                        {
                            allPermissionClear=false;
                            blockPermissionCheck.add(Functions.getPermissionStatus(EditProfileA.this,key));
                        }
                    }
                    if (blockPermissionCheck.contains("blocked"))
                    {
                        Functions.showPermissionSetting(EditProfileA.this,getString(R.string.we_need_storage_and_camera_permission_for_upload_profile_pic));
                    }
                    else
                    if (allPermissionClear)
                    {
                        openBottomSheetforGif();
                    }

                }
            });



    ActivityResultLauncher<Intent> resultCallbackForCrop = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        CropImage.ActivityResult cropResult = CropImage.getActivityResult(data);
                        handleCrop(cropResult.getUri());
                    }
                }
            });


    ActivityResultLauncher<Intent> resultCallbackForGallery = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        Uri selectedImage = data.getData();
                        beginCrop(selectedImage);

                    }
                }
            });

    ActivityResultLauncher<Intent> resultCallbackForCamera = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Matrix matrix = new Matrix();
                        try {
                            android.media.ExifInterface exif = new android.media.ExifInterface(imageFilePath);
                            int orientation = exif.getAttributeInt(android.media.ExifInterface.TAG_ORIENTATION, 1);
                            switch (orientation) {
                                case android.media.ExifInterface.ORIENTATION_ROTATE_90:
                                    matrix.postRotate(90);
                                    break;
                                case android.media.ExifInterface.ORIENTATION_ROTATE_180:
                                    matrix.postRotate(180);
                                    break;
                                case android.media.ExifInterface.ORIENTATION_ROTATE_270:
                                    matrix.postRotate(270);
                                    break;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Uri selectedImage = (Uri.fromFile(new File(imageFilePath)));
                        beginCrop(selectedImage);
                    }
                }
            });


    // below three method is related with taking the picture from camera
    private void openCameraIntent() {
        Intent pictureIntent = new Intent(
                MediaStore.ACTION_IMAGE_CAPTURE);
        if (pictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (Exception ex) {
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(binding.getRoot().getContext().getApplicationContext(), getPackageName() + ".fileprovider", photoFile);
                pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                resultCallbackForCamera.launch(pictureIntent);
            }
        }
    }

    // create a temp image file
    String imageFilePath;

    private File createImageFile() throws Exception {
        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss",
                        Locale.ENGLISH).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir =
                getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        imageFilePath = image.getAbsolutePath();
        return image;
    }

    // this will check the validations like none of the field can be the empty
    public boolean checkValidation() {

        String uname = binding.etUsername.getText().toString();
        String firstname = binding.etFirstname.getText().toString();
        String lastname = binding.etLastname.getText().toString();

        if (TextUtils.isEmpty(uname)) {
            Functions.showValidationMsg(EditProfileA.this,binding.scrollContainer,binding.getRoot().getContext().getString(R.string.please_correct_user_name));
            return false;
        } else if (uname.length() < 4 || uname.length() > 14) {
            Functions.showValidationMsg(EditProfileA.this,binding.scrollContainer,binding.getRoot().getContext().getString(R.string.username_length_between_valid));

            return false;
        } else
        if (!(UserNameTwoCaseValidate(uname)))
        {
            Functions.showValidationMsg(EditProfileA.this,binding.scrollContainer,binding.getRoot().getContext().getString(R.string.username_must_contain_alphabet));
            return false;
        }else if (TextUtils.isEmpty(firstname)) {
            Functions.showValidationMsg(EditProfileA.this,binding.scrollContainer,binding.getRoot().getContext().getString(R.string.please_enter_first_name));
            return false;
        } else if (TextUtils.isEmpty(lastname)) {
            Functions.showValidationMsg(EditProfileA.this,binding.scrollContainer,binding.getRoot().getContext().getString(R.string.please_enter_last_name));
            return false;
        } else if (!binding.rbMale.isChecked() && !binding.rbFemale.isChecked()) {
            Functions.showValidationMsg(EditProfileA.this,binding.scrollContainer,binding.getRoot().getContext().getString(R.string.please_select_your_gender));
            return false;
        }

        return true;
    }

    private boolean UserNameTwoCaseValidate(String name) {

        Pattern let_p = Pattern.compile("[a-z]", Pattern.CASE_INSENSITIVE);
        Matcher let_m = let_p.matcher(name);
        boolean let_str = let_m.find();

        if (let_str)
        {
            return true;
        }
        return false;
    }


    String imageBas64Small,imageBas64Big;

    private void beginCrop(Uri source) {
        Intent intent=CropImage.activity(source).setCropShape(CropImageView.CropShape.OVAL)
                .setAspectRatio(1,1).getIntent(EditProfileA.this);
        resultCallbackForCrop.launch(intent);
    }

    // get the image uri after the image crope
    private void handleCrop(Uri userimageuri) {

        InputStream imageStream = null;
        try {
            imageStream = getContentResolver().openInputStream(userimageuri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        final Bitmap imagebitmap = BitmapFactory.decodeStream(imageStream);

        String path = userimageuri.getPath();
        Matrix matrix = new Matrix();
        android.media.ExifInterface exif = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            try {
                exif = new android.media.ExifInterface(path);
                int orientation = exif.getAttributeInt(android.media.ExifInterface.TAG_ORIENTATION, 1);
                switch (orientation) {
                    case android.media.ExifInterface.ORIENTATION_ROTATE_90:
                        matrix.postRotate(90);
                        break;
                    case android.media.ExifInterface.ORIENTATION_ROTATE_180:
                        matrix.postRotate(180);
                        break;
                    case android.media.ExifInterface.ORIENTATION_ROTATE_270:
                        matrix.postRotate(270);
                        break;
                    default:
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        Bitmap rotatedBitmap = Bitmap.createBitmap(imagebitmap, 0, 0, imagebitmap.getWidth(), imagebitmap.getHeight(), matrix, true);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        imageBas64Big=Functions.bitmapToBase64(rotatedBitmap);
        Bitmap converetdImage = getResizedBitmap(rotatedBitmap, Constants.PROFILE_IMAGE_SQUARE_SIZE);
        imageBas64Small = Functions.bitmapToBase64(converetdImage);

        callApiForImageNew();
    }


    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }


    // call api for upload the image on server
    public void callApiForImageNew() {

        JSONObject parameters = new JSONObject();
        try {
            parameters.put("user_id", Functions.getSharedPreference(binding.getRoot().getContext()).getString(Variables.U_ID, "0"));
            parameters.put("profile_pic", imageBas64Big);
            parameters.put("profile_pic_small", imageBas64Small);
            parameters.put("extension", "png");

        } catch (Exception e) {
            e.printStackTrace();
        }
        Functions.showLoader(EditProfileA.this, false, false);
        VolleyRequest.JsonPostRequest(EditProfileA.this, ApiLinks.addUserImageNew, parameters,Functions.getHeaders(this), new Callback() {
            @Override
            public void onResponce(String resp) {
                Functions.checkStatus(EditProfileA.this,resp);
                Functions.cancelLoader();
                try {
                    JSONObject response = new JSONObject(resp);
                    String code = response.optString("code");
                    JSONObject msg = response.optJSONObject("msg");
                    if (code.equals("200")) {


                        UserModel userDetailModel= DataParsing.getUserDataModel(msg.optJSONObject("User"));

                        Functions.getSharedPreference(binding.getRoot().getContext()).edit().putString(Variables.U_PIC, userDetailModel.getProfilePic()).commit();

                        binding.ivProfile.setController(Functions.frescoImageLoad(Functions.getSharedPreference(binding.getRoot().getContext()).getString(Variables.U_PIC, ""),binding.ivProfile,false));



                        isActivityCallback=true;
                        Functions.showToast(binding.getRoot().getContext(), getString(R.string.image_update_successfully));
                    }
                    else
                    {
                        Functions.showAlert(EditProfileA.this,binding.getRoot().getContext().getString(R.string.alert),
                                ""+response.optString("msg"));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }


    // this will update the latest info of user in database
    public void updateEmptyProfile() {


        JSONObject parameters = new JSONObject();
        try {
            parameters.put("user_id", Functions.getSharedPreference(binding.getRoot().getContext()).getString(Variables.U_ID, "0"));
            parameters.put("profile_gif", "");


        } catch (Exception e) {
            e.printStackTrace();
        }
        Functions.showLoader(EditProfileA.this, false, false);
        VolleyRequest.JsonPostRequest(EditProfileA.this, ApiLinks.editProfile, parameters,Functions.getHeaders(this), new Callback() {
            @Override
            public void onResponce(String resp) {
                Functions.checkStatus(EditProfileA.this,resp);
                Functions.cancelLoader();
                try {
                    JSONObject response = new JSONObject(resp);
                    String code = response.optString("code");
                    JSONObject msg = response.optJSONObject("msg");
                    if (code.equals("200")) {

                        UserModel userDetailModel=DataParsing.getUserDataModel(msg.optJSONObject("User"));

                        SharedPreferences.Editor editor = Functions.getSharedPreference(binding.getRoot().getContext()).edit();
                        editor.putString(Variables.U_GIF, ""+userDetailModel.getProfileGif());
                        editor.commit();
                        isActivityCallback=true;

                        setupScreenData();

                    } else {
                        Functions.showToast(binding.getRoot().getContext(), response.optString("msg"));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }


    // this will update the latest info of user in database
    public void callApiForEditProfile() {

        Functions.showLoader(EditProfileA.this, false, false);

        String uname = binding.etUsername.getText().toString().toLowerCase().replaceAll("\\s", "");
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("username", uname.replaceAll("@", ""));
            parameters.put("user_id", Functions.getSharedPreference(binding.getRoot().getContext()).getString(Variables.U_ID, "0"));
            parameters.put("first_name", binding.etFirstname.getText().toString());
            parameters.put("last_name", binding.etLastname.getText().toString());

            if (binding.rbMale.isChecked()) {
                parameters.put("gender", "Male");

            } else if (binding.rbFemale.isChecked()) {
                parameters.put("gender", "Female");
            }

            parameters.put("website", binding.etWebsite.getText().toString());
            parameters.put("bio", binding.etUserBio.getText().toString());

        } catch (Exception e) {
            e.printStackTrace();
        }

        VolleyRequest.JsonPostRequest(EditProfileA.this, ApiLinks.editProfile, parameters,Functions.getHeaders(this), new Callback() {
            @Override
            public void onResponce(String resp) {
                Functions.checkStatus(EditProfileA.this,resp);
                Functions.cancelLoader();
                try {
                    JSONObject response = new JSONObject(resp);
                    String code = response.optString("code");
                    JSONObject msg = response.optJSONObject("msg");
                    if (code.equals("200")) {

                        UserModel userDetailModel=DataParsing.getUserDataModel(msg.optJSONObject("User"));

                        SharedPreferences.Editor editor = Functions.getSharedPreference(binding.getRoot().getContext()).edit();

                        String u_name = userDetailModel.getUsername();
                        if (!u_name.contains("@"))
                            u_name = "@" + u_name;

                        editor.putString(Variables.U_NAME, u_name);
                        editor.putString(Variables.F_NAME, userDetailModel.getFirstName());
                        editor.putString(Variables.L_NAME, userDetailModel.getLastName());
                        editor.putString(Variables.U_BIO, userDetailModel.getBio());
                        editor.putString(Variables.U_LINK, userDetailModel.getWebsite());
                        editor.putString(Variables.GENDER, userDetailModel.getGender());
                        editor.commit();

                        isActivityCallback=true;
                        onBackPressed();

                    } else {
                        Functions.showToast(binding.getRoot().getContext(), response.optString("msg"));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }


    // this will get the user data and parse the data and show the data into views
    public void callApiForUserDetails() {
        Functions.showLoader(EditProfileA.this, false, false);
        Functions.callApiForGetUserData(EditProfileA.this,
                Functions.getSharedPreference(binding.getRoot().getContext()).getString(Variables.U_ID, ""),
                new APICallBack() {
                    @Override
                    public void arrayData(ArrayList arrayList) {

                    }

                    @Override
                    public void onSuccess(String responce) {
                        Functions.cancelLoader();
                        parseUserData(responce);
                    }

                    @Override
                    public void onFail(String responce) {

                    }
                });
    }

    public void parseUserData(String responce) {
        try {
            JSONObject jsonObject = new JSONObject(responce);

            String code = jsonObject.optString("code");

            if (code.equals("200")) {
                JSONObject msg = jsonObject.optJSONObject("msg");

                UserModel userDetailModel=DataParsing.getUserDataModel(msg.optJSONObject("User"));

                SharedPreferences.Editor editor = Functions.getSharedPreference(binding.getRoot().getContext()).edit();

                String u_name = userDetailModel.getUsername();
                if (!u_name.contains("@"))
                    u_name = "@" + u_name;

                editor.putString(Variables.U_NAME, u_name);
                editor.putString(Variables.F_NAME, userDetailModel.getFirstName());
                editor.putString(Variables.L_NAME, userDetailModel.getLastName());
                editor.putString(Variables.U_BIO, userDetailModel.getBio());
                editor.putString(Variables.U_LINK, userDetailModel.getWebsite());
                editor.putString(Variables.GENDER, userDetailModel.getGender());
                editor.commit();

                setupScreenData();



            } else {
                Functions.showToast(binding.getRoot().getContext(), jsonObject.optString("msg"));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // show the txt char limit of username and userbio
    public void showTextLimit() {
        binding.tvUsernameCount.setText(binding.etUsername.getText().length() + "/" + Constants.USERNAME_CHAR_LIMIT);
        binding.tvBioCount.setText(binding.etUserBio.getText().length() + "/" + Constants.BIO_CHAR_LIMIT);

    }

    boolean isActivityCallback=false;
    @Override
    public void onBackPressed() {
        if(isActivityCallback)
        {
            Intent intent = new Intent();
            intent.putExtra("isShow", true);
            setResult(RESULT_OK, intent);
            finish();
        }
        else
        {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        if (mPermissionImageResult!=null)
        {
            mPermissionImageResult.unregister();
        }
        if (mPermissionVideoResult!=null)
        {
            mPermissionVideoResult.unregister();
        }
        Functions.hideSoftKeyboard(EditProfileA.this);
        super.onDestroy();
    }
}
