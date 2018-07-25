Ext.QuickTips.init();
Ext.define('erp.controller.oa.info.PagingDetail', {
    extend: 'Ext.app.Controller',
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    FormUtil: Ext.create('erp.util.FormUtil'),
    views:[
    		'oa.info.PagingDetail','oa.info.PagingDetailForm','core.form.FileField'
    	],
    init:function(){
    	var me = this;
    	this.control({ 
    		'button[id=close]': {
    			click: function(){
    				me.FormUtil.onClose();
    			}
    		},
    		'button[id=post]': {
    			click: function(){
					var form = Ext.getCmp('form');
					var v = Ext.getCmp('pr_context_r').value;
					if(v != null && v.toString().trim() != ''){
						var o = new Object();
						o.pr_context = v;
						o.pr_releaser = form.down('#prd_recipient').value;
						o.pr_releaserid = form.down('#prd_recipientid').value;
						o.prd_recipient = form.down('#pr_releaser').value;
						o.prd_recipientid = form.down('#pr_releaserid').value;
						me.FormUtil.save(o, []);
					}
    			}
    		},
    		'button[id=draft]': {
    			click: function(btn){
    				if(Ext.getCmp('prd_status').value != 0){//修改状态为保留
    					me.updateStatus(Ext.getCmp('prd_id').value, 0);
    				}
    			}
    		},
    		'field[name=prd_status]': {
    			change: function(f){
    				if(f.value == -1){//修改状态为已阅
    					me.updateStatus(Ext.getCmp('prd_id').value, 1);
    				}
    			}
    		},
    		'button[id=delete]': {
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('prd_id').value);
    			}
    		}
    	});
    },
    /**
     * @param id 明细ID
     * @param status 待修改状态
     */
	updateStatus: function(id, status){
		Ext.Ajax.request({
	   		url : basePath + 'oa/info/updateStatus.action',
	   		params: {
	   			id: id,
	   			status: status
	   		},
	   		method : 'post',
	   		callback : function(options,success,response){
	   			var localJson = new Ext.decode(response.responseText);
	   			if(localJson.exceptionInfo){
	   				showError(localJson.exceptionInfo);return null;
	   			}
	   		}
		});
	}
});