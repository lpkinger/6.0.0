Ext.define('erp.view.core.button.LoadFeature',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpLoadFeatureButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	id:'loadfeature',
    	text: $I18N.common.button.erpLoadFeatureButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 90,        
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});