Ext.QuickTips.init();
Ext.define('erp.controller.fs.cust.SaleReport', {
	extend: 'Ext.app.Controller',
	FormUtil: Ext.create('erp.util.FormUtil'),
	BaseUtil: Ext.create('erp.util.BaseUtil'),
	views:['fs.cust.SaleReport','fs.cust.SaleReportPanel','fs.cust.SaleReportNavition'],
   	init:function(){
	   var me = this;
	   this.control({
	   	'#order':{
	   			'viewready':function(panel){
					var tpl = new Ext.XTemplate( 
						'<div style="line-height:37px;font-size:16px;color:blue"><b>订单概览</b></div></div>',
						'<div style="margin-left:50px">',
						'<div id ="form">',
						'<div><span id="sa_custname" style="width:50%;float:left;"><lable>客户名称 ：</lable></span><span id="sa_kind" ><lable>订单类型：</lable></span></div>',
						'<div><span id="sa_currency" style="width:50%;float:left"><lable>币&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;别：</lable></span><span id="sa_seller" ><lable>业&nbsp;&nbsp;务&nbsp;员：</lable></span></div>',
						'<div><span id="sa_toplace" style="width:50%;float:left"><lable>收货地址 ：</lable></span><span id="sa_transport" ><lable>运输方式 ：</lable></span></div>',
						'</div><div style="line-height:17px;"><span>&nbsp;</span></div>',
						'<table border="1" id="saledetail">',
						'<tr>',
						'<th style="width:35px;text-align:center">序号</th>',
						'<th style="width:120px;text-align:center">产品编号</th>',
						'<th style="width:150px;text-align:center">产品名称</th>',
						'<th style="width:120px;text-align:center">物料规格</th>',
						'<th style="width:60px;text-align:center">单位</th>',
						'<th style="width:60px;text-align:center">单价</th>',
						'<th style="width:60px;text-align:center">数量</th>',
						'<th style="width:60px;text-align:center">税率%</th>',
						'<th style="width:80px;text-align:center">金额</th>',
						'<th style="width:90px;text-align:center">交货日期</th>',
						'<th style="width:180px;text-align:center">备注</th>',
						'</tr>',
						'<tpl for=".">',
					    '<tr>',	     
					    '<td style="text-align:center">{sd_detno}</td>',
						'<td>{sd_prodcode}</td>',
						'<td>{pr_detail}</td>',
						'<td>{pr_spec}</td>',
						'<td>{pr_unit}</td>',
						'<td style="text-align:right">{sd_price}</td>',
						'<td style="text-align:right">{sd_qty}</td>',
						'<td style="text-align:right">{sd_taxrate}</td>',
						'<td style="text-align:right">{sd_total}</td>',
						'<td style="text-align:center">{sd_delivery}</td>',
						'<td>{sd_remark}</td>',
					    '</tr>',
					    '</tpl></table></div></div>'
					);
					var navpanel = panel.up('navpanel');
					if(navpanel.data&&navpanel.data.order){
						var order = navpanel.data.order;
						//订单预览
						if(order.data.length>0){
							tpl.overwrite(panel.getEl(),order.data);	
						}
						if(order.form){
							var form = document.getElementById('form');
							if(form){
								var spans = form.getElementsByTagName('span');
								Ext.Array.each(spans,function(span){
									if(order.form[span.id]){
										var text = document.createElement('text');
										text.innerText = order.form[span.id];
										span.appendChild(text);
									}
								});
							}
						}
						var table = document.getElementById('saledetail');
						if(table){
							var tr = document.createElement('tr');
							var td = document.createElement('td');
							td.setAttribute("colspan", 7);
							var text = document.createElement('text');
							text.setAttribute("style", "margin-left:50px;");
							if(typeof(order.sa_totalupper)=='undefined'||order.sa_totalupper==null){
								order.sa_totalupper = '';
							}
							if(typeof(order.sa_total)=='undefined'||order.sa_total==null){
								order.sa_total = '';
							}
							text.innerText = '合计：'+order.sa_totalupper;
							td.appendChild(text)
							tr.appendChild(td);
							var td = document.createElement('td');
							td.innerText = order.sa_total;
							td.setAttribute("colspan", 2);
							td.setAttribute("style", "text-align:right;");
							tr.appendChild(td);
							var td = document.createElement('td');
							td.setAttribute("colspan", 2);
							tr.appendChild(td);
							table.appendChild(tr);
						}
					}else{
						tpl.overwrite(panel.getEl(),tpl);
					}
	   			}
	   		},
	   		'#deposit':{
	   			'viewready':function(panel){
					var tpl = new Ext.Template( 
						'<hr/><div style="line-height:37px;font-size:16px;color:blue"><b>定金信息</b></div></div>',
						'<div style="margin-left:50px">',
						'<table border="1">',
					    '<tr>',	     
						'<td style="width:150px;">合同定金</td>',
						'<td style="width:150px;">{prd_orderamount}</td>',
						'</tr>',
						'<tr>',	 
						'<td>定金金额</td>',
						'<td>{prd_nowbalance}</td>',
						'</tr>',
						'<tr>',	 
						'<td>定金比例(%)</td>',
						'<td>{prd_ratio}</td>',
						'</tr>',
						'<tr>',	 
						'<td>定金到账日期</td>',
						'<td>{pr_date}</td>',
						'</tr>',
						'<tr>',	 
						'<td>收款银行</td>',
						'<td>{pr_accountname}</td>',
					    '</tr>',
					    '</table></div>'
					);
					var navpanel = panel.up('navpanel');
					if(navpanel.data&&navpanel.data.deposit){
						var deposit = navpanel.data.deposit;
						tpl.overwrite(panel.getEl(),deposit);	
					}else{
						tpl.overwrite(panel.getEl(),tpl);
					}
	   			}
	   		},
	   		'#purchase':{
	   			'viewready':function(panel){
	   				var tpl = new Ext.XTemplate( 
		   				'<hr/><div style="line-height:37px;font-size:16px;color:blue"><b>物料采购信息</b></div></div>',
						'<div style="margin-left:50px">',
						'<table border="1">',
						'<tr>',
						'<th style="width:150px;text-align:center">订单编号</th>',
						'<th style="width:150px;text-align:center">物料编号</th>',
						'<th style="width:80px;text-align:center">订单数</th>',
						'<th style="width:80px;text-align:center">单价</th>',
						'<th style="width:120px;text-align:center">采购情况</th>',
						'<th style="width:120px;text-align:center">订单状态</th>',
						'</tr>',
						'<tpl for=".">',
					    '<tr>',	     
					    '<td>{sa_code}</td>',
						'<td>{sd_prodcode}</td>',
						'<td style="text-align:right">{sd_qty}</td>',
						'<td style="text-align:right">{sd_price}</td>',
						'<td>{sd_pmcremark}</td>',
						'<td>{sd_mrpstatus}</td>',
					    '</tr>',
					    '</tpl></table></div></div>'
					);
	   				var navpanel = panel.up('navpanel');
					if(navpanel.data&&navpanel.data.purchase){
						var purchase = navpanel.data.purchase;
						tpl.overwrite(panel.getEl(),purchase);	
					}else{
						tpl.overwrite(panel.getEl(),tpl);
					}
	   			}
	   		},
	   		'#make':{
	   			'viewready':function(panel){
	   				var tpl = new Ext.XTemplate( 
		   				'<hr/><div style="line-height:37px;font-size:16px;color:blue"><b>生产制造信息</b></div></div>',
						'<div style="margin-left:50px">',
						'<table border="1">',
						'<tr>',
						'<th style="width:150px;text-align:center">制造单号</th>',
						'<th style="width:150px;text-align:center">产品料号</th>',
						'<th style="width:80px;text-align:center">订单数</th>',
						'<th style="width:80px;text-align:center">单价</th>',
						'<th style="width:120px;text-align:center">计划开工日期</th>',
						'<th style="width:120px;text-align:center">计划完工日期</th>',
						'</tr>',
						'<tpl for=".">',
					    '<tr>',	     
					    '<td>{ma_code}</td>',
						'<td>{ma_prodcode}</td>',
						'<td style="text-align:right">{ma_qty}</td>',
						'<td style="text-align:right">{ma_price}</td>',
						'<td style="text-align:center">{ma_planbegindate}</td>',
						'<td style="text-align:center">{ma_planbegindate}</td>',
					    '</tr>',
					    '</tpl></table></div></div>'
					);
	   				var navpanel = panel.up('navpanel');
					if(navpanel.data&&navpanel.data.make){
						var make = navpanel.data.make;
						tpl.overwrite(panel.getEl(),make);	
					}else{
						tpl.overwrite(panel.getEl(),tpl);
					}
	   			}
	   		},
	   		'#accept':{
	   			'viewready':function(panel){
	   				var tpl = new Ext.XTemplate( 
		   				'<hr/><div style="line-height:37px;font-size:16px;color:blue"><b>产成品验收信息</b></div></div>',
						'<div style="margin-left:50px">',
						'<table border="1">',
						'<tr>',
						'<th style="width:150px;text-align:center">完工入库单号</th>',
						'<th style="width:150px;text-align:center">产品料号</th>',
						'<th style="width:80px;text-align:center">数量</th>',
						'<th style="width:80px;text-align:center">单价</th>',
						'<th style="width:120px;text-align:center">生产日期</th>',
						'<th style="width:120px;text-align:center">完工日期</th>',
						'</tr>',
						'<tpl for=".">',
					    '<tr>',	     
					    '<td>{pi_inoutno}</td>',
						'<td>{pd_prodcode}</td>',
						'<td style="text-align:right">{pd_inqty}</td>',
						'<td style="text-align:right">{pd_price}</td>',
						'<td style="text-align:center">{pd_prodmadedate}</td>',
						'<td style="text-align:center">{pi_date}</td>',
					    '</tr>',
					    '</tpl></table></div></div>'
					);
	   				var navpanel = panel.up('navpanel');
					if(navpanel.data&&navpanel.data.accept){
						var accept = navpanel.data.accept;
						tpl.overwrite(panel.getEl(),accept);	
					}else{
						tpl.overwrite(panel.getEl(),tpl);
					}
	   			}
	   		},
	   		'#saleout':{
	   			'viewready':function(panel){
	   				var tpl = new Ext.XTemplate( 
		   				'<hr/><div style="line-height:37px;font-size:16px;color:blue"><b>出库发货信息</b></div></div>',
						'<div style="margin-left:50px">',
						'<table border="1">',
						'<tr>',
						'<th style="width:150px;text-align:center">出货单号</th>',
						'<th style="width:150px;text-align:center">产品料号</th>',
						'<th style="width:80px;text-align:center">数量</th>',
						'<th style="width:80px;text-align:center">出货单价</th>',
						'<th style="width:80px;text-align:center">成本单价</th>',
						'<th style="width:120px;text-align:center">出货日期</th>',
						'</tr>',
						'<tpl for=".">',
					    '<tr>',	     
					    '<td>{pi_inoutno}</td>',
						'<td>{pd_prodcode}</td>',
						'<td style="text-align:right">{pd_outqty}</td>',
						'<td style="text-align:right">{pd_sendprice}</td>',
						'<td style="text-align:right">{pd_price}</td>',
						'<td style="text-align:center">{pi_date}</td>',
					    '</tr>',
					    '</tpl></table></div></div>'
					);
	   				var navpanel = panel.up('navpanel');
					if(navpanel.data&&navpanel.data.saleout){
						var saleout = navpanel.data.saleout;
						tpl.overwrite(panel.getEl(),saleout);	
					}else{
						tpl.overwrite(panel.getEl(),tpl);
					}
	   			}
	   		},
	   		'#payforAR':{
	   			'viewready':function(panel){
	   				var tpl = new Ext.XTemplate( 
		   				'<hr/><div style="line-height:37px;font-size:16px;color:blue"><b>买方应付账款信息</b></div></div>',
						'<div style="margin-left:50px">',
						'<table border="1">',
						'<tr>',
						'<th style="width:150px;text-align:center">发票编号</th>',
						'<th style="width:150px;text-align:center">物料编号</th>',
						'<th style="width:80px;text-align:center">数量</th>',
						'<th style="width:80px;text-align:center">单价</th>',
						'<th style="width:80px;text-align:center">成本单价</th>',
						'<th style="width:120px;text-align:center">发票日期</th>',
						'</tr>',
						'<tpl for=".">',
					    '<tr>',	     
					    '<td>{ab_code}</td>',
						'<td>{abd_prodcode}</td>',
						'<td style="text-align:right">{abd_thisvoqty}</td>',
						'<td style="text-align:right">{abd_price}</td>',
						'<td style="text-align:right">{abd_costprice}</td>',
						'<td style="text-align:center">{ab_date}</td>',
					    '</tr>',
					    '</tpl>',
					    '</table></div></div>'
					);
					var navpanel = panel.up('navpanel');
					if(navpanel.data&&navpanel.data.payforAR){
						var payforAR = navpanel.data.payforAR;
						if(payforAR.useBillOutAR==1){
							tpl = new Ext.XTemplate( 
				   				'<hr/><div style="line-height:37px;font-size:16px;color:blue"><b>买方应付账款信息</b></div></div>',
								'<div style="margin-left:50px">',
								'<table border="1">',
								'<tr>',
								'<th style="width:150px;text-align:center">开票记录号</th>',
								'<th style="width:150px;text-align:center">物料编号</th>',
								'<th style="width:80px;text-align:center">开票数量</th>',
								'<th style="width:80px;text-align:center">开票单价</th>',
								'<th style="width:80px;text-align:center">成本单价</th>',
								'<th style="width:120px;text-align:center">开票日期</th>',
								'</tr>',
								'<tpl for=".">',
							    '<tr>',	     
							    '<td>{bi_code}</td>',
								'<td>{ard_prodcode}</td>',
								'<td style="text-align:right">{ard_nowqty}</td>',
								'<td style="text-align:right">{ard_nowprice}</td>',
								'<td style="text-align:right">{ard_costprice}</td>',
								'<td style="text-align:center">{bi_date}</td>',
							    '</tr>',
							    '</tpl>',
							    '</table></div></div>'
							);
						}
						tpl.overwrite(panel.getEl(),payforAR.data);	
					}else{
						tpl.overwrite(panel.getEl(),tpl);
					}
	   			}
	   		}
	   	});
   	}
});