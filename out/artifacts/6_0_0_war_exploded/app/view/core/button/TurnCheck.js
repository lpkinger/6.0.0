/**
 * 采购收料单转检验单按钮
 */	
Ext.define('erp.view.core.button.TurnCheck',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpTurnCheckButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpTurnCheckButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 110,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});