/**
 * 生成Project
 */
Ext.define('erp.view.core.button.TurnProdinoutIn',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpTurnProdinoutIn',
		text: $I18N.common.button.TurnProdinoutIn,
		iconCls: 'x-button-icon-add',
    	cls: 'x-btn-gray',
    	width: 120,
    	style: {
    		marginLeft: '10px'
        },
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});