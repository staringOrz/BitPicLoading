package cn.xijian.bitpicloading

import android.content.res.AssetManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import cn.xijian.bitpicloading.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val bigPic = binding.bigPic
        var inputStream = assets.open("test.jpeg")
        bigPic.addImage(inputStream)
    }
}