/**
 * 保存船务信息
 */	
Ext.define('erp.view.core.button.SaveShip',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpSaveShipButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpSaveShipButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 130,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});