Ext.define('erp.view.sysmng.message.MessageAddPanel',{
	extend: 'Ext.panel.Panel', 
	alias: 'widget.messageaddpanel', 
	id:'messageaddpanel',
	layout:'card',
	bodyBorder: true,
	border: false,
	autoShow: true, 
	items: [
		{
  	  		tag : 'iframe',
  	 		frame : true,
  	 		title:'新增',
  	 		tabConfig:{tooltip:"详细"},
	  	  	layout : 'fit',
	  	  	anchor : '100% 100%',
	  		html : '<iframe src="' + basePath + 'jsps/sysmng/messagedetail.jsp" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>'
	  		}
		],
	
	initComponent : function(){ 
		var me=this;		   
		this.callParent(arguments);
	},
	


	
});