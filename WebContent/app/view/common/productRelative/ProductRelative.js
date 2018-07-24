Ext.define('erp.view.common.productRelative.ProductRelative',{ 
	extend: 'Ext.Viewport', 
	layout: 'border',
	hideBorders: true, 
	initComponent : function(){ 
		var me = this;
		var cal;
		if(caller=='ProductRelative!Query'){
			cal = 'Product';
		}
		if(caller=='ProjectRelative!Query'){
			cal = 'Project';
		}
		if(caller=='GroupProductRelative!Query'){
			cal = 'GroupProduct';
		}
		Ext.apply(me, { 
			items: [{ 
				region: 'north',  
				xtype: "ProductRelativeFormPanel",				
			},{
				region: 'center',
				tag :'iframe',
				id:'ProductWh',
				close:function(){
					var main = parent.Ext.getCmp("content-panel");
					main.getActiveTab().close();
				},
				html : '<iframe src="' + basePath + 'jsps/common/relativeSearch.jsp?whoami=' + cal + '" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>'  	
			}]
		});
		me.callParent(arguments); 
	}	
});