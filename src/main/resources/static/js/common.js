openId = "";
nickName = "";
headUrl = "";
role = "";
bind = "";
$("#quit").click(function(){
//	$.cookie("openId", "", {expires: -1});
//	$.cookie("nickName", "", {expires: -1});
//	$.cookie("headImg", "", {expires: -1});
//	$.cookie("role", "", {expires: -1});
	window.location.href = "/wechat/quit";
});
$("#login").click(function(){
	$("#alertbd").css('display','none');
	$("#alertZh").css('display','block');
});

$("#xituzhiku, #xituzhiku11, #xituzhiku1, #xituzhiku2").click(function(){
	if(window.role!="" && window.role != "null") {
		window.location.href = "/patent/agmount";
	}else {
		alert("请先登录")
		$("#login").click()
	}
});

$("#xituzhiku3").click(function(){
	if(window.role!="" && window.role != "null") {
		window.location.href = "/expert/list";
	}else {
		alert("请先登录")
		$("#login").click()
	}
});
$("#xituzhiku4").click(function(){
	if(window.role!="" && window.role != "null") {
		window.location.href = "/org/list";
	}else {
		alert("请先登录")
		$("#login").click()
	}
});
$("#xituzhiku5").click(function(){
	if(window.role!="" && window.role != "null") {
		window.location.href = "/patent/agcountry";
	}else {
		alert("请先登录")
		$("#login").click()
	}
});
$("#xituzhiku6").click(function(){
	if(window.role!="" && window.role != "null") {
		window.location.href = "/report/list?front=0";
	}else {
		alert("请先登录")
		$("#login").click()
	}
});

$("#cailiaojisuan, #cailiaojisuan11, #cailiaojisuan1, #cailiaojisuan2").click(function(){
	if(window.role!="" && window.role != "null") {
		window.location.href = "/material/calculate";
	}else {
		alert("请先登录")
		$("#login").click()
	}
});
$("#chanyejiance, #chanyejiance11,#chanyejiance1, #chanyejiance2").click(function(){
	if(window.role!="" && window.role != "null") {
		window.location.href = "/jiance/jiancelist";
	}else {
		alert("请先登录")
		$("#login").click()
	}
});

$("#chanyejiance3").click(function(){
	if(window.role!="" && window.role != "null") {
		window.location.href = "/project/list?front=0";
	}else {
		alert("请先登录")
		$("#login").click()
	}
});

$("#zhishifuwu, #zhishifuwu11, #zhishifuwu1, #zhishifuwu2").click(function(){
	if(window.role!="" && window.role != "null") {
		window.location.href = "/zhishifuwu/zhishifuwu";
	}else {
		alert("请先登录")
		$("#login").click()
	}
});
$("#zhishifuwu3").click(function(){
	if(window.role!="" && window.role != "null") {
		window.location.href = "/zhishifuwu/chanyeqingbao";
	}else {
		alert("请先登录")
		$("#login").click()
	}
});
$("#zhishifuwu4").click(function(){
	if(window.role!="" && window.role != "null") {
		window.location.href = "/zhishifuwu/zhishichanquan";
	}else {
		alert("请先登录")
		$("#login").click()
	}
});

$("#zhishifuwu5").click(function(){
	if(window.role!="" && window.role != "null") {
		window.location.href = "/zhishifuwu/juecezixun";
	}else {
		alert("请先登录")
		$("#login").click()
	}
});

