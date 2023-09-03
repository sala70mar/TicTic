package com.qboxus.tictic.activitesfragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.qboxus.tictic.Constants;
import com.qboxus.tictic.databinding.FragmentEditTextSheetBinding;
import com.qboxus.tictic.interfaces.FragmentCallBack;
import com.qboxus.tictic.interfaces.KeyboardHeightObserver;
import com.qboxus.tictic.models.UsersModel;
import com.qboxus.tictic.R;
import com.qboxus.tictic.simpleclasses.Functions;
import com.qboxus.tictic.simpleclasses.KeyboardHeightProvider;
import com.qboxus.tictic.simpleclasses.Variables;

import java.util.ArrayList;


public class EditTextSheetF extends BottomSheetDialogFragment implements View.OnClickListener {


    FragmentEditTextSheetBinding binding;
    FragmentCallBack callBack;
    String commentType;
    private BottomSheetBehavior mBehavior;
    BottomSheetDialog dialog;
    ArrayList<UsersModel> tagedUser = new ArrayList<>();


    public EditTextSheetF(String commentType,ArrayList<UsersModel> tagedUser, FragmentCallBack callBack) {
        this.callBack=callBack;
        this.tagedUser=tagedUser;
        this.commentType=commentType;
    }

    public EditTextSheetF() {
    }

    @NonNull
    @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
        dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        View view = View.inflate(getContext(), R.layout.fragment_edit_text_sheet, null);
        dialog.setContentView(view);
        mBehavior = BottomSheetBehavior.from((View) view.getParent());
        mBehavior.setHideable(true);
        mBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState!=BottomSheetBehavior.STATE_EXPANDED)
                {
                    mBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
        return  dialog;
    }

    @Override
    public void onDetach() {
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        hideKeyboard();
        super.onDetach();
    }

    private void hideKeyboard() {
        binding.messageEdit.clearFocus();
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(binding.messageEdit.getWindowToken(), 0);
    }


    @Override
    public void onStart() {
        super.onStart();
        mBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        mBehavior.setPeekHeight((int) binding.getRoot().getContext().getResources().getDimension(R.dimen._50sdp),true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        binding= DataBindingUtil.inflate(inflater,R.layout.fragment_edit_text_sheet, container, false);
        initControl();
        actionControl();
        return binding.getRoot();
    }

    private void actionControl() {
        binding.messageEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int count) {
                String message=binding.messageEdit.getText().toString();
                if (message.length() > 0) {
                    String lastChar = charSequence.toString().substring(charSequence.length() - 1);
                    if (lastChar.equals("@")) {
                        binding.tabTagFriends.setVisibility(View.GONE);
                        openFriends();
                    }
                    else
                    {
                        binding.tabTagFriends.setVisibility(View.VISIBLE);
                    }
                }
            }


            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.tabTagFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message=binding.messageEdit.getText().toString();
                message=message+"@";
                binding.messageEdit.setText(""+message);
                binding.messageEdit.setSelection(binding.messageEdit.getText().length());
            }
        });
    }


    private boolean checkUserExistInList(String tagUser) {
        boolean isExist=false;
        for (UsersModel user:tagedUser)
        {
            if (user.username.equals(""+tagUser))
            {
                isExist=true;
            }
        }
        return isExist;
    }

    private void removeUserExistInList(String tagUser) {
        for (UsersModel user:tagedUser)
        {
            if (user.username.equals(""+tagUser))
            {
                tagedUser.remove(tagedUser.indexOf(user));
            }
        }
    }

    private void openFriends() {
        CommentTagedFriendsF fragment=new CommentTagedFriendsF(Functions.getSharedPreference(binding.getRoot().getContext()).getString(Variables.U_ID, ""),
                new FragmentCallBack() {
                    @Override
                    public void onResponce(Bundle bundle) {
                        if (bundle.getBoolean("isShow",false))
                        {
                            ArrayList<UsersModel> arrayList = (ArrayList<UsersModel>) bundle.getSerializable("data");

                            for (int i=0;i<arrayList.size();i++) {
                                UsersModel item=arrayList.get(i);

                                tagedUser.add(item);
                                String lastChar = null;
                                if (!TextUtils.isEmpty(binding.messageEdit.getText().toString()))
                                    lastChar = binding.messageEdit.getText().toString().substring(binding.messageEdit.getText().length() - 1);

                                if (lastChar != null && lastChar.contains("@"))
                                    binding.messageEdit.setText(binding.messageEdit.getText().toString() + item.username + " ");
                                else
                                    binding.messageEdit.setText(binding.messageEdit.getText().toString() + "@" + item.username + " ");

                                binding.messageEdit.setSelection(binding.messageEdit.getText().length());
                            }
                        }
                    }
                });
        fragment.show(getActivity().getSupportFragmentManager(), "CommentTagedFriendsF");
    }

    private void initControl() {
        if (commentType.equals("OwnComment"))
        {
            binding.messageEdit.setHint(binding.getRoot().getContext().getString(R.string.leave_a_comment));
        }
        else
        if (commentType.equals("replyComment"))
        {
            binding.messageEdit.setHint(""+getArguments().getString("replyStr"));
        }


        setKeyboardListener();

        binding.sendBtn.setOnClickListener(this);
        binding.messageEdit.setOnClickListener(this);


        showKeyboard();

    }

    private void showKeyboard() {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                binding.messageEdit.requestFocus();
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(binding.messageEdit,0);
            }
        },200);
    }


    int privious_height = 0;
    public void setKeyboardListener() {

        KeyboardHeightProvider keyboardHeightProvider = new KeyboardHeightProvider(getActivity());
        keyboardHeightProvider.setKeyboardHeightObserver(new KeyboardHeightObserver() {
            @Override
            public void onKeyboardHeightChanged(int height, int orientation) {
                Functions.printLog(Constants.tag, "" + height);
                if (height < 0) {
                    privious_height = Math.abs(height);
                }

                if (Build.VERSION.SDK_INT<Build.VERSION_CODES.P)
                {
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(binding.writeLayout.getWidth(), binding.writeLayout.getHeight());
                    params.bottomMargin = height + privious_height;
                    binding.writeLayout.setLayoutParams(params);
                }

            }


        });
        keyboardHeightProvider.start();

    }




    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.send_btn:
            {
                if (binding.messageEdit.getText().toString().length()>0)
                {
                    Bundle bundle=new Bundle();
                    bundle.putBoolean("isShow",true);
                    bundle.putString("action","sendComment");
                    bundle.putString("message",""+binding.messageEdit.getText().toString());
                    bundle.putSerializable("taggedUserList",tagedUser);
                    callBack.onResponce(bundle);
                    dismiss();
                }

            }
            break;
            case R.id.message_edit:
            {
                view.clearFocus();
                view.requestFocus();
                Log.d(Constants.tag,"Focus True");
            }
            break;
        }
    }


}