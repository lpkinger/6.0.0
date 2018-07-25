/**
 * 制造ECN
 * 全部取消执行
 */	
Ext.define('erp.view.core.button.CloseAllDetail',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpCloseAllDetailButton',
		iconCls: 'x-button-icon-check',
    	cls: 'x-btn-gray',
	    id:'closealldetailbutton',
    	text: $I18N.common.button.erpCloseAllDetailButton,
    	style: {
    		marginLeft: '10px'
        },

        width: 110,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});