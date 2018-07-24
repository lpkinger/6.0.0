Ext.define('erp.view.ma.DBfindSetUI',{ 
	extend: 'Ext.Viewport',
	layout: 'anchor',
	id:'DBfindSetUI',
	border: false,
	initComponent : function(){
		
		
		
		var me=this;
		var windows=Ext.create('Ext.form.Panel', {
			width: '100%',
			height: '100%',
			autoShow: true,
			
			layout: 'border',
			title:'<h1>主表放大镜配置</h1>',
			items:[{
						xtype:'DBfindSetUIForm',
						anchor: '100% 50%'
						
					},
					{
						xtype:'DBfindSetUIGrid',
						anchor: '100% 50%'
					}]
		});
		
		me.items=windows;
		

		me.callParent(arguments); 
	} 
});
