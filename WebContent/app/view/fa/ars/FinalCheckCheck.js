Ext.define('erp.view.fa.ars.FinalCheckCheck',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [
				{
					xtype: "window",
					autoShow: true,
					closable: false,
					maximizable : true,
					width: '65%',
					height: '65%',
					layout: 'border',
					items: [{
						xtype: 'FinalCheckCheck'
					}]
				}
			        
			        
			        
			        
			        /*{ 
				layout: 'anchor', 
				items: [{
					id:'FinalCheckCheckView',
					confirmUrl:'fa/ars/confirmFinalCheckCheck.action',
					
					xtype: 'FinalCheckCheck',
					anchor: '100% 100%',					
				}]
			}*/] 
		}); 
		me.callParent(arguments); 
	} 
});