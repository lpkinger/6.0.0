Ext.define('erp.view.core.button.Start',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpStartButton',
		iconCls: 'x-button-icon-start',
    	cls: 'x-btn-gray',	    
    	text: $I18N.common.button.erpStartButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 60,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});