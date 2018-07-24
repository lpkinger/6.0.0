Ext.define('erp.view.core.button.Stop',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpStopButton',
		iconCls: 'x-button-icon-stop',
    	cls: 'x-btn-gray',	    
    	text: $I18N.common.button.erpStopButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 60,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});