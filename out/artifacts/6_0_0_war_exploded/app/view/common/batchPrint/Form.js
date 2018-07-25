Ext.define('erp.view.common.batchPrint.Form',{ 
	extend: 'Ext.form.Panel', 
	alias: 'widget.erpBatchPrintFormPanel',
	id: 'printform', 
	source:'',
	requires: ['erp.view.core.button.BatchPrintByCondition','erp.view.core.button.BOMAttachDownload'],
    region: 'north',
    frame : true,
    header: false,//不显示title
	layout : 'column',
	autoScroll : true,
	defaultType : 'textfield',
	labelSeparator : ':',
	buttonAlign : 'center',
	fieldDefaults : {
	       fieldStyle : "background:#FFFAFA;color:#515151;",
	       labelAlign : "right",
	       blankText : $I18N.common.form.blankText
	},
	FormUtil: Ext.create('erp.util.FormUtil'),
	tbar: {padding:'0 0 5 0',defaults:{margin:'0 5 0 0'},items:[{
		name: 'query',
		text: $I18N.common.button.erpQueryButton,
		iconCls: 'x-button-icon-query',
    	cls: 'x-btn-gray',
    	handler: function(){
			var grid = Ext.getCmp('batchPrintGridPanel');
			var form = Ext.getCmp('printform');
			var gridParam = {caller: caller, condition: form.getCondition(), start: 1, end: 1000};
			form.beforeQuery(caller, gridParam.condition);//执行查询前逻辑
			grid.GridUtil.loadNewStore(grid, gridParam);
    	}
	},{
		name: 'ertBOMAttachDownloadButton',
		id: 'ertBOMAttachDownloadButton',
		text: $I18N.common.button.ertBOMAttachDownloadButton,
		xtype:'ertBOMAttachDownloadButton',
    	hidden: true
	},{
		name: 'printByCondition',
		id: 'printByCondition',
		text: $I18N.common.button.erpPrintByConditionButton,
		xtype:'erpBatchPrintByConditionButton',
		iconCls: 'x-button-icon-print',
    	cls: 'x-btn-gray',
    	hidden: true
    },{
    	id:'batchPrint',
		name: 'batchPrint',
		text: $I18N.common.button.erpVastPrintButton,
    	iconCls: 'x-button-icon-print',
    	cls: 'x-btn-gray',
    	handler:function(btn){
    		var me = this;
    		var form = btn.ownerCt.ownerCt, grid = form.ownerCt.down('grid');
    		var keyField = form.fo_keyField;
    		var items = grid.getMultiSelected();
    		if (items.length>1000){
    			showMessage('提示', '勾选行数必须小于1000条!');
				return;
    		}
    		var ids =new Array();
    		var length = items.length;
    		var idStr ='';
    		Ext.each(items,function(item,index){
    			if(length!=index+1){
    				idStr = idStr+item.data[keyField]+',';
    			}else{
    				idStr = idStr+item.data[keyField];
    			}
    		});
    		console.log(1);
    		if(printType=='jasper'){
    			Ext.getCmp('printform').jasperReportPrint(caller,idStr);
    		}else{    		
    		var title = form.title;
    		var reportName="";
			var condition="";
			var todate='';
        	var dateFW='';
        	var fromdate='';
        	var enddate='';
    		if (title=='销售变更单打印'){
    			reportName="SaleChange_Batch";
    			condition='{SaleChange.sc_id}in[ '+idStr+' ]';
    		}else if(title=="出货通知单打印"){
    			reportName="SendNotify_batch";
    			condition='{SendNotify.sn_id}in[ '+idStr+' ]';
    		}else if(title=="出货单打印"){
    			reportName="sendlist_yessale_batch";
    			condition='{ProdInout.pi_id}in[ '+idStr+' ]';
    		}else if(title=="出货单Invoice打印"){
    			reportName="sendlist_yessale_lhct";
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
    		}else if(title=="工单备料单批量打印"){
    			reportName="fybl_n_huasl";
    			condition='{MA_MAKEMATERIAL_VIEW.ma_id}in[ '+idStr+' ]';
    		}else if(title=="费用比例报销单批量打印"){
    			reportName="MakeUsagePrebymake_batch";
    			condition='{CUSTOMTABLE.CT_ID}in[ '+idStr+' ]';
    		}else if(title=="BOM多级展开"){
    			reportName="bomLevel3";
    			var boid = Ext.getCmp('bo_id').value;
    			condition='{MA_BOMSTRUCT_VIEW.bs_topbomid}='+ boid;
    		}else if(title=="物料综合查询"){
    			reportName="ProductBalance";
    			var prodcode = Ext.getCmp('pr_code').getValue();
    			if(prodcode!=""){
    				condition='{Product.pr_code}='+"'"+prodcode+"'";
    			}
    		}else if(title=="FQC检验报告打印"){
    			reportName="verifyMake_Batch";
    			condition='{QUA_VerifyApplyDetail.ve_id}in[ '+idStr+' ]';
    		}else if(title=="凭证批量打印"){
    			reportName="vouclist_rmb";
    			condition='{voucher.vo_id}in[ '+idStr+' ]';
    		}else if(title=="采购验收单批量打印"){
    			reportName="acclist_batch";
    			condition='{ProdInout.pi_id}in[ '+idStr+' ]';
    		}else if(title=="费用批量打印"){
    			reportName="AccountRegZW_batch";
    			condition='{FeePlease.fp_id}in[ '+idStr+' ]';
    		}else if(title=="费用批量打印(无备注)"){
    			reportName="AccountRegZW_batch_no";
    			condition='{FeePlease.fp_id}in[ '+idStr+' ]';
    		}else if(title=="IQC/FQC批量打印"){
    			reportName="verifyMake_batch";
    			condition='{QUA_VerifyApplyDetail.ve_id}in[ '+idStr+' ]';
    		}else if(title=='拨出单批量打印'){
    			reportName='piolist_bcbatch';
    			condition='{prodinout_makematerialout_VIEW.pi_id}in[ '+idStr+' ]';
    		}else if(title=='委外领料单批量打印'){
    			reportName='piolist_wwbatch';
    			condition='{prodinout_common_view.pi_id}in[ '+idStr+' ]';
    		}else if(title=='盘点底稿批量打印'){
    			reportName='StockTaking_Batch';
    			condition='{StockTaking.st_id}in[ '+idStr+' ]';
    		}else if(title=='逾期应收批量打印'){
    			reportName='ANTICIPATE_BATCH';
    			condition='{ANTICIPATE.AN_ID}in['+idStr+' ]';
    		}else if(title=='委外单批量打印'){
    			reportName='MAKEWW_BATCH';
    			condition='{Make.ma_id}in['+idStr+' ]';
    		}
    		if(title == '出货单标签批量打印'){//
    			condition='{ProdInout.pi_id}in[ '+idStr+' ]';
    			if(items.length == 0){
    				showError("请勾选需要打印标签的出货单！");
    				return ;
    			}
    			//没有指定标签格式时，
    			var labelcode = Ext.getCmp("lps_code");
    			if(labelcode && (labelcode.value == null || labelcode.value == "") || !labelcode){
    				//检查勾选的出货单是否有不同的默认标签格式
    				form.getCustLabelCode(idStr,function(data){ 	  		    	
		    		    if(data != null){
		    				 form.getPrintUrl(data.LPS_CALLER,data.LPS_LABELURL,title,condition,todate,dateFW,fromdate,enddate,form,idStr);	
		    		    }	
    				});  				
    			}else{    				
    			  //如果有指定“标签格式”，以选择的为准调取这一格式对应的报表，否则取客户默认的标签格式
    			  warnMsg("按照指定的标签格式打印?", function(btn){
    					if(btn == 'yes'){
    					   //打印
	    					form.getPrintCaller(labelcode.value,function(data){ 		    	
					    		   if(data != null){
					    				form.getPrintUrl(data.LPS_CALLER,data.LPS_LABELURL,title,condition,todate,dateFW,fromdate,enddate,form,idStr);	
					    		  }					    		  
				    	  	});	
    					}
    			 });
    			}
    		}else if(title == '委外单批量打印'){
    			var items = grid.selModel.selected.items;
    			if(items.length>1){
	    			for(var i = 0 ; i < items.length ; i++){
	    				for(var j = i+1 ; j<items.length ; j++ ){
	    					if(items[i].data.ma_vendcode != items[j].data.ma_vendcode){
	    						showError('勾选的明细不是都为同一委外商,请重新勾选!');
	    						return;
	    					}
	    				}
	    			}
    			}
    			form.getPrintUrl(caller,reportName,title,condition,todate,dateFW,fromdate,enddate,form,idStr);
    		}else if(title == '逾期应收批量打印'){
    			var items = grid.selModel.selected.items;
    			if(items.length>1){
	    			for(var i = 0 ; i < items.length ; i++){
	    				for(var j = i+1 ; j<items.length ; j++ ){
	    					if(items[i].data.an_custcode != items[j].data.an_custcode||items[i].data.an_currency != items[j].data.an_currency){
	    						showError('您选择的逾期应收单不是同客户同币别,不允许合并打印!');
	    						return;
	    					}
	    					
	    				}
	    			}
    			}
    			form.getPrintUrl(caller,reportName,title,condition,todate,dateFW,fromdate,enddate,form,idStr);
    		}else{//直接打印
    			form.getPrintUrl(caller,reportName,title,condition,todate,dateFW,fromdate,enddate,form,idStr);
    		}
    		//reportName="rpts/"+reportName+'.rpt';
    		//=======================================================    	 
    		}
    	}
	},{
    	name: 'export',
		text: $I18N.common.button.erpExportButton,
		iconCls: 'x-button-icon-excel',
    	cls: 'x-btn-gray',
    	handler: function(btn){
    		var form = btn.ownerCt.ownerCt,
				grid = Ext.getCmp('batchPrintGridPanel');
			var cond = form.getCondition();
			if(Ext.isEmpty(cond)) {
				cond = '1=1';
			}
			grid.BaseUtil.createExcel(caller, 'detailgrid', cond);
    	}
    },'->',{
    	margin:'0',
		text: $I18N.common.button.erpCloseButton,
		iconCls: 'x-button-icon-close',
    	cls: 'x-btn-gray',
    	handler: function(){
    		var main = parent.Ext.getCmp("content-panel"); 
    		main.getActiveTab().close();
    	}
	}]},
	initComponent : function(){ 
		var source = getUrlParam('source');//从url解析参数
		this.source=source;
		var urlcondition = getUrlParam('condition');//从url解析参数
		urlcondition = (urlcondition == null) ? "" : urlcondition.replace(/IS/g,"=");
		var param = {caller: caller, condition: urlcondition};
    	//this.FormUtil.getItemsAndButtons(this, 'common/singleFormItems.action', param);
		this.getItemsAndButtons(this, 'common/singleFormItems.action', param);
		this.callParent(arguments);
		this.addKeyBoardEvents();
	},
	getCondition: function(){
		var grid = Ext.getCmp('batchPrintGridPanel');
		var form = Ext.getCmp('printform');
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
		if(condition == '' || condition == null){	/*2018060640 支持条件为空时，筛选所有数据*/
			condition = '1=1';
		}
		return condition;
	},
	/**
	 * 监听一些事件
	 * <br>
	 * Ctrl+Alt+S	单据配置维护
	 * Ctrl+Alt+P	参数、逻辑配置维护
	 */
	addKeyBoardEvents: function(){
		var me = this;
		Ext.EventManager.addListener(document.body, 'keydown', function(e){
			if(e.altKey && e.ctrlKey) {
				if(e.keyCode == Ext.EventObject.S) {
					var url = "jsps/ma/form.jsp?formCondition=fo_idIS" + me.fo_id + "&gridCondition=fd_foidIS" + me.fo_id, 
						forms = Ext.ComponentQuery.query('form'), 
						grids = Ext.ComponentQuery.query('gridpanel'),
						formSet = [], gridSet = [];
					if(forms.length > 0) {
						Ext.Array.each(forms, function(f){
							f.fo_id && (formSet.push(f.fo_id));
						});
					}
					if(grids.length > 0) {
						Ext.Array.each(grids, function(g){
							if(g.xtype.indexOf('erpBatchPrintGridPanel') > -1)
								gridSet.push(window.caller);
							else if(g.caller)
								gridSet.push(g.caller);
						});
					}
					if(formSet.length > 0 || gridSet.length > 0) {
						url = "jsps/ma/multiform.jsp?formParam=" + formSet.join(',') + '&gridParam=' + gridSet.join(',');
					}
					me.FormUtil.onAdd('form' + caller, 'Form配置维护(' + caller + ')', url);
				} else if(e.keyCode == Ext.EventObject.P) {
					me.FormUtil.onAdd('configs-' + caller, '逻辑配置维护(' + caller + ')', "jsps/ma/logic/config.jsp?whoami=" + caller);
				}
			}
		});
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
	},
	getPrintCaller:function(code,callback){
	      Ext.Ajax.request({
	 		  url : basePath + "pm/mes/getPrintCaller.action",
			  params : {
			  	 caller:caller,
			  	 code  :code
			  },
			  method : 'post',
	 		  callback : function(opt, s, res){
	 			   var r = new Ext.decode(res.responseText);
	 			   if(r && r.exceptionInfo){
	 				   showError(r.exceptionInfo);return;
	 			   } else if(r.data){		   
	 				  callback && callback.call(null, r.data);
	 			   }else{
	 			   	  return;
	 			   }
	 		   }
	 	  });
	  },
	  getPrintUrl:function(caller,reportName,title,condition,todate,dateFW,fromdate,enddate,form,idStr){
		  var me = this;
		  Ext.Ajax.request({
		       url : basePath + 'common/enterprise/getprinturl.action?caller=' + caller,
		   	   callback: function(opt, s, r) {
		   			var re = Ext.decode(r.responseText);
		   			if(re.printtype=="jasper"){
						form.jasperReportPrint(caller,idStr);
					}else{
						thisreport=re.reportname;
			   			if(re.defaultcondition!=null &&re.defaultcondition!=''){
			   				condition=re.defaultcondition.replace(/@IDS/g, idStr);
			   			}
			   			//===========================================
			   			var whichsystem = re.whichsystem;
						var urladdress = "";
						var rpname = re.reportName;
						if(Ext.isEmpty(rpname) || rpname == "null"){
							urladdress = re.printurl;
						} else if(rpname.indexOf(thisreport) > 0){
							urladdress = re.ErpPrintLargeData;
						} else{
							urladdress = re.printurl;
						}
						if(thisreport==""||thisreport==null||thisreport=='null'){
							thisreport=reportName;
						}
					   	//form.FormUtil.batchPrint(idStr,reportName,condition,title,todate,dateFW,fromdate,enddate,urladdress,whichsystem);
					   	form.FormUtil.batchPrint(idStr,thisreport,condition,title,todate,dateFW,fromdate,enddate,urladdress,whichsystem);
					   	 //在这里传条件和报表名字
					}
		   			
		   		}
	   		});
	  },
	  getCustLabelCode:function(condition,callback){
	  	   Ext.Ajax.request({
		   		url : basePath + 'scm/customer/getCustLabelCode.action',
		   		params : {			
				  	 condition  :condition
			    },
		   		callback: function(opt, s, r) {
		   			var r = Ext.decode(r.responseText); 
		   			if(r && r.exceptionInfo){
	 				   showError(r.exceptionInfo);return;
	 			    }else if(r.data){	
	 			    	 callback && callback.call(null, r.data);
	 			    }		   			
		   		}
	   		});
	  },
	  jasperReportPrint:function(caller,idStr){
		var me=this;
	  	if(idStr.length==0){
	  		showError('未勾选任何明细');
	  	}else{
		  	var form=this;
		  	form.setLoading(true);
		  	//调用后台存储过程，报表默认为原报表名
		  	Ext.Ajax.request({
		  		url : basePath +'common/JasperReportPrint/JasperGetReportnameByProcedure.action',
		  		params : {
		  					ids:idStr,
		  					caller:caller,
							reportname:''
		  		},
		  		method : 'post',
				timeout: 360000,
				callback : function(options,success,response){
					var res = new Ext.decode(response.responseText);
					if(res.success){
						//对返回的报表名称进行解析，循环调用jasper打印方法
						var str=res.reportname.split("#");
						if(str!=""&&str!=null&&str.length>0){
							for(i=0;i<=str.length;i++){
								if(str[i]!=""&&str[i] != null && str[i].length > 0){
									var reportname=str[i];
								Ext.Ajax.request({
							    	url : basePath +'common/JasperReportPrint/batchPrint.action',
									params: {
										ids:idStr,
										caller:caller,
										reportname:reportname
									},
									method : 'post',
									async:false,
									timeout: 360000,
									callback : function(options,success,response){
										form.setLoading(false);
										var res = new Ext.decode(response.responseText);
										if(res.success){
											me.openWindowWithPost(res.info.printurl,res.info.userName,reportname,
													res.info.whereCondition,res.info.printtype,encodeURIComponent(res.info.title)); //改用post方式 解决where条件过长的问题
										}else if(res.exceptionInfo){
											var str = res.exceptionInfo;
											showError(str);return;
										}
									}
							    });
								}							
							}
						}
						
					}else{
							Ext.Ajax.request({
							    	url : basePath +'common/JasperReportPrint/batchPrint.action',
									params: {
										ids:idStr,
										caller:caller
									},
									method : 'post',
									timeout: 360000,
									callback : function(options,success,response){
										form.setLoading(false);
										var res = new Ext.decode(response.responseText);
										if(res.success){
											me.openWindowWithPost(res.info.printurl,res.info.userName,res.info.reportName,
													res.info.whereCondition,res.info.printtype,encodeURIComponent(res.info.title)); //改用post方式 解决where条件过长的问题
										}else if(res.exceptionInfo){
											var str = res.exceptionInfo;
											showError(str);return;
										}
									}
							    });
					
								}
				}
		  	});
		  }
	  },
  	  openWindowWithPost : function (url,userName,reportName,whereCondition,printType,titlename){
  		  // ie下跨域document权限问题
  	  	  var me=this;
		  var newWindow = window.open(Ext.isIE ? '' : url, '_blank');  
	      if (!newWindow)  {
	    	  return false;
	      } 
	      var html = "";  
	      html += "<html><head></head><body><form id='batchprintpostwin' method='post' action='" + url + "'>";   	      
	      html += "<input type='hidden' name='userName' value='" + userName + "'/>"; 
	      html += "<input type='hidden' name='reportName' value='" + reportName + "'/>"; 
	      html += "<input type='hidden' name='whereCondition' value='" + me.repleceQuote(whereCondition) + "'/>"; 
	      html += "<input type='hidden' name='printType' value='" + printType + "'/>"; 
	      html += "<input type='hidden' name='title' value='" + titlename + "'/>"; 
	      html += "</form><script type='text/javascript'>document.getElementById('batchprintpostwin').submit();";  
	      html += "<\/script></body></html>".toString().replace(/^.+?\*|\\(?=\/)|\*.+?$/gi, "");   
	      newWindow.document.write(html);    	        
	      return newWindow; 
	  },
	  repleceQuote: function(str) {
	  	//对冒号进行替换，保证传递值不会丢失。
		if (str.indexOf("'")) {
			return str.replace(/'/g,"&#39;");
		}else return str;
	  } ,
	  getItemsAndButtons: function(form, url, param){
		var me = this, tab = me.FormUtil.getActiveTab();
		me.setLoading(true);
		Ext.Ajax.request({//拿到form的items
			url : basePath + url,
			params: param,
			method : 'post',
			callback : function(options, success, response){
				me.setLoading(false);
				if (!response) return;
				var res = new Ext.decode(response.responseText);
				if(res.exceptionInfo != null){
					showError(res.exceptionInfo);return;
				}
				form.fo_id = res.fo_id;
				form.fo_keyField = res.keyField;
				form.tablename = res.tablename;//表名
				if(res.keyField){//主键
					form.keyField = res.keyField;
				}
				if(res.statusField){//状态
					form.statusField = res.statusField;
				}
				if(res.statuscodeField){//状态码
					form.statuscodeField = res.statuscodeField;
				}
				if(res.codeField){//Code
					form.codeField = res.codeField;
				}
				if(res.dealUrl){
					form.dealUrl = res.dealUrl;
				}
				if(res.mainpercent && res.detailpercent){
					form.mainpercent = res.mainpercent;
					form.detailpercent = res.detailpercent;
				}
				form.fo_detailMainKeyField = res.fo_detailMainKeyField;//从表外键字段
				//data&items
				var items = me.FormUtil.setItems(form, res.items, res.data, res.limits, {
					labelColor: res.necessaryFieldColor
				});
				form.add(items);
				//title
				if(res.title && res.title != ''){
					if(form.source=='allnavigation'){
						form.setTitle(res.title+'<font color=red>[界面展示]</font>');
						}else{
						form.title=res.title;
					}
					var _tt = res.title;
					if(form.codeField) {
						var _c = form.down('#' + form.codeField);
						if( _c && !Ext.isEmpty(_c.value) )
							_tt += '(' + _c.value + ')';
					}
				    if(tab && tab.id!='HomePage') {
						try {
			                tab.setTitle(_tt);
			            } catch (e) {
			            }
				    }				     
				}
				var buttonString = res.buttons;//支持配置批量按条件打印按钮
				if(buttonString != null && buttonString != ''&& contains(buttonString, 'erpBatchPrintByConditionButton', true)){
					Ext.getCmp('printByCondition').show();
					Ext.getCmp('batchPrint').hide();//配置按条件打印时隐藏原来的批量打印按钮
				}
				if(buttonString != null && buttonString != ''&& contains(buttonString, 'ertBOMAttachDownloadButton', true)){
					Ext.getCmp('ertBOMAttachDownloadButton').show();
				}
				form.fireEvent('afterload', form);
			}
		});
	}
});