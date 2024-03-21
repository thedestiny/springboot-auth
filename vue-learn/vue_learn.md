```
<div class="aliform" v-html="aliform"></div>
   data() {
	    return {
	      aliform: "",
	    };
  	},
  	methods: {
		async iosAlipay(orderCode) {
		    // 等待结果响应 
      		let data = await requestAlipay(orderCode);
      		if (data.code == 200) {
        		this.aliform = data.data;  //data.data就是支付宝返回给你的form,获取到的表单内容,具体样子可见上面的图片
        		this.$nextTick(() => {
        			// 获取订单详情来轮询支付结果
          			console.log(document.forms);  //跳转之前,可以先打印看看forms,确保后台数据和forms正确,否则，可能会出现一些奇奇怪怪的问题
          			document.forms[0].submit();  //重点--这个才是跳转页面的核心,获取第一个表单并提交
        		});
      		}
    	},


```


```


const newWindow = window.open('', '_self');  
newWindow.document.write(result);
newWindow.focus();

------------------

const div = document.createElement('formdiv');
div.innerHTML = result;
document.body.appendChild(div);
document.forms[0].submit();
div.remove();

document.forms['cashierSubmit'].setAttribute('target', '_self');
document.forms['cashierSubmit'].submit();
```
