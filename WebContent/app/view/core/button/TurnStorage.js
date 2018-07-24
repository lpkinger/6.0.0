/**
 * 采购收料单转入库单按钮
 */	
Ext.define('erp.view.core.button.TurnStorage',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpTurnStorageButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpTurnStorageButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 100,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});