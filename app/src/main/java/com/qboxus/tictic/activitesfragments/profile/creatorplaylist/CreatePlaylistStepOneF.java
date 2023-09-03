package com.qboxus.tictic.activitesfragments.profile.creatorplaylist;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.qboxus.tictic.Constants;
import com.qboxus.tictic.interfaces.FragmentCallBack;
import com.qboxus.tictic.models.CreatePlaylistModel;
import com.qboxus.tictic.R;


public class CreatePlaylistStepOneF extends Fragment {

    EditText edtUsername;
    TextView tvUsernameCount;
    Button btnNext;
    CreatePlaylistModel playlistModel;
    View view;

    FragmentCallBack callBack;
    public CreatePlaylistStepOneF(FragmentCallBack callBack) {
        this.callBack = callBack;
    }

    public CreatePlaylistStepOneF() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view=inflater.inflate(R.layout.fragment_create_playlist_step_one, container, false);
        initContol();
        return view;
    }

    private void initContol() {
        playlistModel=new CreatePlaylistModel();
        btnNext=view.findViewById(R.id.btnNext);
        edtUsername=view.findViewById(R.id.edtUsername);
        tvUsernameCount=view.findViewById(R.id.tvUsernameCount);
        InputFilter[] username_filters = new InputFilter[1];
        username_filters[0] = new InputFilter.LengthFilter(Constants.USERNAME_CHAR_LIMIT);
        edtUsername.setFilters(username_filters);
        edtUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                // check the username field length

                tvUsernameCount.setText(edtUsername.getText().length() + "/" + Constants.USERNAME_CHAR_LIMIT);
                String txtName = edtUsername.getText().toString();
                if (txtName.length() > 0) {
                    btnNext.setEnabled(true);
                    btnNext.setClickable(true);
                } else {
                    btnNext.setEnabled(false);
                    btnNext.setClickable(false);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        view.findViewById(R.id.goBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        view.findViewById(R.id.btnNext).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playlistModel.setName(""+edtUsername.getText().toString());

                CreatePlaylistStepTwoF f = new CreatePlaylistStepTwoF(true,new FragmentCallBack() {
                    @Override
                    public void onResponce(Bundle bundle) {
                        if (!(bundle.getBoolean("isShow")))
                        {
                            callBack.onResponce(bundle);
                            getActivity().onBackPressed();
                        }
                    }
                });
                Bundle bundle=new Bundle();
                bundle.putSerializable("model",playlistModel);
                f.setArguments(bundle);
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
                ft.replace(R.id.stepOnePlaylistContainerId, f,"CreatePlaylistStepTwoF").addToBackStack("CreatePlaylistStepTwoF").commit();
            }
        });
    }
}