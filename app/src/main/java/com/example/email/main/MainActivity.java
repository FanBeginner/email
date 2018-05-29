package com.example.email.main;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.opengl.Visibility;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.email.AddAccountActivity;
import com.example.email.BaseActivity;
import com.example.email.SettingActivity;
import com.example.email.adapter.AddressAdapter;
import com.example.email.adapter.AttachmentAdapter;
import com.example.email.bean.Attachment;
import com.example.email.bean.EmailBean;
import com.example.email.editEmail.EditEmailActivity;
import com.example.email.MyDecoration;
import com.example.email.R;
import com.example.email.adapter.RecyclerAdapter;
import com.example.email.bean.MessageBean;

import org.litepal.crud.DataSupport;
import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements IMainView, View.OnClickListener {

    private MainPresenter mainPresenter;
    private SharedPreferences sharedPreferences;
    private Intent intent;

    private DrawerLayout drawerLayout;
    private ImageView openDrawerLayout;
    private TextView title;
    private RelativeLayout longClickBar;
    private TextView cancel;
    private TextView selectEmail;
    private TextView selectAll;
    private SwipeRefreshLayout swipeRefresh;
    private RecyclerView recyclerView;
    private FloatingActionButton floatButton;
    private LinearLayout longClickBottom;
    private TextView mark;
    private TextView move;
    private TextView delete;
    private LinearLayout deletedBottom;
    private TextView restore;
    private TextView removeCompletely;
    private ProgressBar progressBar;

    private RecyclerAdapter adapter;

    private boolean isSelectAll = false;
    private int bottomMeasureHeight;
    private int deletedBottomMeasureHeight;
    private boolean isLongClick = false;

    private int sidebarMeasureHeight;
    private boolean isExpand_sidebar = false;
    private boolean isAnimating_sidebar = false;//是否正在执行动画

    private int scrollMeasureHeight;

    private int folderMeasureHeight;
    private boolean isExpand_folder = false;
    private boolean isAnimating_folder = false;//是否正在执行动画

    private LinearLayout sidebarExpand;
    private RelativeLayout sidebarHead;
    private ImageView sidebarLogo;
    private TextView sidebarAddress;
    private ImageView rotateExpand;
    private ScrollView scrollView;
    private LinearLayout inbox;
    private LinearLayout unread;
    private LinearLayout importantContacts;
    private LinearLayout attachment;
    private LinearLayout todo;
    private LinearLayout draft;
    private LinearLayout sent;
    private LinearLayout deleted;
    private LinearLayout star;
    private LinearLayout folder;
    private RecyclerView recyclerAddress;
    private AddressAdapter addressAdapter;
    private TextView add;
    private TextView setting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        moveView();
        initData();
        inbox.setSelected(true);
        showRecyclerAddress();
    }

    private void init(){
        mainPresenter = new MainPresenter(this);
        intent = getIntent();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        drawerLayout = findViewById(R.id.drawer_layout);
        openDrawerLayout = findViewById(R.id.open_drawerLayout);
        title = findViewById(R.id.title_main);
        longClickBar = findViewById(R.id.long_click_bar);
        cancel = findViewById(R.id.cancel_main);
        selectEmail = findViewById(R.id.select_email_main);
        selectAll = findViewById(R.id.select_all_main);
        swipeRefresh = findViewById(R.id.swipe_refresh);
        recyclerView = findViewById(R.id.recycler_main);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new MyDecoration(this,RecyclerView.VERTICAL));

        floatButton = findViewById(R.id.float_button);
        longClickBottom = findViewById(R.id.long_click_bottom);
        mark = findViewById(R.id.mark_main);
        move = findViewById(R.id.move_main);
        delete = findViewById(R.id.delete_main);
        deletedBottom = findViewById(R.id.deleted_bottom);
        restore = findViewById(R.id.restore_deleted);
        removeCompletely = findViewById(R.id.remove_completely_deleted);
        progressBar = findViewById(R.id.progressBar_main);


        //sidebar
        sidebarExpand = findViewById(R.id.sidebar_expand);
        sidebarHead = findViewById(R.id.head_sidebar);
        sidebarLogo = findViewById(R.id.logo_sidebar);
        sidebarAddress = findViewById(R.id.address_sidebar);
        rotateExpand = findViewById(R.id.rotate_expand);
        scrollView = findViewById(R.id.scrollView_layout);
        inbox = findViewById(R.id.inbox);
        unread = findViewById(R.id.unread_box);
        importantContacts = findViewById(R.id.important_contacts);
        attachment = findViewById(R.id.attachment_sidebar);
        todo = findViewById(R.id.email_todo);
        draft = findViewById(R.id.draft);
        sent = findViewById(R.id.sent);
        deleted = findViewById(R.id.recycler);
        star = findViewById(R.id.star);
        folder = findViewById(R.id.folder);
        recyclerAddress = findViewById(R.id.recycler_address);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerAddress.setLayoutManager(manager);
        add = findViewById(R.id.add_sidebar);
        setting = findViewById(R.id.setting_sidebar);

        mainPresenter.getMessageList(this);

        openDrawerLayout.setOnClickListener(this);
        cancel.setOnClickListener(this);
        selectAll.setOnClickListener(this);
        floatButton.setOnClickListener(this);
        mark.setOnClickListener(this);
        move.setOnClickListener(this);
        delete.setOnClickListener(this);
        restore.setOnClickListener(this);
        removeCompletely.setOnClickListener(this);

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mainPresenter.refreshData(getEmailId());
            }
        });

        //sidebar
        sidebarExpand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        sidebarHead.setOnClickListener(this);
        inbox.setOnClickListener(this);
        unread.setOnClickListener(this);
        importantContacts.setOnClickListener(this);
        attachment.setOnClickListener(this);
        todo.setOnClickListener(this);
        draft.setOnClickListener(this);
        sent.setOnClickListener(this);
        deleted.setOnClickListener(this);
        star.setOnClickListener(this);
        folder.setOnClickListener(this);
        add.setOnClickListener(this);
        setting.setOnClickListener(this);
    }

    private void moveView(){
        //将sidebarExpand向上移动
        scrollMeasureHeight = measureHeight(scrollView);
        sidebarHead.bringToFront();
        sidebarExpand.animate().translationY(-scrollMeasureHeight);

        //将longClickBottom向下移动
        bottomMeasureHeight = measureHeight(longClickBottom);
        longClickBottom.animate().translationY(bottomMeasureHeight);

        //将已删除邮件页面的底部布局下移，隐藏
        deletedBottomMeasureHeight = measureHeight(deletedBottom);
        deletedBottom.animate().translationY(deletedBottomMeasureHeight);
    }

    private void initData(){

        EmailBean email = DataSupport.find(EmailBean.class,getEmailId());

        String logo = email.getLogo();
        sidebarAddress.setText(email.getAddress());
        switch (logo){
            case "qq":
                sidebarLogo.setImageResource(R.drawable.ic_logo_qq);
                break;
            case "163":
                sidebarLogo.setImageResource(R.drawable.ic_logo_163);
                break;
        }
    }

    private void showRecyclerAddress(){
        List<EmailBean> list = DataSupport.findAll(EmailBean.class);
        addressAdapter = new AddressAdapter(list);
        recyclerAddress.setAdapter(addressAdapter);
        addressAdapter.setAddressListener(new AddressAdapter.OnAddressListener() {
            @Override
            public void setEmailId(EmailBean email) {
                refreshPage();
                //关闭动画
                RotateAnimation animation = new RotateAnimation(180f,360f,
                        Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
                animation.setDuration(200);
                animation.setFillAfter(true);
                animation.setInterpolator(new LinearInterpolator());
                rotateExpand.startAnimation(animation);
                animateClose(sidebarExpand,1);
                isExpand_sidebar = !isExpand_sidebar;
            }
        });
    }

    private void refreshPage(){
        cancelSelect();
        inbox.setSelected(true);
        title.setText(R.string.inbox);
        swipeRefresh.setEnabled(true);      //启用下来刷新
        List<MessageBean> notDeleteList = DataSupport.where("emailId = ? and isLocal = ? and isDelete = ?",
                String.valueOf(getEmailId()), "0","0").order("id desc").find(MessageBean.class);   //查询本地未被删除的邮件
        if (notDeleteList.size() == 0){       //如果邮件都被删除，则弹出消息提示，没有邮件
            Toast.makeText(this,"没有邮件",Toast.LENGTH_SHORT).show();
        }
        adapter.setList(notDeleteList);
        adapter.setPage("收件箱");
        recyclerView.setAdapter(adapter);

        initData();

        List<EmailBean> list = DataSupport.findAll(EmailBean.class);
        addressAdapter.setList(list);
    }

    @Override
    public int getEmailId() {
        return sharedPreferences.getInt("email_id",0);
    }

    @Override
    public void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void showRecyclerView(List<MessageBean> list) {

        adapter = new RecyclerAdapter(list);
        adapter.setPage("收件箱");
        adapter.setItemLongClickListener(new RecyclerAdapter.itemLongClickListener() {
            @Override
            public void onClick(String page, int size) {
                onBottomStatus(page, size);
            }

            @Override
            public void onLongClick(String page) {
                if (adapter.getItemCount() == 1){
                    isSelectAll = true;
                    selectAll.setText("全不选");
                }
                showLongClickInterface(page);
            }
        });
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void hideRefresh() {
        swipeRefresh.setRefreshing(false);
    }

    @Override
    public void showSuccessMessage() {
        Toast.makeText(this,"刷新成功",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showErrorMessage() {
        Toast.makeText(this,"获取信息失败",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.open_drawerLayout:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.cancel_main:
                hideLongClickInterface();
                break;
            case R.id.select_all_main:
                if (isSelectAll){
                    List<Boolean> list = new ArrayList<>();
                    for (int i=0;i<adapter.getItemCount();i++){
                        list.add(false);
                    }
                    adapter.setListCheck(list);
                    adapter.setSize(0);
                    selectEmail.setText(R.string.select_email);
                    selectAll.setText(R.string.select_all);
                }else{
                    List<Boolean> list = new ArrayList<>();
                    for (int i=0;i<adapter.getItemCount();i++){
                        list.add(true);
                    }
                    adapter.setListCheck(list);
                    adapter.setSize(adapter.getItemCount());
                    String select = "已选择 "+adapter.getItemCount()+" 项";
                    selectEmail.setText(select);
                    selectAll.setText("全不选");
                }
                isSelectAll = !isSelectAll;
                onBottomStatus(adapter.getPage(),adapter.getSize());
                break;
            case R.id.float_button:
                Intent intent = new Intent(this, EditEmailActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.bottom_in,R.anim.scale_out);
                break;
            case R.id.delete_main:
                showDeleteMenu(drawerLayout);
                break;
            case R.id.restore_deleted:
                List<Boolean> listCheckRestore = adapter.getListCheck();
                List<MessageBean> messageRestore = adapter.getList();
                for (int i=0;i<listCheckRestore.size();i++){
                    if (listCheckRestore.get(i)){
                        MessageBean messageBean = messageRestore.get(i);
                        messageBean.setToDefault("isDelete");
                        messageBean.update(messageBean.getId());    //在本地数据库上将其设置为未删除
                        messageRestore.remove(i);         //数据源上移除
                        adapter.notifyItemRemoved(i);       //recyclerView上移除
                    }
                }
                hideLongClickInterface();
                break;
            case R.id.remove_completely_deleted:
                showDeleteMenu(drawerLayout);
                break;
            case R.id.head_sidebar:
                if (isAnimating_sidebar) return;
                isAnimating_sidebar = true;
                if (!isExpand_sidebar) {
                    //打开动画
                    RotateAnimation animation = new RotateAnimation(0,180f,
                            Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
                    animation.setDuration(200);
                    animation.setFillAfter(true);
                    animation.setInterpolator(new LinearInterpolator());
                    rotateExpand.startAnimation(animation);
                    animateOpen(sidebarExpand,-scrollMeasureHeight,1);
                } else {
                    //关闭动画
                    RotateAnimation animation = new RotateAnimation(180f,360f,
                            Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
                    animation.setDuration(200);
                    animation.setFillAfter(true);
                    animation.setInterpolator(new LinearInterpolator());
                    rotateExpand.startAnimation(animation);
                    animateClose(sidebarExpand,1);
                }
                isExpand_sidebar = !isExpand_sidebar;
                break;
            case R.id.inbox:
                cancelSelect();
                inbox.setSelected(true);
                title.setText(R.string.inbox);
                drawerLayout.closeDrawers();
                swipeRefresh.setEnabled(true);      //启用下来刷新
                List<MessageBean> notDeleteList = DataSupport.where("emailId = ? and isLocal = ? and isDelete = ?",
                        String.valueOf(getEmailId()), "0","0").order("id desc").find(MessageBean.class);   //查询本地未被删除的邮件
                if (notDeleteList.size() == 0){       //如果邮件都被删除，则弹出消息提示，没有邮件
                    Toast.makeText(this,"没有邮件",Toast.LENGTH_SHORT).show();
                }
                adapter.setList(notDeleteList);
                adapter.setPage("收件箱");
                recyclerView.setAdapter(adapter);
                break;
            case R.id.unread_box:
                cancelSelect();
                unread.setSelected(true);
                title.setText(R.string.unread);
                drawerLayout.closeDrawers();
                swipeRefresh.setEnabled(false);
                List<MessageBean> notReadList = DataSupport
                        .where("emailId = ? and isLocal = ? and isDelete = ? and isRead = ?",
                                String.valueOf(getEmailId()), "0","0","0")
                        .order("id desc").find(MessageBean.class);
                adapter.setList(notReadList);
                adapter.setPage("未读箱");
                recyclerView.setAdapter(adapter);
                break;
            case R.id.important_contacts:
                cancelSelect();
                importantContacts.setSelected(true);
                break;
            case R.id.attachment_sidebar:
                cancelSelect();
                attachment.setSelected(true);
                title.setText(R.string.attachment);
                drawerLayout.closeDrawers();
                swipeRefresh.setEnabled(false);      //禁用下来刷新
                List<Attachment> attachmentList = DataSupport.where("emailId = ? and isLocal = ?",
                        String.valueOf(getEmailId()), "0").order("id desc").find(Attachment.class);   //查询本地保存的草稿邮件
                AttachmentAdapter attachmentAdapter = new AttachmentAdapter(attachmentList);
                recyclerView.setAdapter(attachmentAdapter);
                break;
            case R.id.email_todo:
                cancelSelect();
                todo.setSelected(true);
                break;
            case R.id.draft:
                cancelSelect();
                draft.setSelected(true);
                title.setText(R.string.draft);
                drawerLayout.closeDrawers();
                swipeRefresh.setEnabled(false);      //禁用下来刷新
                List<MessageBean> draftList = DataSupport.where("emailId = ? and isLocal = ? and isSend = ?",
                        String.valueOf(getEmailId()), "1","0").order("id desc").find(MessageBean.class);   //查询本地保存的草稿邮件
                adapter.setList(draftList);
                adapter.setPage("草稿箱");
                recyclerView.setAdapter(adapter);
                break;
            case R.id.sent:
                cancelSelect();
                sent.setSelected(true);
                title.setText(R.string.sent);
                drawerLayout.closeDrawers();
                swipeRefresh.setEnabled(false);      //禁用下来刷新
                List<MessageBean> sentList = DataSupport.where("emailId = ? and isLocal = ? and isSend = ?",
                        String.valueOf(getEmailId()), "1","1").order("id desc").find(MessageBean.class);   //查询本地保存的草稿邮件
                adapter.setList(sentList);
                adapter.setPage("已发送");
                recyclerView.setAdapter(adapter);
                break;
            case R.id.recycler:
                cancelSelect();
                deleted.setSelected(true);
                title.setText(R.string.recycler);
                drawerLayout.closeDrawers();
                swipeRefresh.setEnabled(false);         //禁用下拉刷新
                List<MessageBean> deletedList = DataSupport.where("emailId = ? and isLocal = ? and isDelete = ?",
                        String.valueOf(getEmailId()), "0","1").order("id desc").find(MessageBean.class);
                if (deletedList.size() == 0){
                    Toast.makeText(this,"没有已删除邮件",Toast.LENGTH_SHORT).show();
                }
                adapter.setList(deletedList);
                adapter.setPage("已删除邮件");
                recyclerView.setAdapter(adapter);
                break;
            case R.id.star:
                cancelSelect();
                star.setSelected(true);
                break;
            case R.id.folder:
                break;
            case R.id.add_sidebar:
                Intent i = new Intent(this, AddAccountActivity.class);
                i.putExtra("from",1);
                startActivityForResult(i,2);
                break;
            case R.id.setting_sidebar:
                Intent intent1 = new Intent(this, SettingActivity.class);
                //intent1.putExtra("emailId",getEmailId());
                startActivityForResult(intent1,1);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case 0:
                if (resultCode == RESULT_OK){
                    List<MessageBean> draftList = DataSupport.where("emailId = ? and isLocal = ? and isSend = ?",
                            String.valueOf(getEmailId()), "1","0").order("id desc").find(MessageBean.class);   //查询本地保存的草稿邮件
                    adapter.setList(draftList);
                }
                break;
            case 1:
                if (resultCode == RESULT_OK){
                    //刷新页面
                    refreshPage();
                }else if (resultCode == 4){        //说明删了一个账号，让address列表更新
                    List<EmailBean> list = DataSupport.findAll(EmailBean.class);
                    addressAdapter.setList(list);
                }else if (resultCode == 5){
                    finish();             //凉了，
                }
                break;
            case 2:
                if (resultCode == RESULT_OK){
                    finish();
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawers();
        }else if (isLongClick){
            hideLongClickInterface();
        }else{
            super.onBackPressed();
        }
    }

    private int measureHeight(View view){
        int w = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
        view.measure(w,h);
        return view.getMeasuredHeight();
    }

    private void animateOpen(View view, int measureHeight, final int i){
        view.setVisibility(View.VISIBLE);
        ValueAnimator animator = createDropAnimator(view,measureHeight,0,1);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (i == 1){          //i=1,代表是sidebarExpand
                    isAnimating_sidebar = false;
                }
                if (i == 2){           //i=2,代表是folderExpand
                    isAnimating_folder = false;
                }
            }
        });
        animator.start();
    }

    private void animateClose(final View view, final int i){
        int origHeight = view.getHeight();
        ValueAnimator animator = createDropAnimator(view,0,-origHeight,i);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(View.GONE);
                if (i == 1){          //i=1,代表是sidebarExpand
                    isAnimating_sidebar = false;
                }
                if (i == 2){           //i=2,代表是folderExpand
                    isAnimating_folder = false;
                }
            }
        });
        animator.start();
    }

    private ValueAnimator createDropAnimator(final View view, int start, int end, final int i){
        ValueAnimator animator = ValueAnimator.ofInt(start,end);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                if (i == 1){
                    view.setTranslationY(value);
                }
            }
        });
        return animator;
    }

    private void showLongClickInterface(String page){
        ((DefaultItemAnimator)recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        isLongClick = true;
        longClickBar.setVisibility(View.VISIBLE);
        floatButton.animate().translationY(floatButton.getHeight()+floatButton.getMeasuredHeight());
        switch (page) {
            case "收件箱":
            case "未读箱":
                longClickBottom.setSelected(true);
                longClickBottom.setVisibility(View.VISIBLE);
                longClickBottom.animate().translationY(0);
                break;
            case "草稿箱":
            case "已发送":
                longClickBottom.setSelected(true);
                mark.setVisibility(View.GONE);
                move.setVisibility(View.GONE);
                longClickBottom.setVisibility(View.VISIBLE);
                longClickBottom.animate().translationY(0);
                break;
            case "已删除邮件":
                deletedBottom.setSelected(true);
                deletedBottom.setVisibility(View.VISIBLE);
                deletedBottom.animate().translationY(0);
                break;
        }
    }

    private void hideLongClickInterface(){
        ((DefaultItemAnimator)recyclerView.getItemAnimator()).setSupportsChangeAnimations(true);
        isLongClick = false;
        adapter.setShow(false);
        longClickBar.setVisibility(View.GONE);
        String page = adapter.getPage();
        switch (page) {
            case "收件箱":
            case "未读箱":
                longClickBottom.animate().translationY(bottomMeasureHeight);
                longClickBottom.setVisibility(View.GONE);
                break;
            case "草稿箱":
            case "已发送":
                longClickBottom.animate().translationY(bottomMeasureHeight);
                longClickBottom.setVisibility(View.GONE);
                mark.setVisibility(View.VISIBLE);
                move.setVisibility(View.VISIBLE);
                break;
            case "已删除邮件":
                deletedBottom.animate().translationY(deletedBottomMeasureHeight);
                deletedBottom.setVisibility(View.GONE);
                break;
        }
        floatButton.animate().translationY(0);
    }

    private void onBottomStatus(String page,int size){
        switch (page) {
            case "收件箱":
            case "未读箱":
                if (size != 0) {
                    String select = "已选择 " + size + " 项";
                    selectEmail.setText(select);
                    delete.setClickable(true);
                    longClickBottom.setSelected(true);
                    mark.setTextColor(getResources().getColor(R.color.black));
                    move.setTextColor(getResources().getColor(R.color.black));
                    delete.setTextColor(getResources().getColor(R.color.black));

                    if (size == adapter.getItemCount()){
                        isSelectAll = true;
                        selectAll.setText("全不选");
                    }
                } else {
                    delete.setClickable(false);
                    selectEmail.setText(R.string.select_email);
                    longClickBottom.setSelected(false);
                    mark.setTextColor(getResources().getColor(R.color.text_color_gray));
                    move.setTextColor(getResources().getColor(R.color.text_color_gray));
                    delete.setTextColor(getResources().getColor(R.color.text_color_gray));
                }
                break;
            case "草稿箱":
            case "已发送":
                if (size != 0) {
                    String select = "已选择 " + size + " 项";
                    selectEmail.setText(select);
                    delete.setClickable(true);
                    longClickBottom.setSelected(true);
                    delete.setTextColor(getResources().getColor(R.color.black));

                    if (size == adapter.getItemCount()){
                        isSelectAll = true;
                        selectAll.setText("全不选");
                    }
                } else {
                    delete.setClickable(false);
                    selectEmail.setText(R.string.select_email);
                    longClickBottom.setSelected(false);
                    delete.setTextColor(getResources().getColor(R.color.text_color_gray));
                }
                break;
            case "已删除邮件":
                if (size != 0) {
                    String select = "已选择 " + size + " 项";
                    selectEmail.setText(select);
                    restore.setClickable(true);
                    deleted.setSelected(true);
                    deletedBottom.setSelected(true);
                    restore.setTextColor(getResources().getColor(R.color.black));
                    removeCompletely.setTextColor(getResources().getColor(R.color.black));

                    if (size == adapter.getItemCount()){
                        isSelectAll = true;
                        selectAll.setText("全不选");
                    }
                } else {
                    selectEmail.setText(R.string.select_email);
                    restore.setClickable(false);
                    deleted.setSelected(false);
                    deletedBottom.setSelected(false);
                    restore.setTextColor(getResources().getColor(R.color.text_color_gray));
                    removeCompletely.setTextColor(getResources().getColor(R.color.text_color_gray));
                }
                break;
        }
    }

    //显示删除弹窗
    private void showDeleteMenu(View v) {
        // 加载PopupWindow的布局
        View view = View.inflate(this, R.layout.menu_delete, null);
        TextView deleteMenu = view.findViewById(R.id.delete_menu_main);
        TextView cancelMenu = view.findViewById(R.id.cancel_menu_main);

        String delete = "删除 "+adapter.getSize()+" 封邮件";
        deleteMenu.setText(delete);

        final PopupWindow popWindow = new PopupWindow(view, LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, true);
        popWindow.setBackgroundDrawable(new ColorDrawable(0));
        popWindow.setAnimationStyle(R.style.bottom_menu_anim_style);
        popWindow.showAtLocation(v, Gravity.BOTTOM, 0, 0);
        //设置背景半透明
        backgroundAlpha(0.6f);

        popWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(1.0f);
            }
        });

        deleteMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideLongClickInterface();
                String page = adapter.getPage();
                List<Boolean> list = adapter.getListCheck();
                List<MessageBean> beanList = adapter.getList();
                switch (page) {
                    case "收件箱":
                    case "未读箱":
                        for (int i = 0; i < list.size(); i++) {
                            if (list.get(i)) {
                                MessageBean messageBean = beanList.get(i);
                                messageBean.setDelete(true);
                                messageBean.update(messageBean.getId());    //在本地数据库上将其设置为删除
                                beanList.remove(i);         //数据源上移除
                                adapter.notifyItemRemoved(i);       //recyclerView上移除
                                //adapter.notifyItemRangeChanged(i,adapter.getItemCount());
                            }
                        }
                        break;
                    case "草稿箱":
                    case "已发送":
                        for (int i = 0; i < list.size(); i++) {
                            if (list.get(i)) {
                                MessageBean messageBean = beanList.get(i);
                                if (messageBean.isAttachment()) {         //判断是否有附件
                                    DataSupport.deleteAll(Attachment.class,
                                            "messageId = ?",String.valueOf(messageBean.getId()));
                                }
                                DataSupport.delete(MessageBean.class, messageBean.getId());       //在本地数据库上将其设置为删除
                                beanList.remove(i);         //数据源上移除
                                adapter.notifyItemRemoved(i);       //recyclerView上移除
                                //adapter.notifyItemRangeChanged(i,adapter.getItemCount());
                            }
                        }
                        break;
                    case "已删除邮件":
                        List<Integer> integerList = new ArrayList<>();
                        for (int i = 0; i < list.size(); i++) {
                            if (list.get(i)) {
                                //从本地数据库中得到最新数据，因为adapter中的数据不能实时更新
                                MessageBean messageBean = DataSupport.find(MessageBean.class, beanList.get(i).getId());
                                integerList.add(messageBean.getOtherId());               //得到在服务器上message的下标
                                if (messageBean.isAttachment()) {         //判断是否有附件
                                    List<Attachment> attachmentList = DataSupport.findAll(Attachment.class, messageBean.getOtherId());
                                    for (Attachment attachment : attachmentList) {
                                        if (attachment.getDownload()) {      //判断附件是否下载，下载的话，就删除本地文件
                                            MainActivity.this.deleteFile(attachment.getFileName());
                                        }
                                        DataSupport.delete(Attachment.class, attachment.getId());           //在本地数据库上删除附件
                                    }
                                }
                                DataSupport.delete(MessageBean.class, messageBean.getId());   //在本地数据库上将其删除
                                beanList.remove(i);         //数据源上移除
                                adapter.notifyItemRemoved(i);       //recyclerView上移除
                            }
                        }
                        mainPresenter.deleteEmail(integerList);        //在服务器上彻底删除

                        break;
                }
                popWindow.dismiss();
            }
        });

        cancelMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popWindow.dismiss();
            }
        });
    }

    /**
     * 设置屏幕的背景透明度
     */
    private void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha; // 0.0-1.0
        getWindow().setAttributes(lp);
    }

    private void cancelSelect(){
        inbox.setSelected(false);
        unread.setSelected(false);
        importantContacts.setSelected(false);
        attachment.setSelected(false);
        todo.setSelected(false);
        draft.setSelected(false);
        sent.setSelected(false);
        deleted.setSelected(false);
        star.setSelected(false);
    }
}
