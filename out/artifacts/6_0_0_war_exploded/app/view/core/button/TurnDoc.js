/**
 * 转会议纪要
 */	
Ext.define('erp.view.core.button.TurnDoc',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpTurnTurnDocButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpTurnTurnDocButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 120,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});