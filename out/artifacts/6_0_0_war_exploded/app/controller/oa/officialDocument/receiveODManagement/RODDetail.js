Ext.QuickTips.init();
Ext.define('erp.controller.oa.officialDocument.receiveODManagement.RODDetail', {
    extend: 'Ext.app.Controller',
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    FormUtil: Ext.create('erp.util.FormUtil'),
    views:[
    		'oa.officialDocument.receiveODManagement.RODDetail',
    		'oa.officialDocument.receiveODManagement.RODDetailForm','core.form.FileField'
    	],
    init:function(){
    	var me = this;
    	this.control({ 
    		'button[id=close]': {
    			click: function(){
    				me.FormUtil.onClose();
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
    				var id = Ext.getCmp('rod_id').value;
		    		var panel = Ext.getCmp("drod" + id); 
		    		var main = parent.Ext.getCmp("content-panel");
		    		if(!panel){ 
		    			var title = "收文转发文";
		    			panel = { 
		    					title : title,
		    					tag : 'iframe',
		    					tabConfig:{tooltip: Ext.getCmp('rod_title').value},
		    					frame : true,
		    					border : false,
		    					layout : 'fit',
		    					iconCls : 'x-tree-icon-tab-tab1',
		    					html : '<iframe id="iframe_' + id + '" src="' + basePath + "jsps/oa/officialDocument/sendODManagement/draft.jsp?id=" + id + '" height="100%" width="100%" frameborder="0" scrolling="yes"></iframe>',
		    					closable : true,
		    					listeners : {
		    						close : function(){
		    							main.setActiveTab(main.getActiveTab().id); 
		    						}
		    					} 
		    			};
		    			me.FormUtil.openTab(panel, "drod" + id); 
		    		}else{ 
		    			main.setActiveTab(panel); 
		    		}	   		
    				
    			}
    		}
    	});
    }
});