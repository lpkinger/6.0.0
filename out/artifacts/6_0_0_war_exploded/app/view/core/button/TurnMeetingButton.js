Ext.define('erp.view.core.button.TurnMeetingButton',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpTurnMeetingButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpTurnMeetingButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 150,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});