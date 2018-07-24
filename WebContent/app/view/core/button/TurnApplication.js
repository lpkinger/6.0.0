/**
 * 转请购单
 */	
Ext.define('erp.view.core.button.TurnApplication',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpTurnApplicationButton',
		iconCls: 'x-button-icon-turn',
    	cls: 'x-btn-gray',
    	id:'turnapplicationbutton',
    	text: $I18N.common.button.erpTurnApplicationButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 90,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});