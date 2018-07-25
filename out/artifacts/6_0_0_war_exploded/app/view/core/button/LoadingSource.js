Ext.define('erp.view.core.button.LoadingSource',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpLoadingSourceButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	id:'loadingsource',
    	text: $I18N.common.button.erpLoadingSourceButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 80,     
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});