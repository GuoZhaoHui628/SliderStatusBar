package guozhaohui.com.sliderstatusbar;

import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;

import android.widget.Toast;

import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import guozhaohui.com.satusbarutils.StatusBarUtil;
import guozhaohui.com.sliderstatusbar.rvutils.CommonAdapter;
import guozhaohui.com.sliderstatusbar.rvutils.HeaderRecyclerAndFooterWrapperAdapter;
import guozhaohui.com.sliderstatusbar.rvutils.OnItemClickListener;
import guozhaohui.com.sliderstatusbar.rvutils.ViewHolder;



public class MainActivity extends AppCompatActivity {

    private int [] imgResIds;
    private int currentPageImg;
    private ScheduledExecutorService scheduledExecutorService;   //定时周期执行指定任务
    private List<String> stringLists = new ArrayList<>();
    private RecyclerView recyclerView;
    private CommonAdapter<String> adapter;
    private HeaderRecyclerAndFooterWrapperAdapter mHeaderAdapter ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StatusBarUtil.setTranslucentForImageView(MainActivity.this,0,null);
        initDatas();

        this.scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        this.scheduledExecutorService.scheduleWithFixedDelay(new ViewPagerTask(), 2, 2, TimeUnit.SECONDS);
        initRecyView();

    }

    private void initRecyView() {

        recyclerView = (RecyclerView) this.findViewById(R.id.recyView);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        /**
         * 使用了框架，设置itemdecoration
         * */
        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this)
                .color(Color.LTGRAY)
                .sizeResId(R.dimen.divider)
                .marginResId(R.dimen.leftmargin, R.dimen.rightmargin)
                .build());

        adapter = new CommonAdapter<String>(this,stringLists,R.layout.item) {
           @Override
           public void convert(ViewHolder holder, String s) {
               holder.setText(R.id.tv, s);
           }
       };

        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(ViewGroup parent, View view, Object o, int position) {

                Toast.makeText(MainActivity.this,"你是几-->"+position,Toast.LENGTH_SHORT).show();

            }

            @Override
            public boolean onItemLongClick(ViewGroup parent, View view, Object o, int position) {
                return false;
            }
        });
       initHeaderAndFooter();

    }


    private void initHeaderAndFooter()
    {
        mHeaderAdapter = new HeaderRecyclerAndFooterWrapperAdapter(adapter) {
            @Override
            protected void onBindHeaderHolder(ViewHolder viewHolder, int headerPos, int layoutId, Object o) {

                switch (layoutId){

                    case R.layout.header1:

                        HeaderView1 header1 = (HeaderView1) o;
                        viewHolder.setImageResource(R.id.iv,header1.getImgResId());
                        break;

                }

            }
        };



        //因为这里只有一个头部，所以使用的方法是add,如果多个，则要使用set
        mHeaderAdapter.addHeaderView(R.layout.header1,getHeader1Obj());

        recyclerView.setAdapter(mHeaderAdapter);


    }

    private void initDatas() {

        imgResIds = new int[]{R.mipmap.a1,R.mipmap.a2,R.mipmap.a3};

        for(int i=0;i<40;i++){

            stringLists.add("德德德玛西亚~~"+i);

        }

    }


    public HeaderView1 getHeader1Obj(){
        HeaderView1 o = new HeaderView1(imgResIds[currentPageImg]);
        return o;
    }



    public class ViewPagerTask implements Runnable{

        @Override
        public void run() {
            currentPageImg = (currentPageImg+1) % imgResIds.length;
            //更新界面
            handler.obtainMessage().sendToTarget();
        }

    }
    private Handler handler = new Handler(){
        public void handleMessage(android.os.Message msg) {


            mHeaderAdapter.clearHeaderView();//这里要将header清除，不然会叠加
            mHeaderAdapter.addHeaderView(R.layout.header1,getHeader1Obj());
            mHeaderAdapter.notifyDataSetChanged();

       //注意不能在这里调用这个方法，因为每次调用这个方法都会将整个adapter刷新，并不能保存当前的状态，可以自行验证，当下滑后，会出现bug
//            recyclerView.setAdapter(mHeaderAdapter);

        };
    };

}
