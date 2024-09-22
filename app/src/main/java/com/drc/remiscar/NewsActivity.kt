package com.drc.remiscar

import android.app.Activity
import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter4.BaseQuickAdapter
import com.drc.remiscar.adapter.NewAdapter
import com.drc.remiscar.util.WebActivity


class NewsActivity : Activity() {


    private val adapter by lazy {
        NewAdapter(mutableListOf(
            News(img = R.mipmap.aed, text = "AED急救"),
            News(img = R.mipmap.rexue, text = "热血汇成大爱，莲城传递真情——湘潭市近年来无偿献血事业发展掠影"),
            News(img = R.mipmap.dizhen, text = "这9种自然灾害来临时，如何自救互救？"),
            News(img = R.mipmap.jijiu, text = "湘乡组织开展系列“世界红十字日”主题活动"),
            News(img = R.mipmap.xffs, text = "心肺复苏操作视频"),
            ))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)


      adapter.setOnItemClickListener { adapter, view, position ->
          when(position) {
              0 -> {
                  startActivity(Intent(this@NewsActivity, ExoPlayerActivity::class.java).apply {
                      putExtra("VIDEO_URL", "aed")
                  })
              }

              1 -> {
                  val intent = Intent(this, WebActivity::class.java)
                  intent.putExtra(WebActivity.KEY_WEB_URL, "http://www.hnredcross.gov.cn/content/646840/58/13998981.html")
                  startActivity(intent)
              }

              2 -> {
                  val intent = Intent(this, WebActivity::class.java)
                  intent.putExtra(WebActivity.KEY_WEB_URL, "https://baijiahao.baidu.com/s?id=1765649681348697532&wfr=spider")
                  startActivity(intent)
              }

              3 -> {
                  val intent = Intent(this, WebActivity::class.java)
                  intent.putExtra(WebActivity.KEY_WEB_URL, "http://www.hnredcross.gov.cn/nograb/646849/55/13889751.html")
                  startActivity(intent)
              }

              4 -> {
                  startActivity(Intent(this@NewsActivity, ExoPlayerActivity::class.java).apply {
                      putExtra("VIDEO_URL", "xffs")
                  })
              }
          }
      }

        findViewById<RecyclerView>(R.id.recycleView).apply {
            layoutManager = LinearLayoutManager(this@NewsActivity)
            adapter = this@NewsActivity.adapter
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}