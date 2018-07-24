/**
 * 费用分摊按钮
 */	
Ext.define('erp.view.core.button.FeeShare',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpFeeShareButton',
		iconCls: 'x-button-icon-check',
    	cls: 'x-btn-gray',
	    id: 'feeshare',
    	text: $I18N.common.button.erpFeeShareButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 100,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});