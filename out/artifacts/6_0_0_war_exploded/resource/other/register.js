/**
 * 处理注册界面的一些逻辑
 * 包括切换选项卡、错误信息提示、更新确认信息、提交用户信息等
 * 例如,要切换到选项卡2，调用toggleTo(2)即可;提交用户所有注册信息到后台，调用register()即可
 * @author yingp
 */
$(function(){
	/**
	 * 将form信息序列化
	 */
	$.fn.serializeForm = function(o) {   
		  var a = this.serializeArray();   
		  $.each(a, function() {
			  if (o[this.name]) {   
				  if (!o[this.name].push) {   
				      o[this.name] = [ o[this.name] ];   
				  }
				  o[this.name].push(this.value || '');
			} else {   
				      o[this.name] = this.value || '';   
			} 
		  });   
		  return o;   
	}; 
});
			/**
			 * 切换选项卡
			 * @param img 切换到的id
			 */
			function toggleTo(img){
				if($(".up").attr("id") == "tab1"){//如果当前tab是tab1
					if(!checkTab1()){//判断tab1信息填写是否正确
						return;						
					}
				} else if($(".up").attr("id") == "tab2"){//如果当前tab是tab2
					if(!checkTab2()){//判断tab2信息填写是否正确
						return;						
					}
				}
				$.each($("div[id*='oDIV']"),function(i){//改变tab选项卡样式
					if(this.id == "oDIV"+img){
						$(this).css("display","");
						$(this).parent().removeClass();
						$(this).parent().addClass("up");
					} else {
						$(this).css("display","none");
						$(this).parent().removeClass();
						$(this).parent().addClass("tab"+(i+1));
					}
				});
				if(img == 4){
					updateTab4();//更新tab4信息
				}
			}
			/**
			 * 判断该企业名称是否已存在
			 */
			function checkName(){
				$.ajax({
					type : 'POST',   
					contentType : 'application/json', 
			      	url: "http://localhost:8080/ERP/system/checkEnName.action",  
			      	data: {en_Name:$("#en_Name").val()},  
			     	success: function(data){
			    	  if(data.success){
			    		  $("#en_Name_err").html("<font color=blue>*您输入的企业名称可以使用!</font>");
			    	  }else{
			    		  $("#en_Name_err").html("<font color=red>*您输入的企业名称已存在!</font>"); 
			    		  $("#en_Name").focus();
			    	  }
			      }
			    });
			}
			/**
			 * 提交注册信息
			 */
			function register(){
				//先判断信息填写是否完全
				if(!checkTab1()){//判断tab1信息填写是否正确
					toggleTo(1);
					return;						
				} else if(!checkTab2()){//判断tab2信息填写是否正确
					toggleTo(2);
					return;						
				} else {
					var o = new Object($("#form1").serializeForm({}));//序列化form1数据
					o = $("#form2").serializeForm(o);//序列化form2数据
					$.ajax({
						type : 'POST',   
						contentType : 'application/json', 
				      	url: "system/register.action",  
				      	data: $.toJSON(o),  
				     	success: function(data){
				    	  if(data.success){
				    		  document.location.href = 'login.jsp';
				    	  }else{
				    	  }
				      }
				    });
				}
			}
			/**
			 * 判断tab1信息填写是否正确
			 * @returns true 信息已完善
			 */
			function checkTab1(){
				var bool = true;
				if($("#en_Name").val() == ''){
					$("#en_Name_err").html("<font color=red>*企业名称不能为空!</font>");
					bool = false;
				} else {
					$("#en_Name_err").html("&nbsp;*请输入企业在工商局注册时使用的名称");
				}
				if($("#en_Corporation").val() == ''){
					$("#en_Corporation_err").html("<font color=red>*公司法人不能为空!</font>");
					bool = false;
				} else {
					$("#en_Corporation_err").html("*请输入企业在工商局注册时的法人");
				}
				if($("#en_Businesscode").val() == ''){
					$("#en_Businesscode_err").html("<font color=red>*商业登记证号不能为空!</font>");
					bool = false;
				} else {
					$("#en_Businesscode_err").html("*请输入企业在工商局注册时得到的编号");
				}
				return bool;
			}
			/**
			 * 判断tab2信息填写是否正确
			 * @returns true 信息已完善
			 */
			function checkTab2(){
				var bool = true;
				if($("#en_Admin").val() == ''){
					$("#en_Admin_err").html("<font color=red>*管理员名不能为空!</font>");
					bool = false;
				} else {
					$("#en_Admin_err").html("&nbsp;*管理员联系资料非常重要,请认真填写");
				}
				if($("#en_Adminphone").val() == ''){
					$("#en_Adminphone_err").html("<font color=red>*管理员电话不能为空!</font>");
					bool = false;
				} else {
					$("#en_Adminphone_err").html("&nbsp;*请输入管理员移动电话号");
				}
				if($("#en_Email").val() == ''){
					$("#en_Email_err").html("<font color=red>*管理员邮箱不能为空!</font>");
					bool = false;
				} else {
					$("#en_Email_err").html("&nbsp;*请输入正确邮箱格式");
				}
				return bool;
			}
			/**
			 * 修改tab4信息
			 * 
			 */
			function updateTab4(){
				$("#en_Name_td").text($("#en_Name").val());
				$("#en_Shortname_td").text($("#en_Shortname").val());
				$("#en_Name_En_td").text($("#en_Name_En").val());
				$("#en_Type_td").text($("#en_Type").val());
				$("#en_Corporation_td").text($("#en_Corporation").val());
				$("#en_Businesscode_td").text($("#en_Businesscode").val());
				$("#en_Tel_td").text($("#en_Tel").val());
				$("#en_Fax_td").text($("#en_Fax").val());
				$("#en_Deliveraddr_td").text($("#en_Deliveraddr").val());
				$("#en_Address_td").text($("#en_Address").val());
				$("#en_Registercapital_td").text($("#en_Registercapital").val());
				$("#en_Url_td").text($("#en_Url").val());
				$("#en_Taxcode_td").text($("#en_Taxcode").val());
				$("#en_Time_td").text($("#en_Time").val());
				$("#en_Admin_td").text($("#en_Admin").val());
				$("#en_Adminphone_td").text($("#en_Adminphone").val());
				$("#en_Email_td").text($("#en_Email").val());
				$("#en_Attachment_td").text($("#en_Attachment").val());
			}