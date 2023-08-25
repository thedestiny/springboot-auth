
```

# 股票列表
https://stock.xueqiu.com/v5/stock/screener/quote/list.json?page=1&size=30&order=desc&orderby=code&order_by=symbol&market=CN&type=sh_sz

# k线图
https://stock.xueqiu.com/v5/stock/chart/kline.json?symbol=SH601919&begin=1691395144420&period=month&type=after&count=-190&indicator=kline,pe,pb,ps,pcf,market_capital,agt,ggt,balance

# lsm tree
https://blog.csdn.net/qq_47159522/article/details/126751195
https://zhuanlan.zhihu.com/p/181498475
https://baijiahao.baidu.com/s?id=1720017754384417208&wfr=spider&for=pc



select `code`, `name`, `week`, `month`, `month3`, `half`, `year`, `since`, `fund_type`, issue, baseline, tracking, fund_size from tb_fund_info where since > 2 and since < 5 and `name` like "%C" and month > -0.01 and week > -0.01 and month3 > -0.01 


```
