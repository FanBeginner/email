package com.example.email.displayEmail;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.email.BaseActivity;
import com.example.email.R;
import com.example.email.adapter.AttachmentAdapter;
import com.example.email.bean.Attachment;
import com.example.email.bean.EmailBean;
import com.example.email.bean.MessageBean;
import com.example.email.editEmail.EditEmailActivity;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

public class DisplayEmailActivity extends BaseActivity implements View.OnClickListener,IDisplayEmailView {

    private DisplayEmailPresenter displayEmailPresenter;

    private LinearLayout mainLayout;
    private ImageView back;
    private TextView subject;
    private TextView from;
    private TextView date;
    private TextView to;
    private LinearLayout attachmentLayout;
    private TextView attachmentInformation;
    private ImageView attachmentExpand;
    private RecyclerView recyclerView;
    private WebView webView;
    private TextView reply;
    private TextView replyAll;
    private TextView forward;
    private TextView more;

    private ProgressBar progressBar;

    private float density;
    private int recyclerMeasureHeight;
    private boolean isExpand = false;
    private boolean isAnimating = false;//是否正在执行动画

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_email);

        init();
        setWeb();

        displayEmailPresenter.getInformation(this);
    }

    private void init(){

        displayEmailPresenter = new DisplayEmailPresenter(this);

        mainLayout = findViewById(R.id.main_display);

        back = findViewById(R.id.back_display);

        subject = findViewById(R.id.subject_display);
        from = findViewById(R.id.from_display);
        date = findViewById(R.id.date_display);
        to = findViewById(R.id.to_display);

        attachmentLayout = findViewById(R.id.attachment_display);
        attachmentInformation = findViewById(R.id.attachment_text_display);
        attachmentExpand = findViewById(R.id.attachment_expand_display);

        recyclerView = findViewById(R.id.recycler_display);

        webView = findViewById(R.id.web_display);
        reply = findViewById(R.id.reply_display);
        replyAll = findViewById(R.id.reply_all_display);
        forward = findViewById(R.id.forward_display);
        more = findViewById(R.id.more_display);

        progressBar = findViewById(R.id.progressBar_display);

        back.setOnClickListener(this);
        attachmentLayout.setOnClickListener(this);
        reply.setOnClickListener(this);
        replyAll.setOnClickListener(this);
        forward.setOnClickListener(this);
        more.setOnClickListener(this);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setWeb(){

        webView.setHorizontalScrollBarEnabled(false);//水平不显示
        webView.setVerticalScrollBarEnabled(false); //垂直不显示
        //webView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);//滚动条在WebView内侧显示
        //webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);//滚动条在WebView外侧显示

        final WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        webSettings.setUseWideViewPort(true); //将图片调整到适合webView的大小
        webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小

        //缩放操作
        webSettings.setSupportZoom(true); //支持缩放，默认为true。是下面那个的前提。
        webSettings.setBuiltInZoomControls(true); //设置内置的缩放控件。若为false，则该WebView不可缩放
        webSettings.setDisplayZoomControls(false); //隐藏原生的缩放控件

        //webSettings.setDefaultFontSize(25);
        webSettings.setMinimumFontSize(16);
        webSettings.setLoadsImagesAutomatically(true); //支持自动加载图片

        /*webView.post(new Runnable() {
            @Override
            public void run() {
               int width = webView.getMeasuredWidth();
            }
        });*/

        webView.setInitialScale(25);

        webView.setWebViewClient(new WebViewClient(){
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)         //api21 android5.0
            @Nullable
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                if (request != null && request.getUrl() != null){
                    String url = request.getUrl().toString();
                    if (url.contains("cid")){
                        url = url.substring(4);      //此方法不在主线程中，所以不用新开线程，完美啊！真的是完美，卧槽！！！
                        WebResourceResponse response = displayEmailPresenter.getWebResourceResponse(url);

                        /*File file = displayEmailPresenter.download(url,null);
                        WebResourceResponse response = null;
                        try {
                            response = new WebResourceResponse("image/jpg","utf-8",new FileInputStream(file));
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }*/

                        if (response != null){
                            //回复不为空，说明下载了附件，不然就不更新附件列表了，很好
                            DisplayEmailActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    List<Attachment> attachments = DataSupport
                                            .where("messageId = ?",String.valueOf(getMessageId()))
                                            .find(Attachment.class);
                                    if (attachments.size()>0){
                                        AttachmentAdapter adapter = new AttachmentAdapter(attachments);
                                        recyclerView.setAdapter(adapter);          //更新附件列表,将下载的附件设为已下载
                                    }
                                }
                            });
                            webSettings.setDefaultFontSize(25);
                            return response;
                        }
                    }
                }
                return super.shouldInterceptRequest(view, request);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                hideProgressBar();
                showMainLayout();
            }
        });

        //webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);

        /*DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int mDensity = metrics.densityDpi;
        if (mDensity == 240) {
            webSettings.setDefaultZoom(WebSettings.ZoomDensity.FAR);
        } else if (mDensity == 160) {
            webSettings.setDefaultZoom(WebSettings.ZoomDensity.MEDIUM);
        } else if(mDensity == 120) {
            webSettings.setDefaultZoom(WebSettings.ZoomDensity.CLOSE);
        }else if(mDensity == DisplayMetrics.DENSITY_XHIGH){
            webSettings.setDefaultZoom(WebSettings.ZoomDensity.FAR);
        }else if (mDensity == DisplayMetrics.DENSITY_TV){
            webSettings.setDefaultZoom(WebSettings.ZoomDensity.FAR);
        }*/
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back_display:
                finish();
                break;
            case R.id.attachment_display:
                //如果动画正在执行,直接return,相当于点击无效了,不会出现当快速点击时,
                // 动画的执行和ImageButton的图标不一致的情况
                if (isAnimating) return;
                //如果动画没在执行,走到这一步就将isAnimating制为true , 防止这次动画还没有执行完毕的
                //情况下,又要执行一次动画,当动画执行完毕后会将isAnimating制为false,这样下次动画又能执行
                isAnimating = true;

                if (!isExpand) {
                    //打开动画
                    attachmentExpand.setImageResource(R.drawable.ic_arrow_drop_up_blue_24dp);
                    animateOpen(recyclerView);
                } else {
                    //关闭动画
                    attachmentExpand.setImageResource(R.drawable.ic_arrow_drop_down_blue_24dp);
                    animateClose(recyclerView);
                }
                isExpand = !isExpand;
                break;
            case R.id.reply_display:{
                Intent intent = new Intent(this, EditEmailActivity.class);
                MessageBean message = getMessage();
                intent.putExtra("mark","reply");
                intent.putExtra("to",message.getReplyTo());
                startActivity(intent);
                overridePendingTransition(R.anim.bottom_in,R.anim.scale_out);
                break;
            }
            case R.id.reply_all_display:{
                Intent intent = new Intent(this, EditEmailActivity.class);
                MessageBean message = getMessage();
                intent.putExtra("mark","replyAll");
                intent.putExtra("to",message.getReplyTo());
                startActivity(intent);
                overridePendingTransition(R.anim.bottom_in,R.anim.scale_out);
                break;
            }
            case R.id.forward_display:{
                Intent intent = new Intent(this, EditEmailActivity.class);
                intent.putExtra("mark","forward");
                intent.putExtra("otherId",getMessageId());
                startActivity(intent);
                overridePendingTransition(R.anim.bottom_in,R.anim.scale_out);
                break;
            }
            case R.id.more_display:
                break;
        }
    }

    private MessageBean getMessage(){
        List<MessageBean> list =  DataSupport.where("otherId = ?",String.valueOf(getMessageId())).find(MessageBean.class);
        return list.get(0);
    }

    @Override
    protected void onDestroy() {
        if (webView != null){
            webView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            webView.clearHistory();

            ((ViewGroup) webView.getParent()).removeView(webView);
            webView.destroy();
            webView = null;
        }
        super.onDestroy();
    }

    @Override
    public int getEmailId() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        return sharedPreferences.getInt("email_id",0);
    }

    @Override
    public int getMessageId() {
        Intent intent = getIntent();
        return intent.getIntExtra("messageId", 0);
    }

    @Override
    public void initializeInterface(MessageBean messageBean) {

        subject.setText(messageBean.getSubject());
        from.setText(messageBean.getFrom());
        date.setText(messageBean.getDate());
        to.setText(messageBean.getTo());
        if (messageBean.isAttachment()){
            String attachment = messageBean.getAttachmentNum()+"个附件  共"+messageBean.getAttachmentSize();
            attachmentInformation.setText(attachment);
        }

        StringBuilder html = new StringBuilder();
        if (messageBean.getText()!=null){
            html.append(messageBean.getText());
        }
        int index = html.indexOf("<head>");
        String meta = "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">";
        if (index != -1){
            html.insert(index+6,meta);
        }else{
            html.insert(0,meta);
        }
        String htmlData = "<link rel=\"stylesheet\" type=\"text/css\" href=\"style.css\" />" + html.toString();
        webView.loadDataWithBaseURL("file:///android_asset/",htmlData,"text/html","utf-8",null);
    }

    @Override
    public void showAttachmentLayout() {
        attachmentLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void setRecyclerView(List<Attachment> list) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        AttachmentAdapter adapter = new AttachmentAdapter(list);
        recyclerView.setAdapter(adapter);

        //获取像素密度
        density = getResources().getDisplayMetrics().density;          //无用，留着，以后研究
        //获取布局的高度
        int w = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
        recyclerView.measure(w,h);
        int height = recyclerView.getMeasuredHeight();
        recyclerMeasureHeight = height;        //(int) (density*height+0.5)
    }

    @Override
    public void showErrorMessage() {
        Toast.makeText(this,"出错啦。。。",Toast.LENGTH_SHORT).show();
    }

    private void showMainLayout() {
        mainLayout.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    private void animateOpen(RecyclerView view){
        view.setVisibility(View.VISIBLE);
        ValueAnimator animator = createDropAnimator(view,0,recyclerMeasureHeight);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                isAnimating = false;
            }
        });
        animator.start();
    }

    private void animateClose(final RecyclerView view){
        int origHeight = view.getHeight();
        ValueAnimator animator = createDropAnimator(view,origHeight,0);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(View.GONE);
                isAnimating = false;
            }
        });
        animator.start();
    }

    private ValueAnimator createDropAnimator(final View view, int start, int end){
        ValueAnimator animator = ValueAnimator.ofInt(start,end);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                layoutParams.height = value;
                view.setLayoutParams(layoutParams);
            }
        });
        return animator;
    }
}
