/**
 * 报价公共询价删除按钮
 */	
Ext.define('erp.view.core.button.DeleteAuto',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpDeleteAutoButton',
		iconCls: 'x-button-icon-delete',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpDeleteAutoButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 60,
		initComponent : function(){ 
			this.callParent(arguments); 
		},
	});