$("#y").click(function(){
	if(window.role!="" && window.role != "null") {
		window.location.href = "/yuansu/xiangqing?name=钇";
	}else {
		alert("请先登录")
		$("#login").click()
	}
});
$("#sc").click(function(){
	if(window.role!="" && window.role != "null") {
		window.location.href = "/yuansu/xiangqing?name=钪";
	}else {
		alert("请先登录")
		$("#login").click()
	}
});
$("#lu").click(function(){
	if(window.role!="" && window.role != "null") {
		window.location.href = "/yuansu/xiangqing?name=镥";
	}else {
		alert("请先登录")
		$("#login").click()
	}
});
$("#yb").click(function(){
	if(window.role!="" && window.role != "null") {
		window.location.href = "/yuansu/xiangqing?name=镱";
	}else {
		alert("请先登录")
		$("#login").click()
	}
});
$("#tm").click(function(){
	if(window.role!="" && window.role != "null") {
		window.location.href = "/yuansu/xiangqing?name=铥";
	}else {
		alert("请先登录")
		$("#login").click()
	}
});
$("#er").click(function(){
	if(window.role!="" && window.role != "null") {
		window.location.href = "/yuansu/xiangqing?name=铒";
	}else {
		alert("请先登录")
		$("#login").click()
	}
});
$("#ho").click(function(){
	if(window.role!="" && window.role != "null") {
		window.location.href = "/yuansu/xiangqing?name=钬";
	}else {
		alert("请先登录")
		$("#login").click()
	}
});
$("#dy").click(function(){
	if(window.role!="" && window.role != "null") {
		window.location.href = "/yuansu/xiangqing?name=镝";
	}else {
		alert("请先登录")
		$("#login").click()
	}
});
$("#tb").click(function(){
	if(window.role!="" && window.role != "null") {
		window.location.href = "/yuansu/xiangqing?name=铽";
	}else {
		alert("请先登录")
		$("#login").click()
	}
});
$("#gd").click(function(){
	if(window.role!="" && window.role != "null") {
		window.location.href = "/yuansu/xiangqing?name=钆";
	}else {
		alert("请先登录")
		$("#login").click()
	}
});
$("#eu").click(function(){
	if(window.role!="" && window.role != "null") {
		window.location.href = "/yuansu/xiangqing?name=铕";
	}else {
		alert("请先登录")
		$("#login").click()
	}
});
$("#sm").click(function(){
	if(window.role!="" && window.role != "null") {
		window.location.href = "/yuansu/xiangqing?name=钐";
	}else {
		alert("请先登录")
		$("#login").click()
	}
});
$("#pm").click(function(){
	if(window.role!="" && window.role != "null") {
		window.location.href = "/yuansu/xiangqing?name=钷";
	}else {
		alert("请先登录")
		$("#login").click()
	}
});
$("#nd").click(function(){
	if(window.role!="" && window.role != "null") {
		window.location.href = "/yuansu/xiangqing?name=钕";
	}else {
		alert("请先登录")
		$("#login").click()
	}
});
$("#pr").click(function(){
	if(window.role!="" && window.role != "null") {
		window.location.href = "/yuansu/xiangqing?name=镨";
	}else {
		alert("请先登录")
		$("#login").click()
	}
});
$("#ce").click(function(){
	if(window.role!="" && window.role != "null") {
		window.location.href = "/yuansu/xiangqing?name=铈";
	}else {
		alert("请先登录")
		$("#login").click()
	}
});
$("#la").click(function(){
	if(window.role!="" && window.role != "null") {
		window.location.href = "/yuansu/xiangqing?name=镧";
	}else {
		alert("请先登录")
		$("#login").click()
	}
});


function showLoginInfoFrontend(){
	
	var url = "/wechat/getLoginInfo";
    $.ajax({
        url: url,
        type: "get",
        async: false,
        success: function (resObj) {
//             var resObj = JSON.parse(res);
            if (resObj.code == 200) {
				openId = resObj.openId;
				nickName = resObj.nickName;
				headUrl = resObj.headUrl;
				role = resObj.role;
				console.log("current openId " + resObj.openId)

                if(resObj.bind==="false") {
					$("#showcode").css('display','none');
					// $("#showqrcode").css('display','none');
                	$("#alertbd").css('display','block');
                }
            }
        }
    });
	
	$("#alertbd").css('display','none');
    if(role != "" && role!="null") {
		$("#loginBefore").css('display','none'); 
		$("#headImg").attr("src", headUrl);
		$("#loginAfter").css('display','block');
		if(role==="visitor"){
			$(".vip").html("普通用户"); 
			$("#backgroud").css('display','none'); 
		}
		if(window.role==="admin"){
			$(".vip").html("admin"); 
			$("#backgroud").css('display','block'); 
		}
		$(".vip").html(window.role);
		$("#wodeshenqing").attr("/zhishifuwu/shenqinglist(userid=${" + openId + "})");
		$("#logoinName").html(nickName);
	}else {
		$("#loginBefore").css('display','block'); 
		$("#loginAfter").css('display','none'); 
	}
}
//document.write(unescape("%3Cspan id='_ideConac' %3E%3C/span%3E%3Cscript src='http://dcs.conac.cn/js/07/128/0000/60556095/CA071280000605560950010.js' type='text/javascript'%3E%3C/script%3E"));