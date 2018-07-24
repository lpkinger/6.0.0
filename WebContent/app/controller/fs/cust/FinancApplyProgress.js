Ext.QuickTips.init();
Ext.define('erp.controller.fs.cust.FinancApplyProgress', {
	extend: 'Ext.app.Controller',
	FormUtil: Ext.create('erp.util.FormUtil'),
	BaseUtil: Ext.create('erp.util.BaseUtil'),
	views:['fs.cust.FinancApplyProgress','fs.cust.FinancApplyNavition'],
   	init:function(){
	   var me = this;
	   this.control({
	   		financapplynavition:{
	   			viewready: function(panel){
			 		var tpl = new Ext.XTemplate( 
			 			'<div id =\'show\' style="width:300px;font-size:13px;margin:0 auto;">',
			 			'<div align="center" id = \'title\' style="line-height:50px;font-size:20px;font-weight:bold;">保理融资申请进度</div>',
						'<div class="progress" id="progress"><div class="basic" id="basic">',
						'<svg xmlns="http://www.w3.org/2000/svg" version="1.1">',
						'<line x1="15" y1="23" x2="15" y2="123" stroke="#000" stroke-width="2" />',
						'<line x1="15" y1="133" x2="15" y2="233" stroke="#000" stroke-width="2" />',
						'<line x1="15" y1="243" x2="15" y2="343" stroke="#000" stroke-width="2" />',
						'<line x1="15" y1="353" x2="15" y2="453" stroke="#000" stroke-width="2" />',
						'</svg>',
						'<ul class="step1" style="margin: 10px;">',
						'</ul>',
						'</div>',
						'</div>'
			 	    );
			  	 	tpl.overwrite(panel.getEl(),tpl);	
				  	var params = new Object();
				  	params.busincode = busincode;
				  	if(formCondition){
						params.condition = formCondition.replace(/IS/g,"=");
					}
					Ext.Ajax.request({
						url:basePath + 'fs/cust/getFinancApplyProgress.action',
						params:params,
						method:'post',
						callback:function(options,success,resp){
							var res = new Ext.decode(resp.responseText);
							if(res.success){
								var data = new Array();
								if(res.data.length>0){
									data = res.data;
								}
								var height = 0;
								for(var i=0;i<data.length;i++){
									height = i*110;
									var ul=document.getElementById('progress').getElementsByTagName('ul')[0];
									var li = document.createElement("li");		//创建新元素节点
									var li1 = document.createElement("li");		//创建新元素节点
									li1.setAttribute("style", "height:75px;");	//设置属性
									var status = document.createElement("span");		//创建新元素节点
									status.className=data[i]['isok'];
									var text = document.createElement("span");		//创建新元素节点
									text.innerHTML=data[i]['desc'];
									li.appendChild(status);
									li.appendChild(text);	
									var date = document.createElement("span");		//创建新元素节点
									date.innerHTML=data[i]['date'];
									date.setAttribute("style", "float: right;");	//设置属性
									date.setAttribute("class", "font");	//设置属性
									//添加节点到文档树上:
									li.appendChild(date);	
									ul.appendChild(li);
									ul.appendChild(li1);
								}
								var svg=document.getElementsByTagName('svg')[0];
								svg.setAttribute("style", "position:absolute;height:"+(height+15)+";width:20px;");	//设置属性
								if(res.custname&&res.code){
									var show=document.getElementById('show');
									var progress=document.getElementById('progress');
									var cust = document.createElement("div");
									cust.innerHTML = '客户名称：'+res.custname;
									cust.setAttribute("align", "center");	//设置属性
									cust.setAttribute("style", "line-height:26px;font-size:14px;font-weight:bold;");	//设置属性
									show.insertBefore(cust, progress);
									var code = document.createElement("div");
									code.innerHTML = '额度申请编号：'+res.code;
									code.setAttribute("align", "center");	//设置属性
									code.setAttribute("style", "line-height:26px;font-size:14px;font-weight:bold;");	//设置属性
									show.insertBefore(code, progress);
									
								}
							}else if(res.exceptionInfo){
								showError(res.exceptionInfo);	
							}
						}
					});
	   			}
	   		}
	   });
   	}
});