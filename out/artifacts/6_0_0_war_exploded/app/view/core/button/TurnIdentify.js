/**
 * 转认定
 */	
Ext.define('erp.view.core.button.TurnIdentify',{
		extend: 'Ext.Button', 
		alias: 'widget.erpTurnIdentifyButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpTurnIdentifyButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 110,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});