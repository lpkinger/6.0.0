Ext.define('erp.view.fs.cust.SaleReportNavition',{
	extend: 'Ext.view.View', 
	id:'salereportnavition',
	alias: 'widget.salereportnavition', 
	cls:'salereportna',
	border:false,
	style: {
		position: 'absolute'
	},
	autoHeight:true,
	frame: true, 
	autoScroll : true,
	width:300,
	bodyStyle : 'border-style:none;background-color:#ffffff !important',
	itemSelector:'li',
   	listeners:{
  	 	'viewready':function(panel){
	 		var tpl = new Ext.XTemplate( 
				'<div align="center"><div id = \'title\' style="line-height:50px;font-size:18px;font-weight:bold;">订单、生产、交货流程进度查询图</div>',
				'<div style="line-height:26px;font-size:15px;"><b>订单编号：</b>'+ ordercode+'</div></div>',
				'<div class="progress" id="progress" width="320px"><div class="basic" id="basic">',
				'<svg xmlns="http://www.w3.org/2000/svg" version="1.1" style="position:absolute;height:301px;width:20px;">',
				'<line x1="15" y1="23" x2="15" y2="50" stroke="#000" stroke-width="2" />',
				'<line x1="15" y1="59" x2="15" y2="85" stroke="#000" stroke-width="2" />',
				'<line x1="15" y1="95" x2="15" y2="122" stroke="#000" stroke-width="2" />',
				'<line x1="15" y1="131" x2="15" y2="158" stroke="#000" stroke-width="2" />',
				'<line x1="15" y1="167" x2="15" y2="194" stroke="#000" stroke-width="2" />',
				'<line x1="15" y1="203" x2="15" y2="230" stroke="#000" stroke-width="2" />',
				'<line x1="15" y1="239" x2="15" y2="265" stroke="#000" stroke-width="2" />',
				'<line x1="15" y1="275" x2="15" y2="301" stroke="#000" stroke-width="2" />',
				'</svg>',
				'<ul class="step1" style="margin: 10px;">',
				'<li onclick="changeModule(\'order\')">',
				'<span class="redcircle"></span>',
				'<span class="font">订单签订</span>', 
				'</li>',
				'<li onclick="changeModule(\'deposit\')" >',
				'<span class="redcircle"></span>',
				'<span class="font">预收定金</span>', 
				'</li>',
				'<li onclick="changeModule(\'purchase\')">',
				'<span class="redcircle"></span>',
				'<span class="font">形成采购计划</span>', 
				'</li>',
				'<li onclick="changeModule(\'purchase\')">',
				'<span class="redcircle"></span>',
				'<span class="font">物料采购</span>', 
				'</li>',
				'<li onclick="changeModule(\'purchase\')">',
				'<span class="redcircle"></span>',
				'<span class="font">物料验收入库</span>', 
				'</li>',
				'<li onclick="changeModule(\'make\')">',
				'<span class="redcircle"></span>',
				'<span class="font">生产制造</span>', 
				'</li>',
				'<li onclick="changeModule(\'accept\')">',
				'<span class="redcircle"></span>',
				'<span class="font">产成品验收</span>', 
				'</li>',
				'<li onclick="changeModule(\'saleout\')">',
				'<span class="redcircle"></span>',
				'<span class="font">出库发货</span>', 
				'</li>',
				'<li onclick="changeModule(\'payforAR\')">',
				'<span class="redcircle"></span>',
				'<span class="font">买方确认应付账款</span>', 
				'</li>', 
				'</ul>',
				'</div>',
				'</div>'
	 	    );
	  	 	tpl.overwrite(panel.getEl(),tpl);	
	  	 	var me = this;
		  	var data = new Array();
			Ext.Ajax.request({
				url:basePath + 'fs/cust/getSaleReportProgress.action',
				params:{
					custcode : custcode,
					ordercode:ordercode
				},
				method:'post',
				async: false,
				callback:function(options,success,resp){
					var res = new Ext.decode(resp.responseText);
					if(res.success){
						if(res.data.length>0){
							data = res.data;
						}
					}else if(res.exceptionInfo){
						showError(res.exceptionInfo);	
					}
				}
			});
			
			for(var i=0;i<data.length;i++){
				var lis=document.getElementById('progress').getElementsByTagName('li');
				var status = lis[i].childNodes[0];
				status.className=data[i]['isok'];
				var date = document.createElement("span");		//创建新元素节点
				date.innerHTML=data[i]['date'];
				date.setAttribute("style", "float: right;");	//设置属性
				date.setAttribute("class", "font");	//设置属性
				//添加节点到文档树上:
				lis[i].appendChild(date)			
			}
	  	}
	},
   initComponent: function(){
  	 this.callParent(arguments);
   }
});