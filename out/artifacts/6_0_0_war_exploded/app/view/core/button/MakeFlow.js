/**
 * 冻结按钮
 */	
Ext.define('erp.view.core.button.MakeFlow',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpMakeFlowButton',
		iconCls: 'x-button-icon-check',
    	cls: 'x-btn-gray',
	    id: 'MakeFlow',
    	text: $I18N.common.button.erpMakeFlowButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 120,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});