Ext.define('erp.view.core.button.AvailableReplaceProd',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpAvailableReplaceProdButton',
		iconCls: 'x-button-icon-save',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpAvailableReplaceProdButton,
    	style: {
    		marginLeft: '5px'
        },
        width: 120,  
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});