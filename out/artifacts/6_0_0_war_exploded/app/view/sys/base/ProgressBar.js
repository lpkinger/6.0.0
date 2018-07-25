Ext.define('erp.view.sys.base.ProgressBar',{
	extend: 'Ext.view.View', 
	alias: 'widget.processview', 
	border:false,
	style: {
		position: 'absolute'
	},
	width:'300px',
	itemSelector:'li',
	activeItem:1,
	tpl:[
			'<div class="progress" id="progress" width="320px"><div class="basic" id="basic"><span class="admin"><font class="admin-name1">系统管理员&nbsp;</font><font class="admin-people" id="admin"></font></span>',
			'<svg xmlns="http://www.w3.org/2000/svg" version="1.1" class="svg1">',
			'<polyline points="55,35 55,65 155,65 155,30 165,30 " style="stroke:#333;stroke-width:0.2;fill:#f0f0f0;" />',
			'<polyline points="155,65 165,65" style="stroke:#333;stroke-width:0.2;fill:#f0f0f0;" />',
			'<polyline points="155,65 155,105 165,105" style="stroke:#333; stroke-width:0.2;fill:#f0f0f0;" />',
			'<ul class="step1">',
		     '<li class="active" onclick="changeModule(this)" value="enterprise" data-type="normal"  data-dept="0" group="message_group" data-table="enterprise" data-flag="" data-importcaller="">',
		     '<span class="circle"></span>',
		     '<span class="font">企业信息</span>',
		     '</li>',
		     '<li class="jobpower" value="jobpower" onclick="changeModule(this)" data-type="normal"  data-dept="2" group="message_group" data-table="Job" data-flag="" data-importcaller="">',
		     '<span class="circle"></span>',
		     '<span class="font">岗位权限</span>',
		     '</li>',
		     '<li class="approval" onclick="changeModule(this)" value="approval" data-type="normal"  data-dept="3" data-table="JProcessDeploy" data-flag="" data-importcaller="">',
		     '<span class="circle"></span>',
		     '<span class="font">审批流</span>',
		     '</li>',
		     '</ul>',
		     '</svg>',
			'</div>',
			'<div class="hr" id="hr"><span class="admin"><font class="admin-name">行政人事部&nbsp;</font><font class="admin-people" id="orgadmin"></font></span>',
			'<ul class="step2">',
			 '<li class="orgpeople" onclick="changeModule(this)"  data-dept="1" value="orgpeople" data-type="normal" data-table="HrOrg" data-flag="" data-importcaller="">',
		     '<span class="circle"></span>',
		     '<span class="font">组织人员</span>',
		     '</li>',
			'</ul>',
			'</div>',
			'<div class="salemodule"><span class="admin"><font class="admin-name">销售部&nbsp;</font><font class="admin-people" id="saleadmin"></font></span>',
			'<svg xmlns="http://www.w3.org/2000/svg" version="1.1" class="svg2">',
			'<polyline points="55,35 55,143 155,143 155,35 165,35 " style="stroke:#333;stroke-width:0.2;fill:#f0f0f0;" />',
			'<polyline points="155,120 155,250 165,250" style="stroke:#333;stroke-width:0.2;fill:#f0f0f0;" />',
			'<polyline points="155,143 165,143" style="stroke:#333;stroke-width:0.2;fill:#f0f0f0;" />',
			'<polyline points="155,105 165,105" style="stroke:#333;stroke-width:0.2;fill:#f0f0f0;" />',
			'<polyline points="155,178 165,178" style="stroke:#333;stroke-width:0.2;fill:#f0f0f0;" />',
			'<polyline points="155,213 165,213" style="stroke:#333;stroke-width:0.2;fill:#f0f0f0;" />',
			'<polyline points="155,70 165,70" style="stroke:#333;stroke-width:0.2;fill:#f0f0f0;" />',
			/*'<polyline points="155,60 155,120 165,120" style="stroke:#333;stroke-width:0.2;fill:#f0f0f0;" />',*/
			'<ul class="step3">',
			 '<li class="SaleForecastKind!saas" value="3" onclick="changeModule(this)" data-type="defaultpanel" data-dept="4" data-table="SaleForecastKind" data-flag="Sale-ForecastKind" data-importcaller="">',
		     '<span class="circle"></span>',
		     '<span class="font">销售预测类型</span>',
		     '</li>',
		     '<li class="SaleKind!saas" value="4" onclick="changeModule(this)" data-type="defaultpanel" data-dept="4" data-table="SaleKind" data-flag="Sale-Kind" data-importcaller="">',
		     '<span class="circle"></span>',
		     '<span class="font">销售类型</span>',
		     '</li>',
		     '<li class="Payments!Sale!saas" value="Payments-gathering" onclick="changeModule(this)" data-type="defaultpanel" data-dept="4" data-table="Payments" data-flag="Payments-gathering" data-importcaller="">',
		     '<span class="circle"></span>',
		     '<span class="font">销售收款方式</span>',
		     '</li>',
		     '<li class="Customer" value="Customer" onclick="changeModule(this)" data-type="import" data-dept="5" data-table="Customer" data-flag="" data-importcaller="Customer">',
		     '<span class="circle"></span>',
		     '<span class="font">客户资料</span>',
		     '</li>',
		     '<li class="SalePrice" value="SalePrice" onclick="changeModule(this)" data-type="import" data-dept="5" data-table="SalePrice" data-flag="" data-importcaller="SalePrice">',
		     '<span class="circle"></span>',
		     '<span class="font">销售价格</span>',
		     '</li>',
		     '<li class="SaleForecas" value="SaleForecast" onclick="changeModule(this)" data-type="import" data-dept="5" data-table="SaleForecast" data-flag="" data-importcaller="SaleForecast">',
		     '<span class="circle"></span>',
		     '<span class="font">未完成销售预测</span>',
		     '</li>',
		     '<li class="nofinishSale" value="Sale" onclick="changeModule(this)" data-type="import" data-dept="5" data-table="Sale" data-flag="" data-importcaller="Sale">',
		     '<span class="circle"></span>',
		     '<span class="font">未完成销售订单</span>',
		     '</li>',
			'</ul>',
			'</svg>',
			'</div>',
			'<div class="pucharsemodule"><span class="admin"><font class="admin-name">采购部&nbsp;</font><font class="admin-people" id="purchaseadmin"></font></span>',
			'<svg xmlns="http://www.w3.org/2000/svg" version="1.1" class="svg2">',
			'<polyline points="55,35 55,105 155,105 155,30 165,30 " style="stroke:#333;stroke-width:0.2;fill:#f0f0f0;" />',
			'<polyline points="155,105 165,105" style="stroke:#333;stroke-width:0.2;fill:#f0f0f0;" />',
			'<polyline points="155,105 155,180 165,180" style="stroke:#333;stroke-width:0.2;fill:#f0f0f0;" />',
			'<polyline points="155,140 165,140" style="stroke:#333;stroke-width:0.2;fill:#f0f0f0;" />',
			'<polyline points="155,70 165,70" style="stroke:#333;stroke-width:0.2;fill:#f0f0f0;" />',
			'<ul class="step4">',
			 '<li class="PurchaseKind!saas" value="PurchaseKind!saas" onclick="changeModule(this)" data-type="defaultpanel" data-dept="4" data-table="PurchaseKind" data-flag="Purchase-Kind" data-importcaller="">',
		     '<span class="circle"></span>',
		     '<span class="font">采购类型</span>',
		     '</li>',
		     '<li class="Payments!Purchase!saas" value="Payments!Purchase!saas" onclick="changeModule(this)" data-type="defaultpanel" data-dept="4" data-table="Payments WHERE pa_kind=1" data-flag="Payments-pay" data-importcaller="">',
		     '<span class="circle"></span>',
		     '<span class="font">采购付款方式</span>',
		     '</li>',
		     '<li class="Vendor" value="Vendor" onclick="changeModule(this)" data-type="import" data-dept="5" data-table="Vendor" data-flag="" data-importcaller="Vendor">',
		     '<span class="circle"></span>',
		     '<span class="font">供应商资料</span>',
		     '</li>',
		     '<li class="Customer" value="Customer1" onclick="changeModule(this)" data-type="import" data-dept="5" data-table="PurchasePrice" data-flag="" data-importcaller="PurchasePrice">',
		     '<span class="circle"></span>',
		     '<span class="font">采购价格</span>',
		     '</li>',
		     '<li class="Purchase" value="Purchase" onclick="changeModule(this)" data-type="import" data-dept="5" data-table="Purchase" data-flag="" data-importcaller="Purchase">',
		     '<span class="circle"></span>',
		     '<span class="font">未完成采购订单</span>',
		     '</li>',
			'</ul>',
			'</svg>',
			'</div>',
			'<div class="reseachmodule"><span class="admin"><font class="admin-name">研发部&nbsp;</font><font class="admin-people" id="reseachadmin"></font></span>',
			'<svg xmlns="http://www.w3.org/2000/svg" version="1.1" class="svg2">',
			'<polyline points="55,35 55,105 155,105 155,30 165,30 " style="stroke:#333;stroke-width:0.2;fill:#f0f0f0;" />',
			'<polyline points="155,105 165,105" style="stroke:#333;stroke-width:0.2;fill:#f0f0f0;" />',
			'<polyline points="155,105 155,180 165,180" style="stroke:#333;stroke-width:0.2;fill:#f0f0f0;" />',
			'<polyline points="155,140 165,140" style="stroke:#333;stroke-width:0.2;fill:#f0f0f0;" />',
			'<polyline points="155,70 165,70" style="stroke:#333;stroke-width:0.2;fill:#f0f0f0;" />',
			'<ul class="step4">',
			 '<li class="PreProduct" value="PreProduct" onclick="changeModule(this)" data-type="import" data-dept="6" data-table="datalistcombo WHERE upper(dlc_caller)=\'PREPRODUCT\' AND upper(dlc_fieldname)=\'PRE_UNIT\'">',
		     '<span class="circle"></span>',
		     '<span class="font">物料单位</span>',
		     '</li>',
		     '<li class="productkindtree" value="productkindtree" onclick="changeModule(this)" data-type="import" data-dept="7" data-table="ProductKind">',
		     '<span class="circle"></span>',
		     '<span class="font">物料种类</span>',
		     '</li>',
		     '<li class="Product"  value="Product1" onclick="changeModule(this)" data-type="import" data-dept="5" data-table="Product" data-flag="" data-importcaller="Product">',
		     '<span class="circle"></span>',
		     '<span class="font">物料资料</span>',
		     '</li>',
		     '<li class="Bomlevel!saas"  value="Bomlevel!saas" onclick="changeModule(this)" data-type="defaultpanel" data-dept="4" data-table="Bomlevel" data-flag="Bom-level" data-importcaller="">',
		     '<span class="circle"></span>',
		     '<span class="font">BOM等级</span>',
		     '</li>',
		     '<li class="BOM" value="BOM" onclick="changeModule(this)" data-type="import" data-dept="5" data-table="BOM" data-flag="" data-importcaller="BOM">',
		     '<span class="circle"></span>',
		     '<span class="font">BOM资料</span>',
		     '</li>',
			'</ul>',
			'</svg>',
			'</div>',
			'<div class="financemodule"><span class="admin"><font class="admin-name">财务部&nbsp;</font><font class="admin-people" id="financeadmin"></font></span>',
			'<svg xmlns="http://www.w3.org/2000/svg" version="1.1" class="svg2">',
			'<polyline points="55,35 55,70 155,70 155,30 165,30 " style="stroke:#333;stroke-width:0.2;fill:#f0f0f0;" />',
			'<polyline points="155,70 155,140 165,140" style="stroke:#333;stroke-width:0.2;fill:#f0f0f0;" />',
			'<polyline points="155,105 165,105" style="stroke:#333;stroke-width:0.2;fill:#f0f0f0;" />',
			'<polyline points="155,70 165,70" style="stroke:#333;stroke-width:0.2;fill:#f0f0f0;" />',
			'<ul class="step4">',
			 '<li class="PreProduct" value="PreProduct1" onclick="changeModule(this)" data-type="normal" data-dept="11" data-table="Currencys" data-flag="" data-importcaller="">',
		     '<span class="circle"></span>',
		     '<span class="font">币别</span>',
		     '</li>',
		     '<li class="productkindtree1" value="productkindtree1" onclick="changeModule(this)" data-type="import" data-dept="5" data-table="Category" data-flag="" data-importcaller="Category!Base">',
		     '<span class="circle"></span>',
		     '<span class="font">科目资料</span>',
		     '</li>',
		     '<li class="Product" value="Product2" onclick="changeModule(this)" data-type="import" data-dept="5" data-table="FeeCategorySet" data-flag="" data-importcaller="FeeCategorySet">',
		     '<span class="circle"></span>',
		     '<span class="font">费用科目类型</span>',
		     '</li>',
		     '<li class="Bomlevel!saas" value="Bomlevel!saas1" onclick="changeModule(this)" data-type="import" data-dept="5" data-table="ProdIOCateSet" data-flag="" data-importcaller="ProdIOCateSet">',
		     '<span class="circle"></span>',
		     '<span class="font">出入库科目设置</span>',
		     '</li>',
			'</ul>',
			'</svg>',
			'</div>',
			'<div class="reseachmodule"><span class="admin"><font class="admin-name">仓库部&nbsp;</font><font class="admin-people" id="warehouseadmin"></font></span>',
			'<svg xmlns="http://www.w3.org/2000/svg" version="1.1" class="svg2">',
			'<polyline points="55,35 55,105 155,105 155,30 165,30 " style="stroke:#333;stroke-width:0.2;fill:#f0f0f0;" />',
			'<polyline points="155,105 165,105" style="stroke:#333;stroke-width:0.2;fill:#f0f0f0;" />',
			'<polyline points="155,105 155,180 165,180" style="stroke:#333;stroke-width:0.2;fill:#f0f0f0;" />',
			'<polyline points="155,140 165,140" style="stroke:#333;stroke-width:0.2;fill:#f0f0f0;" />',
			'<polyline points="155,70 165,70" style="stroke:#333;stroke-width:0.2;fill:#f0f0f0;" />',
			'<ul class="step4">',
			 '<li class="PreProduct" value="can" onclick="changeModule(this)" data-type="defaultpanel" data-dept="4" data-table="Warehouse" data-flag="Warehouse-Base" data-importcaller="">',
		     '<span class="circle"></span>',
		     '<span class="font">仓库资料</span>',
		     '</li>',
		     '<li class="productkindtree" value="orthercan" onclick="changeModule(this)" data-type="import" data-dept="8" data-table="datalistcombo WHERE upper(dlc_caller)=\'PRODINOUT!OTHERIN\' AND upper(dlc_fieldname)=\'PI_TYPE\'">',
		     '<span class="circle"></span>',
		     '<span class="font">其它入库类型</span>',
		     '</li>',
		     '<li class="Product" value="ortherout" onclick="changeModule(this)" data-type="import" data-dept="9" data-table="datalistcombo WHERE upper(dlc_caller)=\'PRODINOUT!OTHEROUT\' AND upper(dlc_fieldname)=\'PI_TYPE\'">',
		     '<span class="circle"></span>',
		     '<span class="font">其它出库类型</span>',
		     '</li>',
		     '<li class="Bomlevel!saas" value="out" onclick="changeModule(this)" data-type="import" data-dept="10" data-table="datalistcombo WHERE upper(dlc_caller)=\'PRODINOUT!APPROPRIATIONOUT\' AND upper(dlc_fieldname)=\'PI_TYPE\'">',
		     '<span class="circle"></span>',
		     '<span class="font">拨出单</span>',
		     '</li>',
		     '<li class="Product"  value="kucun" onclick="changeModule(this)" data-type="import" data-dept="5" data-table="ProdInOut" data-flag="" data-importcaller="ProdInOut">',
		     '<span class="circle"></span>',
		     '<span class="font">库存初始化</span>',
		     '</li>',
			'</ul>',
			'</svg>',
			'</div>',
			'<div class="production" id="production"><span class="admin"><font class="admin-name">生产部&nbsp;</font><font class="admin-people" id="productadmin"></font></span>',
			'<svg xmlns="http://www.w3.org/2000/svg" version="1.1" class="svg1">',
			'<polyline points="55,35 55,45 155,45 155,30 165,30 " style="stroke:#333;stroke-width:0.2;fill:#f0f0f0;" />',
			'<polyline points="155,65 165,65" style="stroke:#333;stroke-width:0.2;fill:#f0f0f0;" />',
			/*'<polyline points="155,65 155,105 165,105" style="stroke:#333;stroke-width:0.2;fill:#f0f0f0;" />',*/
			'<polyline points="155,45 155,65" style="stroke:#333;stroke-width:0.2;fill:#f0f0f0;" />',
			'<ul class="step1">',
		     '<li class="enterprise" value="worktype" onclick="changeModule(this)" data-type="defaultpanel" data-dept="4" data-table="MakeKind" data-flag="Make-Kind" data-importcaller="">',
		     '<span class="circle"></span>',
		     '<span class="font">工单类型</span>',
		     '</li>',
		     '<li class="jobpower" value="noworktype" onclick="changeModule(this)" data-type="import" data-dept="5" data-table="Make" data-flag="" data-importcaller="Make">',
		     '<span class="circle"></span>',
		     '<span class="font">未完成工单</span>',
		     '</li>',
		     '</ul>',
		     '</svg>',
			'</div>',
			'</div>'
	     ],
	     listeners:{
	    	 'viewready':function(panel){
	    			Ext.Ajax.request({//拿到form的items
	    				url : basePath + "common/saas/common/sysinitnavigation.action",
	    				params: {},
	    				method : 'post',
	    				callback : function  (options, success, response){
	    					if (!response) return;
	    					/*var confirm=Ext.getCmp('confirm');*/
	    				/*	for(var x=0;x<initabled.length;x++){
	    						if(initabled[x].VALUE==newvalue){*/
	    							/*if(initabled[x].INITABLED==1){
	    								Ext.getCmp('confirm').hide();
	    							}else{
	    								Ext.getCmp('confirm').show();
	    							}*/
	    				/*		}	
	    					}*/
	    					var res = new Ext.decode(response.responseText);
	    					var lis=document.getElementById('progress').getElementsByTagName('li');
	    					var font=document.getElementById('admin');
	    					document.getElementById('admin').innerHTML="("+res.admininfo[0]+")";
	    					document.getElementById('orgadmin').innerHTML="("+res.admininfo[1]+")";
	    					document.getElementById('saleadmin').innerHTML="("+res.admininfo[2]+")";
	    					document.getElementById('purchaseadmin').innerHTML="("+res.admininfo[3]+")";
	    					document.getElementById('reseachadmin').innerHTML="("+res.admininfo[4]+")";
	    					document.getElementById('financeadmin').innerHTML="("+res.admininfo[5]+")";
	    					document.getElementById('warehouseadmin').innerHTML="("+res.admininfo[6]+")";
	    					document.getElementById('productadmin').innerHTML="("+res.admininfo[7]+")";
	    					for(var i=0;i<lis.length;i++){
	    						 for(var x=0;x<res.color.length;x++){
	    							 if(lis[i].getAttribute("value")==res.color[x].VALUE&&res.color[x].INITABLED==1){
	    	    						 lis[i].getElementsByTagName('span')[0].setAttribute("class","bluebackground");
	    							 }
	    						}
	    					}
	    				}
	    			});
	    	 },
	    	 'itemclick':function(view,record,item,index){
	    		 var lis=document.getElementById('progress').getElementsByTagName('li');
	    	 	 for(var i=0;i<lis.length;i++){
	    	 	 	if(i==index){
	    	 	 		if(lis[i].getElementsByTagName('span')[1].innerHTML=='开始'){
	    	 	 			lis[i].setAttribute("class","active");
	    	 	 		}else{
	    	 	 			lis[i].setAttribute("class","normal active");
	    	 	 		}
	    	 	 	}else{
	    	 	 		if(lis[i].getElementsByTagName('span')[1].innerHTML=='开始'){
	    	 	 			lis[i].setAttribute("class","start");
	    	 	 		}else{
	    	 	 			lis[i].setAttribute("class","normal");
	    	 	 		}
	    	 	 	}
	    	 	 }
	    		 var syspanel=Ext.getCmp('syspanel');
	    		 syspanel.changeCard(syspanel,null,index);					
	    	 }
	     },
	     initComponent : function(){
	    	 var me=this;
	    	 var lis=document.getElementById('progress')/*.getElementsByTagName('li')*/; 
	    	 me.store=Ext.create('Ext.data.Store', {
		    	 fields: [{name: 'itemId'},
		    	          {name:'desc'},{name:'type'}],
		    	          data: me.getData()/*[]*/
		     }),
	    	 this.callParent(arguments);
	     },
	     getData:function(dataview){
	    	 var data=[/*{ 
	    	 	 desc:'开始',
	    		 type:'active'
	    	}*//*,{
	    		 desc:'企业信息',
	    		 type:'normal'
	 		},{
	 			desc:'组织人员',
	 			type:'normal'			
	 		},{
	 			desc:'岗位权限',
	 			type:'normal'			
	 		},{
	 			desc:'基础资料',
	 			type:'normal'
	 		},{
	 			desc:'审批流',
	 			type:'normal'
	 		},{ 
	    	 	 desc:'结束',
	    		 type:'normal'
	    	}*/];
	 		return data;
	     }
});