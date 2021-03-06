package com.im1020.controller.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.im1020.R;
import com.im1020.controller.adapter.PickAdapter;
import com.im1020.modle.Modle;
import com.im1020.modle.bean.PickInfo;
import com.im1020.modle.bean.UserInfo;
import com.im1020.utils.ShowToast;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PickContactActivity extends AppCompatActivity {

    @Bind(R.id.tv_pick_save)
    TextView tvPickSave;
    @Bind(R.id.lv_pick)
    ListView lvPick;
    private PickAdapter adapter;
    private List<PickInfo> pickInfos;
    private List<String> members;
    private boolean isMember = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_contact);
        ButterKnife.bind(this);


        getGroupId();
        initView();

        //获取数据
        initData();

        initListener();
    }

    private void getGroupId() {

        String groupid = getIntent().getStringExtra("groupid");

        if (groupid == null){
            //说明是创建群

            members = new ArrayList<>();

            isMember = false;
        }else{
            isMember = true;
            //添加群成员
            EMGroup group = EMClient.getInstance().groupManager().getGroup(groupid);

            members = group.getMembers();
        }

    }

    private void initListener() {

        //item点击事件
        lvPick.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //获取item里的checkbox
                CheckBox cbPick =
                        (CheckBox) view.findViewById(R.id.cb_item_pick_contacts);
                //对当前checkbox状态进行取反
                cbPick.setChecked(!cbPick.isChecked());

                PickInfo pickInfo = pickInfos.get(position);
                //设置当前的状态
                pickInfo.setCheck(cbPick.isChecked());

                adapter.refresh(pickInfos,members);
            }
        });


    }

    private void initData() {

        //获取联系人
        //本地
        List<UserInfo> contacts = Modle.getInstance().getDbManager().getContactDao().getContacts();

        if (contacts == null){
            return;
        }
        if (contacts.size()==0){
            ShowToast.show(this,"您还没有好友");
        }

        //转换数据
        pickInfos = new ArrayList<>();
        for (UserInfo userInfo:contacts) {
            pickInfos.add(new PickInfo(userInfo,false));
        }
        adapter.refresh(pickInfos,members);
    }

    private void initView() {

        adapter = new PickAdapter(this);

        lvPick.setAdapter(adapter);

    }



    //保存联系人
    @OnClick(R.id.tv_pick_save)
    public void onClick() {
        List<String> contactCheck = adapter.getContactCheck();
        if (contactCheck ==  null){
            return;
        }

        Intent intent = new Intent();

        intent.putExtra("members",contactCheck.toArray(new String[contactCheck.size()]));

        if (isMember){
            setResult(2,intent);
        }else{
            setResult(1,intent);
        }


        //结束当前页面
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK){
            //返回事件处理的事情

            finish();
            //返回true事件自己消费掉
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
