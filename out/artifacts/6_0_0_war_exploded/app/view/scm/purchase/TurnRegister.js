Ext.define('erp.view.scm.purchase.TurnRegister',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 20%',
				keyField: 'pu_id',
				codeField: 'pu_code'
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 80%', 
				mainField: 'ca_departmentcode',
				allowExtraButtons: true,
				selModel: Ext.create('Ext.selection.CheckboxModel',{
			    	checkOnly : true,
					ignoreRightMouseSelection : false,
					listeners:{
				        selectionchange:function(selModel, selected, options){
				        	selModel.view.ownerCt.selectall = false;
				        }
				    }
				})				
			}]
		}); 
		me.callParent(arguments); 
	} 
});