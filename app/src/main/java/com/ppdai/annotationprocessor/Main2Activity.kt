package com.ppdai.annotationprocessor

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.ppdai.annotation.Autowired
import com.ppdai.api.PPdaiHelper

class Main2Activity : AppCompatActivity() {

    @Autowired(name = "id")
    @JvmField
    var id: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        PPdaiHelper.inject(this)

        Log.d("ppdai", "id : $id, test")
    }

}