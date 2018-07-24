Ext.QuickTips.init();
Ext.define('erp.controller.oa.officialDocument.sod.Query', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
     		'oa.officialDocument.sod.query.Viewport','common.datalist.GridPanel','common.datalist.Toolbar',
     		'oa.officialDocument.sod.query.Form',
     		'core.trigger.DbfindTrigger','core.form.ConDateField','core.form.WordSizeField','oa.mail.MailPaging'
     	],
    init:function(){
    	var me = this;
    	this.control({
    		'erpDatalistGridPanel': {
    			afterrender: function(grid){
    				grid.onGridItemClick = function(){//改为点击button进入详细界面
    					me.onGridItemClick(grid.selModel.lastSelected);
    				};
    			}
    		}
    	});
    },
    onGridItemClick: function(record){//grid行选择
    	console.log(record);
    	var me = this;
    	var id = record.data.sod_id;
    	var panel = Ext.getCmp("qsod" + id); 
    	var main = parent.Ext.getCmp("content-panel");
    	if(!panel){ 
    		var title = "发文查看";
	    	panel = { 
	    			title : title,
	    			tag : 'iframe',
	    			tabConfig:{tooltip: record.data['sod_title']},
	    			frame : true,
	    			border : false,
	    			layout : 'fit',
	    			iconCls : 'x-tree-icon-tab-tab1',
	    			html : '<iframe id="iframe_' + id + '" src="' + basePath + "jsps/oa/officialDocument/sendODManagement/sodDetail.jsp?flag=query&id=" + id + '" height="100%" width="100%" frameborder="0" scrolling="yes"></iframe>',
	    			closable : true,
	    			listeners : {
	    				close : function(){
	    			    	main.setActiveTab(main.getActiveTab().id); 
	    				}
	    			} 
	    	};
	    	me.FormUtil.openTab(panel, "qsod" + id); 
    	}else{ 
	    	main.setActiveTab(panel); 
    	} 
    }

});