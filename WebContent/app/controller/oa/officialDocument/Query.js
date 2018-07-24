Ext.QuickTips.init();
Ext.define('erp.controller.oa.officialDocument.Query', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
     		'oa.officialDocument.query.Viewport','oa.officialDocument.query.GridPanel','oa.officialDocument.query.Form',
     		'core.trigger.DbfindTrigger','core.form.FtField'
     	],
    init:function(){
    	this.control({
    		'erpQueryGridPanel': { 
    			itemclick: this.onGridItemClick 
    		},
    		'erpQueryFormPanel button[name=confirm]': {
    			click: function(btn){
    				
    			}
    		}
    	});
    },
    onGridItemClick: function(selModel, record){//grid行选择
    	var me = this;
    	var title = '';
    	var value = 0;
    	if (me.FormUtil.contains(caller, "Receive", true)) {
    		url=basePath+"jsps/oa/officialDocument/receiveODManagement/register.jsp";
    		title = "收文";
    		keyField = "rod_id";
		} else if (me.FormUtil.contains(caller, "Send", true)){
			url=basePath+"jsps/oa/officialDocument/sendODManagement/draft.jsp";
			title = "发文";
			keyField = "sod_id";
		}
    	value = record.data[keyField];
//    	alert(value);
    	var panel = Ext.getCmp(keyField + "=" + value);
    	var main = parent.Ext.getCmp("content-panel");
    	if (!panel) {
    		panel = { 
    				//title : main.getActiveTab().title+'('+title+')',
    				title: title + '('+value+')',
    				tag : 'iframe',
    				tabConfig:{tooltip: title + '('+value+')'},
    				frame : true,
    				border : false,
    				layout : 'fit',
    				iconCls : 'x-tree-icon-tab-tab',
    				html : '<iframe id="iframe_maindetail_'+caller+"_"+value+'" src="'+url+'?formCondition=' + keyField + 'IS'+value+'" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>',
    				closable : true,
    				listeners : {
    					close : function(){
    						main.setActiveTab(main.getActiveTab().id); 
    					}
    				}
    		};	
    		me.openTab(panel,keyField + "=" + value); 
        } else { 
        	main.setActiveTab(panel);
		}
    },
    openTab : function (panel,id){ 
//        var me = this;
      	var o = (typeof panel == "string" ? panel : id || panel.id); 
      	var main = parent.Ext.getCmp("content-panel"); 
      	var tab = main.getComponent(o); 
      	if (tab) { 
      		main.setActiveTab(tab); 
      	} else if(typeof panel!="string"){ 
      		panel.id = o; 
      		var p = main.add(panel); 
      		main.setActiveTab(p); 
      	} 
     }

});