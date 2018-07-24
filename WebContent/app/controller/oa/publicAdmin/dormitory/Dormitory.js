Ext.QuickTips.init();
Ext.define('erp.controller.oa.publicAdmin.dormitory.Dormitory', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
   		'oa.publicAdmin.dormitory.Dormitory','core.form.Panel','core.grid.Panel2','core.button.Scan',
		'core.button.Add','core.button.Submit','core.button.Audit','core.button.Save','core.button.Close',
		'core.button.ResSubmit','core.button.Update','core.button.Delete','core.button.DeleteDetail',
		'core.button.ResAudit','core.button.Flow','core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger',
		'core.form.YnField','core.trigger.AutoCodeTrigger','core.toolbar.Toolbar','core.grid.YnColumn'
	],
    init:function(){
    	var me = this;
        	this.control({
    		'erpGridPanel2': { 
    			itemclick: this.onGridItemClick,
    			afterrender: function(grid){
    				var str = me.GridUtil.getGridStore();
    				if(str != null || str != ''){//说明grid加载时带数据
    					me.alloweditor = false;
    				}
    			}
    		},
    		'erpSaveButton': {
    			click: function(btn){
    				this.FormUtil.beforeSave(this);
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				var str = me.GridUtil.getGridStore();
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpDeleteDetailButton':{
    			afterdelete:function(btn){
    				var grid=Ext.getCmp('grid');
    				var records = grid.selModel.getSelection();
    				if(records[0].data[grid.keyField] != null && records[0].data[grid.keyField] > 0){
    					var do_bednull=Ext.getCmp('do_bednull');
        				do_bednull.setValue(do_bednull.value+1);
        				//删除明细时，空床位数也同时改变
        				Ext.Ajax.request({
					   		url : basePath + "oa/publicAdmin/dormitory/Dormitory/updateBednull.action",
					   		params: {
					   			bednull: do_bednull.value,
					   			condition: "do_id=" + Ext.getCmp('do_id').value
					   		},
					   		method : 'post',
					   		callback : function(options,success,response){
					   			var localJson = new Ext.decode(response.responseText);
					   			if(localJson.exceptionInfo){
				        			showError(localJson.exceptionInfo);return;
				        		}
					   		}
						});
    				}
    			}
    		},
    		'erpDeleteButton' : {
    			click: function(btn){
    				this.FormUtil.onDelete(Ext.getCmp('do_id').value);
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				this.FormUtil.onAdd('addDormitory', '新增宿舍', 'jsps/oa/publicAdmin/dormitory/dormitory.jsp');
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				this.FormUtil.beforeClose(this);;
    			}
    		}
    	});
    }, 
    onGridItemClick: function(selModel, record){//grid行选择
    	//if(this.alloweditor){
    		this.GridUtil.onGridItemClick(selModel, record);
    	//}
    },
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
});