Ext.define('erp.view.scm.purchase.vendorbatchdeal.noPassedUU', { 
	extend: 'Ext.panel.Panel', 
	alias:'widget.erpSuppliernoPassedUU',
	layout: 'fit', 
	hideBorders: true, 
	enableTools : true,
	initComponent : function(){ 
		var me = this; 		
		me.callParent(arguments); 
	} ,
	padding:'5 0 0 0',
	items: [{
  	  tag : 'iframe',
  	  frame : true,
  	  layout : 'fit',
  	  anchor : '100% 100%',
  	  html : '<iframe id="erpSuppliernoPassedUU" src="' + basePath + 'jsps/common/batchDeal.jsp?whoami=Vendor!CheckUU&_noc=1" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>'
	}],
 
});