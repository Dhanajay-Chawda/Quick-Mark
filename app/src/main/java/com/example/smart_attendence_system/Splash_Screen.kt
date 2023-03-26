package com.example.smart_attendence_system

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView

class Splash_Screen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        val topAnim: Animation;
        val bottomAnim: Animation;
        val image: ImageView;
        val logo: TextView;
        val slogan: TextView;

        /*supportActionBar?.hide()
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )*/

        //Hide the actionBar
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        actionBar?.hide()
        supportActionBar?.hide()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        //Animations
        topAnim= AnimationUtils.loadAnimation(this,R.anim.top_animation);
        bottomAnim= AnimationUtils.loadAnimation(this,R.anim.bottom_animation);

        //Hooks
        image =findViewById(R.id.imageView);
        logo =findViewById(R.id.textView);

        image.setAnimation(topAnim);
        logo.setAnimation(bottomAnim);

        //come second Acivity
        Handler().postDelayed({
            val intent= Intent(this@Splash_Screen,login_page::class.java)
            startActivity(intent)
            finish()
        },3000)



    }
}