package com.drc.remiscar.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.ViewUtils
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat.NestedScrollType
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.amap.api.maps.model.BaseHoleOptions
import com.chad.library.adapter4.BaseQuickAdapter
import com.chad.library.adapter4.viewholder.DataBindingHolder
import com.chad.library.adapter4.viewholder.QuickViewHolder
import com.drc.remiscar.News
import com.drc.remiscar.R
import com.drc.remiscar.databinding.NewItemBinding

class NewAdapter(mutableList: MutableList<News>): BaseQuickAdapter<News, QuickViewHolder>(mutableList) {
    override fun onBindViewHolder(holder: QuickViewHolder, position: Int, item: News?) {
        holder.setText(R.id.tv_view, item?.text)
        when(position) {
            0 -> {
                holder.setBackgroundResource(R.id.iv_img, R.mipmap.aed)
                holder.setText(R.id.tv_desc, "为切实提高党员干部的急救意识和急救技能，增强突发事件、意外伤害来临时的应急救护能力，发挥党员干部“会急救”“敢于救”的示范带头作用，根据省委党校（湖南行政学院）课程安排，在第25个“世界急救日”到来之际，9月14日下午，湖南省红十字应急救护培训走进省委党校（湖南行政学院）省直处干班，为省直单位50余名领导干部进行应急救护专题培训。")
            }
            1 -> {
                holder.setBackgroundResource(R.id.iv_img, R.mipmap.rexue)
                holder.setText(R.id.tv_desc, "2024年6月14日是世界献血者日20周年。无偿献血，挽救生命，是爱心最直接的体现和最光荣的行动。2023年，湘潭再度荣膺“全国无偿献血先进市”，亦是连续16年第八次蝉联“全国无偿献血先进市”殊荣。至今，湘潭市累计已有51万余人次的市民无偿献血，献血总量近175吨，充分展现了伟人故里爱心满满的热血风采。")
            }
            2 -> {
                holder.setBackgroundResource(R.id.iv_img, R.mipmap.dizhen)
                holder.setText(R.id.tv_desc, "2023年5月12日是第15个全国防灾减灾日，今年全国防灾减灾日主题为“防范灾害风险护航高质量发展”。我国是世界上自然灾害最为严重的国家之一，地震、火灾、洪涝、泥石流、台风……当极端性自然灾害灾害天气来临时，我们应该掌握哪些自救互救知识？一组图带你了解9种自然灾害防灾减灾要点。")
            }
            3 -> {
                holder.setBackgroundResource(R.id.iv_img, R.mipmap.jijiu)
                holder.setText(R.id.tv_desc, "2024年5月8日，是第77个“世界红十字日”，也是中国红十字会成立120周年。近日，湘乡市红十字会开展以“人道精神 生生不息”为主题的公益众筹、应急救护公益培训等系列活动。5月6日至9日，湘乡市红十字会组织会员单位开展“5·8人道公益日”众筹活动，共2650人参与，捐款总额将近20000元。善款将用于扶助困难群众，助力乡村振兴。" )
            }
            4 -> {
                holder.setBackgroundResource(R.id.iv_img, R.mipmap.xffs)
                holder.setText(R.id.tv_desc, "近日，永州市红十字会联合冷水滩区卫健局、区红十字会开展“夜市地摊”应急救护知识普及活动，充分利用群众休闲时的碎片化时间普及应急救护知识，进一步提高广大群众应急救护自救能力。")
            }
        }
    }

    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): QuickViewHolder {
        return QuickViewHolder(R.layout.new_item, parent)
    }
}