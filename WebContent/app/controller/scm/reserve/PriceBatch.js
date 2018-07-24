Ext.QuickTips.init();
Ext.define('erp.controller.scm.reserve.PriceBatch', {
	extend : 'Ext.app.Controller',
	FormUtil : Ext.create('erp.util.FormUtil'),
	GridUtil : Ext.create('erp.util.GridUtil'),
	BaseUtil : Ext.create('erp.util.BaseUtil'),
	views : [ 'scm.reserve.PriceBatch', 'core.form.Panel', 'core.grid.Panel2',
			'core.toolbar.Toolbar', 'core.button.Scan', 'core.button.Export',
			'core.button.Save', 'core.button.Add', 'core.button.Submit',
			'core.button.Print', 'core.button.Upload', 'core.button.ResAudit',
			'core.button.Audit', 'core.button.Close', 'core.button.Delete','core.button.CleanFailed',
			'core.button.Update', 'core.button.DeleteDetail','core.button.ResSubmit', 'core.trigger.DbfindTrigger',
			'core.trigger.TextAreaTrigger','core.trigger.MultiDbfindTrigger',
			'core.button.BatchToMake', 'core.form.YnField' ,'core.trigger.AutoCodeTrigger'],
	init : function() {
		var me = this;
		this.control({
			'erpGridPanel2' : {
				afterrender: function(grid) {
                    grid.plugins[0].on('beforeedit', function(args) {
                    	var status =args.record.data.pbu_status;
                    	if(status > 0){
                    		return false;
                    	}
                    });
                },
				itemclick : this.onGridItemClick
			},
			'erpSaveButton' : {
				click : function(btn) {
					var grid = Ext.getCmp('grid'), items = grid.store.data.items;
					var detno, bool = true;
					if (caller == "PriceBatch"){
				        Ext.each(items,function(item) {
				        	if (item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != "") {
				                if (item.data['pbu_orderprice'] == null || item.data['pbu_orderprice'] == '' || item.data['pbu_orderprice'] == '0' || item.data['pbu_orderprice'] == 0
				                		 || item.data['pbu_taxrate'] == null || item.data['pbu_taxrate'] == '' || item.data['pbu_taxrate'] == '0' || item.data['pbu_taxrate'] == 0) {
				                	if(bool){
				                		detno = item.data['pbu_detno'];
				                	} else {
				                		detno =  detno + ',' + item.data['pbu_detno'];
				                	}			        
				                	bool = false;
				                }
				            }
				        });
				        if(!bool){
				        	showError('明细表行[' + detno + ']采购单价或税率为0，请确认是否无误！');
				        }
					}
					if (caller == "OutPriceBatch"){
				        Ext.each(items,function(item) {
				        	if (item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != "") {
				                if (item.data['pbu_sendprice'] == null || item.data['pbu_sendprice'] == '' || item.data['pbu_sendprice'] == '0' || item.data['pbu_sendprice'] == 0
				                		 || item.data['pbu_taxrate'] == null || item.data['pbu_taxrate'] == '' || item.data['pbu_taxrate'] == '0' || item.data['pbu_taxrate'] == 0) {
				                	if(bool){
				                		detno = item.data['pbu_detno'];
				                	} else {
				                		detno =  detno + ',' + item.data['pbu_detno'];
				                	}			        
				                	bool = false;
				                }
				            }
				        });
				        if(!bool){
				        	showError('明细表行[' + detno + ']出货单价或税率为0，请确认是否无误！');
				        }
					}
					this.FormUtil.beforeSave(this);
				}
			},
			'erpUpdateButton' : {
				afterrender: function(btn){
					var status = Ext.getCmp('em_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click : function(btn) {
					var grid = Ext.getCmp('grid'), items = grid.store.data.items;
					var detno, bool = true;
					if (caller == "PriceBatch"){
				        Ext.each(items,function(item) {
				            if (item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != "") {
				                if (item.data['pbu_orderprice'] == null || item.data['pbu_orderprice'] == '' || item.data['pbu_orderprice'] == '0' || item.data['pbu_orderprice'] == 0
				                		 || item.data['pbu_taxrate'] == null || item.data['pbu_taxrate'] == '' || item.data['pbu_taxrate'] == '0' || item.data['pbu_taxrate'] == 0) {
				                	if(bool){
				                		detno = item.data['pbu_detno'];
				                	} else {
				                		detno =  detno + ',' + item.data['pbu_detno'];
				                	}			        
				                	bool = false;
				                }
				            }
				        });
				        if(!bool){
				        	showError('明细表行[' + detno + ']采购单价或税率为0，请确认是否无误！');
				        }
					}
					if (caller == "OutPriceBatch"){
				        Ext.each(items,function(item) {
				            if (item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != "") {
				                if (item.data['pbu_sendprice'] == null || item.data['pbu_sendprice'] == '' || item.data['pbu_sendprice'] == '0' || item.data['pbu_sendprice'] == 0
				                		 || item.data['pbu_taxrate'] == null || item.data['pbu_taxrate'] == '' || item.data['pbu_taxrate'] == '0' || item.data['pbu_taxrate'] == 0) {
				                	if(bool){
				                		detno = item.data['pbu_detno'];
				                	} else {
				                		detno =  detno + ',' + item.data['pbu_detno'];
				                	}			        
				                	bool = false;
				                }
				            }
				        });
				        if(!bool){
				        	showError('明细表行[' + detno + ']出货单价或税率为0，请确认是否无误！');
				        }
					}
 				   	this.FormUtil.onUpdate(this);					
				}
			},
			'erpDeleteButton' : {
				afterrender: function(btn){
					var status = Ext.getCmp('em_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click : function(btn) {
					me.FormUtil.onDelete(Ext.getCmp('em_id').value);			
				}
			},
			'erpAddButton': {
				click: function(){
					if (caller == "PriceBatch"){
						me.FormUtil.onAdd('addPriceBatch', '新增采购单价更新', 'jsps/scm/reserve/priceBatch.jsp?whoami=PriceBatch');
					}
					else if (caller == "OutPriceBatch"){
						me.FormUtil.onAdd('addOutPriceBatch', '新增出货单价更新', 'jsps/scm/reserve/priceBatch.jsp?whoami=OutPriceBatch');
					}
				}
			},
			'erpCloseButton' : {
				click : function(btn) {
					me.FormUtil.beforeClose(me);
				}
			},
			'erpSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('em_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onSubmit(Ext.getCmp('em_id').value);
				}
			},
			'erpResSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('em_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResSubmit(Ext.getCmp('em_id').value);
				}
			},
			'erpAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('em_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onAudit(Ext.getCmp('em_id').value);
				}
			},
			'erpResAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('em_statuscode');
					if(status && status.value != 'AUDITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResAudit(Ext.getCmp('em_id').value);
				}
			},
			'erpCleanFailedButton' : {
				afterrender: function(btn){
					var status = Ext.getCmp('em_statuscode');
					if(status && status.value != 'AUDITED'){
						btn.hide();
					}
				},
				afterrender: function(btn){
        			var status = Ext.getCmp("em_statuscode");
    				if(status && status.value == 'AUDITED' ){
    					btn.show();
    				} else{
    					btn.hide();
    				}
    			},
				click : function(btn) {
					me.cleanFailed();
				}
			},
			'erpBatchToMakeButton' : {
				afterrender: function(btn) {
					btn.setText('批量更新明细单据');
					var status = Ext.getCmp('em_statuscode');
					if(status && status.value != 'AUDITED'){
						btn.hide();
					}
	            },
				click : function(btn) {	
					me.batchToMake();
				}
			}
		});
	},
	cleanFailed : function() {
		var grid = Ext.getCmp('grid');
		grid.setLoading(true);
		Ext.Ajax.request({
			url : basePath + 'scm/reserve/cleanFailed.action',
			params : {
				id : Ext.getCmp('em_id').value
			},
			method : 'post',
			callback : function(options, success, response) {
				grid.setLoading(false);
				var localJson = new Ext.decode(response.responseText);
				if (localJson.success) {
					showMessage('提示', '操作成功!', 1000);
					//update成功后刷新页面进入可编辑的页面
					window.location.reload();
				} else if (localJson.exceptionInfo) {
					showError(str);
					return;
				}
			}
		});

	},
	batchToMake : function() {
		//form里面数据
		var me = this;	
		var batchUrl;
		if (caller == 'PriceBatch'){
			batchUrl = basePath + 'scm/reserve/batchUpdateBill.action';
		}
		else if (caller == 'OutPriceBatch') {
			batchUrl = basePath + 'scm/reserve/batchUpdateOutBill.action';
		}
		Ext.Ajax.request({
			url : batchUrl,
			params : {
				id : Ext.getCmp('em_id').value
			},
			method : 'post',
			callback : function(options, success, response) {
				me.FormUtil.setLoading(false);
				var localJson = new Ext.decode(response.responseText);
				if (localJson.success) {
					showMessage('提示', '操作成功!', 1000);
					window.location.reload();
				} else if (localJson.exceptionInfo) {
					var str = localJson.exceptionInfo;
					showError(str);
					return;
				}
			}
		});
	},
	onGridItemClick : function(selModel, record) {//grid行选择
		this.GridUtil.onGridItemClick(selModel, record);
	},
	getForm : function(btn) {
		return btn.ownerCt.ownerCt;
	}
});