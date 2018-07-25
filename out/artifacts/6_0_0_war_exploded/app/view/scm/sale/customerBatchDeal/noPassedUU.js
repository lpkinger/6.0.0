Ext.define('erp.view.scm.sale.customerBatchDeal.noPassedUU', { 
	extend: 'Ext.panel.Panel', 
	alias:'widget.erpnoPassedUU',
	layout: 'fit', 
	padding:'5 0 0 0',
	hideBorders: true, 
	enableTools : true,
	initComponent : function(){ 
		var me = this; 		
		me.callParent(arguments); 
	} ,
	items: [{
  	  tag : 'iframe',
  	  frame : true,
  	  layout : 'fit',
  	  anchor : '100% 100%',
  	html : '<iframe src="' + basePath + 'jsps/common/batchDeal.jsp?whoami=Customer!CheckUU&urlcondition=cu_uu is null AND cu_b2benable='+0+'" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>'
  }],
 
});