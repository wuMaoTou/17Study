package com.mt.ledou.service

import android.text.TextUtils
import com.mt.ledou.Contacts
import com.mt.ledou.EventCenter
import com.mt.ledou.Request
import com.mt.ledou.utils.LogUtils
import org.greenrobot.eventbus.EventBus

/**
 * Created by wuchundu on 18-1-26.
 * 王者争霸
 */
class QualifyingService {

    fun init() {
        try {
            personal()
//            teamqua()
//            shop()
        } catch (e: Exception) {
            e.printStackTrace()
            EventBus.getDefault().post(EventCenter(Contacts.CATCH_EVENT))
        }
    }

    /**
     * 王者组队赛
     */
    private fun teamqua() {

        val respone = Request.request("teamqua", "cmd=teamqua")
        if (TextUtils.isEmpty(respone.text)) {
            return
        }
        LogUtils.d("[王者组队赛-初始化]---" + respone.text)
        val free_times = respone.jsonObject.getInt("free_times")
        LogUtils.d("[王者组队赛-初始化]目前还有${free_times}次免费机会")
        for (i in 0..free_times) {
            val matchResponse = Request.request("teamqua", "cmd=teamqua&op=match")
            LogUtils.d("[王者组队赛-匹配对手]---" + matchResponse.text)
            Thread.sleep(1000)
            val fightResponse = Request.request("teamqua", "cmd=teamqua&op=fight&userlist=0|1|2")
            LogUtils.d("[王者组队赛-对战]---" + fightResponse.text)
            Thread.sleep(1000)
        }

        for (i in 0..5) {
            val rewardResponse = Request.request("teamqua", "cmd=teamqua&op=reward&idx=${i}")
            LogUtils.d("[王者组队赛-领奖]---" + rewardResponse.text)
            Thread.sleep(300)
        }
    }


    /**
     * 个人争霸赛
     */
    private fun personal() {

        val respone = Request.request("qualifying", "cmd=qualifying")
        if (TextUtils.isEmpty(respone.text)) {
            return
        }
        LogUtils.d("[个人争霸赛-初始化]---" + respone.text)
        val free_times = respone.jsonObject.getInt("free_times")
        LogUtils.d("[个人争霸赛-初始化]目前还有${free_times}次免费机会")

        for (i in 0..free_times) {
            val fightResponse = Request.request("qualifying", "cmd=qualifying&op=fight")
            LogUtils.d("[个人争霸赛-PK]---" + fightResponse.text)
            Thread.sleep(1000)
        }

        for (i in 0..5) {
            val rewardResponse = Request.request("qualifying", "cmd=qualifying&op=reward&idx=${i}")
            LogUtils.d("[个人争霸赛-领奖]---" + rewardResponse.text)
            Thread.sleep(300)
        }
    }

    /**
     * 个人争霸赛-王者商城
     * @return bool
     */
//    public function shop() {
//        if (!$this->useCurlPost("shop", "cmd=shop&shoptype=6", $initResponse)) {
//        return false;
//    }
//        $this->logInfo("[个人争霸赛-王者商城-初始化]", $initResponse);
//        $kingMedal = array_get($initResponse, 'king_medal', 0);
//        $goodArr = [100076, 100093, 100049, 100050, 100051, 100052];
//        $commodityInfo = array_get($initResponse, 'commodity_info', []);
//        $tmpArr = [];
//        foreach($commodityInfo as $item) {
//        if (in_array($item['id'], $goodArr)) {
//        $tmpArr[$item['id']] = $item;
//    }
//    }
//        foreach($goodArr as $value) {
//        if (!isset($tmpArr[$value])) {
//        continue;
//    }
//        $goodInfo = $tmpArr[$value];
//        $remain = $goodInfo['remain'];
//        if ($remain == 0) {
//        continue;
//    }
//        $price = $goodInfo['price'];
//        $num = intval($kingMedal / $price);
//        if ($num <= 0) {
//        continue;
//    }
//
//        if ($num > $remain) {
//        $num = $remain;
//    }
//        $totalPrice = $num * $price;
//        $postData = "cmd=shop&subtype=1&num={$num}&id={$value}&price={$totalPrice}";
//        if (!$this->useCurlPost("shop", $postData, $initResponse)) {
//        break;
//    }
//        $this->logInfo("[个人争霸赛-王者商城-兑换]", $initResponse);
//        $kingMedal -= $totalPrice;
//    }
//    }
}