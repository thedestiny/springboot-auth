

```sql



select code,brief, price, rate,week,update_date,`month`, `year`, fund_size,detail from tb_etf_info order by detail desc , fund_size desc

```


```

mvn jasypt:encrypt-value -Djasypt.encryptor.password="123456" -Djasypt.plugin.value="root123456!"
mvn jasypt:encrypt-value -Djasypt.encryptor.password="test" -Djasypt.plugin.value="root123456"

mvn jasypt:decrypt-value -Djasypt.encryptor.password="123456" -Djasypt.plugin.value="密文"

--jasypt.encryptor.password=sec123456
https://blog.csdn.net/weixin_48903815/article/details/142494436

```
