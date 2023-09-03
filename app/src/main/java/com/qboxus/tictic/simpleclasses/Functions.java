package com.qboxus.tictic.simpleclasses;


import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ConfigurationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMetadataRetriever;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.amulyakhare.textdrawable.TextDrawable;
import com.danikula.videocache.HttpProxyCacheServer;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.Postprocessor;
import com.facebook.login.LoginManager;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultAllocator;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.snackbar.Snackbar;
import com.qboxus.tictic.BuildConfig;
import com.qboxus.tictic.activitesfragments.livestreaming.CallBack;
import com.qboxus.tictic.activitesfragments.profile.analytics.KeyMatricsModel;
import com.qboxus.tictic.activitesfragments.spaces.utils.CookieBar;
import com.qboxus.tictic.interfaces.GenrateBitmapCallback;
import com.qboxus.tictic.interfaces.GenrateFileCallback;
import com.qboxus.tictic.interfaces.InternetCheckCallback;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.googlecode.mp4parser.authoring.Track;
import com.qboxus.tictic.activitesfragments.accounts.LoginA;
import com.qboxus.tictic.activitesfragments.sendgift.StickerModel;
import com.qboxus.tictic.activitesfragments.SplashA;
import com.qboxus.tictic.apiclasses.ApiLinks;
import com.qboxus.tictic.models.ImageHeightWidthModel;
import com.qboxus.tictic.models.PromotionModel;
import com.qboxus.tictic.models.StoryModel;
import com.qboxus.tictic.models.StoryVideoModel;
import com.qboxus.tictic.models.UsersModel;
import com.volley.plus.VPackages.VolleyRequest;
import com.qboxus.tictic.Constants;
import com.qboxus.tictic.mainmenu.MainMenuActivity;
import com.volley.plus.interfaces.APICallBack;
import com.volley.plus.interfaces.Callback;
import com.qboxus.tictic.interfaces.FragmentCallBack;
import com.qboxus.tictic.models.CommentModel;
import com.qboxus.tictic.models.HomeModel;
import com.qboxus.tictic.models.MultipleAccountModel;
import com.qboxus.tictic.models.PrivacyPolicySettingModel;
import com.qboxus.tictic.models.PushNotificationSettingModel;
import com.qboxus.tictic.models.UserModel;
import com.qboxus.tictic.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.michaelrocks.libphonenumber.android.PhoneNumberUtil;
import io.michaelrocks.libphonenumber.android.Phonenumber;
import io.paperdb.Paper;
import jp.wasabeef.fresco.processors.BlurPostprocessor;
import kotlin.jvm.JvmStatic;

import static android.content.Context.CONNECTIVITY_SERVICE;
import static android.media.MediaMetadataRetriever.METADATA_KEY_DURATION;

/**
 * Created by qboxus on 2/20/2019.
 */

public class Functions {

    public static LoadControl getExoControler() {
        return new DefaultLoadControl.Builder()
                .setAllocator(new DefaultAllocator(true, (12 * 1024 * 1024)))
                .setBufferDurationsMs(1000, 5000, 1000, 1000)
                .setTargetBufferBytes(-1)
                .setPrioritizeTimeOverSizeThresholds(true)
                .build();
    }

    public static boolean isOpenGLVersionSupported(Context context, int version) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();

        return (configurationInfo.reqGlEsVersion >= version);
    }

    public static boolean isRTL(View view) {
        return ViewCompat.getLayoutDirection(view) == ViewCompat.LAYOUT_DIRECTION_RTL;
    }

    public static boolean isWebUrl(String url) {
        String urlRegex = "^((http|https)://)?([a-zA-Z0-9\\-]+\\.)+[a-zA-Z]{2,6}(:[0-9]+)?(/.*)?$";
        Pattern pattern = Pattern.compile(urlRegex);
        Matcher matcher = pattern.matcher(url);
        return matcher.matches();
    }

    @JvmStatic
    public static DisplayMetrics getPhoneResolution(Activity activity){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics;
    }

    public static String getCountryCode(@Nullable Context context) {
        if (context != null) {
            TelephonyManager telephonyManager =
                    (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (telephonyManager != null) {
                String countryCode = telephonyManager.getNetworkCountryIso();
                if (!TextUtils.isEmpty(countryCode)) {
                    return (countryCode.toUpperCase());
                }
            }
        }
        return (Locale.getDefault().getCountry().toUpperCase());
    }

    public static String getValidatedNumber(Context context,String number,String region) {
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.createInstance(context);

        Phonenumber.PhoneNumber phoneNumber=null;
        try {
            phoneNumber = phoneUtil.parse(number, region);
        } catch (Exception e) {
            Log.e(Constants.tag, "error during parsing a number");
        }

        if(phoneNumber != null && phoneUtil.isValidNumber(phoneNumber)){
            return phoneUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);
        }
        else {

            if(number.startsWith("+")){
                return number;
            }
            else if(number.startsWith("0")){
                number = number.substring(1);
                return "+"+number;
            }
            else {
                return "+"+number;
            }

        }

    }

    public static void copyCode(Context context,String text){
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("text", text);
        clipboard.setPrimaryClip(clip);

        Toast.makeText(context, "Copied", Toast.LENGTH_SHORT).show();
    }

    public static String getContryFromNumber(Context context,String number) {
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.createInstance(context);

        Phonenumber.PhoneNumber phoneNumber=null;
        try {
            phoneNumber = phoneUtil.parse(number, null);
        } catch (Exception e) {
            Log.e(Constants.tag, "error during parsing a number");
        }

        if(phoneNumber != null && phoneUtil.isValidNumber(phoneNumber)){
            Locale loc = new Locale("",phoneUtil.getRegionCodeForCountryCode(phoneNumber.getCountryCode()));
            return  loc.getDisplayCountry();
        }
        else {

            return "";
        }

    }

    public static String applyPhoneNoValidation(String number,String countryCode) {
        if (number.charAt(0)=='0')
        {
            number=number.substring(1);
        }
        number=number.replace("+","");
        number=number.replace(countryCode,"");
        if (number.charAt(0)=='0')
        {
            number=number.substring(1);
        }
        number=countryCode+number;
        number=number.replace(" ","");
        number=number.replace("(","");
        number=number.replace(")","");
        number=number.replace("-","");
        return number;
    }


    public static String changeDateToTimebase(String date) {
        try {
            Calendar currentCal = Calendar.getInstance();

            Calendar dateCal = Calendar.getInstance();



            SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.ENGLISH);
            Date d = null;
            try {
                d = f.parse(date);
                dateCal.setTime(d);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            long difference = (currentCal.getTimeInMillis() - dateCal.getTimeInMillis()) / 1000;
            Log.d(Constants.tag,"difference: "+difference);

            if (difference < 60) {
                return difference + "s ago";
            }  else
            if (difference < 3600) {
                return (0+(difference / 60)) + "m ago";
            }  else
            if (difference < 86400) {
                return (0+(difference / 3600)) + "h ago";
            }  else
            if (difference<604800)
            {
                return (0+(difference / 86400)) + "d ago";
            }
            else
            {
                if (difference<2592000)
                {
                    return (0+(difference / 604800)) + "week ago";
                }
                else
                {
                    if (difference<31536000)
                    {
                        return (0+(difference / 2592000)) + "month ago";
                    }
                    else
                    {
                        return (0+(difference / 31536000)) + "year ago";
                    }

                }

            }


        } catch (Exception e) {

            return date;
        }


    }


    // change the color of status bar into black
    public static void blackStatusBar(Activity activity) {
        View view = activity.getWindow().getDecorView();

        int flags = view.getSystemUiVisibility();
        flags &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        view.setSystemUiVisibility(flags);
        activity.getWindow().setStatusBarColor(Color.BLACK);
    }


    public static void PrintHashKey(Context context) {
        try {
            final PackageInfo info = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
            for (android.content.pm.Signature signature : info.signatures) {
                final MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                final String hashKey = Base64.encodeToString(md.digest(), Base64.DEFAULT);
                Log.d(Constants.tag, "KeyHash : " + hashKey);
            }
        } catch (Exception e) {
            Log.e(Constants.tag, "error:", e);
        }
    }


    // change the color of status bar into white
    public static void whiteStatusBar(Activity activity) {
        View view = activity.getWindow().getDecorView();
        int flags = view.getSystemUiVisibility();
        flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        view.setSystemUiVisibility(flags);
        if (new DarkModePrefManager(activity).isNightMode())
        {
            activity.getWindow().setNavigationBarColor(ContextCompat.getColor(activity, R.color.black));
            activity.getWindow().setStatusBarColor(Color.BLACK);
        }
        else
        {
            activity.getWindow().setStatusBarColor(Color.WHITE);
        }

    }



    // close the keybord
    @JvmStatic
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    // open the keyboard
    public static void showKeyboard(Activity activity) {
        View view = activity.findViewById(android.R.id.content);
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }
    }

    // retun the sharepref instance
    public static SharedPreferences getSharedPreference(Context context) {
        if (Variables.sharedPreferences != null)
            return Variables.sharedPreferences;
        else {
            Variables.sharedPreferences = context.getSharedPreferences(Variables.PREF_NAME, Context.MODE_PRIVATE);
            return Variables.sharedPreferences;
        }

    }


    public static SharedPreferences getSettingsPreference(Context context) {
        if (Variables.settingsPreferences != null)
            return Variables.settingsPreferences;
        else {
            Variables.settingsPreferences = context.getSharedPreferences(Variables.SETTING_PREF_NAME, Context.MODE_PRIVATE);
            return Variables.settingsPreferences;
        }

    }

    // print any kind of log
    public static void printLog(String title, String text) {
        if (!Constants.IS_SECURE_INFO) {
            if (title != null && text != null)
                Log.d(title, text);
        }

    }

    // get the audio file duration that is store in our directory
    public static long getfileduration(Context context, Uri uri) {
        try {

            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(context, uri);
            String durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            final int file_duration = Functions.parseInterger(durationStr);

            return file_duration;
        } catch (Exception e) {
            Log.d(Constants.tag,"Exception: "+e);
        }
        return 0;
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    // change string value to integer
    public static int parseInterger(String value) {
        if (value != null && !value.equals("")) {
            return Integer.parseInt(value);
        } else
            return 0;
    }

    // format the count value
    public static String getSuffix(String value) {
        try {

            if (value != null && (!value.equals("") && !value.equalsIgnoreCase("null"))) {
                long count = Long.parseLong(value);
                if (count < 1000)
                    return "" + count;
                int exp = (int) (Math.log(count) / Math.log(1000));
                return String.format(Locale.ENGLISH,"%.1f %c",
                        count / Math.pow(1000, exp),
                        "kMBTPE".charAt(exp - 1));
            } else {
                return "0";
            }
        } catch (Exception e) {
            return value;
        }

    }


    // return  the rundom string of given length
    public static String getRandomString(int n) {
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "abcdefghijklmnopqrstuvxyz";

        StringBuilder sb = new StringBuilder(n);

        for (int i = 0; i < n; i++) {

            int index
                    = (int) (AlphaNumericString.length()
                    * Math.random());

            sb.append(AlphaNumericString
                    .charAt(index));
        }

        return sb.toString();
    }


    public static String removeSpecialChar(String s){
        return s.replaceAll("[^a-zA-Z0-9]", "");
    }

    // show loader of simple messages
    public static void showAlert(Activity activity, String title, String Message) {
        if (activity!=null)
        {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setTitle(title);
                    builder.setMessage(Message);
                    builder.setNegativeButton(activity.getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.create();
                    builder.show();
                }
            });
        }

    }




    // dialog for show loader for showing dialog with title and descriptions
    public static void showAlert(Context context, String title, String description, final CallBack callBack) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(description);
        builder.setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (callBack != null)
                    callBack.getResponse("alert", "OK");
            }
        });
        builder.create();
        builder.show();

    }


    // dialog for show any kind of alert
    public static void showAlert(Context context, String title, String Message, String postivebtn, String negitivebtn, final Callback callback) {

        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(Message)
                .setNegativeButton(negitivebtn, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        callback.onResponce("no");
                    }
                })
                .setPositiveButton(postivebtn, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                        callback.onResponce("yes");

                    }
                }).show();
    }


    public static void showDoubleButtonAlert(Context context, String title, String message, String negTitle, String posTitle,boolean isCancelable, FragmentCallBack callBack)
    {
        final Dialog dialog = new Dialog(context);
        dialog.setCancelable(isCancelable);
        dialog.setContentView(R.layout.show_double_button_new_popup_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        final TextView tvtitle,tvMessage,tvPositive,tvNegative;
        tvtitle=dialog.findViewById(R.id.tvtitle);
        tvMessage=dialog.findViewById(R.id.tvMessage);
        tvNegative=dialog.findViewById(R.id.tvNegative);
        tvPositive=dialog.findViewById(R.id.tvPositive);


        tvtitle.setText(title);
        tvMessage.setText(message);
        tvNegative.setText(negTitle);
        tvPositive.setText(posTitle);

        tvNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Bundle bundle=new Bundle();
                bundle.putBoolean("isShow",false);
                callBack.onResponce(bundle);
            }
        });
        tvPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Bundle bundle=new Bundle();
                bundle.putBoolean("isShow",true);
                callBack.onResponce(bundle);
            }
        });
        dialog.show();
    }


    public static String readableFileSize(long size) {
        if(size <= 0) return "0";
        final String[] units = new String[] { "B", "kB", "MB", "GB", "TB" };
        int digitGroups = (int) (Math.log10(size)/ Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size/ Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }


    public static List<List<StickerModel>> createChunksOfList(List<StickerModel> originalList,
                                                              int chunkSize) {
        List<List<StickerModel>> listOfChunks = new ArrayList<List<StickerModel>>();
        for (int i = 0; i < originalList.size() / chunkSize; i++) {
            listOfChunks.add(originalList.subList(i * chunkSize, i * chunkSize
                    + chunkSize));
        }
        if (originalList.size() % chunkSize != 0) {
            listOfChunks.add((List<StickerModel>) originalList.subList(originalList.size()
                    - originalList.size() % chunkSize, originalList.size()));
        }
        return listOfChunks;
    }


    // format the username
    public static String showUsername(String username) {
        if (username != null && username.contains("@"))
            return username;
        else
            return "@" + username;
    }


    // format the username
    public static String showUsernameOnVideoSection(HomeModel item) {
        if (item.first_name != null && !(TextUtils.isEmpty(item.first_name)) &&
                item.last_name != null && !(TextUtils.isEmpty(item.last_name)))
            return item.first_name+" "+item.last_name;
        else
            return ""+item.username;
    }


    public static boolean isMyServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i("isMyServiceRunning?", true + "");
                return true;
            }
        }
        Log.i("isMyServiceRunning?", false + "");
        return false;
    }


    public static boolean checkTimeDiffernce(Calendar current_cal, String date) {
        try {


            Calendar date_cal = Calendar.getInstance();

            SimpleDateFormat f = new SimpleDateFormat("dd-MM-yyyy HH:mm:ssZZ",Locale.ENGLISH);
            Date d = null;
            try {
                d = f.parse(date);
                date_cal.setTime(d);
            } catch (Exception e) {
                e.printStackTrace();
            }

            long difference = (current_cal.getTimeInMillis() - date_cal.getTimeInMillis()) / 1000;


            Log.d(Constants.tag,"Tag : "+difference);

            if (difference <0) {
               return true;
            }
            else {
                return false;
            }

        } catch (Exception e) {
            return false;
        }


    }


    public static String changeDateTodayYesterday(Context context, String date) {
        try {
            Calendar current_cal = Calendar.getInstance();

            Calendar date_cal = Calendar.getInstance();

            SimpleDateFormat f = new SimpleDateFormat("dd-MM-yyyy HH:mm:ssZZ",Locale.ENGLISH);
            Date d = null;
            try {
                d = f.parse(date);
                date_cal.setTime(d);
            } catch (Exception e) {
                e.printStackTrace();
            }


            long difference = (current_cal.getTimeInMillis() - date_cal.getTimeInMillis()) / 1000;

            if (difference < 86400) {
                if (current_cal.get(Calendar.DAY_OF_YEAR) - date_cal.get(Calendar.DAY_OF_YEAR) == 0) {

                    SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a",Locale.ENGLISH);
                    return sdf.format(d);
                } else
                    return context.getString(R.string.yesterday);
            } else if (difference < 172800) {
                return context.getString(R.string.yesterday);
            } else
                return (difference / 86400) + context.getString(R.string.day_ago);

        }
        catch (Exception e) {
            return date;
        }


    }


    public static String bitmapToBase64(Bitmap imagebitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imagebitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        byte[] byteArray = baos.toByteArray();
        String base64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
        return base64;
    }

    public static Bitmap base64ToBitmap(String base_64) {
        Bitmap decodedByte = null;
        try {

            byte[] decodedString = Base64.decode(base_64, Base64.DEFAULT);
            decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        } catch (Exception e) {

        }
        return decodedByte;
    }


    public static boolean isShowContentPrivacy(Context context, String string_case, boolean isFriend) {
        if (string_case == null)
            return true;
        else {
            string_case = stringParseFromServerRestriction(string_case);

            if (string_case.equalsIgnoreCase("Everyone")) {
                return true;
            } else if (string_case.equalsIgnoreCase("Friends") &&
                    Functions.getSharedPreference(context).getBoolean(Variables.IS_LOGIN, false) && isFriend) {
                return true;
            } else {
                return false;
            }
        }
    }

    public static String stringParseFromServerRestriction(String res_string) {
        res_string = res_string.toUpperCase();
        res_string = res_string.replace("_", " ");
        return res_string;
    }

    public static String stringParseIntoServerRestriction(String res_string) {
        res_string = res_string.toLowerCase();
        res_string = res_string.replace(" ", "_");
        return res_string;
    }


    public static double correctTimeToSyncSample(Track track, double cutHere, boolean next) {
        double[] timeOfSyncSamples = new double[track.getSyncSamples().length];
        long currentSample = 0;
        double currentTime = 0;
        for (int i = 0; i < track.getSampleDurations().length; i++) {
            long delta = track.getSampleDurations()[i];

            if (Arrays.binarySearch(track.getSyncSamples(), currentSample + 1) >= 0) {
                timeOfSyncSamples[Arrays.binarySearch(track.getSyncSamples(), currentSample + 1)] = currentTime;
            }
            currentTime += (double) delta / (double) track.getTrackMetaData().getTimescale();
            currentSample++;

        }
        double previous = 0;
        for (double timeOfSyncSample : timeOfSyncSamples) {
            if (timeOfSyncSample > cutHere) {
                if (next) {
                    return timeOfSyncSample;
                } else {
                    return previous;
                }
            }
            previous = timeOfSyncSample;
        }
        return timeOfSyncSamples[timeOfSyncSamples.length - 1];
    }


    // make the directory on specific path
    public static void makeDirectry(String path) {
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdir();
        }
    }

    // make the directory on specific path
    public static void makeDirectryAndRefresh(Context context,String dirPath,String filePath) {
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdir();
        }
        File file=new File(dirPath,filePath);

        InputStream imageStream = null;
        try {
            imageStream = context.getContentResolver().openInputStream(Uri.fromFile(file));
        } catch (Exception e) {
            e.printStackTrace();
        }
        BitmapFactory.decodeStream(imageStream);

        MediaScannerConnection.scanFile(context,
                new String[]{dir.getAbsolutePath(),file.getAbsolutePath()}, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Log.i("ExternalStorage", "Scanned " + path + ":");
                        Log.i("ExternalStorage", "-> uri=" + uri);
                    }
                });
    }


    // return the random string of 10 char
    public static String getRandomString() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 10) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;

    }


    public static long getFileDuration(Context context, Uri uri) {
        try {

            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(context, uri);
            String durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            final int file_duration = Functions.parseInterger(durationStr);

            return file_duration;
        } catch (Exception e) {

        }
        return 0;
    }

