Ext.QuickTips.init();
Ext.define('erp.controller.oa.officialDocument.sendODManagement.SODDetail', {
    extend: 'Ext.app.Controller',
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    FormUtil: Ext.create('erp.util.FormUtil'),
    views:[
    		'oa.officialDocument.sendODManagement.SODDetail','oa.officialDocument.sendODManagement.SODDetailForm',
    		'core.button.Update','core.button.Delete','core.button.Submit','core.button.ResSubmit','core.button.Audit',
    		'core.button.ResAudit'
    	],
    init:function(){
    	var me = this;
    	this.control({ 
    		'button[id=close]': {
    			click: function(){
    				me.FormUtil.onClose();
    			}
    		},
    		'erpUpdateButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('sod_statuscode');
    				if(status && status.value != null && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('sod_statuscode');
    				if(status && status.value != null && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				this.FormUtil.onSubmit(Ext.getCmp('sod_id').value);
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('sod_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				this.FormUtil.onResSubmit(Ext.getCmp('sod_id').value);
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('sod_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				this.FormUtil.onAudit(Ext.getCmp('sod_id').value);
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('sod_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				this.FormUtil.onResAudit(Ext.getCmp('sod_id').value);
    			}
    		},
    		'erpDeleteButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('sod_statuscode');
    				if(status && status.value != null && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('sod_id').value);
    			}
    		},
    		'button[id=distribute]': {
    			afterrender: function(btn){
    				var flag = getUrlParam('flag');
    				if(flag == 'query'){
    					btn.setVisible(false);
    				}
    			},
    			click: function(){
    				var me = this;
    				var id = Ext.getCmp('sod_id').value;
		    		var panel = Ext.getCmp("dsod" + id); 
		    		var main = parent.Ext.getCmp("content-panel");
		    		if(!panel){ 
		    			var title = "发文转收文";
		    			panel = { 
		    					title : title,
		    					tag : 'iframe',
		    					tabConfig:{tooltip: Ext.getCmp('sod_title').value},
		    					frame : true,
		    					border : false,
		    					layout : 'fit',
		    					iconCls : 'x-tree-icon-tab-tab1',
		    					html : '<iframe id="iframe_' + id + '" src="' + basePath + "jsps/oa/officialDocument/receiveODManagement/register.jsp?id=" + id + '" height="100%" width="100%" frameborder="0" scrolling="yes"></iframe>',
		    					closable : true,
		    					listeners : {
		    						close : function(){
		    							main.setActiveTab(main.getActiveTab().id); 
		    						}
		    					} 
		    			};
		    			me.FormUtil.openTab(panel, "dsod" + id); 
		    		}else{ 
		    			main.setActiveTab(panel); 
		    		}	   		
    			}
    		}
    	});
    }
});