Ext.QuickTips.init();
Ext.define('erp.controller.oa.info.PagingRelease', {
    extend: 'Ext.app.Controller',
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    FormUtil: Ext.create('erp.util.FormUtil'),
    views:[
    		'oa.info.PagingRelease','oa.info.IcqForm','oa.mail.TreePanel', 'core.form.FileField'
    	],
    init:function(){
    	var me = this;
    	this.control({ 
    		'erpMailTreePanel': {
    			afterrender: function(tree){
    				tree.selModel.on('select', function(selModel, record){
    					record.selected = true;//标志为已选
    					if(record.childNodes.length > 0){
    						selModel.isOnSelect = true;//标志为正在select，否则直接死循环
    						selModel.select(record.childNodes);
    						Ext.each(record.childNodes, function(){
    							this.selected = true;
    						});
    						selModel.isOnSelect = false;
    						me.setRecipient(selModel.getSelection());
    						me.setRecipientId(selModel.getSelection());
    						me.setMobile(selModel.getSelection());
    					} else {
    						if(!selModel.isOnSelect){
    							var arr = selModel.getSelection();
        						arr.push(record);
        						selModel.isOnSelect = true;
        						selModel.select(arr);
        						selModel.isOnSelect = false;
        						me.setRecipient(selModel.getSelection());
        						me.setRecipientId(selModel.getSelection());
        						me.setMobile(selModel.getSelection());
    						}
    						return;
    					}
    				});
    				tree.selModel.on('deselect', function(selModel, record){
    					record.selected = false;
    					if(record.childNodes.length > 0){
    						selModel.deselect(record.childNodes);
    						Ext.each(record.childNodes, function(){
    							this.selected = false;
    						});
    						me.setRecipient(selModel.getSelection());
    						me.setRecipientId(selModel.getSelection());
    						me.setMobile(selModel.getSelection());
    					} else {
    						selModel.deselect(record);
    						me.setRecipient(selModel.getSelection());
    						me.setRecipientId(selModel.getSelection());
    						me.setMobile(selModel.getSelection());
    						return;
    					}
    				});
    			}
    		},
    		'button[id=close]': {
    			click: function(){
    				me.FormUtil.onClose();
    			}
    		},
    		'button[id=post]': {
    			click: function(btn){
					var form = btn.ownerCt.ownerCt;
					if(form.down('#prd_recipient').value != null && form.down('#prd_recipient').value != ''){
						me.FormUtil.beforeSave(form);
					}
    			}
    		},
    		'field[name=pr_context]': {
    			afterrender: function(f) {
    				var contextId = getUrlParam('pr_id');
    				if(!Ext.isEmpty(contextId)) {
    					Ext.Ajax.request({
    				   		url : basePath + 'common/getFieldData.action',
    				   		async: false,
    				   		params: {
    				   			caller: 'OA_PAGINGRELEASE_VIEW',
    				   			field: 'pr_context',
    				   			condition: 'pr_id=' + contextId
    				   		},
    				   		method : 'post',
    				   		callback : function(options,success,response){
    				   			var localJson = new Ext.decode(response.responseText);
    				   			if(localJson.exceptionInfo){
    				   				showError(localJson.exceptionInfo);return;
    				   			}
    			    			if(localJson.success){
    			    				if(localJson.data != null){
    			    					f.setValue(localJson.data);
    			    				}
    				   			}
    				   		}
    					});
    				}
    			}
    		}
    	});
    },
	setRecipient: function(records){
		var r = '';
		Ext.each(records, function(){
			if(r != ''){
				r += ';';
			}
			r += this.get('text');
		});
		Ext.getCmp('prd_recipient').setHeight(20*Math.ceil(records.length/20) || 20);
		Ext.getCmp('prd_recipient').setValue(r);
	},
	setRecipientId: function(records){
		var r = '';
		Ext.each(records, function(){
			if(r != ''){
				r += ';';
			}
			r += Math.abs(this.get('id'));
		});
		Ext.getCmp('prd_recipientid').setValue(r);
	},
	setMobile: function(records){
		var r = '';
		Ext.each(records, function(){
			if(r != ''){
				r += ';';
			}
			r += this.get('qtitle');
		});
		Ext.getCmp('prd_mobile').setHeight(20*Math.ceil(records.length/10) || 20);
		Ext.getCmp('prd_mobile').setValue(r);
	}
});