// getCurrent Date
    public static String getCurrentDate(String dateFormat) {
        SimpleDateFormat format=new SimpleDateFormat(dateFormat,Locale.ENGLISH);
        Calendar date = Calendar.getInstance();
        return format.format(date.getTime());
    }

    // getCurrent Date
    public static String getCurrentDate(String dateFormat,int days) {
        SimpleDateFormat format=new SimpleDateFormat(dateFormat,Locale.ENGLISH);
        Calendar date = Calendar.getInstance();
        date.add(Calendar.DAY_OF_MONTH,days);
        return format.format(date.getTime());
    }

    //use to get fomated time
    public static String getTimeWithAdditionalSecond(String dateFormat,int second) {
        Calendar calendarDate = Calendar.getInstance();
        String date="00:00:00";
        SimpleDateFormat f = new SimpleDateFormat("HH:mm:ss",Locale.ENGLISH);
        Date d = null;
        try {
            d = f.parse(date);
            calendarDate.setTime(d);
        } catch (Exception e) {
            e.printStackTrace();
        }

        SimpleDateFormat format=new SimpleDateFormat(dateFormat,Locale.ENGLISH);
        calendarDate.add(Calendar.SECOND,second);
        return format.format(calendarDate.getTime());
    }

    public static String getAppFolder(Context context)
    {
        try {
            return context.getExternalFilesDir(null).getPath()+"/";
        }
        catch (Exception e)
        {
            return context.getFilesDir().getPath()+"/";
        }
    }


    public static void createAppNameVideoDirectory(Context context) {
        if (Build.VERSION.SDK_INT>Build.VERSION_CODES.P)
        {
            ContentResolver resolver = context.getContentResolver();
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM+File.separator+context.getString(R.string.app_name)+File.separator+Variables.VideoDirectory);
            String path = String.valueOf(resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues));
            File folder = new File(path);
            boolean isCreada = folder.exists();
            if(!isCreada) {
                folder.mkdirs();
            }
        }
        else
        {


            MediaScannerConnection.scanFile(context,
                    new String[]{ createDefultFolder(Environment.DIRECTORY_DCIM,context.getString(R.string.app_name)+File.separator+Variables.VideoDirectory)},
                    null,
                    new MediaScannerConnection.OnScanCompletedListener() {

                        public void onScanCompleted(String path, Uri uri) {
                        }
                    });

        }
    }

    public static String createDefultFolder(String root,String folderName) {

        File defultFile=new File(Environment.getExternalStoragePublicDirectory(root),folderName);
        if (!(defultFile.exists()))
        {
            defultFile.mkdirs();
        }
        Log.d(Constants.tag,"File Path "+defultFile.getAbsolutePath());

        Log.d(Constants.tag,"Exist Path "+defultFile.exists());
        return defultFile.getAbsolutePath();
    }


    // Bottom is all the Apis which is mostly used in app we have add it
    // just one time and whenever we need it we will call it

    public static void callApiForLikeVideo(final Activity activity,
                                           String video_id, String action,
                                           final APICallBack api_callBack) {

        JSONObject parameters = new JSONObject();
        try {
            parameters.put("user_id", Functions.getSharedPreference(activity).getString(Variables.U_ID, "0"));
            parameters.put("video_id", video_id);

        } catch (Exception e) {
            e.printStackTrace();
        }

        VolleyRequest.JsonPostRequest(activity, ApiLinks.likeVideo, parameters,Functions.getHeaders(activity), new Callback() {
            @Override
            public void onResponce(String resp) {
                Functions.checkStatus(activity,resp);

                if (api_callBack != null)
                    api_callBack.onSuccess(resp);
            }
        });


    }


    public static void callApiForFavouriteVideo(final Activity activity,
                                           String video_id, String action,
                                           final APICallBack api_callBack) {

        JSONObject parameters = new JSONObject();
        try {
            parameters.put("user_id", Functions.getSharedPreference(activity).getString(Variables.U_ID, "0"));
            parameters.put("video_id", video_id);

        } catch (Exception e) {
            e.printStackTrace();
        }

        VolleyRequest.JsonPostRequest(activity, ApiLinks.addVideoFavourite, parameters,Functions.getHeaders(activity), new Callback() {
            @Override
            public void onResponce(String resp) {
                Functions.checkStatus(activity,resp);

                if (api_callBack != null)
                    api_callBack.onSuccess(resp);
            }
        });


    }


    // this method will like the comment
    public static void callApiForLikeComment(final Activity activity,
                                             String video_id,
                                             final APICallBack api_callBack) {

        JSONObject parameters = new JSONObject();
        try {
            parameters.put("user_id", Functions.getSharedPreference(activity).getString(Variables.U_ID, "0"));
            parameters.put("comment_id", video_id);

        } catch (Exception e) {
            e.printStackTrace();
        }

        VolleyRequest.JsonPostRequest(activity, ApiLinks.likeComment, parameters, Functions.getHeaders(activity),new Callback() {
            @Override
            public void onResponce(String resp) {
                Functions.checkStatus(activity,resp);

                if (api_callBack != null)
                    api_callBack.onSuccess(resp);
            }
        });


    }

    // this method will like the reply comment
    public static void callApiForLikeCommentReply(final Activity activity,
                                                  String comment_reply_id,
                                                  String video_id,
                                                  final APICallBack api_callBack) {

        JSONObject parameters = new JSONObject();
        try {
            parameters.put("user_id", Functions.getSharedPreference(activity).getString(Variables.U_ID, "0"));
            parameters.put("comment_reply_id", comment_reply_id);
            parameters.put("video_id", video_id );

        } catch (Exception e) {
            e.printStackTrace();
        }

        VolleyRequest.JsonPostRequest(activity, ApiLinks.likeCommentReply, parameters,Functions.getHeaders(activity), new Callback() {
            @Override
            public void onResponce(String resp) {
                Functions.checkStatus(activity,resp);

                Functions.printLog(Constants.tag, "resp at like comment reply : " + resp);

                if (api_callBack != null)
                    api_callBack.onSuccess(resp);
            }
        });


    }


    public static void callApiForSendComment(final Activity activity, String videoId, String comment, ArrayList<UsersModel> taggedUserList, final APICallBack api_callBack) {

        JSONObject parameters = new JSONObject();
        try {
            parameters.put("user_id", Functions.getSharedPreference(activity).getString(Variables.U_ID, "0"));
            parameters.put("video_id", videoId);
            parameters.put("comment", comment);
            JSONArray tagUserArray=new JSONArray();
            for (UsersModel item:taggedUserList)
            {
                if(comment.contains("@"+item.username))
                {
                    JSONObject tagUser=new JSONObject();
                    tagUser.put("user_id",item.fb_id);
                    tagUserArray.put(tagUser);
                }
            }
            parameters.put("users_json", tagUserArray);

        } catch (Exception e) {
            e.printStackTrace();
        }


        VolleyRequest.JsonPostRequest(activity, ApiLinks.postCommentOnVideo, parameters,Functions.getHeaders(activity), new Callback() {
            @Override
            public void onResponce(String resp) {
                Functions.checkStatus(activity,resp);

                ArrayList<CommentModel> arrayList = new ArrayList<>();
                try {
                    JSONObject response = new JSONObject(resp);
                    String code = response.optString("code");
                    if (code.equals("200")) {

                        JSONObject msg = response.optJSONObject("msg");
                        JSONObject videoComment = msg.optJSONObject("VideoComment");
                        JSONObject videoObj = msg.optJSONObject("Video");

                        UserModel userDetailModel=DataParsing.getUserDataModel(msg.optJSONObject("User"));

                        CommentModel item = new CommentModel();

                        item.isLikedByOwner=videoComment.optString("owner_like");
                        item.videoOwnerId = videoObj.optString("user_id");
                        item.pin_comment_id = videoObj.optString("pin_comment_id");
                        item.userId = userDetailModel.getId();
                        item.isVerified=userDetailModel.getVerified();
                        item.user_name = userDetailModel.getUsername();
                        item.first_name = userDetailModel.getFirstName();
                        item.last_name = userDetailModel.getLastName();
                        item.setProfile_pic(userDetailModel.getProfilePic());

                        item.arrayList = new ArrayList<>();
                        item.arraylist_size = "0";
                        item.video_id = videoComment.optString("video_id");
                        item.comments = videoComment.optString("comment");
                        item.liked = videoComment.optString("like");
                        item.like_count = videoComment.optString("like_count");
                        item.comment_id = videoComment.optString("id");
                        item.created = videoComment.optString("created");

                        arrayList.add(item);

                        api_callBack.arrayData(arrayList);

                    } else {
                        Functions.showToast(activity, "" + response.optString("msg"));
                    }

                } catch (Exception e) {
                    api_callBack.onFail(e.toString());
                    e.printStackTrace();
                }

            }
        });


    }


    // this method will send the reply to the comment of the video
    // this method will send the reply to the comment of the video
    public static void callApiForSendCommentReply(final Activity activity, String commentId, String comment,String videoId,String videoOwnerId,ArrayList<UsersModel> taggedUserList, final APICallBack api_callBack) {

        JSONObject parameters = new JSONObject();
        try {
            parameters.put("comment_id", ""+commentId);
            parameters.put("user_id", ""+Functions.getSharedPreference(activity).getString(Variables.U_ID, "0"));
            parameters.put("comment", ""+comment);
            parameters.put("video_id",""+videoId);
            JSONArray tagUserArray=new JSONArray();
            for (UsersModel item:taggedUserList)
            {
                if(comment.contains("@"+item.username))
                {
                    JSONObject tagUser=new JSONObject();
                    tagUser.put("user_id",item.fb_id);
                    tagUserArray.put(tagUser);
                }
            }
            parameters.put("users_json", tagUserArray);

            Functions.printLog(Constants.tag, "parameters at reply : " + parameters);
        } catch (Exception e) {
            e.printStackTrace();
        }


        VolleyRequest.JsonPostRequest(activity, ApiLinks.postCommentReply, parameters,Functions.getHeaders(activity), new Callback() {
            @Override
            public void onResponce(String resp) {
                Functions.checkStatus(activity,resp);
                ArrayList<CommentModel> arrayList = new ArrayList<>();
                try {
                    JSONObject response = new JSONObject(resp);
                    String code = response.optString("code");
                    if (code.equals("200")) {

                        JSONObject msg = response.optJSONObject("msg");
                        JSONObject videoComment = msg.optJSONObject("VideoComment");
                        JSONObject videoCommentReply = msg.optJSONObject("VideoCommentReply");
                        UserModel userDetailModel=DataParsing.getUserDataModel(msg.optJSONObject("User"));

                        CommentModel item = new CommentModel();

                        item.userId = userDetailModel.getId();
                        item.isVerified=userDetailModel.getVerified();
                        item.isLikedByOwner=videoComment.optString("owner_like");
                        item.videoOwnerId =  videoOwnerId;
                        item.pin_comment_id = "0";
                        item.first_name = userDetailModel.getFirstName();
                        item.last_name = userDetailModel.getLastName();
                        item.replay_user_name = userDetailModel.getUsername();
                        item.replay_user_url = userDetailModel.getProfilePic();

                        item.video_id = videoComment.optString("video_id");
                        item.comments = videoComment.optString("comment");
                        item.created = videoComment.optString("created");


                        item.comment_reply_id = videoCommentReply.optString("id");
                        item.comment_reply = videoCommentReply.optString("comment");
                        item.parent_comment_id = videoCommentReply.optString("comment_id");
                        item.reply_create_date = videoCommentReply.optString("created");
                        item.reply_liked_count = "0";
                        item.comment_reply_liked = "0";

                        arrayList.add(item);
                        item.item_count_replies = "1";
                        api_callBack.arrayData(arrayList);

                    } else {
                        Functions.showToast(activity, "" + response.optString("msg"));
                    }

                } catch (Exception e) {
                    api_callBack.onFail(e.toString());
                    e.printStackTrace();
                }
            }
        });


    }


    public static void callApiForUpdateView(final Activity activity,
                                            String video_id,Callback callback) {

        JSONObject parameters = new JSONObject();
        try {
            parameters.put("device_id", Functions.getSharedPreference(activity).getString(Variables.DEVICE_ID, "0"));
            parameters.put("video_id", video_id);
            parameters.put("user_id", Functions.getSharedPreference(activity).getString(Variables.U_ID, ""));
        } catch (Exception e) {
            e.printStackTrace();
        }

        VolleyRequest.JsonPostRequest(activity, ApiLinks.watchVideo, parameters, Functions.getHeaders(activity),callback);


    }


    public static void callApiForFollowUnFollow
            (final Activity activity,
             String fbId,
             String followedFbId,
             final APICallBack api_callBack) {

        Functions.showLoader(activity, false, false);


        JSONObject parameters = new JSONObject();
        try {
            parameters.put("sender_id", fbId);
            parameters.put("receiver_id", followedFbId);


        } catch (Exception e) {
            e.printStackTrace();
        }

        VolleyRequest.JsonPostRequest(activity, ApiLinks.followUser, parameters,Functions.getHeaders(activity), new Callback() {
            @Override
            public void onResponce(String resp) {
                Functions.checkStatus(activity,resp);
                Functions.cancelLoader();
                try {
                    JSONObject response = new JSONObject(resp);
                    String code = response.optString("code");
                    if (code.equals("200")) {
                        api_callBack.onSuccess(response.toString());
                        JSONObject msg=response.optJSONObject("msg");
                        JSONObject receiver=msg.optJSONObject("User");
                        UserModel receiverDetailModel = DataParsing.getUserDataModel(receiver);
                        if (Variables.followMapList.containsKey(receiverDetailModel.getId()))
                        {
                            String status=receiverDetailModel.getButton();

                            if (status.equalsIgnoreCase("following")) {
                                Variables.followMapList.put(receiverDetailModel.getId(),status);
                            } else if (status.equalsIgnoreCase("friends")) {
                                Variables.followMapList.put(receiverDetailModel.getId(),status);
                            } else if (status.equalsIgnoreCase("follow back")) {
                                Variables.followMapList.remove(receiverDetailModel.getId());
                            } else {
                                Variables.followMapList.remove(receiverDetailModel.getId());
                            }
                        }
                        else
                        {
                            Variables.followMapList.put(receiverDetailModel.getId(),receiverDetailModel.getButton());
                        }

                    } else {
                        Functions.showToast(activity, "" + response.optString("msg"));
                    }

                } catch (Exception e) {
                    api_callBack.onFail(e.toString());
                    e.printStackTrace();
                }
            }
        });


    }


    public static void callApiForGetUserData
            (final Activity activity,
             String fbId,
             final APICallBack api_callBack) {

        JSONObject parameters = new JSONObject();
        try {
            parameters.put("user_id", fbId);
            if (Functions.getSharedPreference(activity).getBoolean(Variables.IS_LOGIN, false) && fbId != null) {
                parameters.put("user_id", Functions.getSharedPreference(activity).getString(Variables.U_ID, ""));
                parameters.put("other_user_id", fbId);
            } else if (fbId != null) {
                parameters.put("user_id", fbId);
            } else {
                parameters.put("username", fbId);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        Functions.printLog("resp", parameters.toString());

        VolleyRequest.JsonPostRequest(activity, ApiLinks.showUserDetail, parameters, Functions.getHeaders(activity),new Callback() {
            @Override
            public void onResponce(String resp) {
                Functions.checkStatus(activity,resp);
                Functions.cancelLoader();
                try {
                    JSONObject response = new JSONObject(resp);
                    String code = response.optString("code");
                    if (code.equals("200")) {
                        api_callBack.onSuccess(response.toString());

                    } else {
                        Functions.showToast(activity, "" + response.optString("msg"));
                    }

                } catch (Exception e) {
                    api_callBack.onFail(e.toString());
                    e.printStackTrace();
                }
            }
        });

    }


    public static void callApiForDeleteVideo
            (final Activity activity,
             String videoId,
             final APICallBack api_callBack) {

        JSONObject parameters = new JSONObject();
        try {
            parameters.put("video_id", videoId);

        } catch (Exception e) {
            e.printStackTrace();
        }

        VolleyRequest.JsonPostRequest(activity, ApiLinks.deleteVideo, parameters,Functions.getHeaders(activity), new Callback() {
            @Override
            public void onResponce(String resp) {
                Functions.checkStatus(activity,resp);
                Functions.cancelLoader();

                try {
                    JSONObject response = new JSONObject(resp);
                    String code = response.optString("code");
                    if (code.equals("200")) {
                        if (api_callBack != null)
                            api_callBack.onSuccess(response.toString());

                    } else {
                        Functions.showToast(activity, "" + response.optString("msg"));
                    }

                } catch (Exception e) {
                    if (api_callBack != null)
                        api_callBack.onFail(e.toString());
                    e.printStackTrace();
                }


            }
        });


    }


    public static HomeModel parseVideoData(JSONObject userObj, JSONObject sound, JSONObject video, JSONObject userPrivacy, JSONObject userPushNotification) {
        HomeModel item = new HomeModel();

        UserModel userDetailModel=DataParsing.getUserDataModel(userObj);
        if (!(TextUtils.isEmpty(userDetailModel.getId()))) {
            item.user_id = userDetailModel.getId();
            item.username = userDetailModel.getUsername();
            item.first_name = userDetailModel.getFirstName();
            item.last_name = userDetailModel.getLastName();
            item.setProfile_pic(userDetailModel.getProfilePic());

            item.verified = userDetailModel.getVerified();
            item.follow_status_button = userDetailModel.getButton();
        }


        try {
            if (userObj.has("story"))
            {
                ArrayList<StoryModel> storyDataList=new ArrayList<>();
                JSONArray storyArray=userObj.getJSONArray("story");
                StoryModel storyItem=new StoryModel();
                storyItem.setUserModel(userDetailModel);
                ArrayList<StoryVideoModel> storyVideoList=new ArrayList<>();
                for (int i=0; i<storyArray.length();i++)
                {
                    JSONObject itemObj=storyArray.getJSONObject(i);
                    StoryVideoModel storyVideoItem=DataParsing.getVideoDataModel(itemObj.optJSONObject("Video"));
                    storyVideoList.add(storyVideoItem);
                }
                storyItem.setVideoList(storyVideoList);
                if (storyVideoList.size()>0)
                {
                    storyDataList.add(storyItem);
                }
                item.storyDataList=storyDataList;
            }

        }
        catch (Exception e)
        {
            Log.d(Constants.tag,"Exception: story: "+e);
        }


        if (sound != null) {
            item.sound_id = sound.optString("id");
            item.sound_name = sound.optString("name");
            item.setSound_pic(sound.optString("thum"));
            item.setSound_url_mp3(sound.optString("audio"));
            item.setSound_url_acc(sound.optString("audio"));
        }

        if (video != null) {

            item.like_count = "0" + video.optInt("like_count");
            item.favourite_count = "0" + video.optInt("favourite_count");
            item.share = "0" + video.optInt("share");
            item.duration = video.optString("duration","0");
            item.video_comment_count = video.optString("comment_count");
            item.video_user_id=video.optString("user_id");

            item.privacy_type = video.optString("privacy_type");
            item.allow_comments = video.optString("allow_comments");
            item.allow_duet = video.optString("allow_duet");
            item.video_id = video.optString("id");
            item.liked = video.optString("like");
            item.favourite = video.optString("favourite");
            item.block = video.optString("block");
            item.aws_label = video.optString("aws_label");

            try {
                JSONObject playlistObject=video.getJSONObject("PlaylistVideo");

                if (playlistObject.optString("id").equals("0"))
                {
                    item.playlistId="0";
                    item.playlistName="";
                }
                else
                {
                    item.playlistId=playlistObject.getJSONObject("Playlist").optString("id","0");
                    item.playlistName=playlistObject.getJSONObject("Playlist").optString("name","");
                }
            }catch (Exception e){
                item.playlistId="0";
                item.playlistName="";
            }

            item.pin=video.optString("pin","0");

            item.repost= video.optString("repost","0");
            item.repost_video_id=video.optString("repost_video_id","0");
            item.repost_user_id=video.optString("repost_user_id","0");

            item.views = video.optString("view");

            item.video_description = video.optString("description");
            item.favourite = video.optString("favourite");
            item.created_date = video.optString("created");

            item.setThum(video.optString("thum"));
            item.setGif(video.optString("gif"));
            item.setVideo_url(video.optString("video", ""));

           try {
               if (TicTic.appLevelContext!=null)
               {
                   HttpProxyCacheServer proxy = TicTic.getProxy(TicTic.appLevelContext);
                   String proxyUrl = proxy.getProxyUrl(item.getVideo_url());
                   if (Functions.isWebUrl(proxyUrl))
                   {
                       item.setVideo_url(proxyUrl);
                   }
               }
           }
           catch (Exception e){}

            item.allow_duet = video.optString("allow_duet");
            item.duet_video_id = video.optString("duet_video_id");
            if (video.has("duet")) {
                JSONObject duet = video.optJSONObject("duet");
                if (duet != null) {
                    UserModel userDetailModelDuet=DataParsing.getUserDataModel(duet.optJSONObject("User"));
                    if (!(TextUtils.isEmpty(userDetailModelDuet.getId())))
                        item.duet_username = userDetailModelDuet.getUsername();
                }

            }
            item.promote = video.optString("promote");
            try {
                if (video.has("Promotion"))
                {
                    JSONObject Promotion = video.optJSONObject("Promotion");
                    if (Promotion != null)
                    {
                        JSONObject promotionObj=video.getJSONObject("Promotion");
                        PromotionModel promotionModel=new PromotionModel();
                        promotionModel.setId(promotionObj.optString("id"));
                        promotionModel.setUser_id(promotionObj.optString("user_id"));
                        promotionModel.setWebsite_url(promotionObj.optString("website_url"));
                        promotionModel.setStart_datetime(promotionObj.optString("start_datetime"));
                        promotionModel.setEnd_datetime(promotionObj.optString("end_datetime"));
                        promotionModel.setActive(promotionObj.optString("active"));
                        promotionModel.setCoin(promotionObj.optString("coin"));
                        promotionModel.setDestination(promotionObj.optString("destination"));
                        promotionModel.setAction_button(promotionObj.optString("action_button"));
                        promotionModel.setDestination_tap(promotionObj.optString("destination_tap"));
                        promotionModel.setFollowers(promotionObj.optString("followers"));
                        promotionModel.setReach(promotionObj.optString("reach"));
                        promotionModel.setTotal_reach(promotionObj.optString("total_reach"));
                        promotionModel.setClicks(promotionObj.optString("clicks"));
                        promotionModel.setAudience_id(promotionObj.optString("audience_id"));
                        promotionModel.setPayment_card_id(promotionObj.optString("payment_card_id"));
                        promotionModel.setVideo_id(promotionObj.optString("video_id"));
                        promotionModel.setCreated(promotionObj.optString("created"));

                        item.setPromotionModel(promotionModel);
                    }
                }
                else
                {
                    item.setPromotionModel(null);
                }

            }catch (Exception e)
            {
                item.setPromotionModel(null);
            }
        }





        if (userPrivacy != null) {
            item.apply_privacy_model = new PrivacyPolicySettingModel();
            item.apply_privacy_model.setVideo_comment(userPrivacy.optString("video_comment"));
            item.apply_privacy_model.setLiked_videos(userPrivacy.optString("liked_videos"));
            item.apply_privacy_model.setDuet(userPrivacy.optString("duet"));
            item.apply_privacy_model.setDirect_message(userPrivacy.optString("direct_message"));
            item.apply_privacy_model.setVideos_download(userPrivacy.optString("videos_download"));
        }

        if (userPushNotification != null) {
            item.apply_push_notification_model = new PushNotificationSettingModel();
            item.apply_push_notification_model.setComments(userPushNotification.optString("comments"));
            item.apply_push_notification_model.setDirectmessage(userPushNotification.optString("direct_messages"));
            item.apply_push_notification_model.setLikes(userPushNotification.optString("likes"));
            item.apply_push_notification_model.setMentions(userPushNotification.optString("mentions"));
            item.apply_push_notification_model.setNewfollowers(userPushNotification.optString("new_followers"));
            item.apply_push_notification_model.setVideoupdates(userPushNotification.optString("video_updates"));
        }

        return item;

    }

    // initialize the loader dialog and show
    public static Dialog dialog;

    public static void showLoader(Activity activity, boolean outside_touch, boolean cancleable) {
        try {
            if (dialog != null)
            {
                cancelLoader();
                dialog=null;
            }
            if (activity!=null)
            {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        dialog = new Dialog(activity);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.item_dialog_loading_view);
                        dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(activity,R.drawable.d_round_white_background));

                        if (!outside_touch)
                            dialog.setCanceledOnTouchOutside(false);

                        if (!cancleable)
                            dialog.setCancelable(false);

                        dialog.show();

                    }
                });
            }
        }
        catch (Exception e)
        {
            Log.d(Constants.tag,"Exception : "+e);
        }
    }

    public static void cancelLoader() {
        try {
            if (dialog != null || dialog.isShowing()) {
                dialog.cancel();
            }
        }catch (Exception e){
            Log.d(Constants.tag,"Exception : "+e);
        }
    }

    public static Dialog indeterminantDialog;

    public static void showIndeterminentLoader(Activity activity,String title, boolean outside_touch, boolean cancleable) {
        try {

            if (indeterminantDialog != null)
            {
                cancelIndeterminentLoader();
                indeterminantDialog=null;
            }
            if (activity!=null)
            {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        indeterminantDialog = new Dialog(activity);
                        indeterminantDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        indeterminantDialog.setContentView(R.layout.item_indeterminant_progress_layout);
                        indeterminantDialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(activity, R.drawable.d_round_white_background));
                        TextView tvTitle=indeterminantDialog.findViewById(R.id.tvTitle);
                        if (title!=null && TextUtils.isEmpty(title))
                        {
                            tvTitle.setText(title);
                        }
                        if (!outside_touch)
                        {
                            indeterminantDialog.setCanceledOnTouchOutside(false);
                        }
                        if (!cancleable)
                        {
                            indeterminantDialog.setCancelable(false);
                        }
                        indeterminantDialog.show();

                    }
                });
            }

        }
        catch (Exception e)
        {
            printLog(Constants.tag,"Exception: "+e);
        }
    }

    public static void cancelIndeterminentLoader() {
        try {
            if (indeterminantDialog != null || indeterminantDialog.isShowing()) {
                indeterminantDialog.cancel();
            }
        }catch (Exception e){
            Log.d(Constants.tag,"Exception : "+e);
        }
    }

    public static Dialog determinantDialog;
    public static ProgressBar determinantProgress;

    public static void showDeterminentLoader(Activity activity, boolean outside_touch, boolean cancleable, boolean isProgressShow,String title) {
        try
        {
            if (determinantDialog != null)
            {
                cancelDeterminentLoader();
                determinantDialog=null;
            }

            if (activity!=null)
            {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {


                        determinantDialog = new Dialog(activity);
                        determinantDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        determinantDialog.setContentView(R.layout.item_determinant_progress_layout);
                        determinantDialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(activity, R.drawable.d_round_white_background));
                        TextView tvTitle=determinantDialog.findViewById(R.id.tvTitle);
                        determinantProgress = determinantDialog.findViewById(R.id.pbar);
                        SimpleDraweeView ivLoadingProgress=determinantDialog.findViewById(R.id.ivLoadingProgress);
                        ivLoadingProgress.setController(Fresco.newDraweeControllerBuilder()
                                .setImageRequest(ImageRequestBuilder.newBuilderWithResourceId(R.raw.loading_progress).build())
                                .setOldController(ivLoadingProgress.getController())
                                .setAutoPlayAnimations(true)
                                .build());

                        if (isProgressShow)
                        {
                            ivLoadingProgress.setVisibility(View.GONE);
                            determinantProgress.setVisibility(View.VISIBLE);
                        }
                        else
                        {
                            ivLoadingProgress.setVisibility(View.VISIBLE);
                            determinantProgress.setVisibility(View.GONE);
                        }

                        if (!(title.isEmpty()))
                        {
                            tvTitle.setText(""+title);
                        }

                        if (!outside_touch)
                        {
                            determinantDialog.setCanceledOnTouchOutside(false);
                        }
                        if (!cancleable)
                        {
                            determinantDialog.setCancelable(false);
                        }
                        determinantDialog.show();

                    }
                });
            }

        }catch (Exception e)
        {
            Log.d(Constants.tag,"Exception: "+e);
        }
    }

    public static void showLoadingProgress(int progress) {
        if (determinantProgress != null) {
            determinantProgress.setProgress(progress);
        }
    }
    public static void cancelDeterminentLoader() {
        try {
            if (determinantDialog != null || determinantDialog.isShowing()) {
                determinantDialog.cancel();
            }
        }catch (Exception e){
            Log.d(Constants.tag,"Exception : "+e);
        }
    }



    //store single account record
    public static void setUpMultipleAccount(Context context) {
        MultipleAccountModel accountModel=new MultipleAccountModel();
        accountModel.setId(Functions.getSharedPreference(context).getString(Variables.U_ID, "0"));
        accountModel.setfName(Functions.getSharedPreference(context).getString(Variables.F_NAME, ""));
        accountModel.setlName(Functions.getSharedPreference(context).getString(Variables.L_NAME, ""));
        accountModel.setuName(Functions.getSharedPreference(context).getString(Variables.U_NAME, ""));
        accountModel.setuBio(Functions.getSharedPreference(context).getString(Variables.U_BIO, ""));
        accountModel.setuLink(Functions.getSharedPreference(context).getString(Variables.U_LINK, ""));
        accountModel.setPhoneNo(Functions.getSharedPreference(context).getString(Variables.U_PHONE_NO, ""));
        accountModel.setEmail(Functions.getSharedPreference(context).getString(Variables.U_EMAIL, ""));
        accountModel.setSocialId(Functions.getSharedPreference(context).getString(Variables.U_SOCIAL_ID, ""));
        accountModel.setGender(Functions.getSharedPreference(context).getString(Variables.GENDER, ""));
        accountModel.setuPic(Functions.getSharedPreference(context).getString(Variables.U_PIC, ""));
        accountModel.setuGif(Functions.getSharedPreference(context).getString(Variables.U_GIF, ""));
        accountModel.setProfileView(Functions.getSharedPreference(context).getString(Variables.U_PROFILE_VIEW, "0"));
        accountModel.setuWallet(Functions.getSharedPreference(context).getString(Variables.U_WALLET, "0"));
        accountModel.setuPayoutId(Functions.getSharedPreference(context).getString(Variables.U_PAYOUT_ID, ""));
        accountModel.setAuthToken(Functions.getSharedPreference(context).getString(Variables.AUTH_TOKEN, ""));
        accountModel.setVerified(Functions.getSharedPreference(context).getString(Variables.IS_VERIFIED, ""));
        accountModel.setApplyVerification(Functions.getSharedPreference(context).getString(Variables.IS_VERIFICATION_APPLY, ""));
        accountModel.setReferalCode(Functions.getSharedPreference(context).getString(Variables.REFERAL_CODE, ""));
        accountModel.setLogin(Functions.getSharedPreference(context).getBoolean(Variables.IS_LOGIN, false));
        Paper.book(Variables.MultiAccountKey).write(accountModel.getId(),accountModel);
    }


    //remove account signout
    public static void removeMultipleAccount(Context context) {
        Paper.book(Variables.MultiAccountKey).delete(Functions.getSharedPreference(context).getString(Variables.U_ID, "0"));
    }



    //store single account record
    public static void setUpNewSelectedAccount(Context context,MultipleAccountModel item) {

        SharedPreferences.Editor editor = getSharedPreference(context).edit();
        editor.putString(Variables.U_ID, item.getId());
        editor.putString(Variables.F_NAME, item.getfName());
        editor.putString(Variables.L_NAME, item.getlName());
        editor.putString(Variables.U_NAME, item.getuName());
        editor.putString(Variables.U_BIO, item.getuBio());
        editor.putString(Variables.U_LINK, item.getuLink());
        editor.putString(Variables.U_PHONE_NO, item.getPhoneNo());
        editor.putString(Variables.U_EMAIL, item.getEmail());
        editor.putString(Variables.U_SOCIAL_ID, item.getSocialId());
        editor.putString(Variables.GENDER, item.getGender());
        editor.putString(Variables.U_PIC, item.getuPic());
        editor.putString(Variables.U_GIF,item.getuGif());
        editor.putString(Variables.U_PROFILE_VIEW,item.getProfileView());
        editor.putString(Variables.U_WALLET, item.getuWallet());
        editor.putString(Variables.U_PAYOUT_ID, item.getuPayoutId());
        editor.putString(Variables.AUTH_TOKEN, item.getAuthToken());
        editor.putString(Variables.IS_VERIFIED, item.getVerified());
        editor.putString(Variables.IS_VERIFICATION_APPLY, item.getApplyVerification());
        editor.putString(Variables.REFERAL_CODE,item.getReferalCode());
        editor.putBoolean(Variables.IS_LOGIN, true);
        editor.commit();


        Intent intent=new Intent(context, SplashA.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    // use this method for lod muliple account in case one one account logout and other one can logout
    public static void setUpExistingAccountLogin(Context context)
    {
        if (!(Functions.getSharedPreference(context).getBoolean(Variables.IS_LOGIN, false)))
        {
            if (Paper.book(Variables.MultiAccountKey).getAllKeys().size()>0)
            {
                MultipleAccountModel account=Paper.book(Variables.MultiAccountKey).read(Paper.book(Variables.MultiAccountKey).getAllKeys().get(0));
                setUpNewSelectedAccount(context,account);
            }
        }
    }


    public static void setUpSwitchOtherAccount(Context context,String userId)
    {
        for(String key:Paper.book(Variables.MultiAccountKey).getAllKeys())
        {
            MultipleAccountModel account=Paper.book(Variables.MultiAccountKey).read(key);
            if (userId.equalsIgnoreCase(account.getId()))
            {
                setUpNewSelectedAccount(context,account);
                return;
            }

        }
    }


    //check login status
    public static boolean checkLoginUser(Activity context) {
        if (Functions.getSharedPreference(context)
                .getBoolean(Variables.IS_LOGIN, false)) {

            return true;
        } else {
            Intent intent = new Intent(context, LoginA.class);
            context.startActivity(intent);
            context.overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);
            return false;
        }
    }


    // these function are remove the cache memory which is very helpfull in memmory managmet
    public static void deleteCache(Context context) {


        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if (dir != null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

    @JvmStatic
    public static void copyFile(File sourceFile, File destFile) throws Exception {
        if (!destFile.getParentFile().exists())
            destFile.getParentFile().mkdirs();

        if (!destFile.exists()) {
            destFile.createNewFile();
        }

        FileChannel source = null;
        FileChannel destination = null;

        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        }
        finally {
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
            }
        }
    }


    public static void showToast(Context context, String msg) {
        if (Constants.IS_TOAST_ENABLE) {
            Toast.makeText(context, "" + msg, Toast.LENGTH_SHORT).show();
        }
    }

    // use for image loader and return controller for image load
    public static DraweeController frescoImageLoad(String url, SimpleDraweeView simpleDrawee, boolean isGif)
    {
        if(url==null){
            url = Constants.BASE_URL;
        }
        else if (!url.contains(Variables.http)) {
            url = Constants.BASE_URL + url;
        }

        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(url))
                .build();
        DraweeController controller;
        if (isGif)
        {
            controller = Fresco.newDraweeControllerBuilder()
                    .setImageRequest(request)
                    .setOldController(simpleDrawee.getController())
                    .setAutoPlayAnimations(true)
                    .build();
        }
        else
        {
            controller = Fresco.newDraweeControllerBuilder()
                    .setImageRequest(request)
                    .setOldController(simpleDrawee.getController())
                    .build();
        }



        return controller;
    }

    // use for image loader and return controller for image load
    public static DraweeController frescoImageLoad(Drawable drawable, SimpleDraweeView simpleDrawee, boolean isGif)
    {


        DraweeController controller;
        simpleDrawee.getHierarchy().setPlaceholderImage(drawable);
        simpleDrawee.getHierarchy().setFailureImage(drawable);
        if (isGif)
        {
            controller = Fresco.newDraweeControllerBuilder()
                    .setOldController(simpleDrawee.getController())
                    .setAutoPlayAnimations(true)
                    .build();
        }
        else
        {
            controller = Fresco.newDraweeControllerBuilder()
                    .setOldController(simpleDrawee.getController())
                    .build();
        }

        return controller;
    }

    // use for image loader and return controller for image load
    public static DraweeController frescoImageLoad(Uri resourceUri,int resource, SimpleDraweeView simpleDrawee)
    {
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(resourceUri)
                .build();

        DraweeController controller;
        simpleDrawee.getHierarchy().setPlaceholderImage(resource);
        simpleDrawee.getHierarchy().setFailureImage(resource);

        controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .setOldController(simpleDrawee.getController())
                .setAutoPlayAnimations(true)
                .build();

        return controller;
    }


    // use for image loader and return controller for image load
    public static DraweeController frescoImageLoad(Uri resourceUri, boolean isGif)
    {
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(resourceUri)
                .build();
        DraweeController controller;
        if (isGif)
        {
            controller = Fresco.newDraweeControllerBuilder()
                    .setImageRequest(request)
                    .setAutoPlayAnimations(true)
                    .build();
        }
        else
        {
            controller = Fresco.newDraweeControllerBuilder()
                    .setImageRequest(request)
                    .build();
        }



        return controller;
    }

    // use for image loader and return controller for image load
    public static DraweeController frescoGifLoad(String url,int resource,SimpleDraweeView simpleDrawee)
    {
        if (url==null)
        {
            url="null";
        }
        if (!url.contains(Variables.http)) {
            url = Constants.BASE_URL + url;
        }

        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(url))
                .build();

        DraweeController controller;
        simpleDrawee.getHierarchy().setPlaceholderImage(resource);
        simpleDrawee.getHierarchy().setFailureImage(resource);

        controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .setOldController(simpleDrawee.getController())
                .setAutoPlayAnimations(true)
                .build();
        return controller;
    }

    // use for image loader and return controller for image load
    public static DraweeController frescoImageLoad(String url,int resource, SimpleDraweeView simpleDrawee, boolean isGif)
    {
        if (url==null)
        {
            url="null";
        }
        if (!url.contains(Variables.http)) {
            url = Constants.BASE_URL + url;
        }

        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(url))
                .build();

        DraweeController controller;
        simpleDrawee.getHierarchy().setPlaceholderImage(resource);
        simpleDrawee.getHierarchy().setFailureImage(resource);

        if (isGif)
        {

            RoundingParams roundingParams = RoundingParams.asCircle().setRoundingMethod(RoundingParams.RoundingMethod.OVERLAY_COLOR).setOverlayColor(ContextCompat.getColor(simpleDrawee.getContext(),R.color.white));
            roundingParams.setRoundAsCircle(true);
            simpleDrawee.getHierarchy().setRoundingParams(roundingParams);

            controller = Fresco.newDraweeControllerBuilder()
                    .setImageRequest(request)
                    .setOldController(simpleDrawee.getController())
                    .setAutoPlayAnimations(true)
                    .build();
        }
        else
        {
            controller = Fresco.newDraweeControllerBuilder()
                    .setImageRequest(request)
                    .setOldController(simpleDrawee.getController())
                    .build();
        }



        return controller;
    }


    // use for image loader and return controller for image load
    public static DraweeController frescoBlurImageLoad(String url,Context context,int radius)
    {
        if (!url.contains(Variables.http)) {
            url = Constants.BASE_URL + url;
        }

        Postprocessor postprocessor = new BlurPostprocessor(context,radius);
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(url))
                .setPostprocessor(postprocessor)
                .build();
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .build();
        return controller;
    }


    public static DraweeController frescoImageLoad(Context context,String name,String url, SimpleDraweeView simpleDrawee)
    {
        if(url==null || url.equals("null")){
            url = Constants.BASE_URL;
        }
        else if (!url.contains(Variables.http)) {
            url = Constants.BASE_URL + url;
        }

        String placeholderName=getNameFirstLatter(name);
        TextDrawable drawable = TextDrawable.builder()
                .beginConfig()
                .textColor(ContextCompat.getColor(context, R.color.black))
                .useFont(Typeface.DEFAULT)
                .fontSize((int) context.getResources().getDimension(R.dimen._18sdp)) /* size in px */
                .bold()
                .toUpperCase()
                .endConfig()
                .buildRect(""+placeholderName,ContextCompat.getColor(context,R.color.graycolor));

        simpleDrawee.getHierarchy().setPlaceholderImage(drawable);
        simpleDrawee.getHierarchy().setFailureImage(drawable);

        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(url))
                .setResizeOptions(new ResizeOptions(Constants.ALL_IMAGE_DEFAULT_SIZE, Constants.ALL_IMAGE_DEFAULT_SIZE))
                .build();
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .setOldController(simpleDrawee.getController())
                .build();

        return controller;
    }

    public static DraweeController frescoImageLoad(Context context,String name,int fontSize,String url, SimpleDraweeView simpleDrawee)
    {
        if(url==null || url.equals("null")){
            url = Constants.BASE_URL;
        }
        else if (!url.contains(Variables.http)) {
            url = Constants.BASE_URL + url;
        }

        String placeholderName=getNameFirstLatter(name);
        TextDrawable drawable = TextDrawable.builder()
                .beginConfig()
                .textColor(ContextCompat.getColor(context, R.color.black))
                .useFont(Typeface.DEFAULT)
                .fontSize( fontSize) /* size in px */
                .bold()
                .toUpperCase()
                .endConfig()
                .buildRect(""+placeholderName,ContextCompat.getColor(context, R.color.gainsboro));

        simpleDrawee.getHierarchy().setPlaceholderImage(drawable);
        simpleDrawee.getHierarchy().setFailureImage(drawable);

        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(url))
                .setResizeOptions(new ResizeOptions(Constants.ALL_IMAGE_DEFAULT_SIZE, Constants.ALL_IMAGE_DEFAULT_SIZE))
                .build();
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .setOldController(simpleDrawee.getController())
                .build();

        return controller;
    }



    private static String getNameFirstLatter(String name) {
        try {
            if (TextUtils.isEmpty(name))
            {
                return "";
            }
            else
            {
                String[] str=name.split(" ");
                if (str.length>0)
                {
                    return str[0].charAt(0)+""+str[1].charAt(0);
                }
                else
                {
                    return str[0].charAt(0)+"";
                }
            }
        }
        catch (Exception e)
        {
            return ""+name.charAt(0);
        }
    }


    public static String getFollowButtonStatus(String button,Context context) {
        String userStatus=button;
        if (userStatus.equalsIgnoreCase("following"))
        {
            return  context.getString(R.string.following);
        }
        else
        if (userStatus.equalsIgnoreCase("friends"))
        {
            return  context.getString(R.string.friends_);
        }
        else
        if (userStatus.equalsIgnoreCase("follow back"))
        {
            return  context.getString(R.string.follow_back);
        }
        else
        {
            return  context.getString(R.string.follow);
        }
    }


    public static boolean isNotificaitonShow(String userStatus) {
        if (userStatus.equalsIgnoreCase("following"))
        {
            return true;
        }
        else
        if (userStatus.equalsIgnoreCase("friends"))
        {
            return  true;
        }
        else
        if (userStatus.equalsIgnoreCase("follow back"))
        {
            return true;
        }
        else
        {
            return  false;
        }
    }

    public static void addDeviceData(Activity context){
        JSONObject headers=new JSONObject();
        try {
            headers.put("user_id", getSharedPreference(context).getString(Variables.U_ID, null));
            headers.put("device", "android");
            headers.put("lat", getSharedPreference(context).getString(Variables.DEVICE_LAT, "0.0"));
            headers.put("long", getSharedPreference(context).getString(Variables.DEVICE_LNG, "0.0"));
            headers.put("version", BuildConfig.VERSION_NAME);
            headers.put("ip", getSharedPreference(context).getString(Variables.DEVICE_IP, null));
            headers.put("device_token", getSharedPreference(context).getString(Variables.DEVICE_TOKEN, null));
        } catch (Exception e) {
            Log.d(Constants.tag,"Exception: "+e);
        }
        VolleyRequest.JsonPostRequest(context, ApiLinks.addDeviceData, headers, Functions.getHeaders(context),new Callback() {
            @Override
            public void onResponce(String resp) {
                Functions.checkStatus(context,resp);
            }
        });
    }


    //    app language change
    public static void setLocale(String lang, Activity context, Class<?> className,boolean isRefresh) {

        String[] languageArray=context.getResources().getStringArray(R.array.app_language_code);
        List<String> languageCode = Arrays.asList(languageArray);
        if (languageCode.contains(lang)) {
            Locale myLocale = new Locale(lang);
            Resources res = context.getBaseContext().getResources();
            DisplayMetrics dm = res.getDisplayMetrics();
            Configuration conf = new Configuration();
            conf.setLocale(myLocale);
            res.updateConfiguration(conf, dm);
            context.onConfigurationChanged(conf);

            if (isRefresh)
            {
                updateActivity(context,className);
            }
        }

        if (new DarkModePrefManager(context).isNightMode()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

    }
    public static void updateActivity(Activity context, Class<?> className) {
        Intent intent = new Intent(context,className);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);

    }


    // manage for store user data
    public static void storeUserLoginDataIntoDb(Context context,UserModel userDetailModel) {
        SharedPreferences.Editor editor = Functions.getSharedPreference(context).edit();
        editor.putString(Variables.U_ID, userDetailModel.getId());
        editor.putString(Variables.F_NAME, userDetailModel.getFirstName());
        editor.putString(Variables.L_NAME, userDetailModel.getLastName());
        editor.putString(Variables.U_NAME, userDetailModel.getUsername());
        editor.putString(Variables.U_BIO, userDetailModel.getBio());
        editor.putString(Variables.U_LINK, userDetailModel.getWebsite());
        editor.putString(Variables.U_PHONE_NO, userDetailModel.getPhone());
        editor.putString(Variables.U_EMAIL, userDetailModel.getEmail());
        editor.putString(Variables.U_SOCIAL_ID, userDetailModel.getSocial_id());
        editor.putString(Variables.GENDER, userDetailModel.getGender());
        editor.putString(Variables.U_PIC, userDetailModel.getProfilePic());
        editor.putString(Variables.U_GIF, userDetailModel.getProfileGif());
        editor.putString(Variables.U_PROFILE_VIEW, userDetailModel.getProfileView());
        editor.putString(Variables.U_WALLET, ""+userDetailModel.getWallet());
        editor.putString(Variables.U_PAYOUT_ID, userDetailModel.getPaypal());
        editor.putString(Variables.AUTH_TOKEN, userDetailModel.getAuthToken());
        editor.putString(Variables.IS_VERIFIED, userDetailModel.getVerified());
        editor.putString(Variables.IS_VERIFICATION_APPLY, userDetailModel.getApplyVerification());
        editor.putString(Variables.REFERAL_CODE,userDetailModel.getReferalCode());
        editor.putBoolean(Variables.IS_LOGIN, true);
        editor.commit();
    }


    //use to get Directory Storage Used Capacity
    public static String getDirectorySize(String path) {

        File dir = new File(path);

        if(dir.exists()) {
            long bytes = getFolderSize(dir);
            if (bytes < 1024) return bytes + " B";
            int exp = (int) (Math.log(bytes) / Math.log(1024));
            String pre = ("KMGTPE").charAt(exp-1) + "";

            return String.format("%.1f %sB", bytes / Math.pow(1024, exp), pre);
        }

        return "0";
    }

    private static long getFolderSize(File dir) {
        if (dir.exists()) {
            long result = 0;
            File[] fileList = dir.listFiles();
            for(int i = 0; i < fileList.length; i++) {
                // Recursive call if it's a directory
                if(fileList[i].isDirectory()) {
                    result += getFolderSize(fileList[i]);
                } else {
                    // Sum the file size in bytes
                    result += fileList[i].length();
                }
            }
            return result; // return the file size
        }
        return 0;
    }



    public static BroadcastReceiver broadcastReceiver;
    public static IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");

    public static void unRegisterConnectivity(Context mContext) {
        try {
            if (broadcastReceiver != null)
                mContext.unregisterReceiver(broadcastReceiver);

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    public static void RegisterConnectivity(Context context, final InternetCheckCallback callback) {

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (isConnectedToInternet(context)) {
                    callback.GetResponse("alert", "connected");
                } else {
                    callback.GetResponse("alert", "disconnected");
                }
            }
        };

        context.registerReceiver(broadcastReceiver, intentFilter);
    }

    public static Boolean isConnectedToInternet(Context context) {
        try {

            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            Log.e(Constants.tag, "Exception : "+e.getMessage());
            return false;
        }
    }

    //check rational permission status
    public static String getPermissionStatus(Activity activity, String androidPermissionName) {
        if(ContextCompat.checkSelfPermission(activity, androidPermissionName) != PackageManager.PERMISSION_GRANTED) {
            if(!ActivityCompat.shouldShowRequestPermissionRationale(activity, androidPermissionName)){
                return "blocked";
            }
            return "denied";
        }
        return "granted";
    }

    //show permission setting screen
    public static void showPermissionSetting(Context context,String message) {
        showDoubleButtonAlert(context, context.getString(R.string.permission_alert),message,
                context.getString(R.string.cancel_), context.getString(R.string.settings), false, new FragmentCallBack() {
                    @Override
                    public void onResponce(Bundle bundle) {
                        if (bundle.getBoolean("isShow",false))
                        {
                            Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package",context.getPackageName(), null);
                            intent.setData(uri);
                            context.startActivity(intent);
                        }
                    }
                });
    }

