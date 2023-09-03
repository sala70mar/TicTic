package com.qboxus.tictic.activitesfragments.profile;

import com.qboxus.tictic.databinding.ActivitySeeFullImageBinding;
import com.qboxus.tictic.simpleclasses.AppCompatLocaleActivity;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;

import com.qboxus.tictic.R;
import com.qboxus.tictic.simpleclasses.Functions;
import com.qboxus.tictic.simpleclasses.Variables;

public class SeeFullImageA extends AppCompatLocaleActivity {

    ActivitySeeFullImageBinding binding;
    String imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Functions.setLocale(Functions.getSharedPreference(this).getString(Variables.APP_LANGUAGE_CODE,Variables.DEFAULT_LANGUAGE_CODE)
                , this, getClass(),false);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_see_full_image);


        imageUrl = getIntent().getStringExtra("image_url");

        binding.ivClose.setOnClickListener(v -> {
          onBackPressed();
        });

        binding.ivProfile.setController(Functions.frescoImageLoad(imageUrl,binding.ivProfile,getIntent().getBooleanExtra("isGif",false)));

    }
}