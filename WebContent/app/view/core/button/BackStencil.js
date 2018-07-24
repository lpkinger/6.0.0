/**
 * 归还钢网
 */
Ext.define('erp.view.core.button.BackStencil',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpBackStencilButton',
		iconCls: 'x-button-icon-delete',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpBackStencilButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 60,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});