//    check app is exist or not
    public static boolean appInstalledOrNot(Context context,String uri) {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
        }

        return false;
    }

    public static File getBitmapToUri(Context context, Bitmap bitmap,String fileName) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        File file = new File(Functions.getAppFolder(context)+Variables.APP_HIDED_FOLDER + fileName);
        try {
            FileOutputStream fo = new FileOutputStream(file);
            fo.write(bytes.toByteArray());
            fo.flush();
            fo.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (file.exists())
        {
            return file;
        }
        else
        {
            return null;
        }
    }

    // logout to app automatically when the login token expire
    public static void checkStatus(Activity activity, String responce) {
        try {
        JSONObject response=new JSONObject(responce);
        if (response.optString("code", "").equalsIgnoreCase("501")) {

            GoogleSignInOptions gso = new GoogleSignInOptions.
                    Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).
                    build();
            GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(activity, gso);
            googleSignInClient.signOut();

            LoginManager.getInstance().logOut();

            removeMultipleAccount(activity);

            SharedPreferences.Editor editor = getSharedPreference(activity).edit();
            Paper.book(Variables.PrivacySetting).destroy();
            editor.clear();
            editor.commit();
            activity.finish();

            setUpExistingAccountLogin(activity);
            activity.startActivity(new Intent(activity, MainMenuActivity.class));

        }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static HashMap<String, String> getHeaders(Context context){
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Api-Key", Constants.API_KEY);
//        headers.put("User-Id", getSharedPreference(context).getString(Variables.U_ID, null));
//        headers.put("Auth-Token", getSharedPreference(context).getString(Variables.AUTH_TOKEN, null));
//        headers.put("device", "android");
//        headers.put("version", BuildConfig.VERSION_NAME);
//        headers.put("ip", getSharedPreference(context).getString(Variables.DEVICE_IP, null));
//        headers.put("device-token", getSharedPreference(context).getString(Variables.DEVICE_TOKEN, null));
        return headers;
    }

    public static HashMap<String, String> getHeadersWithOutLogin(Context context){
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Api-Key", Constants.API_KEY);
//        headers.put("User-Id", "");
//        headers.put("Auth-Token", "");
//        headers.put("device", "android");
//        headers.put("version", BuildConfig.VERSION_NAME);
//        headers.put("ip", getSharedPreference(context).getString(Variables.DEVICE_IP, null));
//        headers.put("device-token", getSharedPreference(context).getString(Variables.DEVICE_TOKEN, null));
        return headers;
    }


    public static void createNoMediaFile(Context context) {

        InputStream in = null;
        OutputStream out = null;

        try {

            //create output directory if it doesn't exist
            String path=getAppFolder(context)+"videoCache";
            File dir = new File(path);
            File newFile = new File(dir, ".nomedia");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            if (!newFile.exists()) {
                newFile.createNewFile();

                MediaScannerConnection.scanFile(context,
                        new String[]{path}, null,
                        new MediaScannerConnection.OnScanCompletedListener() {
                            public void onScanCompleted(String path, Uri uri) {
                                Log.i("ExternalStorage", "Scanned " + path + ":");
                                Log.i("ExternalStorage", "-> uri=" + uri);
                            }
                        });
            }

        } catch (Exception e) {
            Log.e(Constants.tag, ""+e);
        }

    }


    public static void refreshFile(Context context,String path) {

        try {

            MediaScannerConnection.scanFile(context,
                    new String[]{path}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                            Log.i("ExternalStorage", "Scanned " + path + ":");
                            Log.i("ExternalStorage", "-> uri=" + uri);
                        }
                    });

        } catch (Exception e) {
            Log.e(Constants.tag, ""+e);
        }

    }


    public static String getTimeAgoOrg(String date_time) {
        TimeAgo2 timeAgo2 = new TimeAgo2();
        String MyFinalValue = timeAgo2.covertTimeToText(date_time);
        return MyFinalValue;
    }


    public static void showValidationMsg(Activity activity,View containerView,String message)
    {

        View layout = activity.getLayoutInflater().inflate(R.layout.validation_message_view, null);
        TextView tvMessage = layout.findViewById(R.id.tvMessage);
        tvMessage.setText(message);

        Snackbar snackbar= Snackbar.make(containerView, "", Snackbar.LENGTH_LONG);

        Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) snackbar.getView();
        TextView textView = (TextView) snackbarLayout.findViewById(R.id.snackbar_text);
        textView.setVisibility(View.INVISIBLE);

        final ViewGroup.LayoutParams params = snackbar.getView().getLayoutParams();
        if (params instanceof CoordinatorLayout.LayoutParams) {
            ((CoordinatorLayout.LayoutParams) params).gravity = Gravity.TOP;
        } else {
            ((FrameLayout.LayoutParams) params).gravity = Gravity.TOP;
        }

        snackbarLayout.setPadding(0, 0, 0, 0);
        snackbarLayout.addView(layout, 0);


        snackbar.getView().setVisibility(View.INVISIBLE);

        snackbar.addCallback(new Snackbar.Callback() {
            @Override
            public void onShown(Snackbar sb) {
                super.onShown(sb);
                snackbar.getView().setVisibility(View.VISIBLE);
            }

        });

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                snackbar.getView().setVisibility(View.INVISIBLE);
            }
        }, 1000);


        snackbar.setDuration(Snackbar.LENGTH_LONG);
        snackbar.show();


        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
                snackbar.getView().setVisibility(View.INVISIBLE);
            }
        });

    }

    @JvmStatic
    public static void clearFilesCacheBeforeOperation(File...files) {
        if (files.length>0)
        {
            for (File file:files)
            {
                if (file.exists())
                {
                    file.delete();
                }
            }
        }
    }


    public static String getDurationInDays(String format, String start, String end) {
        try {

            Calendar startDateCal = Calendar.getInstance();
            Calendar endDateCal = Calendar.getInstance();


            SimpleDateFormat f = new SimpleDateFormat(format,Locale.ENGLISH);

            Date startDate = null;
            try {
                startDate = f.parse(start);
                startDateCal.setTime(startDate);
            } catch (Exception e) {
                Log.d(Constants.tag,"Exception startDate: "+e);
            }

            Date endDate = null;
            try {
                endDate = f.parse(end);
                endDateCal.setTime(endDate);
            } catch (Exception e) {
                Log.d(Constants.tag,"Exception endDate: "+e);
            }

            long difference = (endDateCal.getTimeInMillis() - startDateCal.getTimeInMillis()) / 1000;

            long days=difference/86400;

            return ""+days;

        }
        catch (Exception e) {
            Log.d(Constants.tag,"Exception days: "+e);
            return "0";
        }

    }


    public static String getDurationInPoints(String format, String start, String end) {
        try {

            Calendar startDateCal = Calendar.getInstance();
            Calendar endDateCal = Calendar.getInstance();


            SimpleDateFormat f = new SimpleDateFormat(format,Locale.ENGLISH);

            Date startDate = null;
            try {
                startDate = f.parse(start);
                startDateCal.setTime(startDate);
            } catch (Exception e) {
                Log.d(Constants.tag,"Exception startDate: "+e);
            }

            Date endDate = null;
            try {
                endDate = f.parse(end);
                endDateCal.setTime(endDate);
            } catch (Exception e) {
                Log.d(Constants.tag,"Exception endDate: "+e);
            }

            long difference = (endDateCal.getTimeInMillis() - startDateCal.getTimeInMillis()) / 1000;

            double days=((double)difference)/86400;

            return ""+days;

        }
        catch (Exception e) {
            Log.d(Constants.tag,"Exception days: "+e);
            return "0";
        }

    }




    public static String changeDateLatterFormat(String format,Context context, String date) {
        try {
            Calendar current_cal = Calendar.getInstance();

            Calendar date_cal = Calendar.getInstance();

            SimpleDateFormat f = new SimpleDateFormat(format,Locale.ENGLISH);
            Date d = null;
            try {
                d = f.parse(date);
                date_cal.setTime(d);
            } catch (Exception e) {
                e.printStackTrace();
            }


            long difference = (current_cal.getTimeInMillis() - date_cal.getTimeInMillis()) / 1000;

            if (difference < 60) {
                return difference + " "+context.getString(R.string.s_ago);
            }  else
            if (difference < 3600) {
                return (0+(difference / 60)) + " "+context.getString(R.string.m_ago);
            }  else
            if (difference < 86400) {
                return (0+(difference / 3600)) + " "+context.getString(R.string.h_ago);
            }  else
            if (difference<604800)
            {
                return (0+(difference / 86400)) + " "+context.getString(R.string.d_ago);
            }
            else
            {
                if (difference<2592000)
                {
                    return (0+(difference / 604800)) + " "+context.getString(R.string.week_ago);
                }
                else
                {
                    if (difference<31536000)
                    {
                        return (0+(difference / 2592000)) + " "+context.getString(R.string.month_ago);
                    }
                    else
                    {
                        return (0+(difference / 31536000)) + " "+context.getString(R.string.year_ago);
                    }

                }

            }

        }
        catch (Exception e) {
            return date;
        }


    }

    public static void UrlToBitmapGenrator(String imgUrl, GenrateBitmapCallback callback){

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {

                //Background work here
                InputStream in;
                try {

                    URL url = new URL(imgUrl);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    in = connection.getInputStream();
                    Bitmap myBitmap = BitmapFactory.decodeStream(in);
                    callback.onResult(myBitmap);
                    executor.shutdownNow();

                } catch (Exception e) {
                    Log.d(Constants.tag,"Exception: "+e);
                    executor.shutdownNow();
                }

            }
        });

    }


    public static void UrlToFileGenrator(String imgUrl,File directory,String id, GenrateFileCallback callback){

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {

                //Background work here
                try {
                    if (!(directory.exists()))
                    {
                        directory.mkdirs();
                    }
                    File downloadUrl=new File((directory+ "/"+id+".png"));
                    if (downloadUrl.exists())
                    {
                        callback.onResult(downloadUrl);
                        executor.shutdownNow();
                    }
                    URL url = new URL(imgUrl);
                    InputStream input = url.openStream();
                    try {
                        OutputStream output = new FileOutputStream (directory + "/"+id+".png");
                        try {
                            byte[] buffer = new byte[1024];
                            int bytesRead = 0;
                            while ((bytesRead = input.read(buffer, 0, buffer.length)) >= 0) {
                                output.write(buffer, 0, bytesRead);
                            }
                        } finally {
                            output.close();
                        }
                    } finally {
                        input.close();
                        callback.onResult(downloadUrl);
                        executor.shutdownNow();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    executor.shutdownNow();
                }

            }
        });

    }

    //use to get fomated time
    public static double getTimeInMilli(String dateFormat,String date) {
        Calendar calendarDate = Calendar.getInstance();
        SimpleDateFormat f = new SimpleDateFormat(dateFormat,Locale.ENGLISH);
        Date d = null;
        try {
            d = f.parse(date);
            calendarDate.setTime(d);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return calendarDate.getTime().getTime();
    }

    public static int CalculateFFMPEGTimeToPercentage(String message,int allowRecordingDuration) {
        double preViousPercent =0f;
        try {

            String array[]=message.split(" ");
            String startTime=Functions.getTimeWithAdditionalSecond("HH:mm:ss", (int) 0);
            String endTime=Functions.getTimeWithAdditionalSecond("HH:mm:ss", (int) allowRecordingDuration);
            String currentTime=array[4];

            double start = Functions.getTimeInMilli("HH:mm:ss",startTime);
            double end = Functions.getTimeInMilli("HH:mm:ss",endTime);
            double cur = Functions.getTimeInMilli("HH:mm:ss",currentTime);

            double percent = ((cur - start) / (end - start)) * 100f;
            preViousPercent=percent;
            return (int) percent;
        }
        catch (Exception e)
        {
            return (int) preViousPercent;
        }
    }

    @JvmStatic
    public static String decodeFFMPEGMessage(String message) {
        try {

            message=message.replaceAll("  "," ");
            message=message.replaceAll("  "," ");
            message=message.replaceAll("  "," ");
            message=message.replaceAll("fps= ","");
            message=message.replaceAll("fps=","");
            message=message.replaceAll("frame= ","");
            message=message.replaceAll("frame=","");
            message=message.replaceAll("size= ","");
            message=message.replaceAll("size=","");
            message=message.replaceAll("q= ","");
            message=message.replaceAll("q=","");
            message=message.replaceAll("time= ","");
            message=message.replaceAll("time=","");

            return message;

        }
        catch (Exception e)
        {
            Log.d(Constants.tag,"Exception: "+e);
        }
        return "";
    }


    public static String getTrimVideoFrameRate(String videoPath) {
        MediaExtractor extractor = new MediaExtractor();
        int frameRate = 24; //may be default
        try {
            //Adjust data source as per the requirement if file, URI, etc.
            extractor.setDataSource(videoPath);
            int numTracks = extractor.getTrackCount();
            for (int i = 0; i < numTracks; ++i) {
                MediaFormat format = extractor.getTrackFormat(i);
                String mime = format.getString(MediaFormat.KEY_MIME);
                if (mime.startsWith("video/")) {
                    if (format.containsKey(MediaFormat.KEY_FRAME_RATE)) {
                        frameRate = format.getInteger(MediaFormat.KEY_FRAME_RATE);
                    }
                }
            }
        } catch (Exception e) {
            Log.d(Constants.tag,"Exception: "+e);
        }finally {
            extractor.release();
            return ""+frameRate;
        }
    }


    public static int showVideoDurationInSec(String videoPath) {
       try {
           MediaMetadataRetriever retriever=new MediaMetadataRetriever();
           retriever.setDataSource(videoPath);
           Bitmap bit = retriever.getFrameAtTime();
           int width = bit.getWidth();
           int height = bit.getHeight();
           String duration=retriever.extractMetadata(METADATA_KEY_DURATION);
           int second= Integer.valueOf(duration)/1000;
           return second;
       }catch (Exception e)
       {
           Log.d(Constants.tag,"Exception: "+e);
           return 10;
       }
    }

    public static long getDevidedChunks(int maxProgressTime, int chunkSize) {
        return (maxProgressTime*1000)/chunkSize;
    }


    public static String convertEmoji(String emoji) {
        String result;
        try {
            int convertEmojiToInt = Integer.parseInt(emoji.substring(2), 16);
            char[] var8 = Character.toChars(convertEmojiToInt);
            result = new String(var8);
        } catch (Exception var5) {
            result = "";
        }
        return result;
    }

    //check activity stackclear or not
    public static boolean isAnyActivityRemain(Context context)
    {
        ActivityManager mngr = (ActivityManager) context.getSystemService( Context.ACTIVITY_SERVICE );

        List<ActivityManager.RunningTaskInfo> taskList = mngr.getRunningTasks(10);

        if(taskList.get(0).numActivities == 1 &&
                taskList.get(0).topActivity.getClassName().equals(context.getClass().getName())) {
            return false;
        }
        else
        {
            return true;
        }
    }


    public static void showToastOnTop(Activity activity,View mainView,String message)
    {
        LayoutInflater inflater = activity.getLayoutInflater();
        View layout;
        if (mainView==null)
        {
            layout = inflater.inflate(R.layout.custom_toast, null);
        }
        else
        {
            layout = inflater.inflate(R.layout.custom_toast, mainView.findViewById(R.id.custom_toast_container));
        }

        TextView tvMessage = layout.findViewById(R.id.tvMessage);
        tvMessage.setText(message);

        Toast toast = new Toast(activity);
        toast.setGravity(Gravity.TOP, 0, 40);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }

    //custom snackbar top
    public static void showSnakbarOnTop(Activity activity,View mainView,String message) {
        View layout = activity.getLayoutInflater().inflate(R.layout.custom_message_top_layout, null);
        TextView tvMessage = layout.findViewById(R.id.tvMessage);
        tvMessage.setText(message);
        Snackbar snackbar = Snackbar.make(mainView, "", Snackbar.LENGTH_LONG);

        Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) snackbar.getView();
        TextView textView = (TextView) snackbarLayout.findViewById(R.id.snackbar_text);
        textView.setVisibility(View.INVISIBLE);

        final ViewGroup.LayoutParams params = snackbar.getView().getLayoutParams();
        if (params instanceof CoordinatorLayout.LayoutParams) {
            ((CoordinatorLayout.LayoutParams) params).gravity = Gravity.TOP;
        } else {
            ((FrameLayout.LayoutParams) params).gravity = Gravity.TOP;
        }

        snackbarLayout.setPadding(0, 0, 0, 0);
        snackbarLayout.addView(layout, 0);
        snackbar.getView().setVisibility(View.INVISIBLE);
        snackbar.addCallback(new Snackbar.Callback() {
            @Override
            public void onShown(Snackbar sb) {
                super.onShown(sb);
                snackbar.getView().setVisibility(View.VISIBLE);
            }

        });
        Handler handler = new Handler(Looper.getMainLooper());
        Runnable runnable;
        runnable = new Runnable() {
            @Override
            public void run() {
                snackbar.getView().setVisibility(View.INVISIBLE);
            }
        };
        handler.postDelayed(runnable, 2750);
        snackbar.setDuration(Snackbar.LENGTH_LONG);
        snackbar.show();
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
                snackbar.getView().setVisibility(View.INVISIBLE);
            }
        });
    }

    public static int getPercentage(int currentValue,int totalValue){
        return ((currentValue*100)/totalValue);
    }

    public static List<List<KeyMatricsModel>> createChunksOfListKeyMatrics(List<KeyMatricsModel> originalList,
                                                                           int chunkSize) {
        List<List<KeyMatricsModel>> listOfChunks = new ArrayList<List<KeyMatricsModel>>();
        for (int i = 0; i < originalList.size() / chunkSize; i++) {
            listOfChunks.add(originalList.subList(i * chunkSize, i * chunkSize
                    + chunkSize));
        }
        if (originalList.size() % chunkSize != 0) {
            listOfChunks.add((List<KeyMatricsModel>) originalList.subList(originalList.size()
                    - originalList.size() % chunkSize, originalList.size()));
        }
        return listOfChunks;
    }


    public static String getUserName(UserModel userModel) {
        if (TextUtils.isEmpty(userModel.getFirstName()) && TextUtils.isEmpty(userModel.getLastName()))
        {
            return userModel.getUsername();
        }
        else
        {
            if (TextUtils.isEmpty(userModel.getLastName()))
            {
                return userModel.getFirstName();
            }
            else
            {
                return userModel.getFirstName()+" "+userModel.getLastName();
            }

        }
    }


    public static String getButtonStatus(String status) {
        status=status.toLowerCase();

        if (status.equalsIgnoreCase("following"))
        {
            return "Following";
        }
        else
        if (status.equalsIgnoreCase("friends"))
        {
            return "Following";
        }
        else
        if (status.equalsIgnoreCase("follow back"))
        {
            return "Follow";
        }
        else
        {
            return "Follow";
        }
    }


    public static void showError(Activity activity,String msg)
    {
        CookieBar.build(activity)
                .setCustomView(R.layout.custom_error)
                .setCustomViewInitializer(new CookieBar.CustomViewInitializer() {
                    @Override
                    public void initView(View view) {
                        TextView tvTitle=view.findViewById(R.id.tvTitle);
                        tvTitle.setText(""+msg);
                    }
                })
                .setEnableAutoDismiss(false)
                .setSwipeToDismiss(false)
                .setDuration(4000)
                .show();
    }

    public static void showSuccess(Activity activity,String msg)
    {
        CookieBar.build(activity)
                .setCustomView(R.layout.custom_success)
                .setCustomViewInitializer(new CookieBar.CustomViewInitializer() {
                    @Override
                    public void initView(View view) {
                        TextView tvTitle=view.findViewById(R.id.tvTitle);
                        tvTitle.setText(""+msg);
                    }
                })
                .setDuration(4000)
                .setEnableAutoDismiss(false)
                .setSwipeToDismiss(false)
                .setCookiePosition(Gravity.TOP)
                .show();

    }

    public static String getShareRoomLink(Context context,String userID){
        return Variables.https+"://"+context.getString(R.string.share_profile_domain)+context.getString(R.string.share_room_endpoint_second) +userID;

    }


    public static void shareData(Activity activity,String data){
        try {
            Intent sendIntent = new Intent("android.intent.action.MAIN");
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.setType("text/plain");
            sendIntent.putExtra(Intent.EXTRA_TEXT, data);
            activity.startActivity(sendIntent);
        } catch(Exception e) {
            Log.d(Constants.tag,"Exception : "+e);
        }
    }


    public static String ChangeDateFormat(String fromFormat, String toFormat, String date){

        SimpleDateFormat dateFormat = new SimpleDateFormat(fromFormat, Locale.ENGLISH);
        Date sourceDate = null;

        try {
            sourceDate = dateFormat.parse(date);

            SimpleDateFormat targetFormat = new SimpleDateFormat(toFormat,Locale.ENGLISH);

            return  targetFormat.format(sourceDate);

        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }

    }

    public static int convertDpToPx(Context context, int dp) {
        return (int) ((int) dp * context.getResources().getDisplayMetrics().density);
    }

    public static String ParseDouble(double doubleValue)
    {
        int notation=countDigitsAfterDecimal(doubleValue);
        return String.format("%."+notation+"f", doubleValue);
    }

    private static int countDigitsAfterDecimal(double value) {
        String stringValue = String.valueOf(value);

        int decimalIndex = stringValue.indexOf('.');
        if (decimalIndex == -1) {
            // No decimal point found, return 0
            return 0;
        }

        int digitCount = stringValue.length() - decimalIndex - 1;

        return digitCount;
    }

    public static ImageHeightWidthModel getDropboxIMGSize(Uri uri){
        ImageHeightWidthModel model=new ImageHeightWidthModel();

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(new File(uri.getPath()).getAbsolutePath(), options);
        model.setImageWidth(options.outWidth);
        model.setImageHeight(options.outHeight);
        return model;
    }


    public static Bitmap convertImage(String imagePath)
    {
        // Load the original image from a file or any source
        Bitmap originalImage = BitmapFactory.decodeFile(imagePath);

        // Get the dimensions of the original image
        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();

        // Set the default dimensions
        int defaultWidth = 512;
        int defaultHeight = 1024;

        // Calculate the aspect ratio of the original image
        float aspectRatio = (float) originalWidth / originalHeight;

        // Check if the original image exceeds the default dimensions
        if (originalWidth > defaultWidth || originalHeight > defaultHeight) {
            // Determine the new dimensions to maintain the aspect ratio
            int newWidth = defaultWidth;
            int newHeight = (int) (newWidth / aspectRatio);

            // Check if the new height exceeds the default height
            if (newHeight > defaultHeight) {
                newHeight = defaultHeight;
                newWidth = (int) (newHeight * aspectRatio);
            }

            // Resize the original image to the new dimensions
            originalImage = Bitmap.createScaledBitmap(originalImage, newWidth, newHeight, true);
        }

        // Create a new Bitmap with the default dimensions
        Bitmap convertedImage = Bitmap.createBitmap(defaultWidth, defaultHeight, Bitmap.Config.ARGB_8888);

        // Create a Canvas object to draw on the new Bitmap
        Canvas canvas = new Canvas(convertedImage);
        canvas.drawColor(Color.BLACK); // Set the extra space to black color

        // Calculate the center position to draw the original image
        int centerX = (defaultWidth - originalImage.getWidth()) / 2;
        int centerY = (defaultHeight - originalImage.getHeight()) / 2;

        // Draw the resized original image onto the converted image
        canvas.drawBitmap(originalImage, centerX, centerY, new Paint());

        return convertedImage;
    }
}
