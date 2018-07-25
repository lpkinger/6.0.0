/**
 * 批量更新自动询价-具体物料
 */	
Ext.define('erp.view.core.button.UpdateInquiryAuto',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpUpdateInquiryAutoButton',
		iconCls: 'x-button-icon-update',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpUpdateInquiryAutoButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 80,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});