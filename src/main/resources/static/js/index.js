$(function(){
	$("#publishBtn").click(publish);
});

function publish() {
	$("#publishModal").modal("hide");

	// 获取标题和内容
	var title = $("#recipient-name").val();  //这两个变量也对应Controller中的变量 至于两者怎么映射 交给Spring MVC前端控制器处理
	var content = $("#message-text").val();
	// 发送异步请求(POST)
	$.post(
	    CONTEXT_PATH + "/discuss/add",    //注意这里提交请求的路径 和 Controller中控制器方法保持一致
	    {"title":title,"content":content},
	    function(data) {
	        data = $.parseJSON(data);
	        // 在提示框中显示返回消息
	        $("#hintBody").text(data.msg);
	        // 显示提示框
            $("#hintModal").modal("show");
            // 2秒后,自动隐藏提示框
            setTimeout(function(){
                $("#hintModal").modal("hide");
                // 刷新页面
                if(data.code == 0) {
                    window.location.reload();
                }
            }, 2000);
	    }
	);

}