Ext.QuickTips.init();
Ext.define('erp.controller.oa.persontask.workDaily.Query', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'oa.persontask.workDaily.Query','common.datalist.Toolbar','core.form.ConDateField',
    		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.form.DetailTextField','core.form.FileField','common.batchDeal.Form','common.batchDeal.GridPanel'
    	],
    init:function(){
    	var me = this;
    	this.control({ 
    		'erpBatchDealGridPanel': {
    			afterrender:function(t){
    				t.columns[0].hide();
    			},
    			itemclick: this.onGridItemClick
    		},
    		'dbfindtrigger[name=wd_emp]': {
    			afterrender: function(f) {
    				f.dbBaseCondition = 'or_headmancode=\'' + em_code + '\'';
    			}
    		}
    	});
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	onGridItemClick: function(selModel, record){//grid行选择
    	var me = this;
    	var path = 'jsps/oa/persontask/workDaily/addWorkDaily.jsp?whoami=ResourceAssignment2&urlcondition=ra_emid='+em_uu +'  AND ra_taskpercentdone<100&caller=WorkDaily';
    	var id = record.data.wd_id;
    	var title = '工作日报查看';
    	var panel = Ext.getCmp('workdaily' + id); 
    	var main = parent.Ext.getCmp("content-panel");
    	if(!panel){ 
	    	panel = { 
	    			title : title,
	    			tag : 'iframe',
	    			tabConfig:{tooltip: title},
	    			frame : true,
	    			border : false,
	    			layout : 'fit',
	    			iconCls : 'x-tree-icon-tab-tab1',
	    			html : '<iframe id="iframe_' + id + '" src="' + basePath + path + "&formCondition=wd_idIS" + id + '&gridCondition=" height="100%" width="100%" frameborder="0" scrolling="yes"></iframe>',
	    			closable : true,
	    			listeners : {
	    				close : function(){
	    			    	main.setActiveTab(main.getActiveTab().id); 
	    				}
	    			} 
	    	};
	    	me.FormUtil.openTab(panel, 'workdaily' + id); 
    	}else{ 
	    	main.setActiveTab(panel); 
    	} 
    }
	
});