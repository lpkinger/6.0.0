Ext.QuickTips.init();
Ext.define('erp.controller.scm.sale.Sale', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','scm.sale.Sale','core.grid.Panel2','core.toolbar.Toolbar','core.form.FileField','core.form.MultiField','core.form.CheckBoxGroup',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload','core.button.ResAudit',
  			'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail','core.button.ResSubmit',
  			'core.button.Consign','core.button.TurnNotify','core.button.FeatureDefinition','core.button.PrintA4',
  			'core.button.FeatureView','core.button.OutSchedule','core.button.ResSubmitTurnSale','core.button.SubmitTurnSale','core.button.TurnNormalSale',
      		'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField','core.grid.YnColumn','core.button.PrintByCondition',
      		'core.button.CopyAll','core.button.RunATP','core.button.TurnProdInOutSale','core.button.TurnCustomer', 'core.button.RefreshSync',
      		'core.button.MrpOpen','core.button.MrpClose','core.button.RefreshQty','core.grid.YnColumnNV','core.button.LoadFitting', 
      		'core.button.End','core.button.BOMCost','core.button.ConfirmAgree','core.button.TurnB2CSaleOut','core.button.ModifyDetail','core.button.Sync',
      		'core.button.CopyByConfigs','core.button.TurnPurc','core.button.BackSale','core.button.Recheck','core.button.Resrecheck','core.form.SeparNumber',
      		'core.button.Commonquery','core.trigger.MultiDbfindTrigger','core.button.UpdateRemark','core.button.Modify','core.button.TurnAppropriate','core.button.LookPhoto'
      	],
    init:function(){
    	var me = this;
    	this.control({
    		'erpGridPanel2': {
    			cellclick:function(view,td,cellIndex,record,tr,rowIndex,e,eOpts){
    				var bindCode = "sd_prodcode,sd_qty,sd_price,sd_taxrate";
    				var field = view.ownerCt.columns[cellIndex].dataIndex;
			    	if(Ext.getCmp('sa_ordertype') && Ext.getCmp('sa_ordertype').value == "B2C"){
						if(bindCode.indexOf(field)>=0) return false;
						else return true;
					}
		        },
    			itemclick: function(selModel, record){
    				var bool = true;
    				if(record.data.sd_id != 0 && record.data.sd_id != null && record.data.sd_id != ''){
						var btn = Ext.getCmp('featuredefinition');
						btn && btn.setDisabled(false);
						btn = Ext.getCmp('featureview');
						btn && btn.setDisabled(false);
						btn = Ext.getCmp('outschedule');
						btn && btn.setDisabled(false);
						btn = Ext.getCmp('updatepmc');
						btn && btn.setDisabled(false);
						btn = Ext.getCmp('updateld');
						btn && btn.setDisabled(false);
						btn = Ext.getCmp('splitSaleButton');
						btn && btn.setDisabled(false);
						btn = Ext.getCmp('bomopen');
						btn && btn.setDisabled(false);
						btn = Ext.getCmp('updateDiscount');
						btn && btn.setDisabled(false); 
						btn = Ext.getCmp('MrpOpen');
						btn && btn.setDisabled(false); 
						btn = Ext.getCmp('MrpClose');
						btn && btn.setDisabled(false); 
						//设置载入配件按钮
					    var status = Ext.getCmp('sa_statuscode');
        				if(status && status.value == 'ENTERING'){      		
						    btn = Ext.getCmp("loadFittingbutton");
						    btn && btn.setDisabled(false); 
						}
					}   
					if(Ext.getCmp('sa_ordertype') && Ext.getCmp('sa_ordertype').value == "B2C"){
						bool = false;//商城订单不允许添加明细
					}
					 if(bool) this.onGridItemClick(selModel, record);
    			}
    		},
    		'erpBackSaleButton':{
	    		afterrender: function(btn){
    				if(Ext.getCmp('sa_ordertype') && Ext.getCmp('sa_ordertype').value == "B2C"){
    						btn.hide();
    				}
    			}
    		},
    		'erpBOMCostButton': {
    			click: function(btn) {
    				var form = btn.ownerCt.ownerCt, sa_id = Ext.getCmp('sa_id').value;
    				form.setLoading(true);
        			Ext.Ajax.request({
        				url: basePath + 'scm/sale/salebomcost.action',
        				params: {
        					sa_id: sa_id,
        					caller: caller
        				},
        				timeout: 600000,
        				callback: function(opt, s, r) {
        					form.setLoading(false);
        					var rs = Ext.decode(r.responseText);
        					if(rs.success) {
        						alert('计算完成!');
        						me.GridUtil.loadNewStore(form.ownerCt.down('grid'), {caller: caller, condition: 'sd_said=' + sa_id});
        					} else if(r.exceptionInfo) {
        						showError(r.exceptionInfo);
        					}
        				}
        			});
    			}
    		},
    		/**
    		 * 订单分拆
    		 */
    		'#splitSaleButton': {
    			click: function(btn) {
    				var record=btn.ownerCt.ownerCt.getSelectionModel().getLastSelected();    				    	
    			    me.SaleSplit(record);
    			}
    		},
    		/**
    		 * 更改PMC日期
    		 */
    		'#updatepmc': {
    			click: function(btn) {
    				var record=btn.ownerCt.ownerCt.getSelectionModel().getLastSelected();    				    	
    			    me.UpdatePmc(record);
    			}
    		},
    		/**
    		 * 更改LD料码，万利达专用
    		 * */
    		'#updateld':{
    			click: function(btn) {
    				var record=btn.ownerCt.ownerCt.getSelectionModel().getLastSelected();    				    	
    			    me.UpdateLD(record);
    			}
    		},
    		/**
    		 * BOM多级展开 
    		 */
    		'#bomopen': {
    			click: function(btn) {
    				var record=btn.ownerCt.ownerCt.getSelectionModel().getLastSelected();    
				      var pr_code=record.data.sd_prodcode;
						var url="jsps/pm/bom/BOMStructQuery.jsp?whoami=BOMStruct!Struct!Query";
						var condition="";
						//母件编号带出展开的料号不对  参照万利达配置
						if(pr_code){
						   condition+="pr_codeIS'"+pr_code+"'";
						}
						me.FormUtil.onAdd('BOMStruct'+ pr_code, 'BOM多级展开', url+"&condition="+condition);
    			}
    		},
    		/**
    		 * 更新比例
    		 */
    		'#updateDiscount': {
    			afterrender: function(btn) {
    				Ext.defer(function(){
    					var status = Ext.getCmp('sa_statuscode');
        				if(status && status.value == 'ENTERING'){
        					btn.hide();
        				}
    				}, 100);
    			},
    			click: function() {
    				me.updateDiscount();
    			}
    		},
    		'mfilefield':{//附件一直都可以上传下载
    			beforerender:function(f){
    				f.readOnly=false;
    			}
    		},
    		
    		/**
    		 * 载入配件
    		 * 
    		 */
    		'#loadFittingbutton': {
    			click: function(btn) {
    				//新产生的配件明细，存下原始订单序号到SD_MAKEID。
    				//载入之前判断此序号是否已经存在sd_makeid相等的明细行，如果有就不让再载入
    				var record = btn.ownerCt.ownerCt.getSelectionModel().getLastSelected();    				    	
    			    var pr_code = record.data.sd_prodcode;
    			    var detno = record.data.sd_detno;
    			    var sa_id = Ext.getCmp('sa_id').value;   			    
    			    var sd_qty = record.data.sd_qty;
    			    me.loadFitting (pr_code,sd_qty,sa_id,detno);
    			}
    		},
			'textfield[name=sa_custcode]':{
				beforerender: function(field){
					if(Ext.getCmp('sa_sourcecode')&&Ext.getCmp('sa_sourcecode').value){
						if(Ext.getCmp('sa_sourcetype')&& Ext.getCmp('sa_sourcetype').value=="报价单"){
							field.readOnly=true;
						}
					}
				}
			},			
    		'field[name=sa_currency]': {
    			beforetrigger: function(field) {
    				var t = field.up('form').down('field[name=sa_date]'),
    					value = t.getValue();
    				if(value) {
    					field.findConfig = 'cm_yearmonth=' + Ext.Date.format(value, 'Ym');
    				}
    			}
    		},
    		'erpSaveButton': {
    			click: function(btn){
    				var form = me.getForm(btn);
    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
    					me.getRandomNumber();//自动添加编号
    				}
    				this.beforeSaveSale();
    			}
    		},
    		'erpDeleteButton' : {
    			click: function(btn){
    				me.FormUtil.onDelete({id: Number(Ext.getCmp('sa_id').value)});
    			},
    			afterrender: function(btn){//商城订单不允许删除
    				if(Ext.getCmp('sa_ordertype') && Ext.getCmp('sa_ordertype').value == "B2C"){
    						btn.hide();
    				}
    			}
    		},
    		'erpUpdateButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('sa_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.beforeUpdate();
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addSale', '销售单', 'jsps/scm/sale/sale.jsp?whoami=' + caller);
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				me.FormUtil.beforeClose(me);
    			}
    		},
    		'dbfindtrigger[name=sa_toplace]': {
    			afterrender:function(trigger){
	    			trigger.dbKey='sa_custcode';
	    			trigger.mappingKey='cu_code';
	    			trigger.dbMessage='请先选客户编号！';
    			}
    		},
    		'dbfindtrigger[name=sa_paymentscode]': {
    			afterrender:function(trigger){
    				if(trigger.fieldConfig == 'PT') {
    	    			trigger.dbKey='sa_custcode';
    	    			trigger.mappingKey='cu_code';
    	    			trigger.dbMessage='请先选客户编号！';	
    				}
    			}
    		},
    		'dbfindtrigger[name=sd_custprodcode]': {
  			   focus: function(t){
  				   t.setHideTrigger(false);
  				   t.setReadOnly(false);
  				   if(Ext.getCmp('sa_custcode')){
  					   var cucode = Ext.getCmp('sa_custcode').value,
  					   	   record = Ext.getCmp('grid').selModel.lastSelected;
  					   if(Ext.isEmpty(cucode)){
 	    					 showError("请先选择客户编号!");
 	    					 t.setHideTrigger(true);
 	    					 t.setReadOnly(true);
 	    			   } else {
 	    				   t.dbBaseCondition = "pc_custcode='" + cucode + "'";
  					   }
  				   }
  			   	}
  		    },
    		'dbfindtrigger[name=sd_price]': {
 			   focus: function(t){
 				   t.setHideTrigger(false);
 				   t.setReadOnly(false);
 				   if(Ext.getCmp('sa_custcode')){
 					   var cucode = Ext.getCmp('sa_custcode').value,
 					   	   currency = Ext.getCmp('sa_currency').value;
 					   	   record = Ext.getCmp('grid').selModel.lastSelected,
   				           prodcode = record.data['sd_prodcode'];
 					   if(Ext.isEmpty(cucode)){
	    					 showError("请先选择客户编号!");
	    					 t.setHideTrigger(true);
	    					 t.setReadOnly(true);
	    			   } else if(Ext.isEmpty(currency)){
	    					 showError("请先填写币别!");
	    					 t.setHideTrigger(true);
	    					 t.setReadOnly(true);  
	    			   } else if(Ext.isEmpty(prodcode)){
	    					 showError("请先选择物料编号!");
	    					 t.setHideTrigger(true);
	    					 t.setReadOnly(true);  
	    			   } else {
	    				   t.dbBaseCondition = "SPD_ARCUSTCODE='" + cucode + "' and SPD_CURRENCY='" + currency + "' and spd_prodcode='" + prodcode + "'";
 					   }
 				   }
 			   	}
 		    },
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('sa_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: {
    				lock: 2000,
	                fn:function(btn){
    					me.FormUtil.onSubmit(Ext.getCmp('sa_id').value);
	                }
    			}
    		},
    		'erpMrpOpenButton' : {
    			click: function(btn){
					var grid = Ext.getCmp('grid');
					var record = grid.selModel.lastSelected;
					var id = record.data.sd_id;
					if (id && id>0){
						Ext.Ajax.request({
							url : basePath + "scm/sale/saleMrpOpen.action",
							params: {
								id:id,
								caller:caller
							},
							method : 'post',
							async: false,
							callback : function(options,success,response){
								var res = new Ext.decode(response.responseText);
								if(res.exceptionInfo){
									showError(res.exceptionInfo);
									return;
								}
								showError("打开Mrp成功！");
							}
						});
					}
					
				}
    		},
    		'erpMrpCloseButton' : {
    			click: function(btn){
					var grid = Ext.getCmp('grid');
					var record = grid.selModel.lastSelected;
					var id = record.data.sd_id;
					if (id && id>0){
						Ext.Ajax.request({
							url : basePath + "scm/sale/saleMrpClose.action",
							params: {
								id:id,
								caller:caller
							},
							method : 'post',
							async: false,
							callback : function(options,success,response){
								var res = new Ext.decode(response.responseText);
								if(res.exceptionInfo){
									showError(res.exceptionInfo);
									return;
								}
								showError("关闭Mrp成功！");
							}
						});
					}
					
				}
    		},
    		//刷新已转数
    		'erpRefreshQtyButton':{
    			click: function(btn){
    				warnMsg("确定要刷新已转数?", function(btn){
    					if(btn == 'yes'){
    						me.FormUtil.getActiveTab().setLoading(true);//loading...
    	    				Ext.Ajax.request({
    	    			   		url : basePath + 'scm/sale/refreshSaleYQTY.action',
    	    			   		params: {
    	    			   			id: Ext.getCmp('sa_id').value
    	    			   		},
    	    			   		method : 'post',
    	    			   		callback : function(options,success,response){
    	    			   			me.FormUtil.getActiveTab().setLoading(false);
    	    			   			var localJson = new Ext.decode(response.responseText);
    	    			   			if(localJson.exceptionInfo){
    	    			   				showError(localJson.exceptionInfo);
    	    			   			}
    	    		    			if(localJson.success){
    	    		    				Ext.Msg.alert("提示","刷新成功！");
    	    	        				window.location.reload();
    	    			   			}
    	    			   		}
    	    				});
    					}
    				});
    			}
    		},
    		//帕诺迪专用
    		'erpTurnCustomerButton': {
    			beforerender:function(btn){
    				btn.setText("收款情况");
    			},
    			afterrender:function(btn){
    				var status = Ext.getCmp('sa_statuscode');    				
    				if(status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click:function(){    				
    				var sacode=Ext.getCmp('sa_id').value;
    				var formCondition="sp_saleid IS"+sacode;
    				var linkCaller='SalePayment';    				
    				var win = new Ext.window.Window({  
						id : 'win',
						height : '90%',
						width : '95%',
						maximizable : true,
						buttonAlign : 'center',
						layout : 'anchor',
						items : [ {
							tag : 'iframe',
							frame : true,
							anchor : '100% 100%',
							layout : 'fit',
							 html : '<iframe id="iframe_'+linkCaller+'" src="'+basePath+'jsps/scm/sale/salepayment.jsp?_noc=1&whoami='+linkCaller+'&formCondition='+formCondition+'" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>'
						} ],
                        listeners:{
                          'beforeclose':function(view ,opt){
                        	   //grid  刷新一次
                        	  var grid=Ext.getCmp('grid');
                        	  var gridParam = {caller: caller, condition: gridCondition};
                        	  grid.GridUtil.getGridColumnsAndStore(grid, 'common/singleGridPanel.action', gridParam, "");                        	 
                          }	
                        }
					});
 					win.show(); 
    			
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('sa_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click:{ 
    				lock: 2000,
	                fn:function(btn){
    					me.FormUtil.onResSubmit(Ext.getCmp('sa_id').value);
    				}
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('sa_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click:{ 
    				lock: 2000,
	                fn: function(btn){
    					me.FormUtil.onAudit(Ext.getCmp('sa_id').value);
	                }
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('sa_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click:{ 
    				lock: 2000,
	                fn: function(btn){
    					me.FormUtil.onResAudit(Ext.getCmp('sa_id').value);
	                }
    			}
    		},
    	    'erpSubmitTurnSaleButton':{
    			click:function(btn){
    				var id=Ext.getCmp('sa_id').getValue();
    				Ext.Ajax.request({//拿到grid的columns
						url : basePath + "scm/sale/submitTurnSale.action",
						params: {
						 id:id
						},
						method : 'post',
						callback : function(options,success,response){
							var res = new Ext.decode(response.responseText);
							if(res.exceptionInfo){
								var str = res.exceptionInfo;
		    	   				if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
		    	   					str = str.replace('AFTERSUCCESS', '');
		    	   					submitSuccess(function(){
		    	   						window.location.reload();
		    	   					});
		    	   				}
		    	   				showMessage("提示", str);return;
							}
							else if(res.success){
								submitSuccess(function(){
    	        					window.location.reload();
    	        				});
							}
						}
    				});
    			},
    			afterrender:function(btn){
    				var sa_commitstatus=Ext.getCmp('sa_commitstatus').getValue();
    				var sa_statuscode=Ext.getCmp('sa_statuscode').getValue();
    				var sa_source=Ext.getCmp('sa_source').getValue();
    				var bool=true;
    				if((Ext.isEmpty(sa_commitstatus)||sa_commitstatus=='未提交')&&sa_statuscode=='AUDITED'&&sa_source=='非正常'){
    					bool=false;
    				}
    				if(bool) btn.hide();
    			}
    		},
    		'erpResSubmitTurnSaleButton':{
    			click:function(btn){
    				var id=Ext.getCmp('sa_id').getValue();
    				Ext.Ajax.request({//拿到grid的columns
						url : basePath + "scm/sale/resSubmitTurnSale.action",
						params: {
						 id:id
						},
						method : 'post',
						callback : function(options,success,response){
							var res = new Ext.decode(response.responseText);
							if(res.exceptionInfo){
								showError(res.exceptionInfo);return;
							}
							if(res.success){
								resSubmitSuccess();
								window.location.reload();
							}
						}
    				});
    			},
    			afterrender:function(btn){
    				var sa_commitstatus=Ext.getCmp('sa_commitstatus').getValue();
    				if(sa_commitstatus!='已提交'){
    					btn.hide();
    				}
    			}
    		},
    		'erpTurnNormalSaleButton':{
    			click:function(btn){
    				var id=Ext.getCmp('sa_id').getValue();
    				Ext.Ajax.request({//拿到grid的columns
						url : basePath + "scm/sale/TurnNormalSale.action",
						params: {
						 id:id
						},
						method : 'post',
						callback : function(options,success,response){
							var res = new Ext.decode(response.responseText);
							if(res.exceptionInfo){
								showError(res.exceptionInfo);return;
							}
							if(res.success){
								Ext.Msg.alert('提示','转正式订单成功!',function(){
    	        					window.location.reload();
    	        				});
							}
						}
    				});    				
    			},
    			afterrender:function(btn){
    				var sa_commitstatus=Ext.getCmp('sa_commitstatus');
    				if(sa_commitstatus && sa_commitstatus.value!='已提交'){
    					btn.hide();
    				}
    			}
    		},
    		'erpPrintButton':{
    			click:function(btn){
    				var reportName="salelist";
    				var condition='{Sale.sa_id}='+Ext.getCmp('sa_id').value+'';
    				var id=Ext.getCmp('sa_id').value;
    				me.FormUtil.onwindowsPrint2(id,reportName,condition);
    			}
    		},
    		'erpPrintA4Button':{
    			click:function(btn){
    				var reportName="SaleCheck";
    				var condition='{Sale.sa_id}='+Ext.getCmp('sa_id').value+'';
    				var id=Ext.getCmp('sa_id').value;
    				me.FormUtil.onwindowsPrint2(id,reportName,condition);
    			}
    		},
    		'field[name=sa_statuscode]': {
    			change: function(f){
    				var grid = Ext.getCmp('grid');
    				if(grid && f.value != 'ENTERING' && f.value != 'COMMITED'){
    					grid.setReadOnly(true);//只有未审核的订单，grid才能编辑
    				}
    			}
    		},
    		'erpConsignButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('sa_statuscode'),
    					sk_outtype = Ext.getCmp('sk_outtype');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    				if(sk_outtype && sk_outtype.value != 'TURNSN'){
    					btn.hide();
    				}
    			}
    		},
    		'erpTurnProdInOutSaleButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('sa_statuscode'),
    					sk_outtype = Ext.getCmp('sk_outtype');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    				if(sk_outtype && sk_outtype.value != 'TURNOUT'){
    					btn.hide();
    				}
    			}
    		},
    		'erpFeatureDefinitionButton':{
    			click: function(btn){
    				var grid = Ext.getCmp('grid');
    				var record = grid.selModel.lastSelected;
    				if(record.data.sd_prodcode != null){
    					Ext.Ajax.request({//拿到grid的columns
    						url : basePath + "pm/bom/getDescription.action",
    						params: {
    							tablename: 'Product',
    							field: 'pr_specvalue',
    							condition: "pr_code='" + record.data.sd_prodcode + "'"
    						},
    						method : 'post',
    						async: false,
    						callback : function(options,success,response){
    							var res = new Ext.decode(response.responseText);
    							if(res.exceptionInfo){
    								showError(res.exceptionInfo);return;
    							}
    							if(res.success){
    								if(res.description != '' && res.description != null && res.description == 'NOTSPECIFIC'){
    									var win = new Ext.window.Window({
    			    						id : 'win',
    			    						title: '生成特征料号',
    			    						height: "90%",
    			    						width: "95%",
    			    						maximizable : true,
    			    						buttonAlign : 'center',
    			    						layout : 'anchor',
    			    						items: [{
    			    							tag : 'iframe',
    			    							frame : true,
    			    							anchor : '100% 100%',
    			    							layout : 'fit',
    			    							html : '<iframe id="iframe_' + record.data.sd_id + '" src="' + basePath + 
    			    							"jsps/pm/bom/FeatureValueSet.jsp?fromwhere=SaleDetail&condition=formidIS" + record.data.sd_id + ' AND pr_codeIS' + record.data.sd_prodcode + ' AND pr_nameIS' + record.data.pr_detail +'" height="100%" width="100%" frameborder="0" scrolling="yes"></iframe>'
    			    						}]
    			    					});
    			    					win.show();    									
    								} else {
    									showError('物料特征必须为虚拟特征件');return;
    								}
    							}
    						}
    					});
    				}
    			}
    		},
    		'erpTurnAppropriateButton':{
    			afterrender:function(btn){
    				if(Ext.getCmp('sa_need1').value == '已转拨出单'){
    					btn.hide();
    				}
    			}
    		},
    		'erpFeatureViewButton':{
    			click: function(btn){
    				var grid = Ext.getCmp('grid');
    				var record = grid.selModel.lastSelected;
    				if(record.data.sd_prodcode != null){
    					Ext.Ajax.request({//拿到grid的columns
    						url : basePath + "pm/bom/getDescription.action",
    						params: {
    							tablename: 'Product',
    							field: 'pr_specvalue',
    							condition: "pr_code='" + record.data.sd_prodcode + "'"
    						},
    						method : 'post',
    						async: false,
    						callback : function(options,success,response){
    							var res = new Ext.decode(response.responseText);
    							if(res.exceptionInfo){
    								showError(res.exceptionInfo);return;
    							}
    							if(res.success){
    								if(res.description != '' && res.description != null && res.description == 'SPECIFIC'){
    									var win = new Ext.window.Window({
    										id : 'win' + record.data.sd_id,
    										title: '特征查看',
    										height: "90%",
    										width: "70%",
    										maximizable : true,
    										buttonAlign : 'center',
    										layout : 'anchor',
    										items: [{
    											tag : 'iframe',
    											frame : true,
    											anchor : '100% 100%',
    											layout : 'fit',
    											html : '<iframe id="iframe_' + record.data.sd_id + '" src="' + basePath + 
    											"jsps/pm/bom/FeatureValueView.jsp?fromwhere=SaleDetail&formid=" + record.data.sd_id + '&pr_code=' + record.data.sd_prodcode +'" height="100%" width="100%" frameborder="0" scrolling="yes"></iframe>'
    										}]
    									});
    									win.show();    									
    								} else {
    									showError('物料特征必须为 虚拟特征件');return;
    								}
    							}
    						}
    					});
    				}
    			}
    		},
    		'erpRunATPButton':{
    			click: function(btn){ 
    				if(Ext.getCmp('sa_code').value != null){
    					var mb = new Ext.window.MessageBox();
    				    mb.wait('正在运算中','请稍后...',{
    					   interval: 10000, //bar will move fast!
    					   duration: 1000000,
    					   increment: 20, 
    					   scope: this
    					});
    					Ext.Ajax.request({//拿到grid的columns
    						url : basePath + "/pm/atp/runATPFromOther.action",
    						params: {
    							fromcode:Ext.getCmp('sa_code').value,
    							fromwhere:'SALE'
    						},
    						method : 'post', 
    						timeout: 600000,
    						callback : function(options,success,response){
    							mb.close();
    							var res = new Ext.decode(response.responseText);
    							if(res.exceptionInfo){
    								showError(res.exceptionInfo);return;
    							}
    							if(res.success){
    								if(res.atpid != '' && res.atpid != null && res.atpid>0){
    									me.FormUtil.onAdd(null, 'ATP运算', 'jsps/pm/atp/ATPMain.jsp?formCondition=am_id=' + res.atpid + '&&gridCondition=ad_amid='+res.atpid+'&_noc=1');
    								} else {
    									showError('无数据，运算失败');return;
    								}
    							}
    						}
    					});
    				}
    			}
    		},
    		'dbfindtrigger[name=sd_batchcode]': {
    			focus: function(t){
    				t.setHideTrigger(false);
    				t.setReadOnly(false);//用disable()可以，但enable()无效
    				var record = Ext.getCmp('grid').selModel.getLastSelected();
    				var pr = record.data['sd_prodcode'];
    				if(pr == null || pr == ''){
    					showError("请先选择料号!");
    					t.setHideTrigger(true);
    					t.setReadOnly(true);
    				} else {
    					var id = record.data['sd_warehouseid'];
        				if(id == null || id == ''){
        					id = Ext.getCmp('sd_warehouseid');
        					if(id == null || id == '' || id== 0 ){
        						showError("请先选择仓库!");
            					t.setHideTrigger(true);
            					t.setReadOnly(true);
        					}
        				} else {
        					t.dbBaseCondition = "ba_warehouseid='" + id + "' AND ba_prodcode='" + pr + "'";
        				}
        				t.dbBaseCondition = "ba_prodcode='" + pr + "'";
    				}
    				
    			}
    		},
    		'erpOutScheduleButton': {
    			click: function() {
    				var grid = Ext.getCmp('grid'),record = grid.selModel.lastSelected;
    				if(record) {
    					me.schedule(record);
    				}
    			}
    		},
    		'erpCopyButton': {
    			click: function(btn) {
    				me.copy();
    			}
    		},
    		'erpEndButton': {
                afterrender: function(btn) {
                    var status = Ext.getCmp('sa_statuscode');
                    var sa_ordertype = Ext.getCmp('sa_ordertype');
                    if (status && status.value != 'AUDITED') {
                        btn.hide();
                    }
    				if(sa_ordertype && sa_ordertype.value == "B2C"&&status && status.value != 'FINISH'){
    						btn.show();
    				}
                },
                click: function(btn) {
                	warnMsg("确定结案?", function(btn){
    					if(btn == 'yes'){
    						me.FormUtil.getActiveTab().setLoading(true);//loading...
    						var id=Ext.getCmp('sa_id').value;
    						var grid = Ext.getCmp('grid'), jsonData=new Array();
    						grid.store.each(function(item){
    							if(item.get('sd_id') > 0)
    								jsonData.push({sd_id: item.get('sd_id')});
    						});
    	    				Ext.Ajax.request({
    	    			   		url : basePath + 'scm/sale/endSale.action',
    	    			   		params: {
    	    			   			caller: caller,
    	    			   			data: Ext.JSON.encode(jsonData),
    	    			   			id:id
    	    			   		},
    	    			   		method : 'post',
    	    			   		callback : function(options,success,response){
    	    			   			me.FormUtil.getActiveTab().setLoading(false);
    	    			   			var localJson = new Ext.decode(response.responseText);
    	    			   			if(localJson.exceptionInfo){
    	    			   				showError(localJson.exceptionInfo);
    	    			   			}
    	    		    			if(localJson.success){
    	    		    				alert("结案成功！");
    	    	        				window.location.reload();
    	    			   			}
    	    			   		}
    	    				});
    					}
    				});
                }
            },
    		'dbfindtrigger[name=sd_forecastdetno]': {
    			focus: function(t){
    				t.setHideTrigger(false);
    				t.setReadOnly(false);
    				var record = Ext.getCmp('grid').selModel.lastSelected;
    				var code = record.data['sd_forecastcode'];
    				if(code == null || code == ''){
    					showError("请先选择预测单号!");
    					t.setHideTrigger(true);
    					t.setReadOnly(true);
    				} else {
    					t.dbBaseCondition = "sf_code='" + code + "'";
    				}
    			}
    		},
         /*   'field[name=sa_sellercode]' : {// 自动带事业部
            	aftertrigger: function(f, r) {
            		var v = r.get('em_depart');
            		if(!Ext.isEmpty(v)) {
            			var n = f.up('form').down('#sa_parentorname');
            			console.log(n);
                		if (n && n.store.findRecord('value', v)) {
                			n.setValue(v);
                		}
            		}
            	}
            },*/
            /**
             * 是否使用客户物料对照表
             */
            'field[name=sa_statuscode]': {
            	afterrender: function(f) {
            		if('ENTERING' == f.getValue()) {
            			me['UseCustomerProduct'] = me.getSetting('UseCustomerProduct');
            		}
            	}
            },
            /**
             * 将客户编号传入
             */
            'dbfindtrigger[name=sd_prodcode]': {
    			focus: function(t) {
    				if(me.UseCustomerProduct) {
    					t.setHideTrigger(false);
        				t.setReadOnly(false);
        				var cust = Ext.getCmp('sa_custcode').getValue();
        				if(cust != null && cust != '') {
        					t.dbOrderby = "ORDER BY case when cp_custcode='" + cust + "' then 0 else 1 end,PR_CODE DESC";
        				} else {
        					t.dbOrderby = null;
        				}
    				}
    			}
    		},
            'erpRefreshSyncButton': {
            	afterrender: function(btn) {
                    var form = btn.ownerCt.ownerCt,
                    	s = form.down('#sa_statuscode'),
                    	v = form.down('#sa_sync');
                    if (s.getValue() != 'AUDITED' || (v && v.getValue() == null)) {
                        btn.hide();
                    }
                }
            },
            'erpConfirmAgreeButton':{//平台获取的销售订单确认接收
            	click:function(){           		
					me.FormUtil.getActiveTab().setLoading(true);//loading...
					var id = Ext.getCmp("sa_id").value;
    				Ext.Ajax.request({
    			   		url : basePath + 'scm/sale/confirmAgree.action',
    			   		params: {
    			   			caller: caller,
    			   			sa_id : id
    			   		},
    			   		method : 'post',
    			   		callback : function(options,success,response){
    			   			me.FormUtil.getActiveTab().setLoading(false);
    			   			var localJson = new Ext.decode(response.responseText);
    			   			if(localJson.exceptionInfo){
    			   				showError(localJson.exceptionInfo);
    			   			}
    		    			if(localJson.success){
    		    				showMessage('提示', '接收成功!', 1000);
    	        				window.location.reload();
    			   			}
    			   		}
    				});   					
            	},
            	afterrender:function(btn){
            		var form = btn.ownerCt.ownerCt,
                        s = form.down('#sa_statuscode');
                    if (s.getValue() != 'AUDITED') {
                        btn.hide();
                    }
                    //商城订单不需要确认
                    if(Ext.getCmp('sa_ordertype') && Ext.getCmp('sa_ordertype').value == "B2C"){
	    						btn.hide();
	    			}
            	}
            },
            'erpTurnB2CSaleOutButton':{
            	click:function(){           		
					me.FormUtil.getActiveTab().setLoading(true);//loading...
					var id = Ext.getCmp("sa_id").value;
    				Ext.Ajax.request({
    			   		url : basePath + 'scm/sale/turnB2CSaleOut.action',
    			   		params: {
    			   			caller: caller,
    			   			sa_id : id
    			   		},
    			   		method : 'post',
    			   		timeout: 30000,
    			   		callback : function(options,success,response){
    			   			me.FormUtil.getActiveTab().setLoading(false);
    			   			var localJson = new Ext.decode(response.responseText);
    			   			if(localJson.exceptionInfo){
    			   				showError(localJson.exceptionInfo);
    			   			}
    		    			if(localJson.success){
			    				if(localJson.log){
			    					showMessage("提示", localJson.log);
			    				}
    		    			}
    			   		}
    				});   					
            	},
            	afterrender:function(btn){
            		var form = btn.ownerCt.ownerCt,
                        s = form.down('#sa_statuscode');
                    if (s.getValue() != 'AUDITED') {
                        btn.hide();
                    }
            	}
            },
            'erp2PurcButton':{
    			afterrender: function(btn){
    				var status = Ext.getCmp('sa_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: {
    				lock: 2000,
	                fn:function(btn){
    					me.turnPurc(btn);
	                }
    			}		
	    	},
	    	'erpRecheckButton':{
	    		afterrender:function(btn){
	    			var recheckStatusCode = Ext.getCmp('sa_recheckstatuscode');
	    			if(recheckStatusCode){
	    				if(recheckStatusCode.value=='COMMITED'){
	    					btn.hide();
	    				}
	    				Ext.Ajax.request({
							url:basePath + 'common/getFieldData.action',
							method:'post',
							params:{
								field:'data',
								caller:'configs',
								condition:"code='recheck' and caller='Sale'"
							},
							callback:function(options,successs,response){
								var res = Ext.decode(response.responseText);
								if(res.exceptionInfo){
									showError(res.exceptionInfo);
								}else{
									if(res.data!=null){
										me.hideCheckBtn(btn,res.data);
									}
								}
							}
	    				});
	    			}
	    		},
	    		click:function(btn){
	    			Ext.Msg.confirm('确定','是否确定复审',function(btn){
	    				if(btn=='yes'){
	    					var id = Ext.getCmp('sa_id').value;
	    					me.FormUtil.setLoading(true);
			    			Ext.Ajax.request({
			    				url:basePath + 'scm/sale/recheck.action',
			    				params:{
			    					caller:'Sale!Recheck',
			    					id:id
			    				},
			    				callback:function(options,success,response){
			    					me.FormUtil.setLoading(false);
			    					var res = Ext.decode(response.responseText);
			    					if(res.exceptionInfo){
			    						showError(res.exceptionInfo);
			    					}else{
			    						showMessage('发起复审流程成功!',1000);
			    						window.location.reload();
			    					}
			    				}
			    			});
	    				}
	    			});
	    		}
	    	},
	    	'erpResrecheckButton':{
	    		afterrender:function(btn){
	    			var recheckStatusCode = Ext.getCmp('sa_recheckstatuscode');
	    			if(!recheckStatusCode||recheckStatusCode.value!='COMMITED'){
	    				btn.hide();
	    			}
	    		},
	    		click:function(btn){
	    			Ext.Msg.confirm('确定','是否确定反复审',function(btn){
	    				if(btn=='yes'){
	    					var id = Ext.getCmp('sa_id').value;
	    					me.FormUtil.setLoading(true);
			    			Ext.Ajax.request({
			    				url:basePath + 'scm/sale/resRecheck.action',
			    				params:{
			    					caller:'Sale!Recheck',
			    					id:id
			    				},
			    				callback:function(options,success,response){
			    					me.FormUtil.setLoading(false);
			    					var res = Ext.decode(response.responseText);
			    					if(res.exceptionInfo){
			    						showError(res.exceptionInfo);
			    					}else{
			    						showMessage('反复审成功!',1000);
			    						window.location.reload();
			    					}
			    				}
			    			});
	    				}
	    			});
	    		}
	    	},
	    	'erpUpdateRemarkButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('sa_statuscode');
    				if (status && status.value == 'ENTERING') {
    					btn.hide();
    				}
    			},
    			click : function(btn) {
    				Ext.Ajax.request({
    					url : basePath + 'common/updateByCondition.action',
    					params : {
    						caller : caller,
    						table : 'Sale',
    						update : 'sa_remark=\'' + Ext.getCmp('sa_remark').value + '\'',
    						condition : 'sa_id=' + Ext.getCmp('sa_id').value
    					},
    					callback : function(opt, s, res) {
    						var r = Ext.decode(res.responseText);
    						if (r.success) {
    							alert('修改成功!');
    						}
    					}
    				});
    			}
    		},
    		'button[id=Voucher]':{
            	afterrender:function(btn){
            		var btn = Ext.getCmp('Voucher');
            		btn.hide();
            	}
            },
    		'field[name=sa_remark]':{
				afterrender: function(field){
					Ext.defer(function(){
						field.setReadOnly(false);
					}, 200);
				}
			}
    	});
    }, 
    hideCheckBtn:function(btn,num){
		var sa_prepayamount = Ext.getCmp('sa_prepayamount');
		var sa_apamount_user = Ext.getCmp('sa_apamount_user');
		var pay,receive;
		if(sa_apamount_user==null||''==sa_apamount_user.value){
			receive = 0;
		}else{
			receive = sa_apamount_user.value.toFixed(2);
		}
		if(sa_prepayamount==null||''==sa_prepayamount.value){
			pay = 0; 
		}else{
			pay = sa_prepayamount.value.toFixed(2);
		} 
		var saDate = Ext.getCmp('sa_date');
		if(saDate){
			var extCurrentDate = Ext.Date.format(new Date,'Y-m-d');
        	var saleDate = Ext.Date.format(saDate.value,'Y-m-d');
        	var subDate = new Date(extCurrentDate)-new Date(saleDate);
        	if(subDate<=1000*60*60*24*num||Number(pay)>=Number(receive)){ //信扬付款金额-销售订单中的预收金额, 如果结果是正数, 则需要提示评审, 如果等于0或是小于0时就不用再做评审了
      			btn.hide();
        	}
		}
    },
    onGridItemClick: function(selModel, record){//grid行选择
    	if(!selModel.ownerCt.readOnly){
    			this.GridUtil.onGridItemClick(selModel, record);
    	}
    },
    getRecordByPrCode: function(){
    	if(this.gridLastSelected && this.gridLastSelected.findable){
    		var data = Ext.getCmp('grid').store.data.items[this.gridLastSelected.index].data;
    		var code = data.pd_prodcode;
    		if(code != null && code!= ''){//看用户输入了编号没有
            	var str = "sd_prodcode='" + code + "'";
            	this.GridUtil.getRecordByCode({caller: 'Sale', condition: str});	
    		}
    	}
    },
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	beforeSaveSale: function(){
		var grid = Ext.getCmp('grid');
		var cust = Ext.getCmp('sa_custid').value;
		if(cust == null || cust == '' || cust == '0' || cust == 0){
			showError('未选择客户，或客户编号无效!');
			return;
		}
		var items = grid.store.data.items;
		var bool = true, code = Ext.getCmp('sa_code').value;
		//数量不能为空或0
		Ext.each(items, function(item){
			if('sd_bodycost' in item.data && 'sd_flash' in item.data && 'sd_sisvel' in item.data) {// check if {key} in {Object} 
				item['sd_bodycost']=item.data['sd_price'] - item.data['sd_flash'] - item.data['sd_sisvel'];
			}
			if(!Ext.isEmpty(item.data['sd_prodcode'])){
				//item.set('sd_code', code);
				item['sd_code']=code;
				if(item.data['sd_qty'] == null || item.data['sd_qty'] == '' || item.data['sd_qty'] == '0'
					|| item.data['sd_qty'] == 0){
					bool = false;
					showError('明细表第' + item.data['sd_detno'] + '行的数量为空');return;
				}
			}
			if(!Ext.isEmpty(item.data['sd_forecastcode'])&&(Ext.isEmpty(item.data['sd_forecastdetno'])||item.data['sd_forecastdetno']==0||item.data['sd_forecastdetno']=='')){
				bool = false;
				showError('明细表第' + item.data['sd_detno'] + '行的预测序号不能为空');return false;
			}
		});
		//PMC回复日期不能小于当前日期
		Ext.each(items, function(item){
			if(!Ext.isEmpty(item.data['sd_prodcode'])){
			 	/*if(Ext.isEmpty(item.data['sd_pmcdate'])){
			 		item.set('sd_pmcdate', '');
			 		item.data['sd_pmcdate']='';
			 	}*/
			 	if(!Ext.isEmpty(item.data['sd_pmcdate'])){
					if(Ext.Date.format(item.data['sd_pmcdate'], 'Y-m-d') < Ext.Date.format(new Date(), 'Y-m-d')){
						bool = false;
						showError('明细表第' + item.data['sd_detno'] + '行的PMC回复日期小于当前日期');return;
					}
			 	}
			}
		});
		var cr = Ext.getCmp('sa_currency');
		if(cr && cr.getValue() == 'RMB') {
			var record = grid.store.findRecord('sd_taxrate', 0);
			if (record) {
				
			}
		}
		 //物料交货日期默认值为单据日期,交互日期不能小于当前时间
        Ext.each(items, function(item) {
            if (item.dirty && item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != "") {
                if (item.data['sd_delivery'] == null) {
                	if(Ext.getCmp('sa_plandelivery')&&Ext.getCmp('sa_plandelivery').value!=null&&Ext.getCmp('sa_plandelivery').value!=""){
                		item.data['sd_delivery']=Ext.getCmp('sa_plandelivery').value;
                	}
                }else if (Ext.Date.format(item.data['sd_delivery'],'Y-m-d') < Ext.Date.format(new Date(),'Y-m-d')) {
                    bool = false;
                    showError('明细表第' + item.data['sd_detno'] + '行的交货日期小于当前日期');
                    return;
                }
            }
        });
		//保存sale
		if(bool)
			this.FormUtil.beforeSave(this);
	},
	beforeUpdate: function(){
		var grid = Ext.getCmp('grid');
		var cust = Ext.getCmp('sa_custid').value;
		var items = grid.store.data.items, sacode = Ext.getCmp('sa_code').value;
		var bool = true;
		if(cust == null || cust == '' || cust == '0' || cust == 0){
			showError('未选择客户，或客户编号无效!');return;
		}
		//数量不能为空或0
		Ext.each(items, function(item){
			if('sd_bodycost' in item.data && 'sd_flash' in item.data && 'sd_sisvel' in item.data) {// check if {key} in {Object} 
				item['sd_bodycost']=item.data['sd_price'] - item.data['sd_flash'] - item.data['sd_sisvel'];
			}
			if(!Ext.isEmpty(item.data['sd_prodcode'])){
				item['sd_code']=sacode;
				if(item.data['sd_qty'] == null || item.data['sd_qty'] == '' || item.data['sd_qty'] == '0'
					|| item.data['sd_qty'] == 0){
					bool = false;
					showError('明细表第' + item.data['sd_detno'] + '行的数量为空');return;
				}
			}
			if(!Ext.isEmpty(item.data['sd_forecastcode'])&&(Ext.isEmpty(item.data['sd_forecastdetno'])||item.data['sd_forecastdetno']==0||item.data['sd_forecastdetno']=='')){
				bool = false;
				showError('明细表第' + item.data['sd_detno'] + '行的预测序号不能为空');return false;
			}
		});
		//PMC回复日期不能小于当前日期
		Ext.each(items, function(item){
			 if(!Ext.isEmpty(item.data['sd_prodcode'])){
			 	/*if(Ext.isEmpty(item.data['sd_pmcdate'])){
			 		item.set('sd_pmcdate', '');
			 		item.data['sd_pmcdate']='';
			 	}*/
			 if(!Ext.isEmpty(item.data['sd_pmcdate'])){
				 	if(Ext.Date.format(item.data['sd_pmcdate'], 'Y-m-d') < Ext.Date.format(new Date(), 'Y-m-d')){
						bool = false;
						showError('明细表第' + item.data['sd_detno'] + '行的PMC回复日期小于当前日期');
						return;
					}
			 	}
			}
		});
		//物料交货日期默认值为单据日期,交互日期不能小于当前时间
		Ext.each(items,function(item) {
            if (item.dirty && item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != "") {
                if (item.data['sd_delivery'] == null) {
                	if(Ext.getCmp('sa_plandelivery')&&Ext.getCmp('sa_plandelivery').value!=null&&Ext.getCmp('sa_plandelivery').value!=""){
                		item['sd_delivery']=Ext.getCmp('sa_plandelivery').value;
                	}
                }else if (Ext.Date.format(item.data['sd_delivery'],'Y-m-d') < Ext.Date.format(new Date(),'Y-m-d')) {
                    bool = false;
                    showError('明细表第' + item.data['sd_detno'] + '行的交货日期小于当前日期');
                    return;
                }
            }
        });
		//保存
		if(bool)
			this.FormUtil.onUpdate(this);
	},
	getLDWindow:function(){

		var me = this;
		return Ext.create('Ext.window.Window',{
			width: 330,
	       	height: 180,
	       	closeAction: 'hide',
	       	cls: 'custom-blue',
	       	title:'<h1>更改LD料码</h1>',
	       	layout: {
	       		type: 'vbox'
	       	},
	       	items:[{
	        	 width:'100%'
	       	},{
	        	 margin: '20 0 0 5',
	       		 xtype:'textfield',
	       		 id:'LDcode',
	       		 fieldLabel:'LD料码'
	       	 }],
	       	 buttonAlign:'center',
	       	 buttons:[{
	 				xtype:'button',
	 				text:'保存',
	 				width:60,
	 				iconCls: 'x-button-icon-save',
	 				handler:function(btn){
	 					var w = btn.up('window');
	 					me.saveLD(w);
	 					w.hide();
	 				}
	 			},{
	 				xtype:'button',
	 				columnWidth:0.1,
	 				text:'关闭',
	 				width:60,
	 				iconCls: 'x-button-icon-close',
	 				margin:'0 0 0 10',
	 				handler:function(btn){
	 					btn.up('window').hide();
	 				}
	 			}]
	        });
	},
	getPmcWindow : function() {
		var me = this;
		return Ext.create('Ext.window.Window',{
			width: 330,
	       	height: 210,
	       	closeAction: 'hide',
	       	cls: 'custom-blue',
	       	title:'<h1>更改PMC交期</h1>',
	       	layout: {
	       		type: 'vbox'
	       	},
	       	items:[{
	        	 width:'100%',
	        	 html: '<div style="background:transparent;border:none;width:100%;height:30px;' + 
	        	 	'color:#036;vertical-align:middle;line-height:30px;font-size:14px;">' + 
	        	 	'*注:修改订单交期请制作销售变更单<a style="float:right" href="javascript:' + 
	        	 	'openTable(\'变更交期\',\'jsps/scm/sale/saleChange.jsp?whoami=SaleChange\',\'SaleChange\');">进入</a></div>'
	         },{
	        	 margin: '5 0 0 5',
	       		 xtype:'datefield',
	       		 fieldLabel:'PMC回复交期',
	       	     name:'pmcdate',
	       	     format:'Y-m-d',
	       	     id:'pmcdate',
	       	     listeners:{
	       			 afterrender:function(f){
	       				Ext.getCmp("save").setDisabled(true);
	       			 },
	                 change:function(f){
	                	 if((f.value==null || f.value=="")&&(Ext.getCmp("pmcremark").value==null||Ext.getCmp("pmcremark").value=="")){
	                    	 Ext.getCmp("save").setDisabled(true);
	                     }else{
	                    	 Ext.getCmp("save").setDisabled(false);
	                     }
	              }               
	          }
	       	 },{
	       		 margin:'5 0 0 5',
	       		 xtype:'textfield',
	       		 fieldLabel:'PMC备注',
	       		 name:'pmcremark',
	       		 id:'pmcremark',
	       		 listeners:{
	       			 afterrender:function(f){
	       				Ext.getCmp("save").setDisabled(true);
	       			 },
	                 change:function(f){        
	                	 if((f.value==null || f.value=="")&&(Ext.getCmp("pmcdate").value==null||Ext.getCmp("pmcdate").value=="")){
	                    	 Ext.getCmp("save").setDisabled(true);
	                     }else{
	                    	 Ext.getCmp("save").setDisabled(false);
	                     }
	              }               
	          }
	       	 },{
	       		margin: '5 0 0 5',
	                xtype: 'fieldcontainer',
	                fieldLabel: '全部更新',
	                combineErrors: false,
	                defaults: {
	                    hideLabel: true
	                },
	                layout: {
	                    type: 'column',
	                    defaultMargins: {top: 0, right: 5, bottom: 0, left: 0}
	                },
	                items: [{
	                    xtype:'checkbox',
	                    columnidth: 0.4,
	                    fieldLabel:'全部更新',
	                    name:'allupdate',
	                    id:'allupdate'
	           	 },{
	           		 xtype:'displayfield',
	           		 fieldStyle:'color:red',
	           		 columnidth: 0.6,
	           		 value:'  *更改当前所有明细'
	           	 }]
	         }],
	       	 buttonAlign:'center',
	       	 buttons:[{
	 				xtype:'button',
	 				text:'保存',
	 				width:60,
	 				id:'save',
	 				//formBind: true,
	 				iconCls: 'x-button-icon-save',
	 				handler:function(btn){
	 					var w = btn.up('window');
	 					me.savePmc(w);
	 					w.hide();
	 				}
	 			},{
	 				xtype:'button',
	 				columnWidth:0.1,
	 				text:'关闭',
	 				width:60,
	 				iconCls: 'x-button-icon-close',
	 				margin:'0 0 0 10',
	 				handler:function(btn){
	 					btn.up('window').hide();
	 				}
	 			}]
	        });
	},
	saveLD:function(w){
		var	LDcode = Ext.getCmp('LDcode').value,
			grid = Ext.getCmp('grid'),
			record = grid.getSelectionModel().getLastSelected(); 
			if(LDcode==null){
				LDcode="";
			}
			var dd = {
				LDCode:LDcode
			};
			Ext.Ajax.request({
				url : basePath +'scm/sale/updateld.action?caller=Sale',
				params : {
					sd_id : record.data.sd_id,
					LDCode:Ext.JSON.encode(dd)
				},
				method : 'post',
				callback : function(opt, s, res){
					var r = new Ext.decode(res.responseText);
					if(r.success){
	    				showMessage('提示', '更新成功!', 1000);
		   			} else if(r.exceptionInfo){
		   				showError(r.exceptionInfo);
		   			} else{
		   				saveFailure();
		   			}
				}
			});
	},
	savePmc: function(w) {
		var pmcdate = w.down('field[name=pmcdate]').getValue(),
			grid = Ext.getCmp('grid'),
			pmcremark = w.down('field[name=pmcremark]').getValue(),
			record = grid.getSelectionModel().getLastSelected(); 
		if(!pmcdate && !pmcremark) {
			showError('请先设置PMC回复日期或者PMC备注') ;  
			return;
		} else if(pmcdate&&Ext.Date.format(pmcdate, 'Y-m-d') < Ext.Date.format(new Date(), 'Y-m-d')){
			showError('PMC回复日期小于系统当前日期') ;  
			return;
		} else {
			var allupdate = w.down('field[name=allupdate]').getValue();
			var dd = {
					sd_id : record.data.sd_id,
					sd_said : record.data.sd_said,
					pmcdate : pmcdate ? Ext.Date.format(pmcdate,'Y-m-d') : null,
					allupdate : allupdate ? 1 : 0,
					pmcremark : pmcremark
			};
			Ext.Ajax.request({
				url : basePath +'scm/sale/updatepmc.action',
				params : {
					_noc: 1,
					data: unescape(Ext.JSON.encode(dd))
				},
				method : 'post',
				callback : function(opt, s, res){
					var r = new Ext.decode(res.responseText);
					if(r.success){
	    				showMessage('提示', '更新成功!', 1000);
	    				window.location.reload();
		   			} else if(r.exceptionInfo){
		   				showError(r.exceptionInfo);
		   			} else{
		   				saveFailure();
		   			}
				}
			});
		}
	},
	UpdatePmc:function(record){
		var win = this.pmcwindow;
		if (!win) {
			win = this.pmcwindow = this.getPmcWindow();
		}
		win.show();
	},
	UpdateLD:function(record){
		var win = this.ldwindow;
		if (!win) {
			win = this.ldwindow = this.getLDWindow();
		}
		win.show();
	},
	/**
	 *销售订单拆分
	 * */
	SaleSplit:function(record){
		var me=this,originaldetno=Number(record.data.sd_detno);
		var said=record.data.sd_said;
		var sdid=record.data.sd_id;
		var sync = Ext.getCmp('sa_sync');
		if(sync && sync.value == '已同步'){
			showError('订单已抛转不能进行拆分操作!');
			return;
		}
		Ext.create('Ext.window.Window',{
    		width:850,
    		height:'80%',
    		iconCls:'x-grid-icon-partition',
    		title:'<h1>销售订单拆分</h1>',
    		id:'win',
    		layout: 'anchor',
    		items:[{
    			xtype:'form',
    			layout:'column',
    			anchor:'100% 30%',
    			frame:true,
    			defaults:{
    				xtype:'textfield',
    				columnWidth:0.5,
    				readOnly:true,
    				fieldStyle:'background:#f0f0f0;color:blue;',
    				labelAlign:'right'
    			},
    			items:[{
    			 fieldLabel:'销售单号',
    			 value:record.data.sd_code,
    			 id:'sacode'
    			},{
    			 fieldLabel:'产品编号'	,
    			 value:record.data.sd_prodcode
    			},{
    			 fieldLabel:'产品名称',
    			 value:record.data.pr_detail
    			},{
    			  fieldLabel:'公司型号',
    			  value:record.data.sd_companytype
    			},{
    			 fieldLabel:'原序号'	,
    			 value:record.data.sd_detno
    			},{
    		     fieldLabel:'原数量',
    		     value:record.data.sd_qty,
    		     id:'sdqty'
    			}],
    			buttonAlign:'center',
    			buttons:[{
    				xtype:'button',
    				columnWidth:0.12,
    				text:'保存',
    				width:60,
    				iconCls: 'x-button-icon-save',
    				margin:'0 0 0 30',
    				handler:function(btn){
    				   var store=Ext.getCmp('smallgrid').getStore();
    				   var count=0;
    				   var jsonData=new Array();
    				   var dd; 
    				   var remainqty;
    				   var bool = true;
    				   Ext.Array.each(store.data.items,function(item){
    					  if(Ext.isEmpty(item.data.sd_delivery)){
    						 bool = false; 
    						 showError('交货日期不能为空!') ;  
  	    					 return;
    					  }
    					  //拆分数量不能为0
    					  if(!item.data.sd_qty) {
    						  bool = false;
    						  showError('拆分数量不能为0!');
    						  return;
    					  }
    					  if(item.data.sd_qty!=0 && !Ext.isEmpty(item.data.sd_delivery) && item.data.sd_qty>0){
    						  if(item.dirty){
    							  var dd=new Object(),date = new Date();
    							  //说明是新增批次
    							  if(item.data.sd_delivery) {
    								   var d = (item.data.sd_delivery.getTime()>date.getTime()) ? item.data.sd_delivery : date;
    								   dd['sd_delivery']=Ext.Date.format(d, 'Y-m-d');
    							  }    								
    							  if(item.data.sd_pmcdate) {
    								  var d = (item.data.sd_pmcdate.getTime()>date.getTime()) ? item.data.sd_pmcdate : date;
    								  dd['sd_pmcdate']=Ext.Date.format(d, 'Y-m-d');
    							  }
    							  dd['sd_qty']=item.data.sd_qty; 
    							  dd['sd_id']=item.data.sd_id;
    							  dd['sd_detno']=item.data.sd_detno;
    							  jsonData.push(Ext.JSON.encode(dd));
    							  if(item.data.sd_id!=0&&item.data.sd_id!=null&&item.data.sd_id>0){
    								  remainqty=item.data.sd_qty; 
  								  }
    						  }
    						  count+=Number(item.data.sd_qty);
    					  }   
    				   });	  
    				   var assqty=Ext.getCmp('sdqty').value;
    				   if(bool){
    					   if(count!=assqty){
   	    					showError('分拆数量必须等于原数量!') ;  
   	    					return;
       				   }else{
       					   var r=new Object();
           				   r['sd_id']=record.data.sd_id;
           				   r['sd_said']=record.data.sd_said;
           				   r['sd_detno']=record.data.sd_detno;  
           				   if(record.data.sd_pmcdate)
           					   r['sd_pmcdate']=Ext.Date.format(record.data.sd_pmcdate,'Y-m-d');
           				   if(record.data.sd_delivery)
           					   r['sd_delivery']=Ext.Date.format(record.data.sd_delivery,'Y-m-d');
           				   var params=new Object();
           				   params.formdata = unescape(Ext.JSON.encode(r).replace(/\\/g,"%"));
           				   params.data = unescape(jsonData.toString().replace(/\\/g,"%"));
       					   Ext.Ajax.request({
       					   	  url : basePath +'scm/sale/splitSale.action',
       					   	  params : params,
       					   	  waitMsg:'拆分中...',
       					   	  method : 'post',
       					   	  callback : function(options,success,response){
       					   		var localJson = new Ext.decode(response.responseText);
       					   		if(localJson.success){
       			    				saveSuccess(function(){
       			    					Ext.getCmp('sdqty').setValue(remainqty);
       			    					//add成功后刷新页面进入可编辑的页面 
       			    					me.loadSplitData(originaldetno,said,record);  
       			    				});
       				   			} else if(localJson.exceptionInfo){
       				   				var str = localJson.exceptionInfo;
       				   				if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
       				   					str = str.replace('AFTERSUCCESS', '');
       				   					saveSuccess(function(){
       				    					//add成功后刷新页面进入可编辑的页面 
       				   					 me.loadSplitData(originaldetno,said,record);  
       				    				});
       				   					showError(str);
       				   				} else {
       				   					showError(str);
       					   				return;
       				   				}
       					   	 } else{
       				   				saveFailure();
       				   			}
       					   	  }
       					   });
       				   	}
    				   }
    				}
    			},{
    				xtype:'button',
    				columnWidth:0.1,
    				text:'关闭',
    				width:60,
    				iconCls: 'x-button-icon-close',
    				margin:'0 0 0 10',
    				handler:function(btn){
    					Ext.getCmp('win').close();
    				}
    			}]
    		},{
    		  xtype:'gridpanel',
    		  anchor:'100% 70%',
    		  id:'smallgrid',
    		  layout:'fit',
    		  columnLines:true,
    		  store:Ext.create('Ext.data.Store',{
					fields:[{name:'sd_delivery',type:'date'},{name:'sd_qty',type:'int'},{name:'sd_sendqty',type:'int'},{name:'sd_ysendnotify',type:'int'},{name:'sd_id',type:'int'},{name:'sd_pmcdate',type:'date'}],
				    data:[]
    		  }),
    		  plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
    		        clicksToEdit: 1,
    		        listeners:{
    		        	'edit':function(editor,e,Opts){
    		        		var record=e.record;
    		        		var version=record.data.ma_version;
    		        		if(version){
    		        			e.record.reject();
    		        			Ext.Msg.alert('提示','不能修改已拆分明细!');
    		        		}
    		        	}
    		        }
    		    })],
    		  tbar: [{
    			    tooltip: '添加批次',
    	            iconCls: 'x-button-icon-add',
    	            width:25,
    	            handler : function() {
    	            	var store = Ext.getCmp('smallgrid').getStore();
    	                var r = new Object();
    	                r.sd_delivery=record.get('sd_delivery');
    	                r.sd_pmcdate=record.get('sd_pmcdate');
    	                r.sd_qty=0; 
    	                r.sd_id=0;
    	                r.sd_detno=store.getCount()+1;
    	                store.insert(store.getCount(), r);
    	            }
    	        }, {
    	            tooltip: '删除批次',
    	            width:25,
    	            itemId: 'delete',
    	            iconCls: 'x-button-icon-delete',
    	            handler: function(btn) {
    	                var sm = Ext.getCmp('smallgrid').getSelectionModel();
    	                var record=sm.getSelection();
    	                var sd_id=record[0].data.sd_id;
    	                if(sd_id&&sd_id!=0){
    	                	Ext.Msg.alert('提示','不能删除已拆批次或原始行号!');
    	                	return;
    	                }
    	                var store=Ext.getCmp('smallgrid').getStore();
    	                store.remove(record);
    	                if (store.getCount() > 0) {
    	                    sm.select(0);
    	                }
    	            },
    	            disabled: true
    	        }],
    	      listeners:{
    	    	  itemmousedown:function(selmodel, record){
    	    		  selmodel.ownerCt.down('#delete').setDisabled(false);
    	    	  },
    	    	  afterrender : function(grid) {
    	    		  me.BaseUtil.getSetting('Sale', 'sd_delivery', function(bool) {
	       				if(bool) {
	       					grid.down('gridcolumn[dataIndex=sd_delivery]').hide();
	       				}
	       	          });
	       	    	  me.BaseUtil.getSetting('Sale', 'sd_pmcdate', function(bool) {
	       				if(bool) {
	       					grid.down('gridcolumn[dataIndex=sd_pmcdate]').hide();
	       				}
	       	          });
   			   	  }
    	      }, 
    		  columns:[{
    			 dataIndex:'sd_detno',
    			 header:'序号',
    			 format:'0',
    			 xtype:'numbercolumn'
    		   },{
    			  dataIndex:'sd_delivery',
    			  header:'交货日期',
    			  xtype:'datecolumn',
    			  width:120,
    			  editable:true,
    			  renderer:function(val,meta,record){
    				   if(record.data.ma_version){
    					  meta.tdCls = "x-grid-cell-renderer-cl";
    				   }
    				   if(val)
    					   return Ext.Date.format(val, 'Y-m-d');
    				   else return null;
    			   },
    			  editor:{
    				  xtype: 'datefield',
    				  format:'Y-m-d'
    			  }
    		  },{
    			  dataIndex:'sd_pmcdate',
    			  header:'PMC回复日期',
    			  xtype:'datecolumn',
    			  width:120,
    			  editable:true,
    			  renderer:function(val,meta,record){
    				   if(record.data.ma_version){
    					  meta.tdCls = "x-grid-cell-renderer-cl";
    				   }
    				   if(val)
    					   return Ext.Date.format(val, 'Y-m-d');
    				   else return null;
    			   },
    			  editor:{
    				  xtype: 'datefield',
    				  format:'Y-m-d'
    			  }
    		  },{
    			  dataIndex:'sd_qty',
    			  header:'数量',
    			  width:120,
    			  xtype:'numbercolumn',
    			  editable:true,
    			  renderer:function(val,meta,record){
   				   if(record.data.ma_version){
   					  meta.tdCls = "x-grid-cell-renderer-cl";
   				   }
   				   return val;
   			     },
    			  editor:{
    				  xtype:'numberfield',
    				  format:'0',
    				  hideTrigger: true
    			  }
    		  },{
    			dataIndex:'sd_yqty',
    			header:'已转发货数',
    			xtype:'numbercolumn',
    			width:100,
    			editable:false
    		  },{
    			 dataIndex:'sd_sendqty',
      			header:'已转通知数',
      			xtype:'numbercolumn',
      			width:100,
      			editable:false,
      			flex:1
    		  },{
    			  dataIndex:'sd_id',
    			  header:'sdid',
    			  width:0,
    			  xtype:'numbercolumn',
    			  editable:true,
    			  editor:{
    				  xtype:'numberfield',
    				  format:'0',
    				  hideTrigger: true
    			  }
    		  }]
    		}]
    		
    	}).show();
         this.loadSplitData(originaldetno,said,record); 
	},
	loadSplitData:function(detno,said,record){
		 var grid=Ext.getCmp('smallgrid');
         grid.setLoading(true);//loading...
 		Ext.Ajax.request({//拿到grid的columns
         	url : basePath + "common/loadNewGridStore.action",
         	params:{
         	  caller:'SaleSplit',
         	  condition:"sd_detno="+detno+" AND sd_said="+said+" order by sd_id asc"
         	},
         	method : 'post',
         	callback : function(options,success,response){
         		grid.setLoading(false);
         		var res = new Ext.decode(response.responseText);
         		if(res.exceptionInfo){
         			showError(res.exceptionInfo);return;
         		}
         		var data = res.data;
         		if(!data || data.length == 0){
         			grid.store.removeAll();
         			var o=new Object();
         			o.sd_detno=detno;
         			o.sd_delivery=record.data.sd_delivery;
         			o.sd_pmcdate=record.data.sd_pmcdate;
         			o.sd_qty=record.data.sd_qty;
         			o.sd_yqty=record.data.sd_yqty;
         			o.sd_sendqty=record.data.sd_sendqty;
         			o.sd_id=record.data.sd_id;
         			data.push(o);
         		}
         		 grid.store.loadData(data);
         	}
         });
	},
	
	loadFitting : function (pr_code,sd_qty,sa_id,detno){
		Ext.Ajax.request({
	   		url : basePath + 'scm/sale/getFittingData.action',	   
	   		params: {
	   			caller: caller,
	   			pr_code: pr_code,
	   			qty:sd_qty,
	   			sa_id:sa_id,
	   			detno:detno	   			
	   		},
	   		method : 'post',
	   		callback : function(opt, s, res){
	   			var r = new Ext.decode(res.responseText);
	   			if(r.exceptionInfo){
	   				showError(r.exceptionInfo);return;
	   			} else if(r.success){
	   				Ext.Msg.alert("提示", "载入配件成功!", function() {
										var g = Ext.getCmp('grid');
										g.GridUtil.loadNewStore(g, {
												    caller : caller,
													condition : 'sd_said ='+ sa_id
												});
									});
	   			}
	   		}
		});											 
	},
	/**
	 * 排程
	 */
	schedule: function(record) {
		var width = Ext.isIE ? screen.width*0.7*0.9 : '80%',
	   		height = Ext.isIE ? screen.height*0.75 : '100%';
		var sd_id = record.get('sd_id');
		Ext.Ajax.request({
			url : basePath + "scm/sale/checkSaleDetailDet.action",
			params: {
				whereString: "sd_id="+sd_id
			},
			method : 'post',
			async: false,
			callback:function(options,success,response){
				var res = new Ext.decode(response.responseText);
				if(res.exceptionInfo){
					showError(res.exceptionInfo);return;
				}
			}
		});
		Ext.create('Ext.Window', {
			width: width,
			height: height,
			autoShow: true,
			layout: 'anchor',
			items: [{
				tag : 'iframe',
				frame : true,
				anchor : '100% 100%',
				layout : 'fit',
				html : '<iframe id="iframe_dbfind" src="' + basePath + 'jsps/scm/sale/saleDetail.jsp?formCondition=sd_id=' 
					+ sd_id + '&gridCondition=sdd_sdid=' + sd_id + '" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>'
			}]
		});
	},
	/**
	 * 复制订单
	 */
	copy: function(){
		var me = this, form = Ext.getCmp('form');
		var v = form.down('#sa_id').value;
		if(v > 0) {
			form.setLoading(true);
			Ext.Ajax.request({
				url: basePath + 'scm/sale/copySale.action',
				params: {
					caller : caller,
					id : v
				},
				callback: function(opt, s, r){
					form.setLoading(false);
					var res = Ext.decode(r.responseText);
					if(res.data) {
						var url = 'jsps/scm/sale/sale.jsp?whoami=' + caller 
							+ '&formCondition=sa_idIS' + res.data.id + '&gridCondition=sd_saidIS' 
							+ res.data.id;
						showMessage('提示', '复制成功', 2000);
						me.FormUtil.onAdd(null, '订单', url);
					} else {
						showError(res.exceptionInfo);
					}
				}
			});
		}
	},
	/**
	 * 更新比例
	 */
	updateDiscount: function() {
		var win = this.discountWin;
		if(!win) {
			win = this.discountWin = this.createDiscountWin();
		}
		win.show();
	},
	createDiscountWin: function() {
		var me = this, grid = Ext.getCmp('grid'), arr = new Array();
		grid.store.each(function(){
			if(this.get('sd_id') > 0) {
				arr.push({
					sd_code: this.get('sd_code'),
					sd_detno: this.get('sd_detno'),
					sd_olddiscount: this.get('sd_discount'),
					sd_discount: this.get('sd_discount')
				});
			}
		});
		return new Ext.window.Window({
			width: 600,
			height: 400,
			title: '更新比例',
			cls: 'custom-blue',
			closeAction: 'hide',
			items: [{
				xtype: 'grid',
				width: '100%',
				height: 300,
				plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
			        clicksToEdit: 1
			    })],
			    columnLines: true,
				columns: [{
					text: '订单号',
					hidden: true,
					dataIndex: 'sd_code'
				},{
					text: '序号',
					xtype: 'numbercolumn',
					dataIndex: 'sd_detno',
					format: '0',
					flex: 1
				},{
					text: '原比例%',
					dataIndex: 'sd_olddiscount',
					align: 'center',
					flex: 4
				},{
					text: '新比例%',
					dataIndex: 'sd_discount',
					align: 'center',
					editor: {
						xtype: 'numberfield',
						format: '0.00'
					},
					flex: 4
				}],
				store: new Ext.data.Store({
					fields: ['sd_code','sd_detno','sd_olddiscount','sd_discount'],
					data: arr
				})
			},{
				margin: '0 0 0 5',
				xtype: 'checkbox',
				checked: true,
				boxLabel: '同时修改到通知单、出货单、Invoice和包装单'
			}],
			buttonAlign: 'center',
			buttons: [{
				text: $I18N.common.button.erpConfirmButton,
				cls: 'x-btn-blue',
				handler: function(b) {
					var w = b.ownerCt.ownerCt;
					me.onDiscountChange(w.down('grid'), w.down('checkbox').getValue());
					w.hide();
				}
			},{
				text: $I18N.common.button.erpCloseButton,
				cls: 'x-btn-blue',
				handler: function(b) {
					b.ownerCt.ownerCt.hide();
				}
			}]
		});
	},
	onDiscountChange: function(grid, oth) {
		var data = new Array(), id = Ext.getCmp('sa_id').value;
		grid.store.each(function(){
			if(this.dirty)
				data.push(this.data);
		});
		Ext.Ajax.request({
			url: basePath + 'scm/sale/updateDiscount.action',
			params: {
				caller: caller,
				id: id,
				data: Ext.encode(data),
				_noc: 1,
				oth: oth
			},
			callback: function(opt, s, res) {
				var r = Ext.decode(res.responseText);
				if(r.success) {
					alert('修改成功.');
					var g = Ext.getCmp('grid');
					g.GridUtil.loadNewStore(g, {
						caller: caller, 
						condition: 'sd_said=' + id
					});
				} else {
					alert('操作失败.');
				}
			}
		});
	},
	getSetting: function(type) {
		var result = false;
		Ext.Ajax.request({
	   		url : basePath + 'common/getFieldData.action',
	   		async: false,
	   		params: {
	   			caller: 'Setting',
	   			field: 'se_value',
	   			condition: 'se_what=\'' + type + '\''
	   		},
	   		method : 'post',
	   		callback : function(opt, s, res){
	   			var r = new Ext.decode(res.responseText);
	   			if(r.exceptionInfo){
	   				showError(r.exceptionInfo);return;
	   			} else if(r.success && r.data){
	   				result = r.data == 'true';
	   			}
	   		}
		});
		return result;
	},
	getRandomNumber: function(table, type, codeField){
		var me = this;
		if(Ext.getCmp('sa_kind')){
			var form = Ext.getCmp('form');
			if(form){
				table = table == null ? form.tablename : table;
			}
			type = type == null ? 2 : type;
			codeField = codeField || form.codeField;
			Ext.Ajax.request({
		   		url : basePath + 'scm/sale/getCodeString.action',
		   		async: false,//同步ajax请求
		   		params: {
		   			caller: caller,//如果table==null，则根据caller去form表取对应table
		   			table: table,
		   			type: type,
		   			conKind:Ext.getCmp('sa_kind').getValue()
		   		},
		   		method : 'post',
		   		callback : function(options,success,response){
		   			var localJson = new Ext.decode(response.responseText);
		   			if(localJson.exceptionInfo){
		   				showError(localJson.exceptionInfo);
		   			}
	    			if(localJson.success){
	    				Ext.getCmp(codeField).setValue(localJson.code);
		   			}
		   		}
			});
		} else {
			me.BaseUtil.getRandomNumber(caller);//自动添加编号
		}
	},
	turnPurc:function(btn){//销售订单转采购
		var me=this;
		var said=Ext.getCmp('sa_id').value;
		var form =btn.ownerCt.ownerCt;
		form.setLoading(true);
		Ext.Ajax.request({
			url : basePath + 'scm/sale/saleturnPurc.action',
			params: {
				id:said,
		   		caller: caller
		   	},
		    method : 'post',
		   	callback : function(options,success,response){
		   		form.setLoading(false);
		   		var localJson = new Ext.decode(response.responseText);
				if(localJson.exceptionInfo){
	  				showError(localJson.exceptionInfo);
	   			}
	   			if(localJson.success){
	    			if(localJson.log){
			    		showMessage("提示", localJson.log);
			    	}
		   		}
		 	}
		});
	}
});