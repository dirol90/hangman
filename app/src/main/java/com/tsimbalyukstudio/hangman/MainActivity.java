package com.tsimbalyukstudio.hangman;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {
    int bestScore = 0;
    int noLooseScore = 0;
    SharedPreferences sPref;
    ADDS adds;

    @Override
    protected void onStop() {
        super.onStop();
        try {
            mediaPlayer.pause();
        } catch (Exception e) {
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        sPref = getPreferences(MODE_PRIVATE);
        bestScore = sPref.getInt("bestScore", 0);
        noLooseScore = sPref.getInt("noLooseScore", 0);
        mediaPlayer.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sPref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putInt("bestScore", bestScore);
        ed.putInt("noLooseScore", noLooseScore);
        ed.commit();
        try {
            mediaPlayer.pause();
        } catch (Exception e) {
        }
    }

    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        sPref = getPreferences(MODE_PRIVATE);
        bestScore = sPref.getInt("bestScore", 0);
        noLooseScore = sPref.getInt("noLooseScore", 0);
        TextView tbs = (TextView) findViewById(R.id.textBestScore);
        TextView ts = (TextView) findViewById(R.id.textScore);
        tbs.setText(tbs.getText() + " " + bestScore);
        ts.setText(ts.getText() + " " + noLooseScore);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        try {
            mediaPlayer = MediaPlayer.create(this, R.raw.hamgman_music);
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
        } catch (Exception e) {
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                adds = new ADDS (MainActivity.this);
                adds.showBanner(MainActivity.this);
            }
        }).start();
    }

    public Integer[] allLetters = {
            R.drawable.a1, R.drawable.a2,
            R.drawable.a3, R.drawable.a4,
            R.drawable.a5, R.drawable.a6,
            R.drawable.a8,
            R.drawable.a9, R.drawable.a10,
            R.drawable.a11, R.drawable.a12,
            R.drawable.a13, R.drawable.a14,
            R.drawable.a15, R.drawable.a16,
            R.drawable.a17, R.drawable.a18,
            R.drawable.a19, R.drawable.a20,
            R.drawable.a21, R.drawable.a22,
            R.drawable.a23, R.drawable.a24,
            R.drawable.a25, R.drawable.a26,
            R.drawable.a27, R.drawable.a28,
            R.drawable.a29, R.drawable.a30,
            R.drawable.a31, R.drawable.a32,
            R.drawable.a33
    };

    EditText editWord;
    EditText editTheme;

    public void playVSplayer(View view) {
        setContentView(R.layout.activity_main_player);
        editWord = (EditText) findViewById(R.id.editWord);
        editTheme = (EditText) findViewById(R.id.editTheme);
        new Thread(new Runnable() {
            @Override
            public void run() {
                adds = new ADDS (MainActivity.this);
                adds.showBanner(MainActivity.this);
            }
        }).start();

    }


    public void playVSpc(View view) {
        setContentView(R.layout.activity_theme);
        new Thread(new Runnable() {
            @Override
            public void run() {
                adds = new ADDS (MainActivity.this);
                adds.showBanner(MainActivity.this);
            }
        }).start();
    }

    int theme;

    public void theme(View view) {
        theme = Integer.parseInt(view.getTag().toString());
        setContentView(R.layout.activity_main_pc);
        new Thread(new Runnable() {
            @Override
            public void run() {
                adds = new ADDS (MainActivity.this);
                adds.showBanner(MainActivity.this);
            }
        }).start();
    }

    int tillWin = 0;
    int lose = 0;

    public void playCustomGame(View view) {
        final GameActivity ga = new GameActivity(editWord.getText().toString(), editTheme.getText().toString());
        setContentView(R.layout.activity_game);

        new Thread(new Runnable() {
            @Override
            public void run() {
                adds = new ADDS (MainActivity.this);
                adds.showBanner(MainActivity.this);
            }
        }).start();

        if (iv != null) {
            iv.setBackgroundResource(0);
            arBackNow = 0;
        }
        TextView tv = (TextView) findViewById(R.id.textTheme);
        tv.setText(tv.getText() + " " + ga.tempTheme);
        tillWin = ga.parceByLetter().length;


        final GridView gridWord = (GridView) findViewById(R.id.wordGrid);
        gridWord.setNumColumns(ga.parceByLetter().length);

        GridView gridLetter = (GridView) findViewById(R.id.letterGrid);
        //Display d = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        //int width = d.getWidth();
        gridLetter.setNumColumns(8);
        try {
            gridLetter.setGravity(View.TEXT_ALIGNMENT_CENTER);
        } catch (Exception e) {

        }
        gridLetter.setAdapter(new ImageLetterAdapter(this));

        gridLetter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                if (v.isEnabled()) {
                    if (ga.checkWord(position) && tillWin != 0) {
                        v.setBackgroundResource(R.drawable.right);
                        for (int x = 0; x < ga.parceByLetter().length; x++) {
                            if (ga.arCorrectWord[x] == 1) {
                                --tillWin;
                                ImageView iv = (ImageView) gridWord.getChildAt(x);
                                iv.setImageResource(allLetters[position]);
                            }
                        }
                        v.setEnabled(false);
                    } else {
                        ++lose;
                        change();
                        v.setBackgroundResource(R.drawable.wrong);
                        v.setEnabled(false);
                        if (lose == 10) {
                            toResult(false, 0, ga);
                        }
                    }
                    if (tillWin == 0) {
                        toResult(true, 0, ga);
                    }
                }
            }
        });

        try {
            gridWord.setGravity(View.TEXT_ALIGNMENT_CENTER);
        } catch (Exception e) {

        }
        gridWord.setAdapter(new ImageWordAdapter(this, ga.parceByLetter().length));


    }

    int arBackGround[] = {
            R.drawable.back1, R.drawable.back2,
            R.drawable.back3, R.drawable.back4,
            R.drawable.back5, R.drawable.back6,
            R.drawable.back7, R.drawable.back8,
            R.drawable.back9, R.drawable.back10
    };


    int arBackNow = 0;
    ImageView iv;
    ImageView ih;

    public void change() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        ih = (ImageView) findViewById(R.id.hangman);
                        iv = (ImageView) findViewById(R.id.imageHang);
                        if (arBackNow < arBackGround.length) {
                            ih.setX(ih.getX() - 50);
                            iv.setBackgroundResource(arBackGround[arBackNow++]);
                        } else {
                            arBackNow = 0;
                        }
                    }
                });
            }
        };
        thread.start();
    }

    public void light(View view) {
        setContentView(R.layout.activity_game);
        final GameActivity ga = new GameActivity(0, theme);

        new Thread(new Runnable() {
            @Override
            public void run() {
                adds = new ADDS (MainActivity.this);
                adds.showBanner(MainActivity.this);
            }
        }).start();

        if (iv != null) {
            iv.setBackgroundResource(0);
            arBackNow = 0;
        }
        TextView tv = (TextView) findViewById(R.id.textTheme);
        tv.setText(tv.getText() + " " + ga.tempTheme);
        tillWin = ga.parceByLetter().length;


        final GridView gridWord = (GridView) findViewById(R.id.wordGrid);
        gridWord.setNumColumns(ga.parceByLetter().length);

        GridView gridLetter = (GridView) findViewById(R.id.letterGrid);
        //Display d = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        //int width = d.getWidth();
        gridLetter.setNumColumns(8);
        try {
            gridLetter.setGravity(View.TEXT_ALIGNMENT_CENTER);
        } catch (Exception e) {

        }
        gridLetter.setAdapter(new ImageLetterAdapter(this));

        gridLetter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                if (v.isEnabled()) {
                    if (ga.checkWord(position) && tillWin != 0) {
                        v.setBackgroundResource(R.drawable.right);
                        for (int x = 0; x < ga.parceByLetter().length; x++) {
                            if (ga.arCorrectWord[x] == 1) {
                                --tillWin;
                                ImageView iv = (ImageView) gridWord.getChildAt(x);
                                iv.setImageResource(allLetters[position]);
                            }
                        }
                        v.setEnabled(false);
                    } else {
                        ++lose;
                        change();
                        v.setBackgroundResource(R.drawable.wrong);
                        v.setEnabled(false);
                        if (lose == 10) {
                            toResult(false, 1, ga);
                            noLooseScore = 0;
                        }
                    }
                    if (tillWin == 0) {
                        toResult(true, 1, ga);
                        noLooseScore++;
                        if (bestScore < noLooseScore) bestScore = noLooseScore;
                    }
                }
            }
        });

        try {
            gridWord.setGravity(View.TEXT_ALIGNMENT_CENTER);
        } catch (Exception e) {

        }
        gridWord.setAdapter(new ImageWordAdapter(this, ga.parceByLetter().length));

    }

    public void medium(View view) {
        setContentView(R.layout.activity_game);
        final GameActivity ga = new GameActivity(1, theme);

        new Thread(new Runnable() {
            @Override
            public void run() {
                adds = new ADDS (MainActivity.this);
                adds.showBanner(MainActivity.this);
            }
        }).start();

        if (iv != null) {
            iv.setBackgroundResource(0);
            arBackNow = 0;
        }
        TextView tv = (TextView) findViewById(R.id.textTheme);
        tv.setText(tv.getText() + " " + ga.tempTheme);
        tillWin = ga.parceByLetter().length;


        final GridView gridWord = (GridView) findViewById(R.id.wordGrid);
        gridWord.setNumColumns(ga.parceByLetter().length);

        GridView gridLetter = (GridView) findViewById(R.id.letterGrid);
        //Display d = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        //int width = d.getWidth();
        gridLetter.setNumColumns(8);
        try {
            gridLetter.setGravity(View.TEXT_ALIGNMENT_CENTER);
        } catch (Exception e) {

        }
        gridLetter.setAdapter(new ImageLetterAdapter(this));

        gridLetter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                if (v.isEnabled()) {
                    if (ga.checkWord(position) && tillWin != 0) {
                        v.setBackgroundResource(R.drawable.right);
                        for (int x = 0; x < ga.parceByLetter().length; x++) {
                            if (ga.arCorrectWord[x] == 1) {
                                --tillWin;
                                ImageView iv = (ImageView) gridWord.getChildAt(x);
                                iv.setImageResource(allLetters[position]);
                            }
                        }
                        v.setEnabled(false);
                    } else {
                        ++lose;
                        change();
                        v.setBackgroundResource(R.drawable.wrong);
                        v.setEnabled(false);
                        if (lose == 10) {
                            toResult(false, 2, ga);
                            noLooseScore = 0;
                        }
                    }
                    if (tillWin == 0) {
                        toResult(true, 2, ga);
                        noLooseScore++;
                        if (bestScore < noLooseScore) bestScore = noLooseScore;
                    }
                }
            }
        });

        try {
            gridWord.setGravity(View.TEXT_ALIGNMENT_CENTER);
        } catch (Exception e) {

        }
        gridWord.setAdapter(new ImageWordAdapter(this, ga.parceByLetter().length));

    }

    public void hard(View view) {
        setContentView(R.layout.activity_game);
        final GameActivity ga = new GameActivity(2, theme);

        new Thread(new Runnable() {
            @Override
            public void run() {
                adds = new ADDS (MainActivity.this);
                adds.showBanner(MainActivity.this);
            }
        }).start();

        if (iv != null) {
            iv.setBackgroundResource(0);
            arBackNow = 0;
        }
        TextView tv = (TextView) findViewById(R.id.textTheme);
        tv.setText(tv.getText() + " " + ga.tempTheme);
        tillWin = ga.parceByLetter().length;


        final GridView gridWord = (GridView) findViewById(R.id.wordGrid);
        gridWord.setNumColumns(ga.parceByLetter().length);

        GridView gridLetter = (GridView) findViewById(R.id.letterGrid);
        //Display d = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        //int width = d.getWidth();
        gridLetter.setNumColumns(8);
        try {
            gridLetter.setGravity(View.TEXT_ALIGNMENT_CENTER);
        } catch (Exception e) {

        }
        gridLetter.setAdapter(new ImageLetterAdapter(this));

        gridLetter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                if (v.isEnabled()) {
                    if (ga.checkWord(position) && tillWin != 0) {
                        v.setBackgroundResource(R.drawable.right);
                        for (int x = 0; x < ga.parceByLetter().length; x++) {
                            if (ga.arCorrectWord[x] == 1) {
                                --tillWin;
                                ImageView iv = (ImageView) gridWord.getChildAt(x);
                                iv.setImageResource(allLetters[position]);
                            }
                        }
                        v.setEnabled(false);
                    } else {
                        ++lose;
                        change();
                        v.setBackgroundResource(R.drawable.wrong);
                        v.setEnabled(false);
                        if (lose == 10) {
                            toResult(false, 3, ga);
                            noLooseScore = 0;
                        }
                    }
                    if (tillWin == 0) {
                        toResult(true, 3, ga);
                        noLooseScore++;
                        if (bestScore < noLooseScore) bestScore = noLooseScore;
                    }
                }
            }
        });

        try {
            gridWord.setGravity(View.TEXT_ALIGNMENT_CENTER);
        } catch (Exception e) {

        }
        gridWord.setAdapter(new ImageWordAdapter(this, ga.parceByLetter().length));

    }

    public void rate(View view) {
        final String appPackageName = getPackageName();
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    public void music(View view) {
        try {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            } else {
                mediaPlayer.start();
            }
        } catch (Exception e) {
        }
    }

    public void exit(View v) {
        sPref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putInt("bestScore", bestScore);
        ed.putInt("noLooseScore", noLooseScore);
        ed.commit();
        try {
            mediaPlayer.stop();
            mediaPlayer.release();
        } catch (Exception e) {
        }
        finish();
    }


    /////////
    public void toResult(boolean b, final int i, GameActivity ga) {
        lose = 0;

        LayoutInflater factory = LayoutInflater.from(this);
        final View deleteDialogView = factory.inflate(R.layout.activity_result, null);
        final AlertDialog deleteDialog = new AlertDialog.Builder(this).create();
        deleteDialog.setView(deleteDialogView);
        TextView tv = (TextView) deleteDialogView.findViewById(R.id.result);
        TextView tw = (TextView) deleteDialogView.findViewById(R.id.winWord);
        if (b) {
            tv.setText("ТЫ ПОБЕДИЛ!");
            tv.setTextColor(Color.YELLOW);
            tw.setText(tw.getText() + " " + ga.tempWord.toUpperCase());
        } else {
            tv.setText("ТЫ ПОВЕШЕН!");
            tv.setTextColor(Color.RED);
            if (i == 0) {
                tw.setText(tw.getText() + " " + ga.tempWord.toUpperCase());
            } else {
                tw.setText("");
            }
        }
        deleteDialogView.findViewById(R.id.retry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (i) {
                    case 0:
                        menu();
                        break;
                    case 1:
                        light(v);
                        break;
                    case 2:
                        medium(v);
                        break;
                    case 3:
                        hard(v);
                    default:
                        break;
                }
                deleteDialog.dismiss();
            }
        });
        deleteDialogView.findViewById(R.id.menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menu();
                deleteDialog.dismiss();
            }
        });
        adds.showInterstitial(this);
        deleteDialog.show();
    }


    public void menu() {
        setContentView(R.layout.activity_main);

        new Thread(new Runnable() {
            @Override
            public void run() {
                adds = new ADDS (MainActivity.this);
                adds.showBanner(MainActivity.this);
            }
        }).start();

        TextView tbs = (TextView) findViewById(R.id.textBestScore);
        TextView ts = (TextView) findViewById(R.id.textScore);
        tbs.setText(tbs.getText() + " " + bestScore);
        ts.setText(ts.getText() + " " + noLooseScore);
        sPref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putInt("bestScore", bestScore);
        ed.putInt("noLooseScore", noLooseScore);
        ed.commit();
    }


    @Override
    public void onBackPressed() {
        setContentView(R.layout.activity_main);

        new Thread(new Runnable() {
            @Override
            public void run() {
                adds = new ADDS (MainActivity.this);
                adds.showBanner(MainActivity.this);
            }
        }).start();

        sPref = getPreferences(MODE_PRIVATE);
        bestScore = sPref.getInt("bestScore", 0);
        noLooseScore = sPref.getInt("noLooseScore", 0);
        TextView tbs = (TextView) findViewById(R.id.textBestScore);
        TextView ts = (TextView) findViewById(R.id.textScore);
        tbs.setText(tbs.getText() + " " + bestScore);
        ts.setText(ts.getText() + " " + noLooseScore);
    }


    ////////////////////////////////////////////////////
    public class ImageLetterAdapter extends BaseAdapter {
        private Context mContext;

        public ImageLetterAdapter(Context c) {
            mContext = c;
        }

        public int getCount() {
            return mThumbIds.length;
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        // create a new ImageView for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) {
                // if it's not recycled, initialize some attributes
                imageView = new ImageView(mContext);
                imageView.setLayoutParams(new GridView.LayoutParams(150, 150));
                imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                imageView.setPadding(0, 0, 0, 64);
            } else {
                imageView = (ImageView) convertView;
            }

            imageView.setImageResource(mThumbIds[position]);
            return imageView;
        }

        // references to our images
        public Integer[] mThumbIds = {
                R.drawable.a1, R.drawable.a2,
                R.drawable.a3, R.drawable.a4,
                R.drawable.a5, R.drawable.a6,
                R.drawable.a8,
                R.drawable.a9, R.drawable.a10,
                R.drawable.a11, R.drawable.a12,
                R.drawable.a13, R.drawable.a14,
                R.drawable.a15, R.drawable.a16,
                R.drawable.a17, R.drawable.a18,
                R.drawable.a19, R.drawable.a20,
                R.drawable.a21, R.drawable.a22,
                R.drawable.a23, R.drawable.a24,
                R.drawable.a25, R.drawable.a26,
                R.drawable.a27, R.drawable.a28,
                R.drawable.a29, R.drawable.a30,
                R.drawable.a31, R.drawable.a32,
                R.drawable.a33
        };
    }

    ////////////////////////////////////////////////////
    public class ImageWordAdapter extends BaseAdapter {
        private Context mContext;

        public ImageWordAdapter(Context c, int length) {
            mContext = c;
            mThumbIds = new Integer[length];
            for (int x = 0; x < length; x++) {
                mThumbIds[x] = R.drawable.free;
            }
        }

        public int getCount() {
            return mThumbIds.length;
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        // create a new ImageView for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) {
                // if it's not recycled, initialize some attributes
                imageView = new ImageView(mContext);
                imageView.setLayoutParams(new GridView.LayoutParams(150, 150));
                imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                imageView.setPadding(2, 0, 2, 64);
            } else {
                imageView = (ImageView) convertView;
            }

            imageView.setImageResource(mThumbIds[position]);
            return imageView;
        }

        // references to our images
        private Integer[] mThumbIds;
    }
    //////////////////////////////////


}
