/**
 * ECN
 * 关闭所有明细
 */	
Ext.define('erp.view.core.button.CloseECNAllDetail',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpCloseECNAllDetailButton',
		iconCls: 'x-button-icon-check',
    	cls: 'x-btn-gray',
	    id:'closealldetailbutton',
    	text: $I18N.common.button.erpCloseECNAllDetailButton,
    	style: {
    		marginLeft: '10px'
        },

        width: 110,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});