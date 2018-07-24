Ext.define('erp.view.scm.product.IntegratedQuery.Form',{ 
	extend: 'Ext.form.Panel', 
	alias: 'widget.erpIntegratedQueryFormPanel',
	id: 'integratedform', 
    region: 'north',
    frame : true,
    header: false,//不显示title
	layout : 'column',
	autoScroll : true,
	defaultType : 'textfield',
	labelSeparator : ':',
	buttonAlign : 'center',
	fieldDefaults : {
	       margin : '2 2 2 2',
	       fieldStyle : "background:#FFFAFA;color:#515151;",
	       labelAlign : "right",
	       blankText : $I18N.common.form.blankText
	},
	FormUtil: Ext.create('erp.util.FormUtil'),
	tbar: [{
		name: 'query',
		text: $I18N.common.button.erpQueryButton,
		iconCls: 'x-button-icon-query',
    	cls: 'x-btn-gray',
    	handler: function(){
			var grid = Ext.getCmp('integratedQueryGridPanel');
			var form = Ext.getCmp('integratedform');
			var gridParam = {caller: caller1, condition: form.getCondition(), start: 1, end: 1000};
			console.log(caller1);
			form.beforeQuery(caller1, gridParam.condition);//执行查询前逻辑
			grid.GridUtil.loadNewStore(grid, gridParam);
    	}
	}, '->', {
		name: 'batchPrint',
		text: $I18N.common.button.erpVastPrintButton,
    	iconCls: 'x-button-icon-print',
    	cls: 'x-btn-gray',
    	handler:function(btn){
    		var form = btn.ownerCt.ownerCt, grid = form.ownerCt.down('grid');
    		var keyField = form.fo_keyField;
    		var items = grid.getMultiSelected();
    		var ids =new Array();
    		var length = items.length;
    		var idStr ='';
    		Ext.each(items,function(item,index){
    			if(length!=index+1){
    				//idStr = idStr+'[[['+item.data[keyField]+']]],';
    				idStr = idStr+item.data[keyField]+',';
    			}else{
    				idStr = idStr+item.data[keyField];
    			}
//    			var id =item.data[keyField];
//    			ids.push(id);
    		});
    		var title = form.title;
    		var reportName="";
			var condition="";
    		if (title=='销售变更单打印'){
    			reportName="SaleChange_Batch";
    			condition='{SaleChange.sc_id}in[ '+idStr+' ]';
    		}else if(title=="出货通知单打印"){
    			reportName="SendNotify_batch";
    			condition='{SendNotify.sn_id}in[ '+idStr+' ]';
    		}else if(title=="出货单打印"){
    			reportName="sendlist_yessale_batch";
    			condition='{ProdInout.pi_id}in[ '+idStr+' ]';
    		}else if(title=="请购单打印"){
    			reportName="application_batch";
    			condition='{application.ap_id}in[ '+idStr+' ]';
    		}
    		else if(title=="采购变更单打印"){
    			reportName="PURCChange_batch";
    			condition='{PurchaseChange.pc_id}in[ '+idStr+' ]';
    		}else if(title=="采购收料单打印"){
    			reportName="VerifyApply_batch";
    			condition='{VerifyApply.va_id}in[ '+idStr+' ]';
    		}else if(title=="估价单打印"){
    			reportName="sale_gj_batch";
    			condition='{evaluation.ev_id}in[ '+idStr+' ]';
    		}else if(title=="报价单打印"){
    			reportName="QUOTLIST_batch";
    			condition='{Quotation.qu_id}in[ '+idStr+' ]';
    		}else if(title=="销售订单评审表打印"){
    			reportName="SaleForecastAudit1_batch";
    			condition='{SaleForecast.sf_id}in[ '+idStr+' ]';
    		}else if(title=="借货出货单打印"){
    			reportName="jhch";
    			condition='{ProdInout.pi_id}in[ '+idStr+' ]';
    		}else if(title=="销售退货单打印"){
    			reportName="retulist_batch";
    			condition='{ProdInout.pi_id}in[ '+idStr+' ]';
    		}else if(title=="采购物料明细表"){
    			reportName="ProductBuy";
    			condition='{Purchase.pu_id}in[ '+idStr+' ]';
    		}else if(title=="收料明细打印"){
    			reportName="VerifyApplyDetail";
    			condition='{verifyapply.va_id}in[ '+idStr+' ]';
    		}else if(title=="采购退料单打印"){
    			reportName="piolist_yt_batch";
    			condition='{ProdInout.pi_id}in[ '+idStr+' ]';
    		}else if(title=="采购退货清单打印"){
    			reportName="piolistytList";
    			condition='{ProdInout.pi_id}in[ '+idStr+' ]';
    		}else if(title=="采购退货清单打印"){
    			reportName="piolistytList";
    			condition='{ProdInout.pi_id}in[ '+idStr+' ]';
    		}else if(title=="采购单批量打印"){
    			reportName="PURCLIST_Batch";
    			condition='{purchase.pu_id}in[ '+idStr+' ]';
    		}else if(title=="未审核采购单打印"){
    			reportName="PURCLIST_Batch";
    			condition='{purchase.pu_id}in[ '+idStr+' ]';
    		}else if(title=="采购单打印(英文版)"){
    			reportName="PURCLIST_ENGLISH";
    			condition='{purchase.pu_id}in[ '+idStr+' ]';
    		}else if(title=="采购预测单打印"){
    			reportName="PurchaseYC_Batch";
    			condition='{PurchaseForecast.pf_id}in[ '+idStr+' ]';
    		}else if(title=="委外加工单资料打印"){
    			reportName="MAKEWW_Batch";
    			condition='{make.ma_id}in[ '+idStr+' ]';
    		}else if(title=="委外领料单打印"){
    			reportName="PIOLISTMWW_Batch";
    			condition='{ProdInout.pi_id}in[ '+idStr+' ]';
    		}else if(title=="委外退料单打印"){
    			reportName="EXPLIST_yt_Batch";
    			condition='{ProdInout.pi_id}in[ '+idStr+' ]';
    		}else if(title=="委外补料单打印"){
    			reportName="Expiolist_Batch";
    			condition='{ProdInout.pi_id}in[ '+idStr+' ]';
    		}else if(title=="委外报废单打印"){
    			reportName="MakeScrapWW_Batch";
    			condition='{ProdInout.pi_id}in[ '+idStr+' ]';
    		}else if(title=="制造单资料打印"){
    			reportName="MakeList";
    			condition='{make.ma_id}in[ '+idStr+' ]';
    		}else if(title=="生产领料单打印"){
    			reportName="PIOLISTM_Batch";
    			condition='{ProdInout.pi_id}in[ '+idStr+' ]';
    		}else if(title=="采购退货单打印"){
    			reportName="PIOLISTM_Back";
    			condition='{ProdInout.pi_id}in[ '+idStr+' ]';
    		}else if(title=="生产补料单打印"){
    			reportName="PIOLIST_bl_Batch";
    			condition='{ProdInout.pi_id}in[ '+idStr+' ]';
    		}else if(title=="生产报废单打印"){
    			reportName="MakeScrap_Batch";
    			condition='{make.ma_id}in[ '+idStr+' ]';
    		}else if(title=="完工入库单打印"){
    			reportName="finish_Batch";
    			condition='{ProdInout.pi_id}in[ '+idStr+' ]';
    		}else if(title=="拆件完工入库单打印"){
    			reportName="chaijian_Batch";
    			condition='{ProdInout.pi_id}in[ '+idStr+' ]';
    		}else if(title=="BOM多级展开"){
    			reportName="bomlevel3";
    			var boid = Ext.getCmp('bo_id').getValue();
    			var kind=Ext.getCmp('bo_remark').getValue();
    			console.log(kind);
    			console.log(boid);
    			condition='{BomStruct.bs_topbomid}in[ '+boid+' ]';
    			if(kind=="原材料"){
    				condition+=' and '+'{BomStruct.bs_sonbomid}='+"0";
    			}
    			if(kind=="半成品"){
    				condition+=' and '+'(isnull({product.pr_manutype}) or {product.pr_manutype}<>'+"'PURCHASE'"+') and '+'{BomStruct.bs_sonbomid}>'+"0";
    			}
    			console.log(condition);
    		}else if(title=="物料综合查询"){
    			reportName="ProductBalance";
    			var prodcode = Ext.getCmp('pr_code').getValue();
    			if(prodcode!=""){
    				condition='{Product.pr_code}='+"'"+prodcode+"'";
    			}
    		}else if(title=="FQC检验报告打印"){
    			reportName="verifyMake_Batch";
    			condition='{QUA_VerifyApplyDetail.ve_id}in[ '+idStr+' ]';
    		    console.log(condition);
    		}else if(title=="凭证批量打印"){
    			reportName="vouclist_rmb";
    			condition='{voucher.vo_id}in[ '+idStr+' ]';
    		    console.log(condition);
    		}else if(title=="采购验收单批量打印"){
    			reportName="acclist_batch";
    			condition='{ProdInout.pi_id}in[ '+idStr+' ]';
    		    console.log(condition);
    		}else if(title=="费用批量打印"){
    			reportName="AccountRegZW_batch";
    			condition='{FeePlease.fp_id}in[ '+idStr+' ]';
    		    console.log(condition);
    		}
    		
    		form.FormUtil.batchPrint(idStr,reportName,condition,title);
    		
    	}
	},'-',{
    	name: 'export',
		text: $I18N.common.button.erpExportButton,
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	handler: function(btn){
    		var form = btn.ownerCt.ownerCt,
				grid = Ext.getCmp('integratedQueryGridPanel');
			var cond = form.getCondition();
			if(Ext.isEmpty(cond)) {
				cond = '1=1';
			}
			grid.BaseUtil.createExcel(caller1, 'detailgrid', cond);
    	}
    },'-',{
		text: $I18N.common.button.erpCloseButton,
		iconCls: 'x-button-icon-close',
    	cls: 'x-btn-gray',
    	handler: function(){
    		var main = parent.Ext.getCmp("content-panel"); 
    		main.getActiveTab().close();
    	}
	}],
	initComponent : function(){ 
		var urlcondition = getUrlParam('condition');//从url解析参数
		urlcondition = (urlcondition == null) ? "" : urlcondition.replace(/IS/g,"=");
		var param = {caller: caller1, condition: urlcondition};
    	this.FormUtil.getItemsAndButtons(this, 'common/singleFormItems.action', param);
		this.callParent(arguments);
		this.addKeyBoardEvents();
	},
	getCondition: function(){
		var grid = Ext.getCmp('integratedQueryGridPanel');
		var form = Ext.getCmp('integratedform');
		var condition = grid.defaultCondition || '';
		Ext.each(form.items.items, function(f){
			if(f.logic != null && f.logic != ''){
				if(f.xtype == 'checkbox' && f.value == true){
					if(condition == ''){
						condition += f.logic;
					} else {
						condition += ' AND ' + f.logic;
					}
				} else if(f.xtype == 'datefield' && f.value != null){
					var v = Ext.Date.format(new Date(f.value), 'Y-m-d');
					if(condition == ''){
						condition += f.logic + "=to_date('" + v + "', 'yyyy-MM-dd')";
					} else {
						condition += ' AND ' + f.logic + "=to_date('" + v + "', 'yyyy-MM-dd')";
					}
				} else if(f.xtype == 'datetimefield' && f.value != null){
					var v = Ext.Date.format(new Date(f.value), 'Y-m-d H:i:s');
					if(condition == ''){
						condition += f.logic + "=to_date('" + v + "', 'yyyy-MM-dd HH24:mi:ss')";
					} else {
						condition += ' AND ' + f.logic + "=to_date('" + v + "', 'yyyy-MM-dd HH24:mi:ss')";
					}
				} else if((f.xtype == 'numberfield' || f.xtype == 'monthdatefield') && f.value != null && f.value != ''){
					if(condition == ''){
						condition += f.logic + '=' + f.value;
					} else {
						condition += ' AND ' + f.logic + '=' + f.value;
					}
				} else if(f.xtype == 'combo' && f.value == '$ALL'){
					if(f.store.data.length > 1) {
						if(condition == ''){
							condition += '(';
						} else {
							condition += ' AND (';
						}
						var _a = '';
						f.store.each(function(d, idx){
							if(d.data.value != '$ALL') {
								if(_a == ''){
									_a += f.logic + "='" + d.data.value + "'";
								} else {
									_a += ' OR ' + f.logic + "='" + d.data.value + "'";
								}
							}
						});
						condition += _a + ')';
					}
				} else if(f.xtype == 'erpTextAreaSelectField'){

					

		    		var text = f.rawValue;
		    		var codes = text.split(/\n/);
		    		for(var i=0; i<codes.length; i++){
		    			if(codes[i] == ''){
		    				codes.splice(i,1);
		    			}
		    			
		    		}
		    		
		    		if(codes.length>0){
						if(condition == ''){
							condition += f.logic;
						} else {
							condition += " AND " + f.logic;
						}
		    			condition += " IN (";
			    		for(var i=0, len=codes.length; i<len; i++){
			    			
			    			if (i != len-1){
			    				condition += " '"+codes[i]+"', ";
			    			
			    			} else {
			    				condition += " '"+codes[i]+"') ";
			    			
			    			}
			    			
			    		}
		    		}

					
				} else {
					if(contains(f.logic, 'to:', true)){
						if(!grid.toField){
							grid.toField = new Array();
						}
						grid.toField.push(f.logic.split(':')[1]);
					} else {
						if(!Ext.isEmpty(f.value)){
							if(contains(f.value.toString(), 'BETWEEN', true) && contains(f.value.toString(), 'AND', true)){
								if(condition == ''){
									condition += f.logic + " " + f.value;
								} else {
									condition += ' AND (' + f.logic + " " + f.value + ")";
								}
							} else if(contains(f.value.toString(), '||', true)){
								var str = '';
								Ext.each(f.value.split('||'), function(v){
									if(v != null && v != ''){
										if(str == ''){
											str += f.logic + "='" + v + "'";
										} else {
											str += ' OR ' + f.logic + "='" + v + "'";
										}
									}
								});
								if(condition == ''){
									condition += "(" + str + ")";
								} else {
									condition += ' AND (' + str + ")";
								}
							} else if(f.value.toString().charAt(0) == '!'){ 
								if(condition == ''){
									condition += 'nvl(' + f.logic + ",' ')<>'" + f.value.substr(1) + "'";
								} else {
									condition += ' AND (nvl(' + f.logic + ",' ')<>'" + f.value.substr(1) + "')";
								}
							} else {
								if(!Ext.isNumber(f.value) && f.value.indexOf('%') >= 0) {
									if(condition == ''){
										condition += f.logic + " like '" + f.value + "'";
									} else {
										condition += ' AND (' + f.logic + " like '" + f.value + "')";
									}
								} else {
									if(condition == ''){
										condition += f.logic + "='" + f.value + "'";
									} else {
										condition += ' AND (' + f.logic + "='" + f.value + "')";
									}
								}
							}
						}
					}
				}
			}
		});
		return condition;
	},
	addKeyBoardEvents: function(){
		var me = this;
		if(Ext.isIE && !Ext.isIE11){
			document.body.attachEvent('onkeydown', function(){//ie的事件名称不同,也不支持addEventListener
				if(window.event.altKey && window.event.ctrlKey && window.event.keyCode == 83){
					me.FormUtil.onAdd('form' + caller1, 'Form配置维护(' + caller1 + ')', "jsps/ma/multiform.jsp?formCondition=fo_idIS" + me.fo_id + 
							"&gridCondition=fd_foidIS" + me.fo_id + "&whoami=" + caller1);
				}
			});
		} else {
			document.body.addEventListener("keydown", function(e){
				if(Ext.isFF5){//firefox不支持window.event
					e = e || window.event;
				}
				if(e.altKey && e.ctrlKey && e.keyCode == 83){
					me.FormUtil.onAdd('form' + caller1, 'Form配置维护(' + caller1 + ')', "jsps/ma/multiform.jsp?formCondition=fo_idIS" + me.fo_id + 
							"&gridCondition=fd_foidIS" + me.fo_id + "&whoami=" + caller1);
				}
	    	});
		}
	},
	beforeQuery: function(call, cond) {
		Ext.Ajax.request({
			url: basePath + 'common/form/beforeQuery.action',
			params: {
				caller: call,
				condition: cond
			},
			async: false,
			callback: function(opt, s, r) {
				var rs = Ext.decode(r.responseText);
				if(rs.exceptionInfo) {
					showError(rs.exceptionInfo);
				}
			}
		});
	}
});