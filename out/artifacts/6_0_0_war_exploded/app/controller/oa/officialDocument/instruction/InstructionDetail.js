Ext.QuickTips.init();
Ext.define('erp.controller.oa.officialDocument.instruction.InstructionDetail', {
    extend: 'Ext.app.Controller',
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    FormUtil: Ext.create('erp.util.FormUtil'),
    views:[
    		'oa.officialDocument.instruction.InstructionDetail','oa.officialDocument.instruction.InstructionDetailForm'
    	],
    init:function(){
    	var me = this;
    	this.control({ 
    		'button[id=close]': {
    			click: function(){
    				me.FormUtil.onClose();
    			}
    		},
//    		'button[id=distribute]': {
//    			afterrender: function(btn){
//    				var flag = getUrlParam('flag');
//    				if(flag == 'query'){
//    					btn.setVisible(false);
//    				}
//    			},
//    			click: function(){
//    				var me = this;
////    				alert(Ext.getCmp('sod_id').value);
//    				var id = Ext.getCmp('sod_id').value;
//		    		var panel = Ext.getCmp("dsod" + id); 
//		    		var main = parent.Ext.getCmp("content-panel");
//		    		if(!panel){ 
//		    			var title = "发文转收文";
////		    			alert(title);
//		    			panel = { 
//		    					title : title,
//		    					tag : 'iframe',
//		    					tabConfig:{tooltip: Ext.getCmp('sod_title').value},
//		    					frame : true,
//		    					border : false,
//		    					layout : 'fit',
//		    					iconCls : 'x-tree-icon-tab-tab1',
//		    					html : '<iframe id="iframe_' + id + '" src="' + basePath + "jsps/oa/officialDocument/receiveODManagement/register.jsp?id=" + id + '" height="100%" width="100%" frameborder="0" scrolling="yes"></iframe>',
//		    					closable : true,
//		    					listeners : {
//		    						close : function(){
//		    							main.setActiveTab(main.getActiveTab().id); 
//		    						}
//		    					} 
//		    			};
//		    			me.FormUtil.openTab(panel, "dsod" + id); 
//		    		}else{ 
//		    			main.setActiveTab(panel); 
//		    		}	   		
//    			}
//    		}
    	});
    }
});