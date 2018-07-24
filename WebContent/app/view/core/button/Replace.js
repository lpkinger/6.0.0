/**
 * 查看替代关系维护按钮
 */	
Ext.define('erp.view.core.button.Replace',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpReplaceButton',
		iconCls: 'x-button-icon-check',
    	cls: 'x-btn-gray',
	    id: 'replace',
	    disabled: true,
    	text: $I18N.common.button.erpReplaceButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 90,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});