package com.kedacom.truetouch.ok.widgt;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;


import com.kedacom.truetouch.ok.R;

/**
 * 带清空的EditText界面
 * Created by zhoutianjie on 2018/12/12.
 */

public class LoginEditText extends FrameLayout {

    private String hitText = "";
    private boolean requestFocus = false;

    private EditText editText;
    private ImageView imageView;
    private View view;

    public LoginEditText(Context context) {
        super(context);
    }

    public LoginEditText(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context,attrs);
    }

    public LoginEditText(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context,attrs);
    }


    private void initView(Context context,AttributeSet attrs){


        TypedArray array = context.obtainStyledAttributes(attrs,R.styleable.LoginEditText);
        hitText = array.getString(R.styleable.LoginEditText_hit);
        requestFocus = array.getBoolean(R.styleable.LoginEditText_requestFocus,false);
        array.recycle();


        view = LayoutInflater.from(context).inflate(R.layout.item_login_edittext,this,true);

        editText = view.findViewById(R.id.item_edit);
        imageView = view.findViewById(R.id.clean_img);

        editText.setHint(hitText);
        if(requestFocus){
            editText.requestFocus();
        }

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(TextUtils.isEmpty(s)){
                    imageView.setVisibility(GONE);
                }else {
                    imageView.setVisibility(VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        imageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText("");
            }
        });

        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.requestFocus();
            }
        });

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

    }

    public void setText(CharSequence s){
        editText.setText(s);
    }



    public boolean EditRequestFocus(){
        return editText.requestFocus();
    }

    public Editable getText(){
        return editText.getText();
    }



}
