Ext.QuickTips.init();
Ext.define('erp.controller.scm.reserve.BarPrint', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','scm.reserve.BarPrint','core.grid.Panel2','core.toolbar.Toolbar',
      		'core.button.Save','core.button.Add','core.button.Close','core.button.Update','core.button.DeleteDetail',
      		'core.button.PrintBar','core.button.ClearSubpackage','core.button.Subpackage','core.button.Scan',
			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField'
      	],
    init:function(){
    	var me = this;
    	this.control({
    		'erpGridPanel2': { 
    			itemclick: this.onGridItemClick
    		},
    		'erpSaveButton': {
    			click: function(btn){
    				var me = this;
    				var form = me.getForm(btn);
    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
    					me.BaseUtil.getRandomNumber();//自动添加编号
    				}
    				this.FormUtil.beforeSave(this);
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addBarPrint', '新增条码打印', 'jsps/scm/reserve/barPrint.jsp');
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				me.FormUtil.beforeClose(me);
    			}
    		},
    		'erpSubpackageButton':{
    			click: function(btn){
    				warnMsg("确定分装?", function(btn){
    					if(btn == 'yes'){
    						me.FormUtil.setLoading(true);//loading...
    	    				Ext.Ajax.request({
    	    			   		url : basePath + 'scm/reserve/BarPrint/Subpackage.action',
    	    			   		params: {
    	    			   			id: Ext.getCmp('bp_id').value,
    	    			   			tqty: Ext.getCmp('bp_unitpackage').value
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
    	    			   		url : basePath + 'scm/reserve/BarPrint/ClearSubpackage.action',
    	    			   		params: {
    	    			   			id: Ext.getCmp('bp_id').value
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
	    			var reportName = "bar_52_2";
	    			var id = Ext.getCmp('bp_id').value;
					var condition = '{BarPrint.bp_id}=' + id;
					me.FormUtil.onwindowsPrint(id, reportName, condition);
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