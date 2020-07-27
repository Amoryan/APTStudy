package com.ppdai.annotationprocessor

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ppdai.annotation.Autowired
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        click.setOnClickListener {
            val intent = Intent(this, Main2Activity::class.java)
            intent.putExtra("id", 1000L)
            startActivity(intent)
        }
    }
}