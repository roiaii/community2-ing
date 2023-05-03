$(function(){
	$("#sendBtn").click(send_letter);   //点击发送按钮时，调用send_letter方法
	$(".close").click(delete_msg);      //点击关闭提示框时，调用delete_msg方法
});

function send_letter() {
	$("#sendModal").modal("hide");     //把对话框隐藏掉
										//这个地方写整个的逻辑，将数据发送给服务器，服务器返回数据并进行处理

	var toName = $("#recipient-name").val();    //从页面获取数据
	var content = $("#message-text").val();
	$.post(												//异步的发送数据向服务器
	    CONTEXT_PATH + "/letter/send",    //访问路径
	    {"toName":toName,"content":content},   //声明要传的数据的参数
	    function(data) {                  //处理服务端返回的数据 接收到data data是一个字符串 满足JSON的格式
	        data = $.parseJSON(data);    //将data转化为js对象
	        if(data.code == 0) {         //对返回结果进行判断  //0表示发送成功
	            $("#hintBody").text("发送成功!");
	        } else {
	            $("#hintBody").text(data.msg);
	        }

	        $("#hintModal").modal("show");         //把提示框显示出来
            setTimeout(function(){
                $("#hintModal").modal("hide");
                location.reload();                //刷新页面
            }, 2000);
	    }
	);
}

function delete_msg() {
	// TODO 删除数据
	$(this).parents(".media").remove();
}