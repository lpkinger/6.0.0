//问题反馈编号：2016120061
Ext.QuickTips.init();
Ext.define('erp.controller.ma.DataListCombo', {
	extend: 'Ext.app.Controller',
	requires: ['erp.util.BaseUtil'],
	views:['ma.comboset.DataListCombo','ma.comboset.GridPanel','core.grid.YnColumn'],
	      init:function(){
	    	   this.BaseUtil = Ext.create('erp.util.BaseUtil');
	    	   var me =this;
	    	   this.control({
	    		   'erpCombolistGridPanel': { 
	    			   	itemclick: this.onGridItemClick
	    		   }
	    	   });
	      }, 
	      onGridItemClick: function(selModel, record){//grid行选择
	       		this.openUrl(record);
	      }, 
	      openUrl: function(record) {
	    	   var me = this;
	    	   var field = record.data['FIELDNAME'];
	    	   var caller = record.data['CALLER'];
	    	   var gridCondition = 'callerIS'+record.data['CALLER']+ ' AND fieldIS'+field;
	    	   var panelId = caller + field;
	    	   var panel = Ext.getCmp(panelId); 
	    	   var main = parent.Ext.getCmp("content-panel");
	    	   if(!main){
	    		   main = parent.parent.Ext.getCmp("content-panel");
	    	   }
	    	   if(!panel){ 
	    		   var title = field;
	    		   var myurl = '';
	    		   if(me.BaseUtil.contains(url, '?', true)){
	    			   myurl = url + '&gridCondition='+gridCondition;
	    		   } else {
	    			   myurl = url + '?gridCondition='+gridCondition;
	    		   }
	    		   myurl += "&datalistId=" + main.getActiveTab().id;
	    		   if(getUrlParam('_config')){
	    			   myurl +='&_config='+getUrlParam('_config');
	    		   }
	    		   if(main._mobile) {
	    			   main.addPanel(me.BaseUtil.getActiveTab().title+'('+title+')', myurl, panelId);
	    		   } else {
	    			   panel = {       
	    					   title : me.BaseUtil.getActiveTab().title+'('+title+')',
	    					   tag : 'iframe',
	    					   tabConfig:{tooltip:me.BaseUtil.getActiveTab().tabConfig.tooltip+'(caller='+caller+' field='+field+')'},
	    					   border : false,
	    					   layout : 'fit',
	    					   iconCls : 'x-tree-icon-tab-tab1',
	    					   html : '<iframe id="iframe_maindetail_'+caller+"_"+field+'" src="' + myurl + '" height="100%" width="100%" frameborder="0" style="border-width: 0px;padding: 0px;" scrolling="auto"></iframe>',
	    					   closable : true,
	    					   listeners : {
	    						   close : function(){
	    							   if(!main){
	    								   main = parent.parent.Ext.getCmp("content-panel");
	    							   }
	    							   main.setActiveTab(main.getActiveTab().id); 
	    						   }
	    					   } 
	    			   };
	    			   this.openTab(panel, panelId);
	    		   }
	    	   }else{ 
	    		   main.setActiveTab(panel); 
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