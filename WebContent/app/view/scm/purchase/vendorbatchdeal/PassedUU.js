Ext.define('erp.view.scm.purchase.vendorbatchdeal.PassedUU', { 
	extend: 'Ext.panel.Panel', 
	alias:'widget.erpSupplierPassedUU',
	layout: 'fit', 
	hideBorders: true, 
	enableTools : true,
	padding:'5 0 0 0',
	initComponent : function(){ 
		var me = this; 
		me.callParent(arguments); 
	} ,
	items: [{
	  	  tag : 'iframe',
	  	  frame : true,
	  	  layout : 'fit',
	  	  anchor : '100% 100%',
	  	html : '<iframe id="erpSuppliernoPassedUU" src="' + basePath + 'jsps/common/batchDeal.jsp?whoami=Vendor!CheckUU!Open&_noc=1" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>'
	  }],
});