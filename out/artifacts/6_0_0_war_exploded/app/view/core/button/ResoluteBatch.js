/**
 * 冻结按钮
 */	
Ext.define('erp.view.core.button.ResoluteBatch',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpResoluteBatch',
		iconCls: 'x-button-icon-check',
    	cls: 'x-btn-gray',
	    id: 'MakeFlow',
    	text: $I18N.common.button.erpResoluteBatch,
    	style: {
    		marginLeft: '10px'
        },
        width: 100,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});