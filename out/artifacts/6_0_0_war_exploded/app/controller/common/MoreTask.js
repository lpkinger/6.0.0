Ext.QuickTips.init();
Ext.define('erp.controller.common.MoreTask', {
	extend : 'Ext.app.Controller',
	requires : ['erp.util.BaseUtil', 'erp.util.FormUtil', 'erp.util.RenderUtil'],
	views : ['common.DeskTop.MoreTask', 'common.datalist.GridPanel',
			'common.datalist.Toolbar', 'core.button.VastAudit',
			'core.button.VastDelete', 'core.button.VastPrint',
			'core.button.VastReply', 'core.button.VastSubmit',
			'core.button.ResAudit', 'core.form.FtField', 'core.grid.TfColumn',
			'core.grid.YnColumn', 'core.trigger.DbfindTrigger',
			'core.form.FtDateField', 'core.form.FtFindField',
			'core.form.FtNumberField', 'core.form.MonthDateField','core.grid.HeaderFilter','erp.view.core.window.Task','common.DeskTop.DeskTabPanel'],
	init : function() {
		var me = this;
		this.BaseUtil = Ext.create('erp.util.BaseUtil');
		this.FormUtil = Ext.create('erp.util.FormUtil');
		this.GridUtil = Ext.create('erp.util.GridUtil');
		this.control({
			'erpDatalistGridPanel' : {
				 itemclick: this.onGridItemClick
			},
			'#addTask':{
				click: function(b){
		        	var win = Ext.create('erp.view.core.window.Task');
		        	win.show();				
    			}
			}		
		});
	},
	onGridItemClick: function(selModel, record){
	  //grid行选择
		var grid = selModel.ownerCt;
    	var mainPanel = Ext.getCmp('desktabpanel');
    	var keyField= mainPanel.activeTab.keyField;
    	var pfField= mainPanel.activeTab.pfField;
    	if(keyField != null && keyField != ''){	    		
    		var value = record.data[keyField];
        	var formCondition = keyField + "IS" + value ;
        	var gridCondition = pfField + "IS" + value;
        	var panel = Ext.getCmp(caller + keyField + "=" + value); 
        	var main = parent.Ext.getCmp("content-panel");
        	if(!panel){ 
        		var title = "";
    	    	if (value.toString().length>4) {
    	    		 title = value.toString().substring(value.toString().length-4);	
    	    	} else {
    	    		title = value;
    	    	}
    	    	var myurl = '',
    	    	    url = basePath +selModel.ownerCt.url;
    	    	if(record.data.ra_type=='projecttask'){
    	    		url = basePath + 'jsps/plm/record/workrecord.jsp';
    	    	}else if(record.data.ra_type=='billtask'){
    	    		url = basePath + 'jsps/plm/record/billrecord.jsp?_noc=1';
    	    	}
    	    	if(mainPanel.BaseUtil.contains(url, '?', true)){
    	    		myurl = url + '&formCondition='+formCondition+'&gridCondition='+gridCondition;
    	    	} else {
    	    		myurl = url + '?formCondition='+formCondition+'&gridCondition='+gridCondition;
    	    	}
    	    	myurl += "&datalistId=" + main.getActiveTab().id;
    	    	main.getActiveTab().currentStore = mainPanel.getCurrentStore(value);//用于单据翻页    	
    	    	panel = {       
    	    			title : mainPanel.BaseUtil.getActiveTab().title+'('+title+')',
    	    			tag : 'iframe',
    	    			tabConfig:{tooltip:mainPanel.BaseUtil.getActiveTab().tabConfig.tooltip+'('+keyField + "=" + value+')'},
    	    			frame : true,
    	    			border : false,
    	    			layout : 'fit',
    	    			iconCls : 'x-tree-icon-tab-tab1',
    	    			html : '<iframe id="iframe_maindetail_'+caller+"_"+value+'" src="' + myurl + '" height="100%" width="100%" frameborder="0" style="border-width: 0px;padding: 0px;" scrolling="auto"></iframe>',
    	    			closable : true,
    	    			listeners : {
    	    				close : function(){
    	    			    	main.setActiveTab(main.getActiveTab().id); 
    	    				}
    	    			} 
    	    	};
    	    	this.openTab(panel, caller + keyField + "=" + record.data[keyField]);
        	}else{ 
    	    	main.setActiveTab(panel); 
        	} 
    	}
    
	  }, 
    openTab : function (panel,id){ 
    	var o = (typeof panel == "string" ? panel : id || panel.id); 
    	var main = parent.Ext.getCmp("content-panel"); 
    	if(!main) {
    		main =parent.parent.Ext.getCmp("content-panel"); 
    	}
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