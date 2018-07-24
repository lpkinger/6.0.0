/**
 * 查看下级BOM按钮
 */	
Ext.define('erp.view.core.button.SonBOM',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpSonBOMButton',
		iconCls: 'x-button-icon-check',
    	cls: 'x-btn-gray',
	    id: 'sonbom',
	    disabled: true,
    	text: $I18N.common.button.erpSonBOMButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 90,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});