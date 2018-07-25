Ext.QuickTips.init();
Ext.define('erp.controller.pm.make.MakeBatch', {
	extend : 'Ext.app.Controller',
	FormUtil : Ext.create('erp.util.FormUtil'),
	GridUtil : Ext.create('erp.util.GridUtil'),
	BaseUtil : Ext.create('erp.util.BaseUtil'),
	views : [ 'pm.make.MakeBatch', 'core.form.Panel', 'core.grid.Panel2',
			'core.toolbar.Toolbar', 'core.button.Scan', 'core.button.Export',
			'core.button.Save', 'core.button.Add', 'core.button.Submit',
			'core.button.Print', 'core.button.Upload', 'core.button.ResAudit',
			'core.button.Audit', 'core.button.Close', 'core.button.Delete','core.button.CleanFailed',
			'core.button.Update', 'core.button.DeleteDetail','core.button.ResSubmit', 'core.trigger.DbfindTrigger',
			'core.trigger.TextAreaTrigger', 'core.button.CleanDetail','core.trigger.MultiDbfindTrigger',
			'core.button.BatchToMake', 'core.form.YnField' ,'core.trigger.AutoCodeTrigger',],
	init : function() {
		var me = this;
		this.control({
			'erpGridPanel2' : {
				itemclick : this.onGridItemClick
			},
			'erpUpdateButton' : {
				click : function(btn) {
 				   	this.FormUtil.onUpdate(this);					
				}
			},
			'erpCloseButton' : {
				click : function(btn) {
					me.FormUtil.beforeClose(me);
				}
			},
			//清除明细
			'erpCleanDetailButton' : {
				click : function(btn) {
					me.cleanDetail();
				}
			},
			'erpCleanFailedButton' : {
				click : function(btn) {
					me.cleanFailed();
				}
			},
			'erpBatchToMakeButton' : {
				click : function(btn) {			
					me.batchToMake();
				}
			}
		});
	},
	cleanDetail : function() {
		var grid = Ext.getCmp('grid');
		grid.setLoading(true);
		Ext.Ajax.request({
			url : basePath + 'pm/make/cleanMakeBatch.action',
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
	cleanFailed : function() {
		var grid = Ext.getCmp('grid');
		grid.setLoading(true);
		Ext.Ajax.request({
			url : basePath + 'pm/make/cleanFailed.action',
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
		Ext.Ajax.request({
			url : basePath + 'pm/make/batchToMake.action',
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
    getRandomNumber : function(table,type,codeField){
    	if(Ext.Cmp("mb_kind")){
    		var grid = Ext.getCmp("grid");
    		if(grid){
    			table = table == null ? grid.tablename:tablename;
    		}
    		type = type == null ? 2:type;
    		codeField = codeField == null ? grid.codeField :codeField;
    		Ext.Ajax.request({
    			url:basePath + 'pm/make/getcode.action',
    			async:false,
    			params:{
    				caller : caller,
    				table : table,
    				type : type,
    				conKind: Ext.getCmp('mb_kind').getValue() 
    			},
    			method: 'post',
    			callback:function(options,success,response){
    				var localJson = new Ext.decode(response.responseText);
    				if(localJson.exceptionInfo){
    					showerror(localJson.exceptionInfo);
    				}
    				if(localJson.success){
    					Ext.getCmp(codeField).setValue(localJson.code);
    				}
    			}
    			
    		});
    	}else{
    		this.BaseUtil.getRandomNumber(caller);
    	}
    },
    getNewCode:function(type){
    	var result = null;
    	Ext.Ajax.request({
    		url: basePath + 'common/getFieldData.action',
    		async:false,
    		params:{
    			caller : 'MakeBatch',
    			field : 'mb_makecode',
    			condition : 'mb_kind=\''+ type + '\'',
    		},
    		method:'post',
    		callback : function(opt,s,res){
    			var r = Ext.decode(res.responseText);
    			if(r.exceptionInfo){
    				showError(r.exceptionInfo);
    				return;
    			}else if(r.success){
    				result = r.data;
    			}
    		}
    	});
    	return result;
    },
	onGridItemClick : function(selModel, record) {//grid行选择
		this.GridUtil.onGridItemClick(selModel, record);
	},
	getForm : function(btn) {
		return btn.ownerCt.ownerCt;
	}
});