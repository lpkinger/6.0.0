Ext.define('erp.view.common.sysinit.ViewPort',{ 
	extend: 'Ext.Viewport', 
	layout: 'border', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [/*{region: 'north',
				height: 32,
				xtype:'panel'
					autoEl: {
                        tag: 'div',
                        html:'<p>north - generally for menus, toolbars and/or advertisements</p>'
                    }
			},*/ /*{
				region: 'south',
				xtype:'panel',
				split: true,
				height: 100,
				minSize: 100,
				maxSize: 200,
				collapsible: true,
				collapsed: true,
				title: 'South',
				margins: '0 0 0 0'
			},*/{
				xtype: 'syspanel',
				width:'20%',
				region:'west'
			},{
				xtype:'sysTabPanel'
			}]
		});
		me.callParent(arguments); 
	}
});