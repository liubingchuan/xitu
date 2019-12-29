$("#quit").click(function(){
	$.cookie("openId", "", {expires: -1});
	$.cookie("nickName", "", {expires: -1});
	$.cookie("headImg", "", {expires: -1});
	$.cookie("role", "", {expires: -1});
	window.location.href = "/wechat/quit";
});
$("#login").click(function(){
	$("#alertbd").css('display','none');
	$("#alertZh").css('display','block');
});

$("#xituzhiku").click(function(){
	if($.cookie("role")!=null) {
		window.location.href = "/patent/agmount";
	}else {
		alert("请先登录")
		$("#login").click()
	}
});

$("#xituzhiku11").click(function(){
	if($.cookie("role")!=null) {
		window.location.href = "/patent/agmount";
	}else {
		alert("请先登录")
		$("#login").click()
	}
});

$("#cailiaojisuan").click(function(){
	if($.cookie("role")!=null) {
		window.location.href = "/material/calculate";
	}else {
		alert("请先登录")
		$("#login").click()
	}
});
$("#cailiaojisuan11").click(function(){
	if($.cookie("role")!=null) {
		window.location.href = "/material/calculate";
	}else {
		alert("请先登录")
		$("#login").click()
	}
});
$("#chanyejiance").click(function(){
	if($.cookie("role")!=null) {
		window.location.href = "/jiance/jiancelist";
	}else {
		alert("请先登录")
		$("#login").click()
	}
});
$("#chanyejiance11").click(function(){
	if($.cookie("role")!=null) {
		window.location.href = "/jiance/jiancelist";
	}else {
		alert("请先登录")
		$("#login").click()
	}
});
$("#zhishifuwu").click(function(){
	if($.cookie("role")!=null) {
		window.location.href = "/zhishifuwu/zhishifuwu";
	}else {
		alert("请先登录")
		$("#login").click()
	}
});
$("#zhishifuwu11").click(function(){
	if($.cookie("role")!=null) {
		window.location.href = "/zhishifuwu/zhishifuwu";
	}else {
		alert("请先登录")
		$("#login").click()
	}
});

$("#y").click(function(){
	if($.cookie("role")!=null) {
		window.location.href = "/yuansu/xiangqing?name=钇";
	}else {
		alert("请先登录")
		$("#login").click()
	}
});
$("#sc").click(function(){
	if($.cookie("role")!=null) {
		window.location.href = "/yuansu/xiangqing?name=钪";
	}else {
		alert("请先登录")
		$("#login").click()
	}
});
$("#lu").click(function(){
	if($.cookie("role")!=null) {
		window.location.href = "/yuansu/xiangqing?name=镥";
	}else {
		alert("请先登录")
		$("#login").click()
	}
});
$("#yb").click(function(){
	if($.cookie("role")!=null) {
		window.location.href = "/yuansu/xiangqing?name=镱";
	}else {
		alert("请先登录")
		$("#login").click()
	}
});
$("#tm").click(function(){
	if($.cookie("role")!=null) {
		window.location.href = "/yuansu/xiangqing?name=铥";
	}else {
		alert("请先登录")
		$("#login").click()
	}
});
$("#er").click(function(){
	if($.cookie("role")!=null) {
		window.location.href = "/yuansu/xiangqing?name=铒";
	}else {
		alert("请先登录")
		$("#login").click()
	}
});
$("#ho").click(function(){
	if($.cookie("role")!=null) {
		window.location.href = "/yuansu/xiangqing?name=钬";
	}else {
		alert("请先登录")
		$("#login").click()
	}
});
$("#dy").click(function(){
	if($.cookie("role")!=null) {
		window.location.href = "/yuansu/xiangqing?name=镝";
	}else {
		alert("请先登录")
		$("#login").click()
	}
});
$("#tb").click(function(){
	if($.cookie("role")!=null) {
		window.location.href = "/yuansu/xiangqing?name=铽";
	}else {
		alert("请先登录")
		$("#login").click()
	}
});
$("#gd").click(function(){
	if($.cookie("role")!=null) {
		window.location.href = "/yuansu/xiangqing?name=钆";
	}else {
		alert("请先登录")
		$("#login").click()
	}
});
$("#eu").click(function(){
	if($.cookie("role")!=null) {
		window.location.href = "/yuansu/xiangqing?name=铕";
	}else {
		alert("请先登录")
		$("#login").click()
	}
});
$("#sm").click(function(){
	if($.cookie("role")!=null) {
		window.location.href = "/yuansu/xiangqing?name=钐";
	}else {
		alert("请先登录")
		$("#login").click()
	}
});
$("#pm").click(function(){
	if($.cookie("role")!=null) {
		window.location.href = "/yuansu/xiangqing?name=钷";
	}else {
		alert("请先登录")
		$("#login").click()
	}
});
$("#nd").click(function(){
	if($.cookie("role")!=null) {
		window.location.href = "/yuansu/xiangqing?name=钕";
	}else {
		alert("请先登录")
		$("#login").click()
	}
});
$("#pr").click(function(){
	if($.cookie("role")!=null) {
		window.location.href = "/yuansu/xiangqing?name=镨";
	}else {
		alert("请先登录")
		$("#login").click()
	}
});
$("#ce").click(function(){
	if($.cookie("role")!=null) {
		window.location.href = "/yuansu/xiangqing?name=铈";
	}else {
		alert("请先登录")
		$("#login").click()
	}
});
$("#la").click(function(){
	if($.cookie("role")!=null) {
		window.location.href = "/yuansu/xiangqing?name=镧";
	}else {
		alert("请先登录")
		$("#login").click()
	}
});


function showLoginInfoFrontend(){
	$("#alertbd").css('display','none');
    if($.cookie("role")!=null) {
		$("#loginBefore").css('display','none'); 
		$("#headImg").attr("src", $.cookie("headImg"));
		$("#loginAfter").css('display','block');
		if($.cookie("role")!=null && $.cookie("role")==="visitor"){
			$(".vip").html("普通用户"); 
			$("#backgroud").css('display','none'); 
		}
		if($.cookie("role")!=null && $.cookie("role")==="admin"){
			$(".vip").html("admin"); 
			$("#backgroud").css('display','block'); 
		}
		$(".vip").html($.cookie("role"));
		$("#logoinName").html($.cookie("loginName"));
	}else {
		$("#loginBefore").css('display','block'); 
		$("#loginAfter").css('display','none'); 
	}
}