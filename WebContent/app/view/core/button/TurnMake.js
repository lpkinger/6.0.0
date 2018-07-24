/**
 * 制造通知单转制造单按钮
 */	
Ext.define('erp.view.core.button.TurnMake',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpTurnMakeButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpTurnMakeButton,
    	style: {
    		marginLeft: '10px'
        },
        id:'turnmake',
        width: 100,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});