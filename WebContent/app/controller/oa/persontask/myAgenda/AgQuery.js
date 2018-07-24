Ext.QuickTips.init();
Ext.define('erp.controller.oa.persontask.myAgenda.AgQuery', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
     		'oa.persontask.myAgenda.agQuery.Viewport','common.datalist.GridPanel','common.datalist.Toolbar',
     		'oa.persontask.myAgenda.agQuery.Form',
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
//    	var me = this;
    	var id = record.data.ag_id;
    	var win = new Ext.window.Window({
			id : 'win',
			title: "日程查看",
			height: "320px",
			width: "50%",
			maximizable : false,
			buttonAlign : 'left',
			layout : 'anchor',
			items: [{
				tag : 'iframe',
				frame : true,
				anchor : '100% 100%',
				layout : 'fit',
				html : '<iframe id="iframe_' + id + '" src="' + basePath + 'jsps/oa/persontask/myAgenda/seeAgenda.jsp?id=' + id + '" height="100%" width="100%" frameborder="0" scrolling="yes"></iframe>'
			}]
		});
		win.show();	
    }

});