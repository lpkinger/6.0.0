Ext.QuickTips.init();
Ext.define('erp.controller.scm.reserve.ProdIODetail', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','scm.reserve.ProdIODetail','core.grid.Panel2','core.toolbar.Toolbar',
      		'core.button.PrintBar','core.button.ClearSubpackage','core.button.Subpackage','core.button.Close',
  			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger'
  	],
	init:function(){
		var me = this;
		this.control({
			'erpGridPanel2': { 
				itemclick: this.onGridItemClick
			},
			'erpSubpackageButton':{
    			click: function(btn){
    				warnMsg("确定分装?", function(btn){
    					if(btn == 'yes'){
    						me.FormUtil.setLoading(true);//loading...
    	    				Ext.Ajax.request({
    	    			   		url : basePath + 'scm/reserve/SubpackageDetail.action',
    	    			   		params: {
    	    			   			tqty: Ext.getCmp('pd_unitpackage').value,
    	    			   			id: Ext.getCmp('pd_id').value
    	    			   		},
    	    			   		method : 'post',
    	    			   		callback: function(opt, s, r) {
    	    			   			me.FormUtil.setLoading(false);
    	    						var rs = Ext.decode(r.responseText);
    	    						if(rs.exceptionInfo) {
    	    							showError(rs.exceptionInfo);
    	    						} else {
    	    							if(rs.log)
    	    								showMessage('提示', rs.log);
    	    						}
    	    						window.location.reload();
    	    					}
    	    				});
    					}
    				});
    			}
    		},
    		'erpClearSubpackageButton':{
    			click: function(btn){
    				warnMsg("确定清除分装?", function(btn){
    					if(btn == 'yes'){
    						me.FormUtil.setLoading(true);//loading...
    	    				Ext.Ajax.request({
    	    			   		url : basePath + 'scm/reserve/ClearSubpackageDetail.action',
    	    			   		params: {
    	    			   			id: Ext.getCmp('pd_id').value
    	    			   		},
    	    			   		method : 'post',
    	    			   		callback: function(opt, s, r) {
    	    			   			me.FormUtil.setLoading(false);
    	    						var rs = Ext.decode(r.responseText);
    	    						if(rs.exceptionInfo) {
    	    							showError(rs.exceptionInfo);
    	    						} else {
    	    							if(rs.log)
    	    								showMessage('提示', rs.log);
    	    						}
    	    						window.location.reload();
    	    					}
    	    				});
    					}
    				});
    			}
    		},
    		'erpPrintBarButton':{
    			click: function(btn){
    				var reportName = "bar_53";
					var condition = '{ProdIODetailBar.pdb_pdid}='+ Ext.getCmp('pd_id').value;
					var id = Ext.getCmp('pd_id').value;
					me.FormUtil.onwindowsPrint(id, reportName, condition);
    			}
    		},
			'erpCloseButton': {
				click: function(btn){
					me.FormUtil.beforeClose(me);
				}
			}
    	});
    }, 
    onGridItemClick: function(selModel, record){//grid行选择
    	this.GridUtil.onGridItemClick(selModel, record);
    },
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
});