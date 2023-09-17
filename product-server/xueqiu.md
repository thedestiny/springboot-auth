
```

# 股票列表
https://stock.xueqiu.com/v5/stock/screener/quote/list.json?page=1&size=30&order=desc&orderby=code&order_by=symbol&market=CN&type=sh_sz

# k线图
https://stock.xueqiu.com/v5/stock/chart/kline.json?symbol=SH601919&begin=1691395144420&period=month&type=after&count=-190&indicator=kline,pe,pb,ps,pcf,market_capital,agt,ggt,balance

# lsm tree
https://blog.csdn.net/qq_47159522/article/details/126751195
https://zhuanlan.zhihu.com/p/181498475
https://baijiahao.baidu.com/s?id=1720017754384417208&wfr=spider&for=pc

002019


select `code`, `name`, `week`, `month`, `month3`, `half`, `year`, `since`, `fund_type`, issue, baseline, tracking, fund_size from tb_fund_info where since > 2 and since < 5 and `name` like "%C" and month > -0.01 and week > -0.01 and month3 > -0.01 

0-入账;1-出账;2-冲正;3-销毁;4-增加结算能力;5-减少结算能力;
6-增加手续费;7-减少手续费;
8-分发;9-分发撤回出账;10-分发撤回入账;
11-离职转账出;12-离职转账入


```
