package com.example.administrator.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.SoundPool;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;



public class dafeijiGameView extends SurfaceView implements
        SurfaceHolder.Callback,Runnable,android.view.View.OnTouchListener{

    private Bitmap my;//自己
    private Bitmap baozha;//爆炸
    private Bitmap bg;//背景
    private Bitmap diren;//敌人
    private Bitmap zidan;//子弹
    private Bitmap erjihuancun;//二级缓存
    private WindowManager windowManager;//获得界面长宽高
    private int display_w;  //界面的宽
    private int display_h;  //界面的高
    private ArrayList<GameImage> gameImage = new ArrayList();
    private ArrayList<Zidan> zidans = new ArrayList<Zidan>();

    //注册飞机跟随自己手指移动的事件
    public dafeijiGameView(Context context) {
        super(context);
        getHolder().addCallback(this);
        //事件注册
        this.setOnTouchListener(this);
    }

    //音乐播放
    private SoundPool pool=null;
    private int sound_bomb=0;
    private int sound_gameover=0;
    private int sound_shot=0;

    private void init(){
        //加载照片
        my= BitmapFactory.decodeResource(getResources(),R.drawable.my);
        baozha= BitmapFactory.decodeResource(getResources(),R.drawable.baozha);
        bg= BitmapFactory.decodeResource(getResources(),R.drawable.bg);
        diren= BitmapFactory.decodeResource(getResources(),R.drawable.diren);
        zidan= BitmapFactory.decodeResource(getResources(),R.drawable.zidan);

        erjihuancun=Bitmap.createBitmap(display_w,display_h, Bitmap.Config.ARGB_8888);
        gameImage.add(new BeijingImage(bg));    //先加入背景照片
        gameImage.add(new FeijiImage(my));
        gameImage.add(new DijiImage(diren,baozha));

        //加载声音

        pool= new SoundPool(3, AudioManager.STREAM_SYSTEM,5);

        sound_bomb=pool.load(getContext(),R.raw.bomb,1);
        sound_gameover=pool.load(getContext(),R.raw.gameover,1);
        sound_shot=pool.load(getContext(),R.raw.shot,1);


    }

    private class SoundPlay extends Thread{
        int i=0;
        public SoundPlay(int i){
            this.i=1;
        }

        public void run(){
            pool.play(i,1,1,1,0,1);
        }

    }

    FeijiImage selectfeiji;
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        //手接近屏幕产生的事件
        if(event.getAction()==MotionEvent.ACTION_DOWN){
            for(GameImage game: gameImage){
                if(game instanceof FeijiImage){

                    FeijiImage feiji = (FeijiImage)game;

                    //判断条件为真时选中飞机
                    if(feiji.getX()<event.getX()&&
                            feiji.getY()<event.getY()&&
                            feiji.getX()+feiji.getWidth()>event.getX() &&
                            feiji.getY()+feiji.getHeigth()>event.getY()){
                        selectfeiji=feiji;

                    }else{
                        selectfeiji=null;
                    }
                    break;
                }
            }
        }else if(event.getAction()==MotionEvent.ACTION_MOVE){
            //移动鼠标
            if( selectfeiji!=null){
                selectfeiji.setX((int)event.getX()-selectfeiji.getWidth()/2);
                selectfeiji.setY((int)event.getY()-selectfeiji.getHeigth()/2);
            }

        }else if(event.getAction()==MotionEvent.ACTION_UP){
            //松开鼠标
            selectfeiji=null;
        }

        return true;
    }


    private interface GameImage{
        public Bitmap getBitmap();
        public int getX();
        public int getY();

    }

    private class Zidan implements GameImage{

        Bitmap zidan;
        private FeijiImage feiji;
        private int x;
        private int y;

        public Zidan(FeijiImage feiji,Bitmap zidan){
            this.feiji=feiji;
            this.zidan=zidan;

            x=(feiji.getX()+feiji.getWidth()/2)-25;//居中位置
            y=feiji.getY()-zidan.getHeight();
        }

        public Bitmap getBitmap() {
            y-=19;
            if(y<=-10){
                zidans.remove(this);
            }
            return zidan;
        }

        @Override
        public int getX() {
            return x;
        }

        @Override
        public int getY() {
            return y;
        }
    }

    private class DijiImage implements GameImage{

        private Bitmap diren = null;

        private List<Bitmap> bitmaps = new ArrayList<Bitmap>();
        private List<Bitmap> baozhas = new ArrayList<Bitmap>();
        private int x;
        private int y;
        private int width;
        private int height;

        public DijiImage(Bitmap diren,Bitmap baozha){
            this.diren=diren;
            bitmaps.add(Bitmap.createBitmap(diren,0,0,diren.getWidth()/4,diren.getHeight()));
            bitmaps.add(Bitmap.createBitmap(diren,(diren.getWidth()/4)*1,0,diren.getWidth()/4,diren.getHeight()));
            bitmaps.add(Bitmap.createBitmap(diren,(diren.getWidth()/4)*2,0,diren.getWidth()/4,diren.getHeight()));
            bitmaps.add(Bitmap.createBitmap(diren,(diren.getWidth()/4)*3,0,diren.getWidth()/4,diren.getHeight()));

            baozhas.add(Bitmap.createBitmap(baozha,0,0,baozha.getWidth()/4,baozha.getHeight()/2));
            baozhas.add(Bitmap.createBitmap(baozha,(baozha.getWidth()/4)*1,0,baozha.getWidth()/4,baozha.getHeight()/2));
            baozhas.add(Bitmap.createBitmap(baozha,(baozha.getWidth()/4)*2,0,baozha.getWidth()/4,baozha.getHeight()/2));
            baozhas.add(Bitmap.createBitmap(baozha,(baozha.getWidth()/4)*3,0,baozha.getWidth()/4,baozha.getHeight()/2));

            baozhas.add(Bitmap.createBitmap(baozha,0,baozha.getHeight()/2,
                    baozha.getWidth()/4,baozha.getHeight()/2));
            baozhas.add(Bitmap.createBitmap(baozha,(baozha.getWidth()/4)*1,baozha.getHeight()/2,
                    baozha.getWidth()/4,baozha.getHeight()/2));
            baozhas.add(Bitmap.createBitmap(baozha,(baozha.getWidth()/4)*2,baozha.getHeight()/2,
                    baozha.getWidth()/4,baozha.getHeight()/2));
            baozhas.add(Bitmap.createBitmap(baozha,(baozha.getWidth()/4)*3,baozha.getHeight()/2,
                    baozha.getWidth()/4,baozha.getHeight()/2));


            width=diren.getWidth()/4;
            height=diren.getHeight();

            y=-diren.getHeight();
            Random ran = new Random();
            x=ran.nextInt(display_w-(diren.getWidth()/4));

        }

        private int index=0;
        private int num =0;
        @Override
        public Bitmap getBitmap() {
            Bitmap bitmap = bitmaps.get(index);
            //控制切换四个飞机的频率
            if(num == 7){
                index++;
                //当爆炸动画运行一次时候删除爆炸效果
                if(index==8 &&state){
                    gameImage.remove(this);
                }

                if(index == bitmaps.size()){
                    index=0;
                }
                num=0;
            }
            y+=dijiyidong;
            num++;

            if(y>display_h){
                gameImage.remove(this);
            }

            return bitmap;
        }

        //判断敌机是否被击中
        private boolean state=false;
        //受到攻击
        public void shoudaogongji(ArrayList<Zidan> zidans){

            if(!state){
                for(GameImage zidan:(List<GameImage>)zidans.clone()){

                    if(zidan.getX()>x&&zidan.getY()>y
                            &&zidan.getX()<x+width
                            &&zidan.getY()<y+height){
                        //子弹击中敌机了
                        zidans.remove(zidan);
                        state=true;
                        bitmaps=baozhas;
                        fenshu+=10;
                        new SoundPlay(sound_bomb).start();
                        // pool.play(sound_bomb,1,1,1,0,1);
                        break;
                    }
                }
            }
        }

        @Override
        public int getX() {
            return x;
        }

        @Override
        public int getY() {
            return y;
        }

    }



    private class FeijiImage implements GameImage{

        private Bitmap my;
        private int x;
        private int y;
        private int width;
        private int heigth;

        private int getWidth(){
            return width;
        }

        private int getHeigth(){
            return heigth;
        }

        private List<Bitmap> bitmaps = new ArrayList<Bitmap>();


        //控制飞机的类
        private  FeijiImage(Bitmap my){
            this.my=my;
            bitmaps.add(Bitmap.createBitmap(my,0,0,my.getWidth()/4,my.getHeight()));
            bitmaps.add(Bitmap.createBitmap(my,(my.getWidth()/4),0,my.getWidth()/4,my.getHeight()));
            bitmaps.add(Bitmap.createBitmap(my,(my.getWidth()/4)*2,0,my.getWidth()/4,my.getHeight()));
            bitmaps.add(Bitmap.createBitmap(my,(my.getWidth()/4)*3,0,my.getWidth()/4,my.getHeight()));
            //得到战机的高和宽
            width=my.getWidth()/4;
            heigth=my.getHeight();
            x=(display_w - my.getWidth()/4)/2;
            y=display_h-my.getHeight()-30;
        }

        private int index=0;
        private int num =0;
        @Override
        public Bitmap getBitmap() {
            Bitmap bitmap = bitmaps.get(index);
            //控制切换四个飞机的频率
            if(num == 7){
                index++;
                if(index == bitmaps.size()){
                    index=0;
                }
                num=0;
            }
            num++;
            return bitmap;
        }



        @Override
        public int getX() {
            return x;
        }

        @Override
        public int getY() {
            return y;
        }

        public void setY(int y){
            this.y=y;
        }

        public void setX(int x){
            this.x=x;
        }

    }

    //负责背景照片的处理
    private class BeijingImage implements GameImage{
        private Bitmap bg;
        private BeijingImage(Bitmap bg){
            this.bg=bg;
            newBitmap = Bitmap.createBitmap(display_w,display_h, Bitmap.Config.ARGB_8888);
        }

        private Bitmap newBitmap = null;
        private int height = 0;

        public Bitmap getBitmap(){
            Paint p = new Paint();
            Canvas canvas = new Canvas(newBitmap);

            //第一张图片
            canvas.drawBitmap(bg,
                    new Rect(0,0,bg.getWidth(),bg.getHeight()),
                    new Rect(0,height,display_w,display_h+height),p);
            //第二张图片
            canvas.drawBitmap(bg,
                    new Rect(0,0,bg.getWidth(),bg.getHeight()),
                    new Rect(0,-display_h+height,display_w,height),p);

            height++;
            if(height==display_h){
                height=0;
            }
            return newBitmap;
        }



        public int getX(){
            return 0;
        }

        public int getY(){
            return 0;
        }
    }


    private boolean state = false;
    private SurfaceHolder holder;
    private long fenshu=0;
    private int guanka=1;

    private int chudishu=50;        //出敌机的数字
    private int dijiyidong=5;       //敌机的移动
    private int xiayiguan=50;       //下一关分数

    private int[][] sj={
            {1,50,50,4},
            {2,100,45,4},
            {3,150,40,5},
            {4,200,35,5},
            {5,300,30,6},
            {6,400,30,6},
            {7,500,25,7},
            {8,650,25,7},
            {9,900,20,8},
            {10,1200,20,9}};

    private boolean stopState=false;
    public void stop(){
        stopState=true;
    }

    public void start(){
        stopState=false;
        thread.interrupt(); //叫醒线程
    }

    //绘画中心
    public void run() {
        Paint p1 = new Paint();
        int diren_num=0;//
        int zidan_num=0;
        //绘画分数的笔
        Paint p2=new Paint();
        p2.setColor(Color.WHITE);
        p2.setTextSize(30);
        p2.setDither(true);
        p2.setAntiAlias(true);

        try{
            while(state){
                while(stopState){
                    try{
                        Thread.sleep(1000000);
                    }catch(Exception e){
                    }
                }
                if(selectfeiji!=null){
                    if(zidan_num==5){
                        new SoundPlay(sound_shot).start();
                    //    pool.play(sound_shot,1,1,1,0,1);
                        zidans.add(new Zidan(selectfeiji,zidan));
                        zidan_num=0;
                    }
                    zidan_num++;
                }

                Canvas newCanvas = new Canvas(erjihuancun);
                for(GameImage image:(List<GameImage>)gameImage.clone()){
                    if(image instanceof DijiImage){
                        //把子弹告诉敌机
                        ((DijiImage)image).shoudaogongji(zidans);
                    }
                    newCanvas.drawBitmap(image.getBitmap(),image.getX(),image.getY(),p1);
                }

                for(GameImage image:(List<GameImage>)zidans.clone()){
                    newCanvas.drawBitmap(image.getBitmap(),image.getX(),image.getY(),p1);
                }

                //分数
                newCanvas.drawText("分数："+fenshu,0,30,p2);
                newCanvas.drawText("关卡："+guanka,0,60,p2);
                newCanvas.drawText("下一关："+xiayiguan,0,90,p2);

                //升级关卡
                if(sj[guanka-1][1]<=fenshu){

                    chudishu=sj[guanka][2]; //出敌机的数字
                    dijiyidong=sj[guanka][3];
                    fenshu=sj[guanka-1][1]-fenshu;
                    xiayiguan=sj[guanka][1];
                    guanka=sj[guanka][0];
                }

                if(diren_num==150){
                    diren_num=0;
                    gameImage.add(new DijiImage(diren,baozha));
                }
                diren_num++;
                Canvas canvas = holder.lockCanvas();
                canvas.drawBitmap(erjihuancun,0,0,p1);
                holder.unlockCanvasAndPost(canvas);
                Thread.sleep(10);
            }
        }catch(Exception e){
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
    }



    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        state = false;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        //得到屏幕的宽和高
        display_w=width;
        display_h=height;
        init();
        this.holder=holder;
        state = true;
        thread=new Thread(this);
        thread.start();
    }

    Thread  thread=null;
}
