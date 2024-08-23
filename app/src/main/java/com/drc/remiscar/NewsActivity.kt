package com.drc.remiscar

import android.app.Activity
import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.widget.TextView
import com.drc.remiscar.util.WebActivity


class NewsActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)

       findViewById<TextView>(R.id.tv_one).apply {
           paintFlags = paintFlags or Paint.UNDERLINE_TEXT_FLAG
       }.setOnClickListener {
           startActivity(Intent(this@NewsActivity, ExoPlayerActivity::class.java).apply {
               putExtra("VIDEO_URL", "aed")
           })
       }

        findViewById<TextView>(R.id.tv_two).apply {
            paintFlags = paintFlags or Paint.UNDERLINE_TEXT_FLAG
        }.setOnClickListener {
            val intent = Intent(this, WebActivity::class.java)
            intent.putExtra(WebActivity.KEY_WEB_URL, "http://www.hnredcross.gov.cn/content/646840/58/13998981.html")
            startActivity(intent)
        }

        findViewById<TextView>(R.id.tv_three).apply {
            paintFlags = paintFlags or Paint.UNDERLINE_TEXT_FLAG
        }.setOnClickListener {
            val intent = Intent(this, WebActivity::class.java)
            intent.putExtra(WebActivity.KEY_WEB_URL, "https://baijiahao.baidu.com/s?id=1765649681348697532&wfr=spider&for=pc")
            startActivity(intent)
        }

        findViewById<TextView>(R.id.tv_four).apply {
            paintFlags = paintFlags or Paint.UNDERLINE_TEXT_FLAG
        }.setOnClickListener {
            val intent = Intent(this, WebActivity::class.java)
            intent.putExtra(WebActivity.KEY_WEB_URL, "http://www.hnredcross.gov.cn/nograb/646849/55/13889751.html")
            startActivity(intent)
        }

        findViewById<TextView>(R.id.tv_five).apply {
            paintFlags = paintFlags or Paint.UNDERLINE_TEXT_FLAG
        }.setOnClickListener {
            startActivity(Intent(this@NewsActivity, ExoPlayerActivity::class.java).apply {
                putExtra("VIDEO_URL", "xffs")
            })
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}