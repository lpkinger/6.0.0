/**
 * ECNC
 * 打开所有明细
 */	
Ext.define('erp.view.core.button.OpenECNAllDetail',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpOpenECNAllDetailButton',
		iconCls: 'x-button-icon-check',
    	cls: 'x-btn-gray',
	    id:'openalldetailbutton',
    	text: $I18N.common.button.erpOpenECNAllDetailButton,
    	style: {
    		marginLeft: '10px'
        },

        width: 110,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});