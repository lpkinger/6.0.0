Ext.define('erp.view.core.button.AddLocked',{
	extend : 'Ext.Button',
	alias : 'widget.erpAddLocked',
	requires: ['erp.util.FormUtil'],
	iconCls : 'x-button-icon-check',
	text : $I18N.common.button.erpAddLockedButton,
	cls: 'x-btn-gray',
	width: 110,
	id: 'erpAddLockedButton',
	FormUtil: Ext.create('erp.util.FormUtil'),
	initComponent : function(){
		this.callParent(arguments); 
	},
	handler : function(btn){
		var me = this;
		var grid = Ext.getCmp('batchDealGridPanel');
		var items = grid.getMultiSelected();
		if(items.length!=1){
    		showError("请勾选且只能勾选一条明细");
        	return;
    	}else{
        	var param = new Object();
        	param.sacode = items[0].data.sa_code;
        	param.prodcode = items[0].data.pr_code;
        	param.whichsystem = items[0].data.en_whichsystem;
        	var main = parent.Ext.getCmp("content-panel");
        	var url_ = basePath+'/jsps/common/batchDeal.jsp?whoami=HandLocked!Deal&sacode='+items[0].data.sa_code+'&prodcode='+items[0].data.pr_code+'&prodname='+items[0].data.pr_detail+'&detno='+items[0].data.sd_detno+'&ob_noallqty='+items[0].data.ob_noallqty+'&type='+items[0].data.type+'&id='+items[0].data.sd_id+'&whichsystem='+items[0].data.en_whichsystem;
        	var panel = {
	    			title:'手工加锁',
	    			tag : 'iframe',
	    			frame : true,
	    			border : false,
	    			layout : 'fit',
	    			iconCls : 'x-tree-icon-tab-tab',
	    			html : '<iframe id="iframe_maindetail_'+caller+'" src="'+url_+'" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>',
	    			closable : true
	    	 };
	    	 me.FormUtil.openTab(panel);
    	}
	},
});