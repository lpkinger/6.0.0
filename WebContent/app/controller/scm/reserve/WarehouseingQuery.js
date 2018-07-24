Ext.QuickTips.init();
Ext.define('erp.controller.scm.reserve.WarehouseingQuery', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'scm.reserve.WarehouseingQuery','core.form.Panel','common.query.GridPanel','core.grid.YnColumn', 'core.grid.TfColumn',
    		'core.button.Query','core.button.Update','core.button.Close','core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger'
    	],
    init:function(){
    	var me = this;
    	this.control({
    		'erpQueryButton' : {
    			click: function(btn) {
    				me.query();    				
    			}
    		},
    		'erpUpdateButton':{
    			click:function(btn){
    				me.update();
    			}
    		},
    		'erpCloseButton':{
    			click:function(btn){
    				me.FormUtil.onClose();
    			}
    		},
    		'#grid':{
    			afterrender: function(g) {
    				var whi_code = Ext.getCmp('whi_code').value;
    				if(!Ext.isEmpty(whi_code)){
    					g.getStore().load({
        					params: {
        						whi_code: whi_code
        					}
        				});
    				}
    			}
    		},
    		'#whi_code':{
    			specialkey: function(f, e){//按ENTER执行确认
    				if (e.getKey() == e.ENTER) {
    					if(f.value != null && f.value != '' ){
    						me.query();
        				}
    				}
    			}
    		},
    	});
    },
	query : function(){
		var me = this, grid = Ext.getCmp('grid');
		var whi_code = Ext.getCmp("whi_code").value; 
		if(Ext.isEmpty(whi_code)){
			showError("请输入入仓单号！");
			return ;
		} else {
			grid.getStore().load({
				params: {
					whi_code: whi_code
				}
			});
		}
	},
	update : function(){
		var me = this, grid = Ext.getCmp('grid');
		var whi_code = Ext.getCmp("whi_code").value, 
			whi_status = Ext.getCmp("whi_status").value,
			whi_text = Ext.getCmp("whi_text").value; 
		if(Ext.isEmpty(whi_status)){
			showError("请输入当前状态！");
			return ;
		} else {
			Ext.Ajax.request({
	        	url : basePath + 'scm/reserve/updateWarehouseing.action',
	        	params: {
	        		whi_code: whi_code,
	        		whi_status: whi_status,
	        		whi_text: whi_text
	        	},
	        	method : 'post',
	        	async:false,
	        	callback : function(options,success,response){
	        		var res = new Ext.decode(response.responseText);
	        		if(res.exceptionInfo != null){
	        			showError(res.exceptionInfo);return;
	        		}
	        		me.query();
	        	}
	        });
		}
	},
});