Ext.define('erp.view.hr.employee.ViewPortNew',{ 
	extend: 'Ext.Viewport',
	layout:'border',
	border: false, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				layout: 'border', 
				bodyBorder:false,
				region: 'center',
				items:[{
					region:'west',
					xtype:'orgarcset',
					width:'40%'
				},{
					region:'center',
					xtype:'staffinfo'
				}]
			},{
				xtype: 'toolbar',
				region:'north',
				ui: 'footer',
				items: [{
		        	text: '添加组织',
		        	tooltip:'添加新组织',
		        	iconCls: 'btn-add',
		        	itemId: 'addOrg'
		        },'->',{
					xtype:'tbtext',
					text:'<div style="color:gray;">带'+required+'为必填项</div>'
				},'-',{
					text:'添加员工',
					itemId:'addemployee',
					iconCls:'btn-add',
					tooltip:'添加新员工'
				},'-',{
					text:'保&nbsp;&nbsp;存',
					itemId:'saveemployee',
					iconCls:'btn-save'
				}/*,'-',{
					text:'取&nbsp;&nbsp;消',
					itemId:'canceljob',
					iconCls:'btn-cancel'
				}*/]
			
			}]
		});
		me.callParent(arguments); 
} 
});