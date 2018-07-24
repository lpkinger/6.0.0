/**
 * 单据复制功能，将单据所有复制一份，流水号、编号和ID重新获取
 */
Ext.define('erp.view.core.button.CopyAll',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpCopyButton',
		text: $I18N.common.button.erpCopyButton,
		iconCls: 'x-button-icon-copy',
    	cls: 'x-btn-gray',
    	width: 60,
    	style: {
    		marginLeft: '10px'
        },
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});