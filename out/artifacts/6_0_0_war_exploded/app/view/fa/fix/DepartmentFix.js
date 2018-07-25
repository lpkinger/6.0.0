Ext.define('erp.view.fa.fix.DepartmentFix',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'departmentFixViewport', 
				layout: 'anchor', 
				items: [/*{
					xtype: 'erpToolbar3',
				    anchor: '100% 7%', 
				},*/{
					xtype: 'erpGridPanel4',
					anchor: '100% 100%', 
					/*detno: 'pd_detno',
					necessaryField: 'pd_prodcode',*/
					keyField: 'dp_id',
				/*	mainField: 'pd_puid'*/
					/*type:'onlySingleGrid'*/
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});