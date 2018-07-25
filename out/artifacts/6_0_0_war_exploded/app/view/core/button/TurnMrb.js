/**
 * 转MRB单
 */	
Ext.define('erp.view.core.button.TurnMrb',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpTurnMrbButton',
		iconCls: 'x-button-icon-delete',
    	cls: 'x-btn-gray',
    	id: 'erpTurnMrbButton',
    	text: $I18N.common.button.erpTurnMrbButton,
		initComponent : function(){ 
			this.callParent(arguments); 
		},
		width: 90
	});